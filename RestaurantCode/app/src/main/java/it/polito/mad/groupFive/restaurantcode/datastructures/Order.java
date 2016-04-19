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
 * @date 04/04/16
 * @brief Order class
 */
public class Order {

    transient private Restaurant r = null;

    private int oid;
    private int uid;
    private int mid;
    private int rid;
    private Date date=null;

    public Order(Restaurant restaurant){
        this.r = restaurant;
        this.oid = Order.randInt();
    }

    public Order(Restaurant restaurant, int oid) throws OrderException {
        this.r = restaurant;
        if(oid < 0)
            throw new OrderException("Order ID must be positive");
        this.oid = oid;
    }

    public Order(Restaurant restaurant, int oid, int rid) throws OrderException, RestaurantException {
        this.r = restaurant;
        if(oid < 0)
            throw new OrderException("Order ID must be positive");
        this.oid = oid;
        if(rid < 0)
            throw new RestaurantException("Restaurant ID must be positive");
        this.rid = rid;
    }

    private static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            return rand.nextInt(Integer.MAX_VALUE -1 );
        }
    }

    public void getData() throws OrderException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            this.r.getData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new OrderException(e.getMessage());
        }
        Order dummy = this.r.getOrderByID(this.oid);
        this.copyData(dummy);
    }

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

    public int getRid(){ return this.rid; }

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
    public void setRid(int rid){ this.rid= rid;}

    public void setDate(Date d){ this.date = d;}
}
