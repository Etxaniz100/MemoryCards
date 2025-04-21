<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

// --- CONFIGURACIÓN DE LA CONEXIÓN ---
$servername = "127.0.0.1";
$username = "Xeetxaniz006";
$password = "--";
$dbname = "Xeetxaniz006_miBD";


$conn = new mysqli($servername, $username, $password, $dbname); 

if ($conn->connect_error) 
{
	die("Connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

$parametrouser = $_POST["user"];
$funcion = $_POST["funcion"];


if($funcion == "obtener")
{

	$resultado = mysqli_query($conn, "SELECT * FROM Mazos WHERE Usuario = '$parametrouser'");

	if (!$resultado) 
	{
		echo json_encode(array('tipo' => 'error', 'mensaje' => mysqli_error($conn)));
		exit();
	}

	$arrayresultados = [];
	while ($fila = mysqli_fetch_assoc($resultado)) 
	{
		array_push($arrayresultados, $fila['Nombre']);
	}

	echo json_encode(['tipo' => 'success', "mazos" => $arrayresultados]);

	$conn->close();
}

if($funcion == "subir")
{
	$parametromazo = $_POST["mazo"];

	$sql = "SELECT * FROM Mazos WHERE Usuario = ? AND Nombre = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("ss", $parametrouser, $parametromazo);
	$stmt->execute();
	$resultado = $stmt->get_result();

	// Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo json_encode(array('tipo' => 'error', 'mensaje' => mysqli_error($conn)));
		exit();
	}

	$fila = mysqli_fetch_row($resultado);

	if (!$fila) 
	{
		$sql = "INSERT INTO Mazos (Nombre, Usuario) VALUES (?, ?)";
		$stmt = $conn->prepare($sql);
		$stmt->bind_param("ss", $parametromazo, $parametrouser);
		$stmt->execute();
		$resultado = $stmt->get_result();

		echo json_encode(array('tipo' => 'success', 'mensaje' => 'subido'));
		$conn->close();
		exit();
	}
	else
	{
		echo json_encode(array('tipo' => 'error', 'mensaje' => 'existe'));
		$conn->close();
		exit();
	}
}

if($funcion == "borrar")
{
	$parametromazo = $_POST["mazo"];

	$sql = "DELETE FROM Cartas WHERE Usuario = ? AND Mazo = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("ss", $parametrouser, $parametromazo);
	$stmt->execute();
	$resultado = $stmt->get_result();

	$sql = "DELETE FROM Mazos WHERE Usuario = ? AND Nombre = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("ss", $parametrouser, $parametromazo);
	$stmt->execute();
	$resultado = $stmt->get_result();

	echo json_encode(array('tipo' => 'success', 'mensaje' => 'borrado'));
	$conn->close();
	exit();
}


echo json_encode(array('tipo' => 'error', 'mensaje' => 'funcion no encontrada'));
$conn->close();
exit();
?>
