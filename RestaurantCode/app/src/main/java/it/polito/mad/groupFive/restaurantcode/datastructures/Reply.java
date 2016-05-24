package it.polito.mad.groupFive.restaurantcode.datastructures;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.ReplyException;

/**
 * @author Marco Ardizzone
 * @class Reply
 * @date 2016-05-22
 * @brief Reply class
 */
public class Reply {

    private String repID;
    private String revID;
    private String uid;
    private String title;
    private String text;

    /**
     * Create a new Reply object.
     *
     * @param uid The user ID of this reply.
     * @param revID The review ID to which this reply belongs to.
     * @param repID The ID of this Reply object.
     * @throws ReplyException If user ID, review ID or reply ID are null.
     */
    public Reply(String uid, String revID, String repID) throws ReplyException {
        if(uid == null)
            throw new ReplyException("User ID cannot be null");
        if(revID == null)
            throw new ReplyException("Review ID cannot be null");
        if(repID == null)
            throw new ReplyException("Reply ID cannot be null");
        this.uid = uid;
        this.revID = revID;
        this.repID = repID;
    }

    /**
     * Creates a Map of this Object, ready to be put as value inside Firebase DB.
     *
     * @return A Map representing this object.
     */
    Map<String, Object> toMap(){
        HashMap<String, Object> output = new HashMap<>();
        output.put("repID",this.repID);
        output.put("revID",this.revID);
        output.put("uid",this.uid);
        output.put("title",this.title);
        output.put("text",this.text);
        return output;
    }

    /**
     *
     * @return The reply ID
     */
    public String getRepID(){
        return this.repID;
    }

    /**
     *
     * @return The user ID
     */
    public String getUid() {
        return this.uid;
    }

    /**
     *
     * @return The title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @return The text of the reply
     */
    public String getText() {
        return this.text;
    }

    /**
     *
     * @param title The title of this reply
     */
    public Reply setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     *
     * @param text The text of this reply
     */
    public Reply setText(String text) {
        this.text = text;
        return this;
    }
}
