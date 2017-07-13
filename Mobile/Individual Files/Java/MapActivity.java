package uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz;

/*=============================
Note that code in this file has been adapted from: https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/com/google/maps/android/utils/demo/GeoJsonDemoActivity.java
Note also that putting KML on an Android Google Map functionality is in Beta so there may be some issues
*/

/**
 * <h2> MapActivity </h2>
 *
 * Main activity class for Location Based Quiz
 * Controls behaviour for activity_map.xml
 *
 * @author Lucille Ablett
 *
 * Adapted from code provided as part of CEGEG077 Web and Mobile GIS
 * Also adapted from: https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/com/google/maps/android/utils/demo/GeoJsonDemoActivity.java
 *
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.google.maps.android.geojson.GeoJsonPointStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {

    /**
     * Interface instance variables:
     * @param mLayer map layer containing GeoJson information
     * @param map Google Map
     * @param mLogTag logcat message tag for debugging purposes
     * @param tvScore TextView in which user's score is displayed
     */
    private static GeoJsonLayer mLayer;
    protected GoogleMap map;
    private final static String mLogTag = "GeoJsonDemo";
    //private final Context context = this;
    public static MapActivity mapActivity = null;
    private static TextView tvScore;

    /**
     * Proximity tracking variables
     * @param MINIMUM_DISTANCECHANGE_FOR_UPDATE minimum distance from previous location before LocationListener triggered
     * @param MINIMUM_TIME_BETWEEN_UPDATE minimum rate at which location change updates must be registered
     * @param locationManager location manager instance
     * @param questionList ArrayList to which quiz questions will be added
     * @param currentLat user's current latitude
     * @param currentLng user's current longitude
     */
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private LocationManager locationManager;
    public ArrayList<Question> questionList = new ArrayList<Question>();
    public double currentLat;
    public double currentLng;

    /**
    * @param user User object for current user
    */
    public User user = null;

    /**
     * @param user GET_QUESTION_REQUEST request code for intent that triggers QuestionActivity
     * @param user GET_USERINFO_REQUEST request code for intent that triggers UserInfoActivity
     */
    public static final int GET_QUESTION_REQUEST = 1;  // The request code
    public static final int GET_USERINFO_REQUEST = 2;  // The request code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.instance = this;
        setContentView(R.layout.activity_map);  // set the view to activity_map.xml
        tvScore = (TextView) findViewById(R.id.tvScore);
        this.mapActivity = this;
        getUserInfo();
        initLocationListener();

        // assign the map fragment to a variable so that we can manipulate it
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

        // Call onMapReady once the map is drawn
        mapFragment.getMapAsync(this);

    }

    /**
    * Create new map using user's current location, then retrieve GeoJSON points from server.
    * @param newMap
    *
    */
    @Override
    public void onMapReady(final GoogleMap newMap) {
        //LatLngBounds UCL = new LatLngBounds(new LatLng(51.5223, -0.14), new LatLng(51.528744085048615, -0.1254415512084961));
        LatLng mapCentre = new LatLng(currentLat, currentLng); // set map centre
        map = newMap;
        map.setMyLocationEnabled(true); // display user's location on map
        //map.moveCamera(CameraUpdateFactory.newLatLngBounds(UCL, 4, 4, 0));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCentre,4)); // move map camera to user's current position.  If GPS is off this will be (0,0).
        retrieveFileFromUrl();
        map.setOnMarkerClickListener((OnMarkerClickListener) this);

    }

    /**
     * Trigger UserInfoActivity class
     */
    public void getUserInfo() {
        // Using intents, begin class UserInfoActivity and request result.
        Intent intent = new Intent();
        intent.setClassName("uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz", "uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz.UserInfoActivity");
        this.startActivityForResult(intent, GET_USERINFO_REQUEST); // result request code is passed to new activity
    }

    /**
     * Update tvScore to show user's current score
     */
    public void showScore(){
        tvScore.setText(Integer.toString(user.getScore()));
    }

    public User getUser(){
        return user;
    }


    /**
     * Check the number of questions a user has answered.
     * When user has answered all questions, trigger alert dialog to inform them how many questions they answered correctly.
     * Submit score to database, and exit application.
     *
     * Adapted from:
     * // and http://stackoverflow.com/questions/13377300/how-to-show-dialog-from-a-static-method
     */
    public void checkQuestionCount(){
        Log.i("questionCount", Integer.toString(user.getQuestionCount()) + " " + questionList.size());
        if (user.getQuestionCount() == questionList.size()){ // questionList.size() = total number of questions
            Log.i("quizComplete", "Quiz Complete");
            AlertDialog.Builder quizComplete = new AlertDialog.Builder(this);
            quizComplete.setTitle("Quiz Complete");
            quizComplete.setMessage("Congratulations, you have answered " + user.getScore() + "/" + questionList.size() + " questions correctly!");
            quizComplete.setNegativeButton(R.string.button_exit, (
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface quizComplete, int id) {
                            submitScorePost();  // submit score to database
                            finish(); // exit application
                        };
                    }
            ));
            quizComplete.show();  // show dialog
        }
    }

    /**
     * initialise the custom location listener to track user's position
     */
    private void initLocationListener(){
        Log.i("listener", "listener enabled");
        // set up location listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        CustomLocationListener customLL = new CustomLocationListener();
        customLL.parentActivity = this;
        currentLat = customLL.currentLat;
        currentLng = customLL.currentLng;

        Log.i("currentlat", Double.toString(currentLat));
        Log.i("currentlng", Double.toString(currentLng));

        // set up the location manager and listener
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                customLL
        );
    }

    /**
     * retrieve GeoJSON file from server
     */
    private void retrieveFileFromUrl() {
        String mGeoJsonUrl
                //= "http://developer.cege.ucl.ac.uk:30522/teaching/user16/createGeoJSON.php";
                = "http://developer.cege.ucl.ac.uk:30522/teaching/user16/createQuestionsGeoJSON.php";
        DownloadGeoJsonFile downloadGeoJsonFile = new DownloadGeoJsonFile();
        downloadGeoJsonFile.execute(mGeoJsonUrl);
    }

    /**
     * Defines actions when marker is clicked:
     *      If user is in proximity to question but question has not been answered, trigger getQuestion()
     *      If user has answered question, inform them of this using a dialog
     *      If neither is true, tell user to approach the point to answer the question
     * @param marker marker clicked
     * @return
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        Log.i("marker", marker.getId());

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Question: " + marker.getTitle());
        dialog.setNegativeButton(R.string.button_close, null);

        int qID = Integer.parseInt(marker.getTitle());

        for (Question q : questionList) {
            if (q.getId() == qID) {
                if (q.getProximity() == true && q.getAnswered() == false) {
                    Log.i("getQuestion", Integer.toString(qID) + " " + Boolean.toString(q.getAnswered()));
                    q.setInProgress(true);
                    initQuestion(q);
                    break;
                } if (q.getAnswered() == true) {
                    Log.i("getQuestion", Integer.toString(qID) + " " + Boolean.toString(q.getAnswered()));
                    dialog.setMessage("Sorry, you have already answered this question");
                    dialog.show();
                } else {
                    dialog.setMessage("Approach point to answer question");
                    dialog.show();
                }

            }
        }

        return true;
    }

    /**
     * Extract question information from GeoJSON feature and add to ArrayList questionList
     * @param feature GeoJSON feature
     * @return q question object
     */
    private Question addQuestion(GeoJsonFeature feature) {
        // extract properties from feature and assign to correct local variables
        int id = Integer.parseInt(feature.getProperty("qid"));
        String pointName = feature.getProperty("point_name");
        String question = feature.getProperty("question");
        String answer1 = feature.getProperty("answer1");
        String answer2 = feature.getProperty("answer2");
        String answer3 = feature.getProperty("answer3");
        String answer4 = feature.getProperty("answer4");
        int answerCorrect = Integer.parseInt(feature.getProperty("answer_correct"));
        Log.i("addQuestionCorrect", feature.getProperty("answer_correct"));
        double lat = ((GeoJsonPoint) feature.getGeometry()).getCoordinates().latitude;
        double lng = ((GeoJsonPoint) feature.getGeometry()).getCoordinates().longitude;

        // create new question object from feature properties
        Question q = new Question(id, pointName, question, answer1, answer2, answer3, answer4, answerCorrect, lat, lng);
        questionList.add(q); // add question to ArrayList
        return q;
    }


    /**
     * Get question and associated variables and pass them to QuestionActivity using intents
     * @param q question object
     *
     * Adapted from https://developer.android.com/training/basics/intents/result.html#ReceiveResult
     */
    protected void initQuestion(Question q) {
        Intent intent = new Intent();
        intent.setClassName("uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz", "uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz.QuestionActivity");
        Log.i("userIDma", Integer.toString(user.getID()));
        intent.putExtra("qid", Integer.toString(q.getId()));
        intent.putExtra("uid", user.getID());
        intent.putExtra("question", q.getQuestion());
        intent.putExtra("answer1", q.getAnswer1());
        intent.putExtra("answer2", q.getAnswer2());
        intent.putExtra("answer3", q.getAnswer3());
        intent.putExtra("answer4", q.getAnswer4());
        intent.putExtra("answerCorrect", q.getAnswerCorrect());

        Log.i("getQuestionID", Integer.toString(q.getId()));
        Log.i("getQuestionAnswer", q.getAnswerCorrect());

        this.startActivityForResult(intent, GET_QUESTION_REQUEST);  // start activity, requesting result using result code
    }

    /**
     * Triggered by method setResult() method in activity started using startActivityForResult()
     * @param requestCode request identifier
     * @param resultCode result identifier
     * @param data data received
     *
     * Adapted from https://developer.android.com/training/basics/intents/result.html#ReceiveResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request the method is responding to
        // QuestionActivity response
        if (requestCode == GET_QUESTION_REQUEST) {
            // Make sure request was successful
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                Log.i("activityResultAnswer", result);

                //  if user answered question correctly, increase user score by 1
                if (Boolean.parseBoolean(result) == true){
                    user.setScore(1);
                    Log.i("userScore", Integer.toString(user.getScore()));
                    showScore();
                }

                if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("activityResult", "result failed");
                }

                user.setQuestionCount(1); // increase user question count by 1
                checkQuestionCount(); // check to see whether or not user has answered all questions
            }
        }

        // UserInfoActivity response
        if (requestCode == GET_USERINFO_REQUEST) {
            int userID = data.getIntExtra("result", -10); // get userID generated by database
            Log.i("activityResultUser", Integer.toString(userID));
            user = new User(userID); // create new user using userID
        }
    }

    /**
     * initialise the question markers from GeoJSON features
     */
    private void initialiseMarkers() {
        // Iterate over all the features stored in the layer
        for (GeoJsonFeature feature : mLayer.getFeatures()) {
            Log.i("markerInit", "initialised");
            // Check if qid (questionID) property exists
            if (feature.hasProperty("qid")) {
                Question q = addQuestion(feature); // call method addQuestion on feature

                // get questionID and create marker
                Integer id = Integer.parseInt(feature.getProperty("qid"));
                BitmapDescriptor pointIcon;
                pointIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

                // Create a new point style
                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();

                // Set options for the point style
                pointStyle.setIcon(pointIcon);
                pointStyle.setTitle(Integer.toString(id));

                // Assign the point style to the feature
                feature.setPointStyle(pointStyle);

                // refresh marker colours to reflect proximity flags
                updateMarkerColour();
            }
        }
    }

    /**
     * Update the marker colors based on the following:
     *  Azure:  question not answered, user not in proximity
     *  Purple:  question not answered, user in proximity
     *  Red:  question answered, user answered incorrectly
     *  Azure:  question answered, user answered correctly
     */
    public static void updateMarkerColour() {
        // iterate through GeoJSON features on map
        for (GeoJsonFeature feature : mLayer.getFeatures()) {
            BitmapDescriptor pointIcon;
            //Log.i("markerColours", feature.toString());
            if (feature.hasProperty("qid")) {
                Log.i("markerColours", feature.getProperty("qid"));
                // find question associated with marker and check flags
                for (Question q : mapActivity.questionList) {
                    if (Integer.parseInt(feature.getProperty("qid")) == q.getId()) {
                        Log.i("markerColours", "found question");

                        if (q.getAnswered() == true && (q.getCorrect() == true)) {
                            // Get the icon for the feature
                            pointIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        } else if (q.getAnswered() == true && (q.getCorrect() == false)) {
                            pointIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        } else if (q.getAnswered() == false && (q.getProximity() == true)) {
                            pointIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                        } else {
                            pointIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                        }
                        // Get marker point style
                        GeoJsonPointStyle pointStyle = feature.getPointStyle();

                        // Update the point style icon
                        pointStyle.setIcon(pointIcon);

                        // Assign the point style to the feature
                        feature.setPointStyle(pointStyle);
                    }

                }
            }

        }
    }

    /**
     * Submit the user's score to the database
     */
    private void submitScorePost() {
        // create an asynchronous operation that will take these values
        // and send them to the server

        MapActivity.SendHttpRequestTask sfd = new MapActivity.SendHttpRequestTask();
        try {

            String urlParameters =
                    "uid=" + URLEncoder.encode(Integer.toString(user.getID()), "UTF-8") +
                            "&score=" + URLEncoder.encode(Integer.toString(user.getScore()), "UTF-8");

            sfd.execute(urlParameters);
        }
        catch             (UnsupportedEncodingException e){}

    }

    /**
     * Download GeoJSON file
     */
    private class DownloadGeoJsonFile extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... params) {
            try {
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));


                while ((line = reader.readLine()) != null) {
                    Log.i("writeLine", "line written");
                    // Read and save each line of the stream
                    result.append(line);
                    Log.i("result", result.toString());
                }

                // Close the stream
                reader.close();
                stream.close();

                // Convert result to JSONObject
                Log.i("JSONresult", result.toString());
                return new JSONObject(result.toString());
            } catch (IOException e) {
                Log.e(mLogTag, "GeoJSON file could not be read");
            } catch (JSONException e) {
                Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
            }
            return null;
        }

        /**
         * Once download executed, create new GeoJsonLayer
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                // Create a new GeoJsonLayer, pass in downloaded GeoJSON file as JSONObject
                mLayer = new GeoJsonLayer(map, jsonObject);
                // Add the layer onto the map
                initialiseMarkers();
                mLayer.addLayerToMap();
            }
        }


    }

    /**
     * Class SendHttpRequestTask
     * Communicates with web server to connect with database and upload score
     */
    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String urlParams = params[0];
            String targetURL="http://developer.cege.ucl.ac.uk:30522/teaching/user16/submitScore.php";

            HttpURLConnection connection = null;
            try {
                //Create connection
                url = new URL(targetURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParams.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParams);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                connection.disconnect();
                return response.toString();

            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String response) {


        }
    }

}
