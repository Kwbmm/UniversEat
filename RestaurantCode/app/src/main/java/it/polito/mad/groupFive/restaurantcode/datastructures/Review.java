package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.ReviewException;

/**
 * @author Marco Ardizzone
 * @class Review
 * @date 2016-04-22
 * @brief Review class
 */
public class Review {
    transient private Restaurant restaurant;
    transient private User reviewer;

    private int revID;
    private int uid;
    private int rid;
    private String reviewText;
    private String title;
    private Date date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private ArrayList<String> replies = new ArrayList<>();
    private float rating;
    private float food;
    private float place;
    private float service;
    private float pricequality;

    /**
     * Create an instance of Review: requires, as parameter, its restaurant object and the user
     * object who performed the review.
     * This constructor takes care of setting also the user id and restaurant id, taken from the
     * passed objects.
     * The ID of the review is generated automatically.
     *
     * @param r The restaurant object whose this menu belongs to.
     * @param u The user object who made this review
     * @throws ReviewException If review id is negative
     */
    public Review(Restaurant r, User u) throws ReviewException {
        this(r,u,Review.randInt());
    }

    /**
     * Create an instance of Review: requires, as parameter, its restaurant object and the user
     * object who performed the review.
     * This constructor takes care of setting also the user id and restaurant id, taken from the
     * passed objects.
     * The ID must be a positive integer uniquely identifying the review.
     *
     * @param r The restaurant object whose this menu belongs to.
     * @param u The user object who made this review
     * @param revID A positive integer unique identifier.
     * @throws ReviewException If review id is negative
     */
    public Review(Restaurant r, User u, int revID) throws ReviewException {
        if(revID < 0 )
            throw new ReviewException("Review ID must be positive");
        this.revID = revID;
        this.restaurant = r;
        this.rid = r.getRid();
        this.reviewer = u;
        this.uid = u.getUid();
        this.date = new Date();
    }

    /**
     * Generate a random integer in the range [1, Integer.MAX_VALUE]
     * @return In integer in the range [1, Integer.MAX_VALUE]
     */
    private static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            int result;
            if((result=rand.nextInt(Integer.MAX_VALUE)) == 0)
                return Review.randInt();
            return result;
        }
    }

    /**
     * Fetch the data corresponding to the Review ID of this object from the JSON file.
     * Fetch operations are always performed inside the restaurant object, this is just a call to
     * that method.
     *
     * @throws ReviewException If fetch fails
     */
    public void getData() throws ReviewException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            this.restaurant.getData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new ReviewException(e.getMessage());
        }
        Review dummy = null;
        try {
            dummy = this.restaurant.getReviewByRevID(this.revID);
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new ReviewException(e.getMessage());
        }
        this.copyData(dummy);
    }

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy Review object, on which the JSON data is written to.
     */
    private void copyData(Review dummy) {
        this.revID = dummy.getRevID();
        this.uid = dummy.getUid();
        this.rid = dummy.getRid();
        this.reviewText = dummy.getReviewText();
        this.replies = dummy.getReplies();
        this.rating = dummy.getRating();
        this.title=dummy.getTitle();
        this.date=dummy.getDate();
        this.pricequality=dummy.getPricequality();
    }

    /**
     *
     * @return The Review ID
     */
    public int getRevID(){ return this.revID; }

    /**
     *
     * @return The user ID of the user who made this review.
     */
    public int getUid(){ return this.uid; }

    /**
     *
     * @return The restaurant ID of the reviewed restaurant.
     */
    public int getRid(){ return this.rid; }

    /**
     *
     * @return The text of the review.
     */
    public String getReviewText(){ return this.reviewText; }

    /**
     *
     * @return The comments to the review text, if any.
     */
    public ArrayList<String> getReplies(){ return this.replies; }

    /**
     *
     * @return The rating given to the restaurant from this review.
     */
    public float getRating(){ return this.rating; }

    /**
     *
     * @param revID A positive integer uniquely identifying the Review object
     * @throws ReviewException If review id is negative
     */
    public void setRevID(int revID) throws ReviewException {
        if(revID < 0)
            throw new ReviewException("Review ID must be positive");
        this.revID = revID;
    }

    /**
     * Set the User ID of the User who made this review. PLEASE, be aware that by instantiating a
     * a Review object, a user ID is already set by default.
     * USE THIS METHOD ONLY IF YOU KNOW WHAT YOU ARE DOING.
     * The method logs a warning when it is called to notify the user id change.
     *
     * @param uid A positive integer uniquely identifying the user who made this review.
     * @throws ReviewException If user id is negative
     */
    public void setUid(int uid) throws ReviewException {
        final String METHOD_NAME = this.getClass().getName()+" - setUid";
        if(uid < 0)
            throw new ReviewException("User ID must be positive");
        this.uid = uid;
        Log.w(METHOD_NAME, "User ID for Review ["+this.revID+"] was changed");
    }

    /**
     * Set the Restaurant ID of the reviewed restaurant. PLEASE, be aware that by instantiating a
     * a Review object, a restaurant ID is already set by default.
     * USE THIS METHOD ONLY IF YOU KNOW WHAT YOU ARE DOING.
     * The method logs a warning when it is called to notify the restaurant id change.
     *
     * @param rid A positive integer uniquely identifying the Restaurant object reviewed.
     * @throws ReviewException If restaurant id is negative
     */
    public void setRid(int rid) throws ReviewException {
        final String METHOD_NAME = this.getClass().getName()+" - setRid";
        if(rid < 0)
            throw new ReviewException("Restaurant ID must be positive");
        this.rid = rid;
        Log.w(METHOD_NAME, "Restaurant ID for Review ["+this.revID+"] was changed");
    }

    /**
     *
     * @param s A string representing the text of the review.
     */
    public void setReviewText(String s){ this.reviewText = s; }

    /**
     *
     * @param replies An ArrayList representing the list of comments to the review text.
     */
    public void setReplies(ArrayList<String> replies){ this.replies = replies; }

    /**
     *
     * @param r The rating given by the user for the reviewed restaurant.
     */
    public void setRating(float r){ this.rating = r; }

    public Date getDate(){return this.date;}
    public void setDate(Date d){this.date=d;}

    public float getFood() {
        return food;
    }

    public void setFood(float food) {
        this.food = food;
    }

    public float getPlace() {
        return place;
    }

    public void setPlace(float place) {
        this.place = place;
    }

    public float getService() {
        return service;
    }

    public void setService(float service) {
        this.service = service;
    }

    public float getPricequality() {
        return pricequality;
    }

    public void setPricequality(float pricequality) {
        this.pricequality = pricequality;
    }
}
