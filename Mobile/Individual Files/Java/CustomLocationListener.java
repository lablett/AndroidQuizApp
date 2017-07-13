package uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * <h2> Custom Location Listener </h2>
 *
 * Defines Custom Location Listener utilised in Location Based Quiz
 *
 * This class monitors the location of the user and calculates the distance between the user and the location of the each quiz question.
 * When the user is within proximity of a question, the QuestionActivity is triggered.
 *
 * @author Lucille Ablett
 *
 * Adapted from code provided as part of CEGEG077 Web and Mobile GIS
 *
 */


public class CustomLocationListener extends MapActivity implements LocationListener {

    /**
     * Instance variables:
     * @param parentActivity instance of the MapActivity class, which is the parent class
     * @param currentLat current latitude of the user
     * @param currentLng current longitude of the user
     */
    public MapActivity parentActivity;
    public double currentLat;
    public double currentLng;

    /**
     * Method called when change in position is detected by network provider
     * Monitors current position, and calculates the distance from this location to each quiz question.
     * If the user is within 20 m of the point, the point will turn purple, notifying the user that the question can be answered if the marker is clicked.
     * If the user is within 10 m of the point, and the question has not been answered, QuestionActivity is automatically triggered for that question.
     *
     * @param location Current location of user
     */
    public void onLocationChanged(Location location) {

        //Toast.makeText(parentActivity.getBaseContext(), "You have moved to: Latitude/longitude:"+location.getLatitude()+ " " + location.getLongitude(), Toast.LENGTH_LONG).show();
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();

        LatLng mapCentre = new LatLng(currentLat, currentLng); // set map centre
        mapActivity.map.moveCamera(CameraUpdateFactory.newLatLng(mapCentre));

        // measure distance between current and question locations
        for (int i = 0; i < parentActivity.questionList.size(); i++) {
            Question q = parentActivity.questionList.get(i);
            Location fixedLoc = new Location("one");

            double lat = q.getLatitude();
            double lng = q.getLongitude();

            fixedLoc.setLatitude(lat);
            fixedLoc.setLongitude(lng);
            Log.i("locationlat", lat + " " + location.getLatitude());
            Log.i("locationlng", lng + " " + location.getLongitude());

            // use Android method distanceTo() to calculate distance
            float distance = location.distanceTo(fixedLoc);
            Log.i("distance", Float.toString(distance));

            // if distance is <20, set the proximity variable for question q to 'true', then refresh the markers in MapActivity.
            if (distance < 20) {
                q.setProximity(true);
                updateMarkerColour();
            }

            // if distance is <10, and the user has not already answered the question, trigger getQuestion() method on question q.
            if (distance < 10 && q.getAnswered()==false && mapActivity.getUser() != null && q.getInProgress()==false){
                q.setInProgress(true);
                mapActivity.initQuestion(q);

            }

            // if distance is >20, set the question proximity to 'false' and refresh markers in MapActivity.
            if (distance > 20) {
                q.setInProgress(false);
                q.setProximity(false);
                updateMarkerColour();
            }
        }
    }


    /**
     * Create toast message when network location disabled
     * @param s
     */
    public void onProviderDisabled(String s) {
        Toast.makeText(parentActivity.getBaseContext(),"Please turn on GPS to answer questions",Toast.LENGTH_LONG).show();
    }

    /**
     * Create toast message when network location enabled
     * @param s
     */
    public void onProviderEnabled(String s) {
        Toast.makeText(parentActivity.getBaseContext(),"GPS enabled.  Approach points to answer questions.",Toast.LENGTH_LONG).show();
    }

    /**
     * Method required by class, but not utilised.
     */
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

}

