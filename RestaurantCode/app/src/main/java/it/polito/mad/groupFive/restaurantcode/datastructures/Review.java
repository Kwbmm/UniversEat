package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.os.Build;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private DatabaseReference dbRoot;

    private String revID;
    private String uid;
    private String rid;
    private String title;
    private String reviewText;
    private Date date;
    private float rating;
    private List<Reply> replies = new ArrayList<>();

    public Review(String uid, String rid) throws ReviewException {
        this(null,uid,rid);
    }

    public Review(String revID, String uid, String rid) throws ReviewException {
        if(uid == null)
            throw new ReviewException("User ID cannot be null");
        if(rid == null)
            throw new ReviewException("Restaurant ID cannot be null");
        this.uid = uid;
        this.rid = rid;

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference("review");

        this.revID = revID == null ? this.dbRoot.push().getKey() : revID;
        //Change the dbRoot to the tree specific to this object
        this.dbRoot = this.dbRoot.child(this.rid);
    }

    void saveData(){
        this.dbRoot.child("review-id").setValue(this.revID);
        this.dbRoot.child("user-id").setValue(this.uid);
        this.dbRoot.child("restaurant-id").setValue(this.rid);
        this.dbRoot.child("title").setValue(this.title);
        this.dbRoot.child("review-text").setValue(this.reviewText);
        this.dbRoot.child("date").setValue(this.date);
        this.dbRoot.child("rating").setValue(this.rating);
        this.dbRoot.child("reply").setValue(this.replies);
        for(Reply r : this.replies){
            r.saveData();
        }
    }

    /**
     *
     * @return The Review ID
     */
    public String getRevID(){ return this.revID; }

    /**
     *
     * @return The user ID of the user who made this review.
     */
    public String getUid(){ return this.uid; }

    /**
     *
     * @return The restaurant ID of the reviewed restaurant.
     */
    public String getRid(){ return this.rid; }

    /**
     *
     * @return The review title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @return The text of the review.
     */
    public String getReviewText(){ return this.reviewText; }

    /**
     *
     * @return The comments to the review text, if any.
     */
    public List<Reply> getReplies(){ return this.replies; }

    /**
     *
     * @return The rating given to the restaurant from this review.
     */
    public float getRating(){ return this.rating; }

    /**
     * Set the User ID of the User who made this review. PLEASE, be aware that by instantiating a
     * a Review object, a user ID is already set by default.
     * USE THIS METHOD ONLY IF YOU KNOW WHAT YOU ARE DOING.
     * The method logs a warning when it is called to notify the user id change.
     *
     * @param uid A positive integer uniquely identifying the user who made this review.
     * @throws ReviewException If user id is negative
     */
    public void setUid(String uid) throws ReviewException {
        final String METHOD_NAME = this.getClass().getName()+" - setUid";
        if(uid == null)
            throw new ReviewException("User ID must be positive");
        this.uid = uid;
        Log.w(METHOD_NAME, "User ID for Review ["+this.revID+"] was changed");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param s A string representing the text of the review.
     */
    public void setReviewText(String s){ this.reviewText = s; }

    /**
     *
     * @param replies A list representing the list of comments to the review text.
     */
    public void setReplies(List<Reply> replies){ this.replies = replies; }

    /**
     *
     * @param r The rating given by the user for the reviewed restaurant.
     */
    public void setRating(float r){ this.rating = r; }

    public Date getDate(){return this.date;}
    public void setDate(Date d){this.date=d;}
}
