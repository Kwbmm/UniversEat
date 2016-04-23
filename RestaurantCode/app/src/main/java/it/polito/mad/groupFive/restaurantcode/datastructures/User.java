package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

/**
 * @author Marco Ardizzone
 * @class User
 * @date 2016-04-23
 * @brief User class
 *
 */
public abstract class User {

    transient protected Context appContext;

    protected int uid;
    protected String name;
    protected String surname;
    protected String userName;
    protected String password;
    protected String address;
    protected Uri image;
    protected ArrayList<Review> reviews = new ArrayList<>();

    /**
     * Generate a random integer in the range [1, Integer.MAX_VALUE]
     * @return In integer in the range [1, Integer.MAX_VALUE]
     */
    protected static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            int result;
            if((result=rand.nextInt(Integer.MAX_VALUE)) == 0)
                return Restaurant.randInt();
            return result;
        }
    }

    /**
     * Reads the JSON file corresponding to this user and fills this class with the data found
     * in the file.
     * If the file doesn't exist CreateJSONFile is called and the file is created. Then the reading
     * is performed again: this time the file will be found and fields of this class will be filled
     * with null values (because the created file is empty).
     *
     * If a fail occurs, the error message is logged and this method returns null.
     *
     * @return A User object or null if fails.
     */
    protected abstract User readJSONFile();

    /**
     * Create the JSON file corresponding to this object. The JSON file is identified by the
     * user ID set at instantiation time.
     * If an error occurs, the error is logged.
     */
    protected abstract void createJSONFile();

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy User object, on which the JSON data is written to.
     */
    protected abstract void copyData(User dummy);

    /**
     * Saves the current data store in this object to its JSON file.
     * Note that this method also saves the data for the custom sub-objects embedded inside this
     * class (Review).
     * In case of fail, the error is logged and a UserException is thrown.
     *
     * @throws UserException If writing JSON file fails
     */
    protected abstract void saveData() throws UserException;

    /**
     * Fetch the data corresponding to the User ID of this object from the JSON file.
     * This method is in charge of filling all the other classes of this data set (Review).
     * If an error occurs, it is logged and a UserException is thrown.
     *
     * @throws UserException If fetch fails
     */
    protected abstract void getData() throws UserException;

    /**
     *
     * @return The unique identifier of this User object
     */
    public int getUid() {
        return this.uid;
    }

    /**
     *
     * @return The name of user
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return The surname of user
     */
    public String getSurname() {
        return this.surname;
    }

    /**
     *
     * @return The address of user
     */
    public String getAddress() {
        return this.address;
    }

    /**
     *
     * @return The userName of the user
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     *
     * @return The password of the user
     */
    public String getPassword(){ return this.password; }

    /**
     *
     * @return Uri of user's image
     */
    public Uri getImageUri() {
        return this.image;
    }

    /**
     *
     * @return An ArrayList of reviews belonging to this user
     */
    public ArrayList<Review> getReviews(){ return this.reviews; }

    /**
     * Get the Review object corresponding to the supplied ID.
     * If no Review object is matching the input id, this method returns null.
     * An exception is thrown in case of negative review id.
     *
     * @param revID A positive integer uniquely identifying the review object
     * @return A review object matching the input id, or null if nothing is found.
     * @throws UserException If review id is negative
     */
    public Review getReviewByID(int revID) throws UserException {
        if(revID < 0)
            throw new UserException("Review ID must be positive");
        for(Review r : this.reviews)
            if(r.getRevID() == revID)
                return r;
        return null;
    }

    /**
     * Get the ArrayList of Review objects made by this user matching the supplied restaurant ID.
     * If no Review matches the input restaurant ID, an empty ArrayList is returned
     *
     * @param rid A positive integer uniquely identifying a restaurant.
     * @return An ArrayList of Review objects
     * @throws UserException If restaurant id is negative.
     */
    public ArrayList<Review> getReviewsByRestaurantID(int rid) throws UserException {
        if(rid < 0)
            throw new UserException("Restaurant ID must be positive");
        ArrayList<Review> returnRes = new ArrayList<>();
        for(Review r : this.reviews)
            if(r.getRid() == rid)
                returnRes.add(r);
        return returnRes;
    }

    /**
     *
     * @param uid Positive integer uniquely identifying the user
     * @throws UserException If user id is negative
     */
    public void setUid(int uid) throws UserException {
        if(uid < 0)
            throw new UserException("User ID must be positive");
        this.uid = uid;
    }

    /**
     *
     * @param name Name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param surname Surname of the user
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     *
     * @param address Address of the user
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @param userName of the user
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @param password Password of the user
     */
    public void setPassword(String password){
        //TODO Implement some encryption for password - Sad LIOY is sad;
        this.password = password;
    }

    /**
     *
     * @param image Uri of the user image
     */
    public void setImageUri(Uri image) {
        this.image = image;
    }

    /**
     *
     * @param r ArrayList of reviews made by the user.
     */
    public void setReviews(ArrayList<Review> r){ this.reviews = r; }
}
