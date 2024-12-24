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
