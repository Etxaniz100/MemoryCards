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

$data = json_decode(file_get_contents("php://input"), true);

$funcion = $data["funcion"];
$pregunta = $data["pregunta"];
$respuesta = $data["respuesta"];
$mazo = $data["mazo"];
$id = $data["id"];
$usuario = $data["usuario"];
$estado = $data["estado"];
$proximo = $data["proximoEstudio"];
$dias = $data["diasEntreEstudio"];
$unavez = $data["unaVezCorrecto"];

if ($proximo === '') {
    $proximo = null;
}


if($funcion == "actualizar")
{
		$sql = "UPDATE Cartas 
        SET Pregunta = ?, Respuesta = ?, Estado = ?, ProximoEstudio = ?, DiasEntreEstudio = ?, UnaVezCorrecto = ?
        WHERE ID = ? AND Mazo = ? AND Usuario = ?";

		$stmt = $conn->prepare($sql);
		$stmt->bind_param("ssisiiiss", 
			$pregunta, 
			$respuesta, 
			$estado, 
			$proximo, 
			$dias, 
			$unavez, 
			$id,
			$mazo, 
			$usuario
		);
		$stmt->execute();

		echo json_encode(array('tipo' => 'success', 'mensaje' => 'actualizado'));
		$conn->close();
		exit();

}

if($funcion == "subir")
{
	$sql = "SELECT * FROM Cartas WHERE Usuario = ? AND Mazo = ? AND ID = ?";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("ssi", $usuario, $mazo, $id);
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
		$sql = "INSERT INTO Cartas 
        (ID, Pregunta, Mazo, Usuario, Respuesta, Estado, ProximoEstudio, DiasEntreEstudio, UnaVezCorrecto) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


		$stmt = $conn->prepare($sql);
		$stmt->bind_param("issssisii", 
			$id,
			$pregunta, 
			$mazo, 
			$usuario, 
			$respuesta, 
			$estado, 
			$proximo, 
			$dias, 
			$unavez
		);
		$stmt->execute();

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
	
}

echo json_encode(array('tipo' => 'error', 'mensaje' => 'funcion no encontrada'));
$conn->close();
exit();
?>
