package com.dave.organisationgatepass.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dave.organisationgatepass.R
import com.dave.organisationgatepass.helperClass.CheckedBoxes
import com.dave.organisationgatepass.helperClass.DbScanned
import com.dave.organisationgatepass.helperClass.DbScanned1
import com.dave.organisationgatepass.retrofit.helperClasses.DbUserData


class TimeStatusAdapter(

    private var dbScannedList: List<DbScanned1>,
    private val context: Context

) :
    RecyclerView.Adapter<TimeStatusAdapter.Pager2ViewHolder>() {

    var sharedpreferences = context.getSharedPreferences(
        context.resources.getString(R.string.app_name_use), Context.MODE_PRIVATE);
    var editor = sharedpreferences.edit()


    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val tvEmployeeName : TextView = itemView.findViewById(R.id.tvEmployeeName)
        val tvTimeStatus : TextView = itemView.findViewById(R.id.tvTimeStatus)

        init {

        }


        override fun onClick(v: View?) {

            val position = adapterPosition.toString()
            val details = dbScannedList[adapterPosition].userName
//            val id = details.id

         
        }


    }
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeStatusAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.employee_time_status,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TimeStatusAdapter.Pager2ViewHolder, position: Int) {

        val employeeName = dbScannedList[position].userName
        var timeStatus : String? = ""

        val tabName = sharedpreferences.getString("tabName", null)
        if (tabName != null){

            if (tabName == context.getString(R.string.arrived_early)){
                timeStatus = dbScannedList[position].arrivalTime
            }
            if (tabName == context.getString(R.string.arrived_late)){
                timeStatus = dbScannedList[position].arrivalTime
            }
            if (tabName == context.getString(R.string.left_early)){
                timeStatus = dbScannedList[position].departureTime
            }
            if (tabName == context.getString(R.string.left_late)){
                timeStatus = dbScannedList[position].departureTime
            }

            if (timeStatus != null){
                holder.tvTimeStatus.text = timeStatus
            }

        }

        holder.tvEmployeeName.text = employeeName

    }

    
    override fun getItemCount(): Int {
        return dbScannedList.size
    }

}