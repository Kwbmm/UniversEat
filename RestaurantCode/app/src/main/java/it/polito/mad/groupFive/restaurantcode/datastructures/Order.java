package it.polito.mad.groupFive.restaurantcode.datastructures;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;

/**
 * @author Marco Ardizzone
 * @class Order
 * @date 2016-04-19
 * @brief Order class
 */
public class Order {

    private DatabaseReference dbRoot;

    private String oid;
    private String uid;
    private String mid;
    private String rid;
    private Date date=null;
    private String notes;
    private String name;

    public Order(String rid, String mid, String uid) throws OrderException {
        this(null,rid,mid,uid);
    }

    public Order(String oid, String rid, String mid, String uid) throws OrderException {
        if(rid == null)
            throw new OrderException("Restaurant ID cannot be null");
        if(mid == null)
            throw new OrderException("Menu ID cannot be null");
        if(uid == null)
            throw new OrderException("User ID cannot be null");
        this.rid = rid;
        this.mid = mid;
        this.uid = uid;

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference("order");

        this.oid = oid == null ? this.dbRoot.push().getKey() : oid;

        //Change the dbRoot to the tree specific to this object
        this.dbRoot = this.dbRoot.child(this.oid);
    }

    void saveData(){
        this.dbRoot.child("order-id").setValue(this.oid);
        this.dbRoot.child("user-id").setValue(this.uid);
        this.dbRoot.child("menu-id").setValue(this.mid);
        this.dbRoot.child("restaurant-id").setValue(this.rid);
        this.dbRoot.child("date").setValue(this.date);
        this.dbRoot.child("notes").setValue(this.notes);
        this.dbRoot.child("name").setValue(this.name);
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
