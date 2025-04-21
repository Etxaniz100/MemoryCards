<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

// --- CONFIGURACIÓN DE LA CONEXIÓN ---
$servername = "127.0.0.1";
$username = "Xeetxaniz006";
$password = "--";
$dbname = "Xeetxaniz006_miBD";

// --- CREAR CONEXIÓN (Estilo Orientado a Objetos mysqli) ---
$conn = new mysqli($servername, $username, $password, $dbname);
// --- COMPROBAR CONEXIÓN ---
// mysqli_connect_error() devuelve un error si la conexión falló *antes* de que se creara el objeto $conn
// $conn->connect_error devuelve el error *después* de intentar crear el objeto $conn
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

$parametrouser = $_POST["user"];
$parametromazo = $_POST["mazo"];


// Ejecutar la sentencia SQL
//$resultado = mysqli_query($conn, "SELECT * FROM Cartas WHERE Usuario = '$parametrouser' AND Mazo = '$parametromazo'");

$sql = "SELECT * FROM Cartas WHERE Usuario = ? AND Mazo = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $parametrouser, $parametromazo);
$stmt->execute();
$resultado = $stmt->get_result();

// Comprobar si se ha ejecutado correctamente
if (!$resultado) {
    echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    exit();
}

$arrayresultados = [];
while ($fila = mysqli_fetch_assoc($resultado)) 
{
    $arrayresultados[] = $fila;
}

// Devolver el resultado en formato JSON
echo json_encode(["preguntas" => $arrayresultados]);

// --- NO OLVIDES CERRAR LA CONEXIÓN CUANDO TERMINES ---
$conn->close();

?>
