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

    private String revID;
    private String uid;
    private String rid;
    private String title;
    private String reviewText;
    private Date date;
    private float rating;
    private List<Reply> replies = new ArrayList<>();

    public Review() {
    }

    public Review(String uid, String rid) {
        this.uid = uid;
        this.rid = rid;
    }

    Map<String, Object> toMap(){
        HashMap<String, Object> output = new HashMap<>();

        output.put("revID",this.revID);
        output.put("uid",this.uid);
        output.put("rid",this.rid);
        output.put("title",this.title);
        output.put("reviewText",this.reviewText);
        output.put("date",this.date);
        output.put("rating",this.rating);

        Map<String, Object> replyMap = new HashMap<>();
        for (Reply r : this.replies){
            replyMap.put(r.getRepID(),r.toMap());
        }
        output.put("replies",replyMap);
        return output;
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
