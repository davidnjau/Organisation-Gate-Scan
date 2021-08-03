package com.dave.organisationgatepass.helperClass;

public class CheckedBoxes {

    private String fullName;
    private boolean isChecked;

    public CheckedBoxes() {
    }

    public CheckedBoxes(String fullName, boolean isChecked) {
        this.fullName = fullName;
        this.isChecked = isChecked;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
