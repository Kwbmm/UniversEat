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

    public Reply() {
    }

    public Reply(String uid, String revID) {
        this.uid = uid;
        this.revID = revID;
    }

    Map<String, Object> toMap(){
        HashMap<String, Object> output = new HashMap<>();
        output.put("repID",this.repID);
        output.put("revID",this.revID);
        output.put("uid",this.uid);
        output.put("title",this.title);
        output.put("text",this.text);
        return output;
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
