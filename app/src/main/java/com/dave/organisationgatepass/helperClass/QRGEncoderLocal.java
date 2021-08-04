package com.dave.organisationgatepass.helperClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import androidx.annotation.RequiresApi;

import com.dave.organisationgatepass.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;

public class QRGEncoderLocal {

//    private static final int WHITE = 0xFFFFFFFF;
//    private static final int BLACK = 0xFF000000;

    private int dimension = Integer.MIN_VALUE;
    private String contents = null;
    private String displayContents = null;
    private String title = null;
    private BarcodeFormat format = null;
    private boolean encoded = false;
    private Context context;

    public QRGEncoderLocal(String data, Bundle bundle, String type, int dimension, Context context) {
        this.dimension = dimension;
        encoded = encodeContents(data, bundle, type);
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    private boolean encodeContents(String data, Bundle bundle, String type) {
        // Default to QR_CODE if no format given.
        format = BarcodeFormat.QR_CODE;
        if (format == BarcodeFormat.QR_CODE) {
            this.format = BarcodeFormat.QR_CODE;
            encodeQRCodeContents(data, bundle, type);
        } else if (data != null && data.length() > 0) {
            contents = data;
            displayContents = data;
            title = "Text";
        }
        return contents != null && contents.length() > 0;
    }

    private void encodeQRCodeContents(String data, Bundle bundle, String type) {
        switch (type) {
            case QRGContents.Type.TEXT:
                if (data != null && data.length() > 0) {
                    contents = data;
                    displayContents = data;
                    title = "Text";
                }
                break;
            case QRGContents.Type.EMAIL:
                data = trim(data);
                if (data != null) {
                    contents = "mailto:" + data;
                    displayContents = data;
                    title = "E-Mail";
                }
                break;
            case QRGContents.Type.PHONE:
                data = trim(data);
                if (data != null) {
                    contents = "tel:" + data;
                    displayContents = PhoneNumberUtils.formatNumber(data);
                    title = "Phone";
                }
                break;
            case QRGContents.Type.SMS:
                data = trim(data);
                if (data != null) {
                    contents = "sms:" + data;
                    displayContents = PhoneNumberUtils.formatNumber(data);
                    title = "SMS";
                }
                break;
            case QRGContents.Type.CONTACT:
                if (bundle != null) {
                    StringBuilder newContents = new StringBuilder(100);
                    StringBuilder newDisplayContents = new StringBuilder(100);

                    newContents.append("MECARD:");

                    String name = trim(bundle.getString(ContactsContract.Intents.Insert.NAME));
                    if (name != null) {
                        newContents.append("N:").append(escapeMECARD(name)).append(';');
                        newDisplayContents.append(name);
                    }

                    String address = trim(bundle.getString(ContactsContract.Intents.Insert.POSTAL));
                    if (address != null) {
                        newContents.append("ADR:").append(escapeMECARD(address)).append(';');
                        newDisplayContents.append('\n').append(address);
                    }

                    Collection<String> uniquePhones = new HashSet<>(QRGContents.PHONE_KEYS.length);
                    for (int x = 0; x < QRGContents.PHONE_KEYS.length; x++) {
                        String phone = trim(bundle.getString(QRGContents.PHONE_KEYS[x]));
                        if (phone != null) {
                            uniquePhones.add(phone);
                        }
                    }
                    for (String phone : uniquePhones) {
                        newContents.append("TEL:").append(escapeMECARD(phone)).append(';');
                        //noinspection deprecation
                        newDisplayContents.append('\n').append(PhoneNumberUtils.formatNumber(phone));
                    }

                    Collection<String> uniqueEmails = new HashSet<>(QRGContents.EMAIL_KEYS.length);
                    for (int x = 0; x < QRGContents.EMAIL_KEYS.length; x++) {
                        String email = trim(bundle.getString(QRGContents.EMAIL_KEYS[x]));
                        if (email != null) {
                            uniqueEmails.add(email);
                        }
                    }
                    for (String email : uniqueEmails) {
                        newContents.append("EMAIL:").append(escapeMECARD(email)).append(';');
                        newDisplayContents.append('\n').append(email);
                    }

                    String url = trim(bundle.getString(QRGContents.URL_KEY));
                    if (url != null) {
                        // escapeMECARD(url) -> wrong escape e.g. http\://zxing.google.com
                        newContents.append("URL:").append(url).append(';');
                        newDisplayContents.append('\n').append(url);
                    }

                    String note = trim(bundle.getString(QRGContents.NOTE_KEY));
                    if (note != null) {
                        newContents.append("NOTE:").append(escapeMECARD(note)).append(';');
                        newDisplayContents.append('\n').append(note);
                    }

                    // Make sure we've encoded at least one field.
                    if (newDisplayContents.length() > 0) {
                        newContents.append(';');
                        contents = newContents.toString();
                        displayContents = newDisplayContents.toString();
                        title = "Contact";
                    } else {
                        contents = null;
                        displayContents = null;
                    }

                }
                break;
            case QRGContents.Type.LOCATION:
                if (bundle != null) {
                    // These must use Bundle.getFloat(), not getDouble(), it's part of the API.
                    float latitude = bundle.getFloat("LAT", Float.MAX_VALUE);
                    float longitude = bundle.getFloat("LONG", Float.MAX_VALUE);
                    if (latitude != Float.MAX_VALUE && longitude != Float.MAX_VALUE) {
                        contents = "geo:" + latitude + ',' + longitude;
                        displayContents = latitude + "," + longitude;
                        title = "Location";
                    }
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Bitmap encodeAsBitmap() throws WriterException {

        int color1 = context.getColor(R.color.colorQR);
        int color2 = context.getColor(R.color.colorWhite);

        if (!encoded) return null;

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, dimension, dimension, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? color1 : color2;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private static String trim(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        return result.length() == 0 ? null : result;
    }

    private static String escapeMECARD(String input) {
        if (input == null || (input.indexOf(':') < 0 && input.indexOf(';') < 0)) {
            return input;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == ':' || c == ';') {
                result.append('\\');
            }
            result.append(c);
        }
        return result.toString();
    }
}
