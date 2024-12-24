
# Special Section: Integrated ESP-32 with Mobile Apps via Bluetooth and Remote-Server

This repository contains the source code for a mobile application that connects to an ESP32 device via Bluetooth to retrieve sensor data and send it to a remote server. The application is designed for seamless data collection, visualization, and forwarding for IoT projects.

---

## Features

- **Bluetooth Communication**: Connect to the ESP32 device using Bluetooth (BLE).
- **Real-Time Data Display**: Visualize sensor data on the mobile app.
- **Server Integration**: Forward data to a remote server for storage or further analysis.
- **Platform**: Works on Android platforms.

---

## Architecture

The system consists of three main components:

1. **ESP32 Device**:
   - Collects data from sensor DHT 22.
   - Sends data to the mobile application via Bluetooth.

2. **Mobile Application**:
   - Receives data from ESP32.
   - Displays the data in real-time.
   - Sends data to a remote server.

3. **Remote Server (Hosting)**:
   - Receives data from the mobile app via HTTPs (API).
   - Stores data in a database.

### System Workflow

1. The ESP32 collects sensor data and sends it to the mobile app via Bluetooth.
2. The mobile app visualizes the data in a user-friendly interface.
3. The app forwards the data to a remote server for further processing.

---

## Application Architecture

![Application Architecture](https://github.com/pulunghendroprastyo/Special-Section-Integrated-ESP-32-with-Mobile-Apps-via-Bluetooth-and-Remote-Server/blob/master/Images/Architecture%20of%20System.png?raw=true)

1. **ESP32 Bluetooth Communication**:
   - Data is transmitted over Bluetooth

2. **Mobile App**:
   - **Bluetooth Module**: Handles Bluetooth connection and data retrieval.
   - **UI Module**: Displays sensor data in real-time.
   - **HTTPs Module**: Sends data to the server using RESTful APIs.

3. **Server**:
   - **API Endpoint**: Receives data from the mobile app.
   - **Database**: Stores data for future use.
   - **Dashboard**: Provides analytics and visualizations.

---

## Getting Started


## IoT Schematic

![IoT](https://raw.githubusercontent.com/pulunghendroprastyo/Special-Section-Integrated-ESP-32-with-Mobile-Apps-via-Bluetooth-and-Remote-Server/refs/heads/master/Images/ESP%2032%20Schematic.PNG)

### 1. ESP32 Configuration
- Program the ESP32 with Arduino IDE .

Code for ESP32:
```cpp
#include <BluetoothSerial.h>
#include <DHT.h>
#include <LiquidCrystal_I2C.h>

// Pin sensor DHT22
#define DHTPIN 4
#define DHTTYPE DHT22

DHT dht(DHTPIN, DHTTYPE);
BluetoothSerial ESP_BT;

// Inisialisasi LCD I2C (alamat default 0x27 atau 0x3F tergantung modul LCD)
LiquidCrystal_I2C lcd(0x27, 16, 2); // 16 karakter dan 2 baris

void setup() {
  Serial.begin(115200);
  ESP_BT.begin("ESP32_DHT22");  // Nama Bluetooth
  dht.begin();
  Serial.println("ESP32 Bluetooth Initialized...");
 

   // Memulai LCD
  lcd.init();
  lcd.backlight();  // Menyalakan lampu latar LCD
  lcd.setCursor(0, 0);
  lcd.print("DHT 22 BLE");  // Menampilkan teks awal
  delay(2000);
}

void loop() {
  // Membaca data dari sensor DHT22
  float humidity = dht.readHumidity();
  float temperature = dht.readTemperature();

  // Mengecek jika pembacaan berhasil
  if (isnan(humidity) || isnan(temperature)) {
    Serial.println("Gagal membaca sensor!");
    return;
  }

  // Menampilkan data ke serial monitor
  Serial.print("Suhu: ");
  Serial.print(temperature);
  Serial.print(" C, Kelembapan: ");
  Serial.print(humidity);
  Serial.println(" %");

  // Menampilkan suhu dan kelembapan ke LCD
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Temp: ");
  lcd.print(temperature);
  lcd.print((char)223);  // Karakter derajat
  lcd.print("C");

  lcd.setCursor(0, 1);
  lcd.print("Hum : ");
  lcd.print(humidity);
  lcd.print("%");

  // Mengirim data melalui Bluetooth
 // Kirim data suhu dan kelembapan ke Bluetooth
  // ESP_BT.print("Temp: ");
  // ESP_BT.print(temperature);
  // ESP_BT.print(", Humidity: ");
  // ESP_BT.println(humidity);
  // Kirim data suhu dan kelembapan melalui Bluetooth
  String data = String(temperature) + "," + String(humidity);
  ESP_BT.println(data);
    // Kirim data suhu dan kelembapan ke smartphone
    // ESP_BT.print("T:");
    // ESP_BT.print(temperature);
    // ESP_BT.print(",H:");
    // ESP_BT.println(humidity);


  delay(2000); // Kirim setiap 2 detik
}
```

### 2. Mobile Application Setup
- Open the project in Android Studio.
- Add library implementation ("com.jjoe64:graphview:4.2.2") as graph Visualization in Gradle
- Add Bluetooth and Internet permissions:

#### Android `AndroidManifest.xml`
```xml
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.BLUETOOTH"/>
 <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
 <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

#### Android `MainActivity.kt`
```kotlin
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
                val url = URL("https://xxx.com/API/save_sensor.php")
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
```

#### Android `Activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/connectButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Connect"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="20dp"/>

    <TextView
        android:id="@+id/statusText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Finding device ...."
        android:layout_below="@id/connectButton"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="10dp"
        android:textColor="#000000"/>

    <com.jjoe64.graphview.GraphView
        android:layout_margin="10dp"
        android:id="@+id/graph"
        android:layout_width="match_parent"
        android:layout_height="450dp"
        android:layout_below="@id/statusText"
        android:layout_marginTop="20dp"/>

    <TextView
        android:layout_marginTop="20dp"
        android:layout_centerHorizontal="true"
        android:layout_below="@+id/graph"
        android:id="@+id/temperatureText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Temperature: -- °C"
        android:textSize="18sp"
        android:layout_marginBottom="20dp"/>

    <TextView
        android:layout_centerHorizontal="true"
        android:layout_below="@+id/temperatureText"
        android:id="@+id/humidityText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Humidity: -- %"
        android:textSize="18sp"/>

</RelativeLayout>
```

- For detail code, you can use project ESP32BLE Folder. Then Build and run the app on your device.
## UI Android

![Mobile apps](https://raw.githubusercontent.com/pulunghendroprastyo/Special-Section-Integrated-ESP-32-with-Mobile-Apps-via-Bluetooth-and-Remote-Server/refs/heads/master/Images/mobile.png)

### 3. Server Setup
- Set up a REST API endpoint using PHP.
- Database uses dht11_data as table that consists of 4 attributs, such as id_sensor, temperature, humidity, and timestemp  
- Code:

```php
<?php
header('Content-Type: application/json');
// Konfigurasi koneksi database sesuaikan dengan database anda
$host = "localhost";
$user = "****";
$password = "****";
$database = "*****";

// Koneksi ke database
$conn = new mysqli($host, $user, $password, $database);

// Cek koneksi
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Database connection failed: " . $conn->connect_error]));
}

// Mendapatkan data dari request (GET atau POST)
$temperature = isset($_REQUEST['temperature']) ? $_REQUEST['temperature'] : null;
$humidity = isset($_REQUEST['humidity']) ? $_REQUEST['humidity'] : null;

// Validasi input
if ($temperature === null || $humidity === null) {
    echo json_encode(["success" => false, "message" => "Temperature or humidity is missing"]);
    exit;
}

// Query untuk menyimpan data
$stmt = $conn->prepare("INSERT INTO dht11_data (temperature, humidity) VALUES (?, ?)");
$stmt->bind_param("dd", $temperature, $humidity);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Data saved successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Failed to save data: " . $stmt->error]);
}

// Tutup koneksi
$stmt->close();
$conn->close();
?>
```

#### Database
![Database](https://github.com/pulunghendroprastyo/Special-Section-Integrated-ESP-32-with-Mobile-Apps-via-Bluetooth-and-Remote-Server/blob/master/Images/databse.PNG?raw=true)

---

## License
This project is licensed under the MIT License. See the LICENSE file for details.

---

## Contributions
Contributions are welcome! Feel free to fork the repository and submit a pull request.

---

## Contact
For any questions or issues, please reach out to [pulung.hendro@poliupg.ac.id].

This code created by
Pulung Hendro Prastyo, S.S.T., M.Eng. MCE.
Politeknik Negeri Ujung Pandang


