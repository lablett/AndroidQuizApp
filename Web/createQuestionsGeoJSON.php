<?php
/* Note:  This code is adapted from source code found here: http://stackoverflow.com/questions/17775627/creating-a-geojson-in-php-from-mysql-to-use-with-mapbox-javascript-api
   Accessed: 14th February 2016
 */

// connect to database
	$db = pg_connect('host=localhost user=user16 port=5432 dbname=user16db password=user16password');

	// exit if database not responding
	if (!$db) {
	  echo "An error occurred connecting to the database.\n";
	  exit;
	}


	// retreive data from database table
	$query = "select qid, point_name, question, answer1, answer2, answer3, answer4, answer_correct, ST_astext(coordinates) as coordinates from public.questions";
	$result = pg_query($db,$query);

	// check if it worked, and if not exit
	if (!$result) {
	  echo "An error occurred running the query.\n";
	  exit;
	}

	// Build GeoJSON feature collection array
	$geojson = array(
	   'type'      => 'FeatureCollection',
	   'features'  => array()
	);

	$id=0;
	// Loop through rows to build feature arrays
	while ($row = pg_fetch_array($result))  {

		// get attributes
		$properties = $row;

		// remove coordinates for now
		unset($properties['coordinates']);

		// handle coords to extract lat and lng
		$coordinates = $row['coordinates'];

		// remove 'POINT'
		$coordinates = str_replace('POINT(','',$coordinates);
		// remove )
		$coordinates = str_replace(')','',$coordinates);
		// now extract the $lat and $lng values from the remaining string using explode
		$coordvalues = explode(' ',$coordinates);

		$lat = $coordvalues[0];
		$lng = $coordvalues[1];
		$id = $id + 1;
		// build feature array
		$feature = array(
			'type' => 'Feature',
			'properties' => $properties,
			'geometry' => array(
				'type' => 'Point',
				'coordinates' => array(
					$lat,
					$lng
				)
			),
		'id'=>$id
		);
		// Add feature arrays to feature collection array
		array_push($geojson['features'], $feature);
	}


	// echo data out
	header('Content-type: application/json');
	echo json_encode($geojson, JSON_NUMERIC_CHECK);

	// disconnect from the database - this is good practice
	$db = NULL;
?>