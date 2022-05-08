package com.example.theodoroschristou_assignment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class FragAddRestaurant() : Fragment(R.layout.fragaddrestaurant) {
    var callback: ((String,String,String,Int) -> Unit)? = null
    var notNull = true
    override fun onViewCreated(v: View, b: Bundle?)  {

        val namex = v.findViewById<EditText>(R.id.et1)
        val addressx = v.findViewById<EditText>(R.id.et2)
        val cuisinex = v.findViewById<EditText>(R.id.et3)
        val starrx = v.findViewById<EditText>(R.id.et4)

        val addbtn = v.findViewById<Button>(R.id.btn1)
        addbtn.setOnClickListener{

            var name = ""
            var address = ""
            var cuisine = ""
            var starr = ""
            var starrN = 0

            name = namex.text.toString()
            address = addressx.text.toString()
            cuisine = cuisinex.text.toString()
            starr = starrx.text.toString()

            if(name == ""){
                notNull = false
                Toast.makeText(activity, "Name must have a value", Toast.LENGTH_SHORT).show()
            }else if (address == ""){
                notNull = false
                Toast.makeText(activity, "Address must have a value", Toast.LENGTH_SHORT).show()
            }else if (cuisine == ""){
                notNull = false
                Toast.makeText(activity, "Cuisine must have a value", Toast.LENGTH_SHORT).show()
            }else if (starr == ""){
                notNull = false
                Toast.makeText(activity, "Star rating must have a value", Toast.LENGTH_SHORT).show()
            }else{
                starrN = starrx.text.toString().toInt()
            }

            if(notNull){
                callback?.invoke(name, address, cuisine, starrN)

                namex.setText("")
                addressx.setText("")
                cuisinex.setText("")
                starrx.setText("")
                Toast.makeText(activity, "Restaurant added to map, you can now go back", Toast.LENGTH_SHORT).show()

            }

            notNull = true


        }
    }

}