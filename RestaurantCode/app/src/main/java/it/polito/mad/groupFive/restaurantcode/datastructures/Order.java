package it.polito.mad.groupFive.restaurantcode.datastructures;

/**
 * @author Marco Ardizzone
 * @class Order
 * @date 04/04/16
 * @brief Order class
 */
public class Order {
    private int oid;
    private int uid;
    private int mid;
    private Restaurant restaurant;

    /**
     *
     * @return int: The order ID
     */
    public int getOid(){ return this.oid;}

    /**
     *
     * @return int: The user ID that performed this order
     */
    public int getUid(){ return this.uid;}

    /**
     *
     * @return int: The menu ID ordered
     */
    public int getMid(){ return this.mid;}

    /**
     *
     * @return Restaurant: The restaurant to which this order belongs to.
     */
    public Restaurant getRestaurant(){ return this.restaurant;}

    /**
     *
     * @param oid The order ID
     */
    public void setOid(int oid){ this.oid = oid;}

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
     * @param r The restaurant to which this order belong to.
     */
    public void setRestaurant(Restaurant r){ this.restaurant = r;}
}
