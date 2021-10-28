package com.example.locationtracker

import android.content.Context
import android.location.LocationManager

class Utils {
    companion object
    {
        public fun locationEnabled(context: Context) : Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    }
}