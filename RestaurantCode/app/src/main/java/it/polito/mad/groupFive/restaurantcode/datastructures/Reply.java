package it.polito.mad.groupFive.restaurantcode.datastructures;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.ReplyException;

/**
 * @author Marco Ardizzone
 * @class Reply
 * @date 2016-05-22
 * @brief Reply class
 */
public class Reply {

    private DatabaseReference dbRoot;
    private String repID;
    private String revID;
    private String uid;
    private String title;
    private String text;

    public Reply(String uid,String revID) throws ReplyException {
        this(null,uid,revID);
    }

    public Reply(String repID, String uid, String revID) throws ReplyException {
        if(uid == null)
            throw new ReplyException("User ID cannot be null");
        if(revID == null)
            throw new ReplyException("Review ID cannot be null");
        this.uid = uid;
        this.revID = revID;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference("review").child(this.revID).child("reply");
        this.repID = repID == null ? this.dbRoot.push().getKey() : repID;
        //Change the dbRoot to the tree specific to this object
        this.dbRoot = this.dbRoot.child(this.repID);
    }

    void saveData(){
        this.dbRoot.child("reply-id").setValue(this.repID);
        this.dbRoot.child("review-id").setValue(this.revID);
        this.dbRoot.child("user-id").setValue(this.uid);
        this.dbRoot.child("title").setValue(this.title);
        this.dbRoot.child("text").setValue(this.text);

    }

    public String getRepID(){
        return this.repID;
    }

    public String getUid() {
        return this.uid;
    }

    public String getTitle() {
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }
}
