package it.polito.mad.groupFive.restaurantcode.datastructures;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;

/**
 * @author Marco Ardizzone
 * @class Order
 * @date 2016-04-19
 * @brief Order class
 */
public class Order {

    private String oid;
    private String uid;
    private String mid;
    private String rid;
    private Date date=null;
    private String notes;
    private String name;

    public Order() {
    }

    public Order(String rid, String mid, String uid, String oid){
        this.rid = rid;
        this.mid = mid;
        this.uid = uid;
        this.oid = oid;
    }

    Map<String,Object> toMap(){
        HashMap<String,Object> output = new HashMap<>();
        output.put("oid",this.oid);
        output.put("uid",this.uid);
        output.put("mid",this.mid);
        output.put("rid",this.rid);
        output.put("date",this.date);
        output.put("notes",this.notes);
        output.put("name",this.name);
        return output;
    }

    /**
     *
     * @return The order ID
     */
    public String getOid(){ return this.oid;}

    /**
     *
     * @return The user ID that performed this order
     */
    public String getUid(){ return this.uid;}

    /**
     *
     * @return The menu ID ordered
     */
    public String getMid(){ return this.mid;}

    /**
     *
     * @return The restaurant ID whose this order belongs to
     */
    public String getRid(){ return this.rid; }

    /**
     *
     * @return The customer name who made the order.
     */
    public String getName(){return this.name;}

    /**
     *
     * @return The date of the order
     */
    public Date getDate(){ return this.date;}

    /**
     *
     * @return The additional notes added to the order, or an empty string if null.
     */
    public String getNotes(){
        return notes == null ? " " : notes;
    }

    /**
     *
     * @param name The name of the user who made the order.
     */
    public void setName(String name){this.name=name;}

    /**
     *
     * @param d Date of the order
     */
    public void setDate(Date d){ this.date = d;}

    /**
     *
     * @param notes Some additional notes to attach to the order.
     */
    public void setNotes(String notes){this.notes=notes;}
}
