<?php

echo("This is your data<br>");
print_r($_REQUEST);
echo("<br><br>");

	// connect to the database
	$db = pg_connect('host=localhost user=user16 port=5432 dbname=user16db password=user16password');

	// check database connection
	if (!$db) {
	  echo "An error occurred connecting to the database.\n";
	  exit;
	}

// extract the values from the form
// we use $_REQUEST as this works no matter whether the user has used POST or GET
$uid = $_REQUEST['uid'];
$score = $_REQUEST['score'];

$query = "update public.users set score = ";
$query = $query.$score." where uid = ".$uid;

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