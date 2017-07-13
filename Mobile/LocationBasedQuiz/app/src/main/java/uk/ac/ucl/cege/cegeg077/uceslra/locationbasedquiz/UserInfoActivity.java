package uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * <h2> UserInfoActivity </h2>
 *
 * Activity class for Location Based Quiz
 * Controls behaviour for activity_user_info.xml
 *
 * @author Lucille Ablett
 *
 * Adapted from code provided as part of CEGEG077 Web and Mobile GIS
 *
 */


public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * @param username username of user
     * @param email email of user
     * @param tv TextView for error messages
     */
    private String username;
    private String email;
    private TextView tv;

    //private MapActivity mapActivity = null;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info); // set view to activity_user_info.xml
        tv = (TextView) findViewById(R.id.webResponse);
        Log.i("userinfo", "loaded");
        ((Button) findViewById(R.id.buttonBegin)).setOnClickListener(this);

        //mapActivity = new MapActivity().getInstance();
    }

    /**
     * When click is registered in activity, this class is called to determine what action to take
     * @param view current view
     */
    public void onClick(View view) {
        Log.i("click", "begin clicked");
        try {
            switch (view.getId())
            {
                case R.id.buttonBegin: // if buttonBegin is clicked, validate the input
                    if (validateInput() == true){
                        Log.i("usersubmit", "submit");
                        submitDataPost(); // sumbit data to database
                    };
                    break;
                default:
                    Log.i("view",Integer.toString(view.getId()));
                    break;
            }
        } catch (Exception e){
            Log.i("click", "Error");
        }
    }

    /**
     * validate username and email inputs
     * @return
     */
    private boolean validateInput() {
        boolean valid = true;  // assume information is valid
        // retrieve information from form
        username = ((EditText) findViewById(R.id.username)).getText().toString();
        Log.i("username", username);

        email = (String) ((EditText) findViewById(R.id.email)).getText().toString();
        Log.i("email", email);

        // check validity
        if (username.length() < 6) {
            ((TextView) findViewById(R.id.tvUsername)).setText("Username must be between 6 and 30 character");
            ((TextView) findViewById(R.id.tvUsername)).setTextColor(Color.parseColor("#cc0000"));
            valid = false;  // not valid
        }

        if (!(email.contains("@"))){
            ((TextView) findViewById(R.id.tvEmail)).setText("Invalid email address");
            ((TextView) findViewById(R.id.tvEmail)).setTextColor(Color.parseColor("#cc0000"));
            valid = false;  // not valid
        }

        return valid;
    }

    /**
     *  Asynchronous operation that takes values and sends them to the server
     */
    private void submitDataPost() {

        SendHttpRequestTask sfd = new SendHttpRequestTask();
        try {

            String urlParameters =
                "username=" + URLEncoder.encode(username, "UTF-8") +
                "&email=" + URLEncoder.encode(email, "UTF-8");


            sfd.execute(urlParameters);
        }
        catch             (UnsupportedEncodingException e){}

    }

    /**
     * Class SendHttpRequestTask
     * Communicates with web server to connect with database and upload user information
     */
    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String urlParams = params[0];
            String targetURL="http://developer.cege.ucl.ac.uk:30522/teaching/user16/processUserInfo.php";

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

        /**
         * After server communication complete, validate response code.
         * @param response
         */
        @Override
        protected void onPostExecute(String response) {
            try {
                Log.i("response", response);
                response = response.trim();

                if (response.contains("-")) {
                    int r = Integer.parseInt(response);
                    if (r == -1) {
                        tv.setText("There was an error connecting to the server, please try again later"); // database communication error
                    } else if (r == -999) {
                        tv.setText("The username or email has already been registered, please try again");  //  username or password exists
                    }

                } else {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",Integer.parseInt(response));
                    setResult(Activity.RESULT_OK,returnIntent);  // send userID back to MapActivity

                    //MapActivity.showScore();
                    finish();
                }
            }
            catch (Exception e) {
                Log.e("userinfo_fail", e.toString());
                tv.setText("Please ensure you are connected to the internet and try again");
            }

        }
    }
}