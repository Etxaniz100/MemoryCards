<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

// --- CONFIGURACIÓN DE LA CONEXIÓN ---
$servername = "127.0.0.1";
$username = "Xeetxaniz006";
$password = "--";
$dbname = "Xeetxaniz006_miBD";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

$parametrouser = $_POST["user"];
$parametrocontrasena = $_POST["clave"];

if (empty($parametrouser) || empty($parametrocontrasena)) 
{
  echo json_encode(array('tipo' => 'error', 'mensaje' => 'vacio'));
  exit();  // Detener el script si alguna de las dos está vacía
}

// Ejecutar la sentencia SQL
$resultado = mysqli_query($conn, "SELECT * FROM Usuarios WHERE Nombre = '$parametrouser'");

// Comprobar si se ha ejecutado correctamente
if (!$resultado) 
{
    echo json_encode(array('tipo' => 'error', 'mensaje' => 'mysqli'));
    exit();
}

$fila = mysqli_fetch_row($resultado);

if (!$fila) 
{
  $consulta = "INSERT INTO Usuarios (Nombre, Clave, Comida) VALUES ('$parametrouser', '$parametrocontrasena', 10)";

  // Ejecutar la consulta
  if (mysqli_query($conn, $consulta)) 
  {
      echo json_encode(array('tipo' => 'success', 'mensaje' => 'registrado'));
  } else 
  {
      echo json_encode(array('tipo' => 'error', 'mensaje' => 'mysqli'));
  }

  $conn->close();
  exit();
}

echo json_encode(array('tipo' => 'error', 'mensaje' => 'existe'));

$conn->close();
?>
