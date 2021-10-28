package com.example.locationtracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.locationtracker.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (checkPermission()) {
            if (Utils.locationEnabled(this)) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(this, LocationService::class.java)
                )
            } else {
                Toast.makeText(this@MainActivity, "Permission Enable GPS", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            val permissionListener: PermissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                    startLocationService()
                    Handler(Looper.getMainLooper()).postDelayed({
                        getLocation()
                    }, 5000)
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission Denied\n$deniedPermissions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this Location \n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .check()


        }

        binding.refreshButton.setOnClickListener {
            getLocation()
        }
        binding.logoutButton.setOnClickListener {
            stopService(Intent(this, LocationService::class.java))
            Pref.setBooleanValue(this, "login_key", false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    private fun startLocationService() {
        ContextCompat.startForegroundService(
            this,
            Intent(this, LocationService::class.java)
        )
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        var result = ""
        try {
            val fileName = "location_data.txt"
            if (File(filesDir.absolutePath, fileName).exists()) {
                val inputStream: InputStream? = openFileInput(fileName)
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var temp: String?
                    val stringBuilder = StringBuilder()
                    while (bufferedReader.readLine().also { temp = it } != null) {
                        stringBuilder.append(temp)
                        stringBuilder.append("\n")
                    }
                    inputStream.close()
                    result = stringBuilder.toString()
                }
            }

        } catch (e: NoSuchFileException) {
            e.printStackTrace()
        }
        binding.currentLocationText.text =
            if (result.isBlank()) "Error Fetching Location" else result
        binding.progressBar.visibility = View.GONE
    }


}

