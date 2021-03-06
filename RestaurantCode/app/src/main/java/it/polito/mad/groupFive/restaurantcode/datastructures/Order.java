package it.polito.mad.groupFive.restaurantcode.datastructures;

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
    private String date;
    private String creationDate;

    private String restaurantName;
    private String notes;
    private String name;
    private String menuName;
    private Boolean notified;

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuNmae) {
        this.menuName = menuNmae;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * Create a new Order object.
     *
     * @param rid The restaurant ID to which this order belongs to.
     * @param mid The menu ID to which this order was made for.
     * @param uid The user ID who made the order.
     * @param oid The ID of this Order object.
     * @throws OrderException If restaurant ID, menu ID, user ID or order ID are null.
     */
    public Order(String rid, String mid, String uid, String oid) throws OrderException {
        if(rid == null)
            throw new OrderException("Restaurant ID cannot be null");
        if(mid == null)
            throw new OrderException("Menu ID cannot be null");
        if(uid == null)
            throw new OrderException("User ID cannot be null");
        if(oid == null)
            throw new OrderException("Order ID cannot be null");

        this.rid = rid;
        this.mid = mid;
        this.uid = uid;
        this.oid = oid;
    }
    public Order(){

    }

    /**
     * Creates a Map of this Object, ready to be put as value inside Firebase DB.
     *
     * @return A Map representing this object.
     */
    public Map<String,Object> toMap(){
        HashMap<String,Object> output = new HashMap<>();
        output.put("oid",this.oid);
        output.put("uid",this.uid);
        output.put("mid",this.mid);
        output.put("rid",this.rid);
        output.put("date",this.date.toString());
        output.put("notes",this.notes);
        output.put("name",this.name);
        output.put("menuname",this.menuName);
        output.put("restaurantName",this.restaurantName);
        output.put("creationDate",this.creationDate);
        output.put("notified",this.notified);
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
    public String getDate(){ return this.date;}

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
    public Order setName(String name){this.name=name; return this;}

    /**
     *
     * @param d Date of the order
     */
    public Order setDate(String d){ this.date = d; return this;}

    /**
     *
     * @param notes Some additional notes to attach to the order.
     */
    public Order setNotes(String notes){this.notes=notes; return this;}

    public Order setRestaurantName(String restaurantName){this.restaurantName=restaurantName; return this;}
    public String getRestaurantName(){return this.restaurantName;}
    public Order setCreationDate(String date){this.creationDate=date; return this;}
    public String getCreationDate(){return this.creationDate;}
}

