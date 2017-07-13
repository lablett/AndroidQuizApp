package uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz.MapActivity.mapActivity;
//import static uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz.MapActivity.questionList;
import static uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz.MapActivity.updateMarkerColour;


/**
 * <h2> QuestionActivity </h2>
 *
 * Activity class for Location Based Quiz
 * Controls behaviour for activity_question.xml
 *
 * @author Lucille Ablett
 *
 * Adapted from code provided as part of CEGEG077 Web and Mobile GIS
 *
 */
public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv;
    private WebView wv;
    /*
    private TextView tvID;
    private TextView tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton radioAnswer1;
    private RadioButton radioAnswer2;
    private RadioButton radioAnswer3;
    private RadioButton radioAnswer4;
    private Button buttonSubmit;*/

    /**
     * Instance variables:
     * @param answer answer to question as given by user
     * @param answerCorrect correct answer to question
     * @param correct whether or not the user answered correctly
     * @param questionID question ID
     * @param userID user ID
     */
    private String answer;
    private String answerCorrect;
    private boolean correct = false;
    private String questionID;
    private int userID;


    //private MapActivity mapActivity = new MapActivity().getInstance();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question); // set view to activity_question.xml
        tv = (TextView) findViewById(R.id.webResponse);
        // get information from intent and assign to appropriate variables/layout features
        Bundle extras = getIntent().getExtras();
        questionID = (String)extras.get("qid");
        userID = extras.getInt("uid");
        Log.i("userIDqa", Integer.toString(userID));
        // assign question properties to correct layout features
        ((TextView) findViewById(R.id.tvID)).setText("Question: " + questionID);
        ((TextView) findViewById(R.id.tvQuestion)).setText((String)extras.get("question"));
        ((RadioButton)findViewById(R.id.radioAnswer1)).setText((String)extras.get("answer1"));
        ((RadioButton)findViewById(R.id.radioAnswer2)).setText((String)extras.get("answer2"));
        ((RadioButton)findViewById(R.id.radioAnswer3)).setText((String)extras.get("answer3"));
        ((RadioButton)findViewById(R.id.radioAnswer4)).setText((String)extras.get("answer4"));

        answerCorrect = (String)extras.get("answerCorrect");

        ((Button) findViewById(R.id.buttonSubmit)).setOnClickListener(this);

    }

    /**
     * When click is registered in activity, this class is called to determine what action to take
     * @param view current view
     */
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.buttonSubmit:
                    //Log.i("buttontext", (String) ((Button) findViewById(R.id.buttonSubmit)).getText());
                    //if buttonSubmit is clicked, and it's text is set to "Submit Answer", call method submitDataPost()
                    if (((Button) findViewById(R.id.buttonSubmit)).getText().equals("Submit Answer")) {
                        Log.i("submit", "submit1");
                        submitDataPost();
                        //Log.i("submit", "submit");

                    // if buttonSubmit is clicked, and it's text is set to "Continue"
                    } else if (((Button) findViewById(R.id.buttonSubmit)).getText().equals("Continue")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result",Boolean.toString(correct));
                        setResult(Activity.RESULT_OK,returnIntent);  // send correct back to MapActivity

                        finish();  // end activity
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("submitfail", e.toString());
            }
    }

    /**
     * Validate the answer given by user
     */
    public void validateAnswer(){

        // get selected radio button from radioGroup
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int selectedID = radioGroup.getCheckedRadioButtonId();

        // find the radio button by returned id
        RadioButton radioButton = (RadioButton) findViewById(selectedID);
        answer = (String)radioButton.getText();
        Log.i("answer", answer);
        Log.i("answercorrect", answerCorrect);
        Log.i("answercheck", Boolean.toString(answer.equals(answerCorrect)));

        // validate answer against correct answer, and inform user of the result
        if (answer.equals(answerCorrect)){
            correct = true;
            ((TextView) findViewById(R.id.answerResponse)).setText("Congratulations!  That is the correct answer!");
            ((TextView) findViewById(R.id.answerResponse)).setTextColor(Color.parseColor("#33cc99")); // change colour of text

        } else {
            correct = false;
            ((TextView) findViewById(R.id.answerResponse)).setText("Sorry, that is the incorrect answer.  The correct answer is " + answerCorrect + ".");
            ((TextView) findViewById(R.id.answerResponse)).setTextColor(Color.parseColor("#cc0000"));  // change colour of text
        }
    }

    /**
     * submit data to server using asynchronous operation
     * https://developer.android.com/reference/android/telephony/TelephonyManager.html
     */
    private void submitDataPost() {
        //Log.i("submit", "submit3");

        SendHttpRequestTask sfd = new SendHttpRequestTask();
        Log.i("submit", "submit5");
        try {
            //Log.i("submit", "submit4");
            String qid = questionID;
            //Log.i("questionIDa", qid);

            String question = (String) ((TextView) findViewById(R.id.tvQuestion)).getText();
            //Log.i("questionA", question);

            validateAnswer();

            // get the imei number of the phone using TelephonyManager
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

            String imei = (String) telephonyManager.getDeviceId();

            // place values into url string for sending to server
            String urlParameters =
                "uid=" + URLEncoder.encode(Integer.toString(userID), "UTF-8") +
                "&qid=" + URLEncoder.encode(qid, "UTF-8") +
                "&question=" + URLEncoder.encode(question, "UTF-8") +
                "&answer=" + URLEncoder.encode(answer, "UTF-8") +
                "&correct=" + URLEncoder.encode(Boolean.toString(correct).toUpperCase(), "UTF-8") +
                "&imei=" + URLEncoder.encode(imei, "UTF-8");

            updateQuestion();  // update question to reflect that it has been answered
            updateMarkerColour();  // refresh markers to reflect whether or not question was answered correctly
            ((Button) findViewById(R.id.buttonSubmit)).setText("Continue");  // set text on buttonSubmit to "Continue"

            sfd.execute(urlParameters);
        }
        catch             (UnsupportedEncodingException e){}

    }

    /**
     * update the Answered and Correct flags for the question that was just answered
     */
    private void updateQuestion(){
        for (int i = 0; i < mapActivity.questionList.size(); i++) {
            Question q = mapActivity.questionList.get(i);
            if (q.getId() == Integer.parseInt(questionID)){
                Log.i("updatingQuestion", "question " + questionID + " updated");
                q.setIsAnswered(true);
                q.setIsCorrect(correct);
                Log.i("updatingQuestion", Boolean.toString(mapActivity.questionList.get(i).getAnswered()) + Boolean.toString(mapActivity.questionList.get(i).getCorrect()));
                break;
            } else {
                Log.i("updatingQuestion", "No questions updated");
            }
        }
    }


    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        /**
         * retrieve submitAnswer.php from web
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            URL url;
            String urlParams = params[0];
            String targetURL="http://developer.cege.ucl.ac.uk:30522/teaching/user16/submitAnswer.php";

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
            // used for debugging purposes
            //tv.setText(response);
        }
    }
}