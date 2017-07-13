<?php

//echo("This is your data<br>");
//print_r($_REQUEST);
//echo("<br><br>");

// connect to the database
	$db = pg_connect('host=localhost user=user16 port=5432 dbname=user16db password=user16password');

	// check if the connection worked, if not, exit
	if (!$db) {
	  echo "An error occurred connecting to the database.\n";
	  exit;
	}

// extract the values from the form using $_REQUEST, since this will work regardless of whether POST or GET is used.
$pointname = $_REQUEST['pointname'];
$question = $_REQUEST['question'];
$answer1 = $_REQUEST['answer1'];
$answer2 = $_REQUEST['answer2'];
$answer3 = $_REQUEST['answer3']; 
$answer4 = $_REQUEST['answer4'];
$answercorrect = $_REQUEST['answercorrect'];
$latitude = $_REQUEST['latitude'];
$longitude = $_REQUEST['longitude'];

// create WKT point for database
$coordinates ="ST_geomfromtext('POINT(".$longitude." ".$latitude.")')";

// build query to insert data into database	
$query = "insert into public.questions (point_name, question, answer1, answer2, answer3, answer4, answer_correct, coordinates) values (";
$query = $query."'".$pointname."','".$question."','".$answer1."','".$answer2."','".$answer3."','".$answer4."','".$answercorrect."',".$coordinates.")";

//echo($query);
//echo("<br/><br/>");


// run the query and inform the user of the result.
if (pg_query($db,$query)) {
	echo("Your data has been saved to the database.  Please click the back button to continue adding questions to the database");
}
else {
	echo("There was an error saving your data.  Please click the back button and retry.  Ensure your point name is unique.");
}

?>