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
	$sql = "SELECT * FROM Huevos WHERE Usuario = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("s", $parametrouser);
	$stmt->execute();
	$resultado = $stmt->get_result();

	// Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo json_encode(array('tipo' => 'error', 'mensaje' => mysqli_error($con)));
		exit();
	}

	$fila = mysqli_fetch_row($resultado);

	if (!$fila) 
	{
		echo json_encode(array('tipo' => 'error', 'mensaje' => 'nada'));
		$conn->close();
		exit();
	}


	echo json_encode([
						'tipo' => 'success', 
						'huevo' => [ 
									'Nombre' => $fila[0], 
									'Progreso' => $fila[2],
									'Felicidad' => $fila[3],
									'UltimaVezAbierto' => $fila[4],
									'Color' => $fila[5]
									]
					]);

	$conn->close();
}

if($funcion == "subir")
{
	$parametronombre = $_POST["nombre"];
	$parametroprogreso = $_POST["progreso"];
	$parametrofelicidad = $_POST["felicidad"];
	$parametrocolor = $_POST["color"];
	$parametroabierto = $_POST["ultimaVezAbierto"];


	$sql = "SELECT * FROM Huevos WHERE Usuario = ?";
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

	if (!$fila) 
	{
		// Insertar huevo
		$sql = "INSERT INTO Huevos (Nombre, Usuario, Progreso, Felicidad, UltimaVezAbierto, Color) VALUES (?, ?, ?, ?, ?, ?)";
		$stmt = $conn->prepare($sql);
		$stmt->bind_param("ssddss", $parametronombre, $parametrouser, $parametroprogreso, $parametrofelicidad, $parametroabierto, $parametrocolor);
		$stmt->execute();
		$resultado = $stmt->get_result();

		echo json_encode(array('tipo' => 'success', 'mensaje' => 'subido'));
		$conn->close();
		exit();
	}
	else
	{
		// Actualizar huevo
		$sql = "UPDATE Huevos SET Nombre = ?, Progreso = ?, Felicidad = ?, UltimaVezAbierto = ?, Color = ? WHERE Usuario = ?"; 

		$stmt = $conn->prepare($sql);
		$stmt->bind_param("sddsss", $parametronombre, $parametroprogreso, $parametrofelicidad, $parametroabierto, $parametrocolor, $parametrouser);
		$stmt->execute();
		$resultado = $stmt->get_result();

		echo json_encode(array('tipo' => 'success', 'mensaje' => 'actualizado'));
		$conn->close();
		exit();
	}
}

if($funcion == "borrar")
{
	$sql = "DELETE FROM Huevos WHERE Usuario = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("s", $parametrouser);
	$stmt->execute();
	$resultado = $stmt->get_result();

	echo json_encode(array('tipo' => 'success', 'mensaje' => 'borrado'));
	$conn->close();
	exit();
}

if($funcion == "obtenerposiciones")
{
	$sql = "SELECT * FROM Lugares WHERE Usuario = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("s", $parametrouser);
	$stmt->execute();
	$resultado = $stmt->get_result();

	// Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo json_encode(array('tipo' => 'error', 'mensaje' => mysqli_error($con)));
		exit();
	}

	$arrayresultados = [];

	while ($fila = mysqli_fetch_assoc($resultado)) 
	{
		$arrayresultados[] = $fila;
	}

	// Devolver el resultado en formato JSON
	echo json_encode(["lugares" => $arrayresultados]);

	$conn->close();
}

if($funcion == "comidarecogida")
{	
	$parametrolongitud = $_POST["longitud"];
	$parametrolatitud = $_POST["latitud"];

	$sql = "DELETE FROM Lugares WHERE Usuario = ? AND Longitud = ? AND Latitud = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("sdd", $parametrouser, $parametrolongitud, $parametrolatitud);
	$stmt->execute();
	$resultado = $stmt->get_result();

	echo json_encode(array('tipo' => 'success', 'mensaje' => 'borrado'));
	$conn->close();
	exit();
}

if($funcion == "comidanueva")
{	
	$parametrolongitud = $_POST["longitud"];
	$parametrolatitud = $_POST["latitud"];

	// Insertar huevo
	$sql = "INSERT INTO Lugares (Usuario, Longitud, Latitud) VALUES (?, ?, ?)";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("sdd", $parametrouser, $parametrolongitud, $parametrolatitud);
	$stmt->execute();
	$resultado = $stmt->get_result();

	echo json_encode(array('tipo' => 'success', 'mensaje' => 'subido'));
	$conn->close();
	exit();
}

if($funcion == "obtenercantidad")
{	
	$sql = "SELECT Comida FROM Usuarios WHERE Nombre = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("s", $parametrouser);
	$stmt->execute();
	$resultado = $stmt->get_result();

	// Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo json_encode(array('tipo' => 'error', 'mensaje' => mysqli_error($con)));
		exit();
	}

	$fila = mysqli_fetch_row($resultado);

	if (!$fila) 
	{
		echo json_encode(array('tipo' => 'error', 'mensaje' => 'nada'));
		$conn->close();
		exit();
	}


	echo json_encode([
						'tipo' => 'success', 
						'cantidad' => $fila[0]
					]);

	$conn->close();
}

if($funcion == "subircantidad")
{	
	$parametrocantidad = $_POST["cantidad"];


	// Actualizar huevo
	$sql = "UPDATE Usuarios SET Comida = ? WHERE Nombre = ?"; 

	$stmt = $conn->prepare($sql);
	$stmt->bind_param("is", $parametrocantidad, $parametrouser);
	$stmt->execute();
	$resultado = $stmt->get_result();

	echo json_encode(array('tipo' => 'success', 'mensaje' => 'actualizado'));
	$conn->close();
	exit();
	
}

echo json_encode(array('tipo' => 'error', 'mensaje' => 'funcion no encontrada'));
$conn->close();
exit();

?>
