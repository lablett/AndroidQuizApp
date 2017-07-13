package uk.ac.ucl.cege.cegeg077.uceslra.locationbasedquiz;

/**
 * <h2> User </h2>
 *
 * Defines User object utilised in Location Based Quiz
 *
 * @author Lucille Ablett
 *
 *
 */

public class User {

    /**
    * Instance variables:
    * @param uid user ID from database
    * @param score number of questions correctly answered
    * @param questionsAttempted number of questions attemted by user
    */
    private int uid;
    private int score = 0;
    private int questionsAttempted = 0;

    /**
     * Class constructor, creates new user instance
     * @param u constructor parameter passed to instance variable uid
     */
    public User(int u) {
        this.uid = u;
    }

    /**
     * returns userID number
     * @return uid
     */
    public int getID() {
        return uid;
    }

    /**
     * adds the number specified in the method parameters to the users's score
     * @param s number to increase score by
     */
    public void setScore(int s) {
        score = score + s;
    }

    /**
     * returns the user's score
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * adds the number specified in the method paramteres to the users total number of attempted questions
     * @param s number to increase questionsAttempted by
     */
    public void setQuestionCount(int s) {
        questionsAttempted = questionsAttempted + s;
    }

    /**
     * returns the number of questions the user has attempted
     * @return questionsAttempted
     */
    public int getQuestionCount() {
        return questionsAttempted;
    }
}
