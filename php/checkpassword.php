<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

// --- CONFIGURACIÓN DE LA CONEXIÓN ---
$servername = "127.0.0.1";
$username = "Xeetxaniz006";
$password = "--";
$dbname = "Xeetxaniz006_miBD";

$conn = new mysqli($servername, $username, $password, $dbname); // Nota: 'mysqli' en minúsculas

if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

$parametrouser = $_POST["user"];
$parametrocontrasena = $_POST["clave"];

// Ejecutar la sentencia SQL
$resultado = mysqli_query($conn, "SELECT * FROM Usuarios WHERE Nombre = '$parametrouser'");

// Comprobar si se ha ejecutado correctamente
if (!$resultado) {
    echo json_encode(array('tipo' => 'error', 'mensaje' => 'Error mysqli: ' . mysqli_error($conn)));
    exit();
}

$fila = mysqli_fetch_row($resultado);

if (!$fila) 
{
  // Usuario no encontrado
  echo json_encode(array('tipo' => 'error', 'mensaje' => 'nadie'));
  $conn->close();
  exit();
}

$contrasena_almacenada = $fila[1];  // Suponiendo que 'Clave' es la columna donde está la contraseña

// Verificar la contraseña (si está cifrada, usar password_verify)
if ($contrasena_almacenada == $parametrocontrasena)//password_verify($parametrocontrasena, $contrasena_almacenada)) 
{
    // Contraseña correcta
    echo json_encode(array('tipo' => 'success', 'mensaje' => 'correcto'));
} 
else 
{
    // Contraseña incorrecta
    echo json_encode(array('tipo' => 'error', 'mensaje' => 'incorrecto'));
}

$conn->close();


?>
