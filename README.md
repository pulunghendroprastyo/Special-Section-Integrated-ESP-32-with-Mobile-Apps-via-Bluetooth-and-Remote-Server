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
- **Cross-Platform**: Works on both Android and iOS platforms.

---

## Architecture

The system consists of three main components:

1. **ESP32 Device**:
   - Collects data from sensors.
   - Sends data to the mobile application via Bluetooth.

2. **Mobile Application**:
   - Receives data from ESP32.
   - Displays the data in real-time.
   - Sends data to a remote server.

3. **Remote Server**:
   - Receives data from the mobile app via HTTP.
   - Stores data in a database or processes it for analytics.

### System Workflow

1. The ESP32 collects sensor data and sends it to the mobile app via Bluetooth.
2. The mobile app visualizes the data in a user-friendly interface.
3. The app forwards the data to a remote server for further processing.

---

## Application Architecture

![Application Architecture](https://github.com/pulunghendroprastyo/Special-Section-Integrated-ESP-32-with-Mobile-Apps-via-Bluetooth-and-Remote-Server/blob/master/Images/Architecture%20of%20System.png?raw=true)

1. **ESP32 Bluetooth Communication**:
   - Data is transmitted over BLE.
   - UUIDs and services are defined for specific sensor data.

2. **Mobile App**:
   - **Bluetooth Module**: Handles BLE connection and data retrieval.
   - **UI Module**: Displays sensor data in real-time.
   - **HTTP Module**: Sends data to the server using RESTful APIs.

3. **Server**:
   - **API Endpoint**: Receives data from the mobile app.
   - **Database**: Stores data for future use.
   - **Dashboard (Optional)**: Provides analytics and visualizations.

---

## Prerequisites

1. **ESP32 Setup**:
   - Install the necessary libraries for BLE communication.
   - Load the firmware that streams sensor data over BLE.

2. **Mobile Application**:
   - Install Android Studio or Xcode (for iOS).
   - Set up the required Bluetooth permissions.

3. **Server**:
   - A RESTful API endpoint.
   - Optional: A database such as MySQL or MongoDB.

---

## Getting Started

### 1. ESP32 Configuration
- Program the ESP32 with Arduino IDE or PlatformIO.
- Use the following libraries:
  - `BLEDevice.h`
  - `BLEUtils.h`
  - `BLEServer.h`

Example Code for ESP32:
```cpp
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

BLECharacteristic *pCharacteristic;

void setup() {
  BLEDevice::init("ESP32-Sensor");
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(BLEUUID("0000180d-0000-1000-8000-00805f9b34fb"));

  pCharacteristic = pService->createCharacteristic(
                    BLEUUID("00002a37-0000-1000-8000-00805f9b34fb"),
                    BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);

  pService->start();
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->start();
}

void loop() {
  int sensorValue = analogRead(34);
  pCharacteristic->setValue(sensorValue);
  pCharacteristic->notify();
  delay(1000);
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

