package com.example.esp32ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var graph: GraphView
    private lateinit var connectButton: Button
    private lateinit var statusText: TextView
    private lateinit var temperatureText: TextView
    private lateinit var humidityText: TextView
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private var seriesTemperature: LineGraphSeries<DataPoint>? = null
    private var seriesHumidity: LineGraphSeries<DataPoint>? = null
    private var xAxisValue = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        graph           = findViewById(R.id.graph)
        connectButton   = findViewById(R.id.connectButton)
        statusText      = findViewById(R.id.statusText)
        temperatureText =  findViewById<TextView>(R.id.temperatureText)
        humidityText    =  findViewById<TextView>(R.id.humidityText)

        // Initialize Bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // GraphView setup
        seriesTemperature = LineGraphSeries()
        seriesTemperature?.color = resources.getColor(android.R.color.holo_red_dark)
        seriesHumidity = LineGraphSeries()

        graph.addSeries(seriesTemperature)
        graph.addSeries(seriesHumidity)
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(50.0)  // Set the limit of X axis to display 10 data points

        connectButton.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                connectToESP32()
            } else {
                statusText.text = "Bluetooth is off!"
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToESP32() {
        val deviceName = "ESP32_DHT22" // Replace with your ESP32 Bluetooth device name
        val pairedDevices = bluetoothAdapter.bondedDevices

        var device: BluetoothDevice? = null
        for (pairedDevice in pairedDevices) {
            if (pairedDevice.name == deviceName) {
                device = pairedDevice
                break
            }
        }

        if (device != null) {
            try {
                val uuid = device.uuids[0].uuid
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream
                statusText.text = "Connected to $deviceName"
                connectButton.text = "Disconnect"
                connectButton.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))

                // Start receiving data from ESP32
                receiveData()
            } catch (e: Exception) {
                e.printStackTrace()
                statusText.text = "Sending Data Stopped"
                connectButton.text = "Connect"
                connectButton.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))

            }
        }
    }

    private fun receiveData() {
        Thread {
            while (bluetoothSocket?.isConnected == true) {
                try {
                    val buffer = ByteArray(1024)
                    val bytesRead = inputStream?.read(buffer) ?: 0

                    if (bytesRead > 0) {
                        val data = String(buffer, 0, bytesRead).trim()
                        Log.d("ReceivedData", data)

                        // Parse temperature and humidity from ESP32 (assuming comma separated)
                        val parts = data.split(",")
                        if (parts.size == 2) {
                            val temperature = parts[0].toDoubleOrNull()
                            val humidity = parts[1].toDoubleOrNull()

                            if (temperature != null && humidity != null) {

                                runOnUiThread {
                                    // Kirim data ke API
                                    sendDataToAPI(temperature.toString(), humidity.toString())

                                    // Add data to the graph
                                    temperatureText.text = " Temperature: $temperature C"
                                    humidityText.text = " Humidity: $humidity %"
                                    seriesTemperature?.appendData(DataPoint(xAxisValue, temperature), true, 100)
                                    seriesHumidity?.appendData(DataPoint(xAxisValue, humidity), true, 100)

                                    // Scroll to end
                                    graph.viewport.scrollToEnd()

                                    // Increment X axis value
                                    xAxisValue += 1
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun sendDataToAPI(temperature: String, humidity: String) {
        thread {
            try {
                //sesuaikan dengan Endpoint kalian
                val url = URL("https://prediksidiabetes.com/API/save_sensor.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = "temperature=$temperature&humidity=$humidity"
                connection.outputStream.write(postData.toByteArray())

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("API", "Data berhasil dikirim")
                } else {
                    Log.e("API", "Gagal mengirim data")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
