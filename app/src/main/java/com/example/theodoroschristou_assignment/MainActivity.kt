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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
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
                    val newRestaurant = OverlayItem("${name}", "${name}",GeoPoint(latitude, longitude))
                    items.addItem(newRestaurant)
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

    override fun onDestroy() {
        super.onDestroy()
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
    }
}
