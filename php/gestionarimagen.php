<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

// --- CONFIGURACIÓN DE LA CONEXIÓN ---
$servername = "127.0.0.1";
$username = "Xeetxaniz006";
$password = "--";
$dbname = "Xeetxaniz006_miBD";

// --- CREAR CONEXIÓN (Estilo Orientado a Objetos mysqli) ---
$conn = new mysqli($servername, $username, $password, $dbname); // Nota: 'mysqli' en minúsculas

// --- COMPROBAR CONEXIÓN ---
// mysqli_connect_error() devuelve un error si la conexión falló *antes* de que se creara el objeto $conn
// $conn->connect_error devuelve el error *después* de intentar crear el objeto $conn
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");


$parametrouser = $_POST["user"];
$funcion = $_POST["funcion"];


if($funcion == "obtener")
{
	
	$sql = "SELECT ImagenBase64 FROM Usuarios WHERE Nombre = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $parametrouser);
    $stmt->execute();
    $resultado = $stmt->get_result();
    $fila = mysqli_fetch_row($resultado);

    if (!$fila) 
	{
        echo json_encode(array('tipo' => 'error', 'mensaje' => 'Imagen no encontrada'));
    } 
	else 
	{
        echo json_encode(array('tipo' => 'success', 'imagen' => $fila[0]));
    }

    $conn->close();
    exit();
}

if($funcion == "subir")
{
	$imagen = $_POST["imagen"];


	$sql = "SELECT * FROM Usuarios WHERE Nombre = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("s", $parametrouser);
	$stmt->execute();
	$resultado = $stmt->get_result();

	// Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo json_encode(array('tipo' => 'error', 'mensaje' => mysqli_error($conn)));
		exit();
	}

	$fila = mysqli_fetch_row($resultado);

	if (!$fila) {
        echo json_encode(array('tipo' => 'error', 'mensaje' => 'Usuario no encontrado'));
        $conn->close();
        exit();
    }

	
	// Actualizar huevo
	$sql = "UPDATE Usuarios SET ImagenBase64 = ? WHERE Nombre = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $imagen, $parametrouser);
    if ($stmt->execute()) 
	{
        echo json_encode(array('tipo' => 'success', 'mensaje' => 'Imagen actualizada'));
    } 
	else 
	{
        echo json_encode(array('tipo' => 'error', 'mensaje' => 'Error al actualizar la imagen'));
    }

	$conn->close();
	exit();
	
}

echo json_encode(array('tipo' => 'error', 'mensaje' => 'funcion no encontrada'));
$conn->close();
exit();

?>
