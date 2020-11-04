package com.example.bluetoothchat

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    //bluetooth adapter
    private var bluetoothAdapter: BluetoothAdapter?=null

    //all required permissions
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.BLUETOOTH",
                                                    "android.permission.BLUETOOTH_ADMIN",
                                                                "android.permission.ACCESS_FINE_LOCATION")

    val REQUESTED_PERMISSIONS_CODE=101
    val BlUETOOTH_DISCOVERY_TIME=300
    val BLUETOOTH_DISCOVERY_REQUEST_CODE=102

    //displaying the bluetooth menu on the main activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.bluetooth_control_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //adding on click functionality to our bluetooth menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val onOptionsItemSelected = super.onOptionsItemSelected(item)

        return when(item.itemId) {
            //if item selected is Search Devices
            //then search for all available devices
            R.id.menu_search_devices -> {
                enableBluetooth()

                if (bluetoothAdapter?.scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    val intent = Intent(this, BluetoothDeviceListActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Device not discoverable", Toast.LENGTH_SHORT).show()
                }

                true
            }

            //if item selected is Enable Bluetooth
            //then switch On Bluetooth
            R.id.menu_enable_bluetooth -> {
                //function to enable bluetooth
                enableBluetooth()
                true
            }

            //Else some error has occurred
            else -> {
                Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun enableBluetooth() {
        if(bluetoothAdapter!=null) {
            if (bluetoothAdapter!!.isEnabled) {
//                Toast.makeText(this, "Bluetooth is already On", Toast.LENGTH_SHORT).show()
            } else {
                bluetoothAdapter!!.enable()
                Toast.makeText(this, "Bluetooth is turned On", Toast.LENGTH_SHORT).show()
            }

            if (bluetoothAdapter?.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                requestDiscoverable()
            }
        }
    }

    private fun requestDiscoverable() {
        var discoveryIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoveryIntent.putExtra(
            BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            BlUETOOTH_DISCOVERY_TIME
        )
        startActivityForResult(discoveryIntent, BLUETOOTH_DISCOVERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==BLUETOOTH_DISCOVERY_REQUEST_CODE) {
            if(resultCode== RESULT_CANCELED) {
                Toast.makeText(this, "Allow Discoverable Settings", Toast.LENGTH_SHORT).show()
                requestDiscoverable()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //defines the variables and asks for permissions
        init()
    }

    private fun init() {
        if(checkForAllPermissions()) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            enableBluetooth()
        }
        else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUESTED_PERMISSIONS_CODE)
        }
    }

    //checking if permissions are granted
    private fun checkForAllPermissions(): Boolean {
        for(permission in REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(this, permission)!=PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    //when permission results are received
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(checkForAllPermissions()) {
            init()
        }
        else {
            //close the application
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }
}