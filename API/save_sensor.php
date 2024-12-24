<?php
header('Content-Type: application/json');
// Konfigurasi koneksi database sesuai dengan database anda
$host = "localhost";
$user = "****";
$password = "****";
$database = "****";

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
