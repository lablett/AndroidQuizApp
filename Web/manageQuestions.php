<!DOCTYPE html>
<html>
<!-- taken from: http://code.google.com/apis/maps/documentation/javascript/examples/map-simple.html -->
  <head>
    <title>Google Maps JavaScript API v3 Example: Map Simple</title>
    <meta name="viewport"
        content="width=device-width, initial-scale=1.0, user-scalable=no">
    <meta charset="UTF-8">
    <style type="text/css">
      html, body, #map_canvas {
        margin: 0;
        padding: 0;
        height: 100%;
      }
    </style>
    <script type="text/javascript"
        src="http://maps.googleapis.com/maps/api/js?sensor=false"></script>

    <script type="text/javascript">
      var map;

      // this is the function to initialise the map when the page loads - it is called by the 'addDomListener' call below
      function initialize() {
			// create Google maps centred on UCL
			var myOptions = {
			  zoom: 16,
			  center: new google.maps.LatLng(51.524213444816844, -0.1341801881790161),
			  mapTypeId: google.maps.MapTypeId.ROADMAP
			};

			// create the new map
			map = new google.maps.Map(document.getElementById('map_canvas'),
				myOptions);
				
			// load geoJSON data from KML		
			map.data.loadGeoJson(
				'http://developer.cege.ucl.ac.uk:30522/teaching/user16/createQuestionsGeoJSON.php'
			)
			
			document.getElementById("questionID").disabled = true;
			
			// create info window in which to display question number and question
			var infoWindow = new google.maps.InfoWindow();
			
			// add listener to get point and question attributes
			map.data.addListener('click', function(event) {
			    var point = event.feature;
				
				
				document.getElementById("questionID").value = point.getProperty("qid");
				document.getElementById("pointname").value = point.getProperty("point_name");
				document.getElementById("question").value = point.getProperty("question");
				document.getElementById("answer1").value = point.getProperty("answer1");
				document.getElementById("answer2").value = point.getProperty("answer2");
				document.getElementById("answer3").value = point.getProperty("answer3");
				document.getElementById("answer4").value = point.getProperty("answer4");

				
				var geom = JSON.stringify(point.getGeometry().get());
				var lat = geom.substring(geom.lastIndexOf("t")+3,geom.lastIndexOf(","));
			    var lng = geom.substring(geom.lastIndexOf("g")+3,geom.lastIndexOf("}"));
				document.getElementById("latitude").value = lat;
				document.getElementById("longitude").value = lng;
				//document.getElementById("longitude").value = geom.get();				
				
				var answerCorrect = point.getProperty("answer_correct");
				//document.getElementById("troubleshoot").value = answerCorrect;
				
				switch (answerCorrect) {
					
					case 1:
						//document.getElementById("troubleshoot").value = answerCorrect;
						document.getElementById("ra1").checked = true;
						break;
					case 2:
						//document.getElementById("troubleshoot").value = answerCorrect;
						document.getElementById("ra2").checked = true;						
						break;
					case 3:
						//document.getElementById("troubleshoot").value = answerCorrect;
						document.getElementById("ra3").checked = true;						
						break;
					case 4:
						//document.getElementById("troubleshoot").value = answerCorrect;
						document.getElementById("ra4").checked = true;
						break;
					default:
						break;
				};
				
				var qID = point.getProperty("qid");
				var question = point.getProperty("question");
				
				// set info window content to question ID and question
				infoWindow.setContent("<strong>Question "+qID + ": </strong>" + question);
				infoWindow.setPosition(point.getGeometry().get());
				infoWindow.setOptions({pixelOffset: new google.maps.Size(0,-30)});
				infoWindow.open(map);
							
			    
			});
			
			// listener to detect click on map and set lat lng in corresponding boxes
			google.maps.event.addListener(map, 'click', function(point) {
				document.getElementById("latitude").value = point.latLng.lat();
				document.getElementById("longitude").value = point.latLng.lng();
				});
			
			
			
      } // end of the initialize function
	
	function resetForm() {
		document.getElementById("questionForm").reset();
	};
	
	  // call the initialize method to create the map
	  // this listener is called when the window is first loaded
      google.maps.event.addDomListener(window, 'load', initialize);
    </script>
  </head>
  <body>
    <div id="map_canvas" style="width:70%;float:left"></div>
    <div id="lat_lng" style="width:30%;float:right">
	<form id="questionForm" action="processQuestion.php" method="post">
		<strong>Question ID:</strong><input type="text" name="questionID" id="questionID" /><br />
		<strong>Point Name:</strong><input type="text" name="pointname" id="pointname" size="match_parent" /><br />
		<strong>Question: </strong><input type="text" name="question" id="question"/><br />
		<strong>Answer 1:</strong> <input type="text" name="answer1" id="answer1" /> <input type="radio" name="answercorrect" value="1" id="ra1"> <br /> 
		<strong>Answer 2:</strong> <input type="text" name="answer2" id="answer2" /> <input type="radio" name="answercorrect" value="2" id="ra2"> <br />
		<strong>Answer 3:</strong> <input type="text" name="answer3" id="answer3" />  <input type="radio" name="answercorrect" value="3" id="ra3"> <br />
		<strong>Answer 4:</strong> <input type="text" name="answer4" id="answer4" /> <input type="radio" name="answercorrect" value="4" id="ra4"><br />
	
		<strong>Latitude:</strong> <input type="text" name="latitude" id="latitude" /><br />
		<strong>Longitude:</strong> <input type="text" name="longitude" id="longitude" /><br />

		<input type="submit" value="Submit" />
		<input type="reset" value="Clear Values" onclick="resetForm()" /><br />
	</form>
    </div><!-- end of the lat-lng div -->
  </body>
</html>
