package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

/**
 * @author Marco Ardizzone
 * @class RestaurantV2
 * @date 2015-04-17
 * @brief Restaurant class
 */
public class RestaurantV2 {

    transient private Context appContext;

    private int rid;
    private int uid;
    private String name;
    private String description;
    private String address;
    private String state;
    private String city;
    private String website;
    private String phone;
    private Uri image;
    private float rating;
    private ArrayList<Menu> menus=new ArrayList<>();
    private ArrayList<Order> orders=new ArrayList<>();
    private double xcoord;
    private double ycoord;

    /**
     *
     * @return the id of restaurant
     */
    public int getRid() { return this.rid; }

    /**
     *
     * @return The id of the restaurant owner
     */
    public int getUid() { return this.uid; }

    /**
     * @return string Name of restaurant
     */
    public String getName() { return this.name; }

    /**
     *
     * @return The description of restaurant
     */
    public String getDescription() { return this.description; }

    /**
     *
     * @return Address of restaurant
     */
    public String getAddress() { return this.address; }

    /**
     *
     * @return State of restaurant
     */
    public String getState() { return this.state; }

    /**
     *
     * @return City of Restaurant
     */
    public String getCity() { return this.city; }

    /**
     *
     * @return Uri of the image
     */
    public Uri getImage(){ return this.image;}

    /**
     *
     * @return The latitude coordinate of restaurant
     */
    public double getXcoord() { return this.xcoord; }

    /**
     *
     * @return The longitude coordinate of restaurant
     */
    public double getYcoord() { return this.ycoord; }

    /**
     *
     * @return the rating of restaurant
     */
    public float getRating() { return this.rating; }

    /**
     *
     * @return a string with the restaurant's phone number
     */
    public String getPhone() { return this.phone; }

    /**
     *
     * @return a string with the restaurant's website
     */
    public String getWebsite() { return this.website; }

    /**
     *
     * @return the arraylist of restaurant's menu
     */
    public ArrayList<Menu> getMenus() { return this.menus; }

    /**
     * The method takes as input an integer representing the menu type as follows:
     *  0: Menu of the day
     *  1: Fixed menu
     *  2: Fixed menu with options
     *  3: Complete menu
     * The method returns an ArrayList of Menus or null if nothing is found.
     *
     * @param type The type of menu, must be an integer between 0 and 3.
     * @return An ArrayList of all the menus of the requested type, or null if nothing is found.
     */
    public ArrayList<Menu> getMenusByType(int type){
        ArrayList<Menu> output = new ArrayList<Menu>();
        for(Menu m : this.menus)
            if(m.getType() == type)
                output.add(m);
        return output.isEmpty() ? null : output;
    }

    /**
     *
     * @param mid The id of the menu to search for.
     * @return The Menu object or null if nothing is found.
     */
    public Menu getMenuByID(int mid) throws MenuException {
        if (mid <0)
            throw new MenuException("Menu ID must be positive");
        for(Menu m : this.menus)
            if(m.getMid() == mid)
                return m;
        return null;
    }

    /**
     *
     * @param menu a new menu to add ad menu list of the restaurant
     */
    public void addMenu(Menu menu){ this.menus.add(menu); }

    /**
     *
     * @return the arraylist of restaurant's orders
     */
    public ArrayList<Order> getOrders() { return orders; }

    /**
     *
     * @param oid The id of the order to search for.
     * @return The requested order or null if nothing is found.
     */
    public Order getOrderByID(int oid) throws OrderException {
        if(oid <0)
            throw new OrderException("Order ID must be positive");
        for(Order o : this.orders)
            if(o.getOid() == oid)
                return o;
        return null;
    }

    /**
     *
     * @param uid The user id who submitted
     * @return An ArrayList of Orders or null if nothing is found.
     */
    public ArrayList<Order> getOrdersByUserID(int uid) throws UserException {
        if(uid <0)
            throw new UserException("User ID must be positive");
        ArrayList<Order> output = new ArrayList<Order>();
        for(Order o : this.orders)
            if(o.getUid() == uid)
                output.add(o);
        return output.isEmpty()? null : output;
    }

    /**
     *
     * @param o Order to add
     */
    public void addOrder(Order o){this.orders.add(o);}

    /**
     *
     * @param name Name of restaurant
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *
     * @param address Address of restaurant
     */
    public void setAddress(String address) { this.address = address; }

    /**
     *
     * @param state The state of restaurant
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @param city City of restaurant
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @param uri Uri of the image
     */
    public void setImageUri(Uri uri){ this.image = uri; }

    /**
     *
     * @param uri A string representing the Uri of the image
     */
    public void setImageUriFromString(String uri){ this.image = Uri.parse(uri); }

    /**
     *
     * @param xcoord The latitude coordinate of restaurant
     */
    public void setXcoord(double xcoord) {
        this.xcoord = xcoord;
    }

    /**
     *
     * @param ycoord The longitude coordinate of restaurant
     */
    public void setYcoord(double ycoord) {
        this.ycoord = ycoord;
    }

    /**
     *
     * @param description of the restaurant
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @param rid: the id of restaurant.
     * @throws RestaurantException Thrown if restaurant ID is negative.
     */
    public void setRid(int rid) throws RestaurantException {
        if(rid < 0)
            throw new RestaurantException("Restaurant ID must be positive");
        this.rid = rid;
    }

    /**
     *
     * @param rating of restaurant
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     *
     * @param menus: the arraylist of restaurant's menu
     */
    public void setMenus(ArrayList<Menu> menus) {
        this.menus = menus;
    }

    /**
     *
     * @param orders:the arraylist of restaurant's orders
     */
    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    /**
     *
     * @param uid Set the id of the restaurant owner
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     *
     * @param website of the restaurant
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     *
     * @param phone the phone number of the restaurant
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
