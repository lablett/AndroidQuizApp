<?php

//echo("This is your data<br>");
//print_r($_REQUEST);
//echo("<br><br>");

// connect to the database
	$db = pg_connect('host=localhost user=user16 port=5432 dbname=user16db password=user16password');

	// check if connection worked
	if (!$db) {
	  //echo "An error occurred connecting to the database.\n";
	  echo (-1); // return error code for identification
	  exit;
	}

// extract the values from the form using $_REQUEST, since this will work regardless of whether POST or GET is used.
$username = $_REQUEST['username'];
$email = $_REQUEST['email'];

// build insert query	
$query = "insert into public.users (username, email) values (";
$query = $query."'".$username."','".$email."')";


// run query and if successful, run select query to retrieve user ID and return to mobile app 
if (pg_query($db,$query)) {
	//echo("Query successful");
	$select = "select uid from public.users where username = '".$username."'";
	$result = pg_query($db,$select) or die('Query failed: ' . pg_last_error());
	
	while ($row = pg_fetch_row($result)) {
		echo($row[0]);
	}
	exit;
} else {
	// error code -999 for query unsuccessful
	echo (-999);
	exit;
}


pg_close($db);

?>