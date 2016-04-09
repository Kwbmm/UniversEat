package it.polito.mad.groupFive.restaurantcode.datastructures;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;

/**
 * @author Marco Ardizzone
 * @class Order
 * @date 04/04/16
 * @brief Order class
 */
public class Order {

    private JSONObject JSONFile = null;

    private int oid;
    private int uid;
    private int mid;
    private int rid;
    private Date date=null;
    private Restaurant r = null;

    public Order(Restaurant restaurant){
        this.r = restaurant;
        this.JSONFile = restaurant.getJSONFile();
    }

    public Order(Restaurant restaurant, int oid) throws OrderException {
        this.r = restaurant;
        if(oid < 0)
            throw new OrderException("Order ID must be positive");
        this.oid = oid;
        this.JSONFile = restaurant.getJSONFile();
    }

    public Order(Restaurant restaurant, int oid, int rid) throws OrderException {
        this.r = restaurant;
        if(oid < 0)
            throw new OrderException("Order ID must be positive");
        this.oid = oid;
        this.JSONFile = restaurant.getJSONFile();
        this.rid = rid;
    }

    /**
     * Reads data from JSON configuration file.
     * If some field is missing, it throws JSONException.
     *
     * @throws JSONException if some field is missing
     * @throws ParseException if date from JSON file cannot be parsed correctly
     */
    public void getData() throws JSONException, ParseException {
        for (int i = 0; i < this.JSONFile.getJSONArray("orders").length(); i++) {
            if(this.JSONFile.getJSONArray("orders").getJSONObject(i).getInt("id") == this.oid){
                JSONObject order = this.JSONFile.getJSONArray("orders").getJSONObject(i);
                this.mid = order.getInt("mid");
                this.uid = order.getInt("uid");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                this.date = sdf.parse(order.getString("date"));
                break;
            }
        }
    }

    /**
     * THIS METHOD SHOULD NEVER BE CALLED ON IT'S OWN!
     * Returns a JSONObject to saveData method of Restaurant class.
     *
     * @return JSONObject with the current data of the Order class.
     * @throws JSONException
     */
    public JSONObject saveData() throws JSONException{
        JSONObject order = new JSONObject();
        order.put("id",this.oid);
        order.put("uid",this.uid);
        order.put("mid",this.mid);
        order.put("rid",this.rid);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        order.put("date",sdf.format(this.date));
        return order;
    }
    /**
     *
     * @return The order ID
     */
    public int getOid(){ return this.oid;}

    /**
     *
     * @return The user ID that performed this order
     */
    public int getUid(){ return this.uid;}

    /**
     *
     * @return The menu ID ordered
     */
    public int getMid(){ return this.mid;}

    /**
     *
     * @return The restaurant ID to which this order belongs to.
     */
    public int getRestaurantID(){ return this.rid;}

    public Date getDate(){ return this.date;}

    /**
     *
     * @param oid The order ID
     */
    public void setOid(int oid) throws OrderException {
        if(oid < 0)
            throw new OrderException("Order ID must be positive");
        this.oid = oid;
    }

    /**
     *
     * @param uid The user ID making this order
     */
    public void setUid(int uid){ this.uid = uid;}

    /**
     *
     * @param mid The menu ID ordered
     */
    public void setMid(int mid){ this.mid = mid;}

    /**
     *
     * @param rid The restaurant ID to which this order belong to.
     */
    public void setRestaurantID(int rid){ this.rid= rid;}

    public void setDate(Date d){ this.date = d;}
}
