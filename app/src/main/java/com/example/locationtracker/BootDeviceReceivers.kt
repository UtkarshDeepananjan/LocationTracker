package com.example.locationtracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat

class BootDeviceReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            context?.let {
                if (Utils.locationEnabled(it)) {
                    ContextCompat.startForegroundService(
                        it,
                        Intent(it, LocationService::class.java)
                    )
                } else {
                    Toast.makeText(it, "Permission Enable GPS", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}