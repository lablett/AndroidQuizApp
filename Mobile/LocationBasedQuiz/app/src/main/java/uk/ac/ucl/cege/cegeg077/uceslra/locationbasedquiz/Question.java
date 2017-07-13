package uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz;

import android.util.Log;

/**
 * <h2> Question </h2>
 *
 * Defines Question object utilised in Location Based Quiz
 *
 * @author Lucille Ablett
 *
 *
 */


public class Question {

    /**
     * Instance variables:
     * @param id id number of quiz question
     * @param pointName alphanumeric question identifier
     * @param question quiz question
     * @param answer1 first potential answer to quiz question
     * @param answer2 second potential answer to quiz question
     * @param answer3 third potential answer to quiz question
     * @param answer4 fourth potential answer to quiz question
     * @param answerCorrect correct answer to quiz question
     * @param latitude latitude of quiz question
     * @param longitude longitude of quiz question
     * @param isInProximity whether or not the user is within 20 m of the quiz question
     * @param IsAnswered whether or not the user has answered the qiz question
     * @param IsCorrect whether or not the user answered the question correctly
     */

    private int id;
    private String pointName;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int answerCorrect;
    private double latitude;
    private double longitude;
    private boolean isInProximity = false;
    private boolean isAnswered = false;
    private boolean isCorrect = false;
    private boolean inProgress = false;

    /**
     * Class constructor with the following parameters that are passed to instance variables
     * @param qid id number of quiz question
     * @param name alphanumeric question identifier
     * @param q quiz question
     * @param a1 first potential answer to quiz question
     * @param a2 second potential answer to quiz question
     * @param a3 third potential answer to quiz question
     * @param a4 fourth potential answer to quiz question
     * @param aC correct answer to quiz question
     * @param lat latitude of quiz question
     * @param lng longitude of quiz question
     */
    public Question(int qid, String name, String q, String a1, String a2, String a3, String a4, int aC, double lat, double lng){
        this.id = qid;
        this.pointName = name;
        this.question = q;
        this.answer1 = a1;
        this.answer2 = a2;
        this.answer3 = a3;
        this.answer4 = a4;
        this.answerCorrect = aC;
        this.latitude = lat;
        this.longitude = lng;
    }

    /**
     * returns quiz question ID
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * returns quiz questions name
     * @return pointName
     */
    public String getName () {
        return pointName;
    }

    /**
     * returns quiz question
     * @return question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * returns first potential answer
     * @return answer1
     */
    public String getAnswer1() {
        Log.i("answer1", answer1);
        return answer1;
    }

    /**
     * returns second potential answer
     * @return answer2
     */
    public String getAnswer2() {

        Log.i("answer2", answer2);
        return answer2;
    }

    /**
     * returns third potential answer
     * @return answer3
     */
    public String getAnswer3() {
        Log.i("answer3", answer3);
        return answer3;
    }

    /**
     * returns fourth potential answer
     * @return answer4
     */
    public String getAnswer4() {
        Log.i("answer4", answer4);
        return answer4;
    }

    /**
     * returns correct
     * @return answerCorrect
     */
    public String getAnswerCorrect() {

        String aC = "";
        // perform switch statement on answerCorrect to return text value of correct answer
        switch (answerCorrect) {
            case 1:
                aC = answer1;
                break;
            case 2:
                aC = answer2;
                break;
            case 3:
                aC = answer3;
                break;
            case 4:
                aC = answer4;
                break;
            default:
                break;

        }
        return aC;
    }

    /**
     * return isInProximity boolean
     * @return isInProximity
     */
    public boolean getProximity() {
        return isInProximity;
    }

    /**
     * return isAnswered boolean
     * @return isAnswered
     */
    public boolean getAnswered() {
        return isAnswered;
    }


    /**
     * return whether or not question was answered correctly
     * @return isCorrect
     */
    public boolean getCorrect() {
        return isCorrect;
    }

    /**
     * return question longitude
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * return question latitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * set proximity flag for question
     * @param proximity if true, user is within 20 m of question point
     */
    public void setProximity(boolean proximity) {
        isInProximity = proximity;
    }

    /**
     * set answered flag
     * @param a if true, user has answered question
     */
    public void setIsAnswered(boolean a) {
        isAnswered = a;
    }

    /**
     * records whether or not user answered question correctly
     * @param c if true, user answered question correctly
     */
    public void setIsCorrect(boolean c) {
        isCorrect = c;
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean p) {
        inProgress = p;
    }
}
