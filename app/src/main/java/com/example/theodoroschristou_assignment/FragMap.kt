package com.example.theodoroschristou_assignment

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem


class FragMap() : Fragment(R.layout.fragmap) {
    var pendingLon = 0.0
    var pendingLat = 0.0
    var pendingname = ""
    var pendingaddress = ""
    var pendingcuisine = ""
    var pendingstarr = 0
    var pendingLocationUpdate = false
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    var listOfRestaurants = mutableListOf<Restaurant>()
    lateinit var map1: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Configuration.getInstance()
            .load(activity, PreferenceManager.getDefaultSharedPreferences(activity));

        val markerGestureListener = object:ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
        {
            override fun onItemLongPress(i: Int, item:OverlayItem ) : Boolean
            {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemSingleTapUp(i: Int, item:OverlayItem): Boolean
            {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        items = ItemizedIconOverlay(activity, arrayListOf<OverlayItem>(), markerGestureListener)
        map1 = view.findViewById<MapView>(R.id.map1)
        map1.controller.setCenter(GeoPoint(51.05, -0.72))
        map1?.controller?.setZoom(16.0)
        map1?.overlays?.add(items)

        addMarkers()


        if(pendingLocationUpdate){
            addNewMarker(pendingname, pendingaddress, pendingcuisine, pendingstarr, pendingLat, pendingLon)
        }



    }

    fun LocationChanged(latitude: Double,longitude: Double) {
        map1?.controller?.setCenter(GeoPoint(latitude, longitude))
    }

    fun addNewMarker(name: String, address: String, cuisine: String, starr: Int, lat: Double, lon: Double){
        var restaurant = Restaurant(0,name,address,cuisine,starr,lat,lon)
        val newRestaurantfrag = OverlayItem("${name}", "Name :${name} , Address: ${address} , Cuisine: ${cuisine}, Star Rating: ${starr}", GeoPoint(lat, lon))
        items.addItem(newRestaurantfrag)
        listOfRestaurants.add(restaurant)
        map1?.overlays?.add(items)
        pendingLocationUpdate = false
    }

    fun addMarkers(){
        for (i in 0 until listOfRestaurants.size) {
            var loadRestaurant = listOfRestaurants[i]
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

    fun setPendingLocation(pendingname: String, pendingaddress: String, pendingcuisine: String, pendingstarr: Int, pendingLat: Double, pendingLon: Double) {
        this.pendingLon = pendingLon
        this.pendingLat = pendingLat
        this.pendingname = pendingname
        this.pendingaddress = pendingaddress
        this.pendingcuisine = pendingcuisine
        this.pendingstarr = pendingstarr
        this.pendingLocationUpdate = true
    }

    fun LoadFromSQL(name: String, address: String, cuisine: String, starr: Int, lat: Double, lon: Double){
        var restaurant = Restaurant(0,name,address,cuisine,starr,lat,lon)
        val newRestaurantfrag = OverlayItem("${name}", "Name :${name} , Address: ${address} , Cuisine: ${cuisine}, Star Rating: ${starr}", GeoPoint(lat, lon))
        items.addItem(newRestaurantfrag)
        listOfRestaurants.add(restaurant)
    }

    fun LoadFromWeb(name: String, address: String, cuisine: String, starr: Int, lat: Double, lon: Double){
        var restaurant = Restaurant(0,name,address,cuisine,starr,lat,lon)
        val newRestaurantfrag = OverlayItem("${name}", "Name :${name} , Address: ${address} , Cuisine: ${cuisine}, Star Rating: ${starr}", GeoPoint(lat, lon))
        items.addItem(newRestaurantfrag)
        listOfRestaurants.add(restaurant)
    }



}