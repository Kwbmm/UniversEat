package it.polito.mad.groupFive.restaurantcode.datastructures;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Create a new Review object.
     *
     * @param uid The user ID who made this review.
     * @param rid The restaurant ID reviewed.
     * @param revID The ID of this Review object.
     * @throws ReviewException If user ID, restaurant ID or review ID are null.
     */
    public Review(String uid, String rid, String revID) throws ReviewException {
        if(uid == null)
            throw new ReviewException("User ID cannot be null");
        if(rid == null)
            throw new ReviewException("Restaurant ID cannot be null");
        if(revID == null)
            throw new ReviewException("Review ID cannot be null");
        this.uid = uid;
        this.rid = rid;
        this.revID = revID;
    }

    /**
     * Creates a Map of this Object, ready to be put as value inside Firebase DB.
     *
     * @return A Map representing this object.
     */
    public Map<String, Object> toMap(){
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
     * @return A Date object representing the moment in which this review was made.
     */
    public Date getDate(){return this.date;}

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

    public Review setTitle(String title) {
        this.title = title; return this;
    }

    /**
     *
     * @param s A string representing the text of the review.
     */
    public Review setReviewText(String s){ this.reviewText = s; return this; }

    /**
     *
     * @param d A Date object representing the moment in which this review is made.
     */
    public Review setDate(Date d){this.date=d; return this;}

    /**
     *
     * @param replies A list representing the list of comments to the review text.
     */
    public Review setReplies(List<Reply> replies){ this.replies = replies; return this; }

    /**
     *
     * @param r The rating given by the user for the reviewed restaurant.
     */
    public Review setRating(float r){ this.rating = r; return this; }
}
