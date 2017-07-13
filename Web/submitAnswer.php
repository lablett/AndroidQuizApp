<?php

echo("This is your data<br>");
print_r($_REQUEST);
echo("<br><br>");

// connect to the database
	$db = pg_connect('host=localhost user=user16 port=5432 dbname=user16db password=user16password');

	//check database connection
	if (!$db) {
	  echo "An error occurred connecting to the database.\n";
	  exit;
	}

// extract the values from the form
// we use $_REQUEST as this works no matter whether the user has used POST or GET
$qid = $_REQUEST['qid'];
$question = $_REQUEST['question'];
$answer = $_REQUEST['answer'];
$correct= $_REQUEST['correct'];
$imei= $_REQUEST['imei'];
$uid = $_REQUEST['uid'];

// build query to insert information into database
$query = "insert into public.answers (user_id, question_id, question, answer, correct_boolean, imei) values (";
$query = $query.$uid.",".$qid.",'".$question."','".$answer."',".$correct.",'".$imei."')";
//$comment = "You have chosen: ";
//$comment = $answer.";

echo($query);
echo("<br/><br/>");


// run the insert query
// if the result is TRUE it has worked
if (pg_query($db,$query)) {
	echo("Your data has been saved to the database");
}
else {
	echo("There was an error saving your data");
}

?>