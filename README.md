Created by: Pulung Hendro Prastyo, M.eng.

Date: 24 December 2024

Politeknik Negeri Ujung Pandang


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
   - **Bluetooth Module**: Handles Bluetoot connection and data retrieval.
   - **UI Module**: Displays sensor data in real-time.
   - **HTTPs Module**: Sends data to the server using RESTful APIs.

3. **Server**:
   - **API Endpoint**: Receives data from the mobile app.
   - **Database**: Stores data for future use.
   - **Dashboard **: Provides analytics and visualizations.

---

## Getting Started


## IoT Schematic

![Application Architecture](https://raw.githubusercontent.com/pulunghendroprastyo/Special-Section-Integrated-ESP-32-with-Mobile-Apps-via-Bluetooth-and-Remote-Server/refs/heads/master/Images/ESP%2032%20Schematic.PNG)

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
- Clone this repository.
- Open the project in Android Studio or Xcode.
- Add Bluetooth permissions:

#### Android `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

#### iOS `Info.plist`
```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app requires Bluetooth access to communicate with ESP32 devices.</string>
```

- Build and run the app on your device.

### 3. Server Setup
- Set up a REST API endpoint using Flask, Node.js, or any other framework.
- Example using Flask:
```python
from flask import Flask, request

app = Flask(__name__)

@app.route('/data', methods=['POST'])
def receive_data():
    data = request.json
    print("Received data:", data)
    return {"status": "success"}, 200

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
```

---

## Screenshots

### 1. Data Visualization on Mobile
![Mobile App Screenshot](https://via.placeholder.com/400x800.png?text=Mobile+App+Screenshot)

### 2. Server Logs
![Server Logs](https://via.placeholder.com/800x400.png?text=Server+Logs)

---

## License
This project is licensed under the MIT License. See the LICENSE file for details.

---

## Contributions
Contributions are welcome! Feel free to fork the repository and submit a pull request.

---

## Contact
For any questions or issues, please reach out to [your_email@example.com].

