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
import com.dave.organisationgatepass.retrofit.helperClasses.DbUserData


class UsersAdapter(

    private var dbStaffResultsList: List<DbUserData>,
    private val context: Context

) :
    RecyclerView.Adapter<UsersAdapter.Pager2ViewHolder>() {

    var sharedpreferences = context.getSharedPreferences(
        context.resources.getString(R.string.app_name_use), Context.MODE_PRIVATE);
    var editor = sharedpreferences.edit()

    var selectedCheckBoxList = ArrayList<CheckedBoxes>()

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val checkboxFullName : CheckBox = itemView.findViewById(R.id.checkboxFullName)
        val tvPhoneNumber : TextView = itemView.findViewById(R.id.tvPhoneNumber)

        init {
            checkboxFullName.setOnClickListener(this)
        }


        override fun onClick(v: View?) {

            val position = adapterPosition.toString()
            val details = dbStaffResultsList[adapterPosition].userData
            val id = details.id

            //Check if pos is in stored list
            val checkedStoredList = getPositionSet()
            if (checkedStoredList.contains(id)){
                deletePositionSet(id,checkedStoredList)
            }else{
                addNewSet(id,checkedStoredList)
            }

        }


    }

    private fun addNewSet(position: String, checkedStoredList: ArrayList<String>) {
        checkedStoredList.add(position)
        val checkedList = HashSet<String>()
        for (oldIds in checkedStoredList){
            checkedList.add(oldIds)
        }
        addPositionSet(checkedList)

    }

    private fun addPositionSet(checkedList: HashSet<String>) {
        editor.putStringSet("checkedNames", checkedList)
        editor.apply()
    }

    private fun deletePositionSet(position: String, checkedStoredList: ArrayList<String>) {
        checkedStoredList.remove(position)
        val checkedList = HashSet<String>()

        for (newIds in checkedStoredList){
            checkedList.add(newIds)
        }
        addPositionSet(checkedList)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.employee_data,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsersAdapter.Pager2ViewHolder, position: Int) {

        val fullName = dbStaffResultsList[position].userData.name
        val phoneNumber = dbStaffResultsList[position].userData.phone_number

        holder.checkboxFullName.text = fullName
        holder.tvPhoneNumber.text = phoneNumber

//        val checkedStoredList = getPositionSet()
//        for (posIds in checkedStoredList){
//            val id = posIds.toInt()
//            holder.checkboxFullName.isChecked = position == id
//        }





        /**
         * Checkbox manenos
         */




    }





    private fun getPositionSet() : ArrayList<String>{

        val checkPosList = ArrayList<String>()

        val plupaQuery = sharedpreferences.getStringSet("checkedNames", null)
        if (plupaQuery != null) {
            val list = ArrayList<String>(plupaQuery)
            for (items in list){
                checkPosList.add(items)
            }
        }
        return checkPosList
    }

    override fun getItemCount(): Int {
        return dbStaffResultsList.size
    }

}