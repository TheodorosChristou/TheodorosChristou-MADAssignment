package com.example.theodoroschristou_assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf

class AddRestaurantActivity: AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)

        val addbtn = findViewById<Button>(R.id.btn1)
        addbtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val name = findViewById<EditText>(R.id.et1)
        val address = findViewById<EditText>(R.id.et2)
        val cuisine = findViewById<EditText>(R.id.et3)
        val starr = findViewById<EditText>(R.id.et4)


        var namex = ""
        var addressx = ""
        var cuisinex = ""
        var starrx = 0

        when (v?.id) {
            R.id.btn1 -> {
                namex = name.text.toString()
                addressx = address.text.toString()
                cuisinex = cuisine.text.toString()
                starrx = starr.text.toString().toInt()
                addRestaurantLauncher(namex, addressx, cuisinex, starrx)
            }
        }
    }

    fun addRestaurantLauncher(name: String, address: String, cuisine: String, starr: Int ) {
        val intent = Intent()
        val bundle = bundleOf("com.example.name" to name, "com.example.address" to address, "com.example.cuisine" to cuisine, "com.example.starr" to starr, )
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
    }
}