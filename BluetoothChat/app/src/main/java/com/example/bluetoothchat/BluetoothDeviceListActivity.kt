package com.example.bluetoothchat

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity


class BluetoothDeviceListActivity : AppCompatActivity() {

    //UI components
    var searchButton:Button? = null
    var deviceListView: ListView? = null

    //for bluetooth functionalities
    var bluetoothAdapter:BluetoothAdapter? = null
    var intentFilter= IntentFilter()

    //for setting the list view
    var deviceArrayList= mutableListOf<String>()
    var arrayAdapter: ArrayAdapter<String>? = null

    //to prevent duplicate devices
    var presentMacAddress= mutableListOf<String>()

    val MAC_ADDRESS_DIGITS: Int=17


    //starts searching
    public fun startSearch(view: View) {
//        Toast.makeText(this, "Start Search Clicked", Toast.LENGTH_SHORT).show()
        deviceArrayList.clear()

        if(bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()
        }

        arrayAdapter?.notifyDataSetChanged()
        bluetoothAdapter?.startDiscovery()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    searchButton?.text = "Searching.."
                    searchButton?.isEnabled = false;
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                //    val rssi =
                //        intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toString()

                    if (!presentMacAddress.contains(deviceHardwareAddress)) {
                        val curr_device: String =
                            "Device Name: $deviceName \nDevice Address: $deviceHardwareAddress"
                        deviceArrayList.add(curr_device)
                        arrayAdapter?.notifyDataSetChanged()

                        if (deviceHardwareAddress != null) {
                            presentMacAddress.add(deviceHardwareAddress)
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    searchButton?.text = "Start Search"
                    searchButton?.isEnabled = true

                    if (arrayAdapter?.count == 0) {
                        Toast.makeText(
                            this@BluetoothDeviceListActivity,
                            "No new devices found",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@BluetoothDeviceListActivity,
                            "Search Finished",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_device_list)

        searchButton=findViewById<Button>(R.id.searchButton)

        deviceListView= findViewById<ListView>(R.id.deviceListView)
        arrayAdapter= ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceArrayList)
        deviceListView!!.adapter=arrayAdapter

        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()


        intentFilter= IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter?.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter?.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter?.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        registerReceiver(receiver, intentFilter)

        deviceListView?.onItemClickListener = OnItemClickListener {adapterView, view, i, l ->
            val info: String = (view as TextView).text.toString()
            val address: String = info.takeLast(MAC_ADDRESS_DIGITS)


            val intent = Intent(this@BluetoothDeviceListActivity, ChatActivity::class.java)
            intent.putExtra("deviceAddress", address)
            startActivity(intent)
            finish()
        }
    }
}