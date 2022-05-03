package com.example.theodoroschristou_assignment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import android.widget.Button
import android.widget.TextView
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.net.URL
import androidx.lifecycle.lifecycleScope as lifecycleScope1




class MainActivity : AppCompatActivity(), LocationListener {
    var latitude = 0.0
    var longitude = 0.0
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    var listOfRestaurants = mutableListOf<Restaurant>()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        val markerGestureListener = object:ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
        {
            override fun onItemLongPress(i: Int, item:OverlayItem ) : Boolean
            {
                Toast.makeText(this@MainActivity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemSingleTapUp(i: Int, item:OverlayItem): Boolean
            {
                Toast.makeText(this@MainActivity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
        }


        val map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(16.0)
        map1.controller.setCenter(GeoPoint(51.05, -0.72))
        items = ItemizedIconOverlay(this, arrayListOf<OverlayItem>(), markerGestureListener)
        map1.overlays.add(items)
        requestLocation()
    }

    fun requestLocation() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val mgr=getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0f,this)

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    fun showTheDialog(message : String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setPositiveButton("OK", null)
            .setMessage(message)
            .show()
    }

    fun showTheToast(message : String) {
        Toast.makeText(this, "${message}", Toast.LENGTH_LONG).show()
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            0 -> {

                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation()
                } else {
                    AlertDialog.Builder(this)
                        .setPositiveButton("OK", null)
                        .setMessage("This app will not work without the GPS permission enabled.")
                        .show()
                }
            }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val Boolean = prefs.getBoolean("webupload", false)
        if (Boolean){
            showTheToast("Automatic Web upload is on")
        }


    }

    val addRestaurantLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.apply {
                    val name = this.getStringExtra("com.example.name").toString()
                    val address = this.getStringExtra("com.example.address").toString()
                    val cuisine = this.getStringExtra("com.example.cuisine").toString()
                    val starr = this.getIntExtra("com.example.starr", 0)
                    var restaurant = Restaurant(0,name,address,cuisine,starr,latitude,longitude)
                    showTheDialog("Created marker for ${restaurant.name}")
                    listOfRestaurants.add(restaurant)
                    val newRestaurant = OverlayItem("${name}", "Name :${name} , Address: ${address} , Cuisine: ${cuisine}, Star Rating: ${starr}", GeoPoint(latitude, longitude))
                    items.addItem(newRestaurant)

                    val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                    val Boolean = prefs.getBoolean("webupload", false)

                    if (Boolean){

                        val starRating = starr.toString().toInt()
                        val lat = latitude.toString().toDouble()
                        val lon = longitude.toString().toDouble()

                        val url = "http://10.0.2.2:3000/restaurant/create"
                        val postData = listOf("name" to name, "cuisine" to cuisine, "address" to address, "starRating" to starRating,  "lon" to lon, "lat" to lat)
                        url.httpPost(postData).response { request, response, result ->
                            when (result) {
                                is Result.Success -> {
                                    showTheToast("Restaurant added on Web")
                                }

                                is Result.Failure -> {
                                    showTheToast("Restaurant couldnt be added because of error: ${result.error.message}")

                                }
                            }
                        }
                    }
                }
            }
        }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        when(item.itemId) {
            R.id.addrestaurant -> {
                val intent = Intent(this,AddRestaurantActivity::class.java)
                addRestaurantLauncher.launch(intent)
                return true
            }
            R.id.saveall -> {
                val db = RestaurantDatabase.getDatabase(application)
                lifecycleScope1.launch {
                    for (i in 0 until listOfRestaurants.size) {
                        var id = 0L
                        withContext(Dispatchers.IO) {
                            var addRestaurant = listOfRestaurants[i]
                            id = db.RestaurantDao().insert(addRestaurant)
                        }

                    }
                    showTheDialog("Restaurants got saved to the database")
                }
                return true
            }
            R.id.menuItemPreferences -> {
                val intent = Intent(this, MyPrefsActivity::class.java)
                startActivity(intent)
            }
            R.id.loadfromSQL -> {
                lifecycleScope1.launch {
                    showTheDialog("Successfully loaded restaurants from SQL")
                    withContext(Dispatchers.IO) {
                        val db = RestaurantDatabase.getDatabase(application)
                        var loadRestaurantList = db.RestaurantDao().getAllRestaurants()
                        for (i in 0 until loadRestaurantList.size) {
                            var loadRestaurant = loadRestaurantList[i]
                            loadRestaurant?.apply {
                                var Rname = loadRestaurant.name
                                var Raddress = loadRestaurant.address
                                var Rcuisine = loadRestaurant.cuisine
                                var Rstarr = loadRestaurant.rating
                                var Rlatitude = loadRestaurant.lat
                                var Rlongitude = loadRestaurant.lon
                                val newRestaurant =
                                    OverlayItem("${Rname}", "Name :${Rname} , Address: ${Raddress} , Cuisine: ${Rcuisine}, Star Rating: ${Rstarr}", GeoPoint(Rlatitude, Rlongitude))
                                items.addItem(newRestaurant)
                            }
                        }
                    }
                }
            }
            R.id.loadfromWeb -> {
                lifecycleScope1.launch {
                    var url = "http://10.0.2.2:3000/restaurants/all"
                    url.httpGet().responseJson { request, response, result ->

                        when(result) {
                            is Result.Success -> {
                                val jsonArray = result.get().array()
                                for(i in 0 until jsonArray.length()) {
                                    val curObj = jsonArray.getJSONObject(i)
                                    var Rname = curObj.getString("name")
                                    var Raddress = curObj.getString("address")
                                    var Rcuisine = curObj.getString("cuisine")
                                    var Rstarr = curObj.getString("starRating")
                                    var Rlatitude = curObj.getString("lat").toDouble()
                                    var Rlongitude = curObj.getString("lon").toDouble()
                                    val newRestaurant =
                                        OverlayItem("${Rname}", "Name :${Rname} , Address: ${Raddress} , Cuisine: ${Rcuisine}, Star Rating: ${Rstarr}", GeoPoint(Rlatitude, Rlongitude))
                                    items.addItem(newRestaurant)
                                }
                                showTheDialog("Successfully loaded restaurants from Web")
                            }

                            is Result.Failure -> {
                                showTheDialog("ERROR ${result.error.message}")
                            }
                        }

                    }
                }
            }
        }
        return false
    }



    override fun onLocationChanged(newLoc: Location) {
        val map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(16.0)
        latitude = newLoc.latitude
        longitude = newLoc.longitude
        map1.controller.setCenter(GeoPoint(newLoc.latitude, newLoc.longitude))
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText (this, "Provider disabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText (this, "Provider enabled", Toast.LENGTH_LONG).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onStop() {
        super.onStop()
        val db = RestaurantDatabase.getDatabase(application)
        lifecycleScope1.launch {
            for (i in 0 until listOfRestaurants.size) {
                var id = 0L
                withContext(Dispatchers.IO) {
                    var addRestaurant = listOfRestaurants[i]
                    id = db.RestaurantDao().insert(addRestaurant)
                }

            }
        }
    }
}
