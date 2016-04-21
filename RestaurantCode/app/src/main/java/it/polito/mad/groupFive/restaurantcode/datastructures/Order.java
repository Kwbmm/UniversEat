package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.os.Build;
import android.util.Log;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * @author Marco Ardizzone
 * @class Order
 * @date 2016-04-19
 * @brief Order class
 */
public class Order {

    transient private Restaurant r = null;

    private int oid;
    private int uid;
    private int mid;
    private int rid;
    private Date date=null;

    /**
     * Create an instance of Order: requires, as parameter, its restaurant object.
     * The ID of the order is generated automatically.
     *
     * @param restaurant The restaurant object whose this order belongs to.
     */
    public Order(Restaurant restaurant){
        this.r = restaurant;
        this.oid = Order.randInt();
    }

    /**
     * Create an instance of Order: requires, as parameters, its restaurant object and an integer
     * positive ID to uniquely identifying this object.
     *
     * @param restaurant The restaurant object whose course belongs to
     * @param oid A positive integer unique identifier.
     * @throws OrderException Thrown if oid is negative.
     */
    public Order(Restaurant restaurant, int oid) throws OrderException {
        this.r = restaurant;
        if(oid < 0)
            throw new OrderException("Order ID must be positive");
        this.oid = oid;
    }

    /**
     * Generate a random integer in the range [1, Integer.MAX_VALUE]
     * @return In integer in the range [1, Integer.MAX_VALUE]
     */
    private static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            int result;
            if((result=rand.nextInt(Integer.MAX_VALUE)) == 0)
                return Order.randInt();
            return result;
        }
    }

    /**
     * Fetch the data corresponding to the Order ID of this object from the JSON file.
     * Fetch operations are always performed inside the restaurant object, this is just a call to
     * that method.
     *
     * @throws OrderException If fetch fails
     */
    public void getData() throws OrderException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            this.r.getData();
            Order dummy = this.r.getOrderByID(this.oid);
            this.copyData(dummy);
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new OrderException(e.getMessage());
        }
    }

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy Order object, on which the JSON data is written to.
     */
    private void copyData(Order dummy) {
        this.oid = dummy.getOid();
        this.uid = dummy.getUid();
        this.mid = dummy.getMid();
        this.rid = dummy.getRid();
        this.date = dummy.getDate();
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
     * @return The restaurant ID whose this order belongs to
     */
    public int getRid(){ return this.rid; }

    /**
     *
     * @return The date of the order
     */
    public Date getDate(){ return this.date;}

    /**
     *
     * @param oid The order ID
     * @throws OrderException if order id is negative
     */
    public void setOid(int oid) throws OrderException {
        if(oid < 0)
            throw new OrderException("Order ID must be positive");
        this.oid = oid;
    }

    /**
     *
     * @param uid The user ID making this order
     * @throws OrderException if user id is negative.
     */
    public void setUid(int uid) throws OrderException {
        if(uid < 0 )
            throw new OrderException("User ID must be positive");
        this.uid = uid;
    }

    /**
     *
     * @param mid The menu ID ordered
     * @throws OrderException if menu id is negative
     */
    public void setMid(int mid) throws OrderException {
        if(mid < 0)
            throw new OrderException("Menu ID must be positive");
        this.mid = mid;
    }

    /**
     *
     * @param rid The restaurant ID to which this order belong to.
     * @throws OrderException if restaurant id is negative.
     */
    public void setRid(int rid) throws OrderException {
        if(rid < 0)
            throw new OrderException("Restaurant ID must be positive");
        this.rid= rid;
    }

    /**
     *
     * @param d Date of the order
     */
    public void setDate(Date d){ this.date = d;}
}
