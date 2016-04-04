package it.polito.mad.groupFive.restaurantcode.datastructures;

import java.util.ArrayList;

/**
 * @author Giovanni
 * @class Restaurant
 * @date 04/04/16
 * @brief Restaurant class
 */
public class Restaurant {
    private int rid;
    private String name;
    private String address;
    private String state;
    private String city;
    private byte[] image;
    private float rating;
    private ArrayList<Menu> menus;
    private String description;
    private ArrayList<Order> orders;
    private float xcoord;
    private float ycoord;

    public Restaurant(){


    }

    /**
     * @return string: name of restaurant
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name of restaurant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return address of restaurant
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address of restaurant
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return State of restaurant
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state: the State of restaurant
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return city of Restaurant
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city of restaurant
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return byte of restaurant's image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     *
     * @param image the byte of image of restaurant
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     *
     * @return the latitude coordinate of restaurant
     */
    public float getXcoord() {
        return xcoord;
    }

    /**
     *
     * @param xcoord: the latitude coordinate of restaurant
     */
    public void setXcoord(float xcoord) {
        this.xcoord = xcoord;
    }

    /**
     *
     * @return the longitude coordinate of restaurant
     */
    public float getYcoord() {
        return ycoord;
    }

    /**
     *
     * @param ycoord: the longitude coordinate of restaurant
     */
    public void setYcoord(float ycoord) {
        this.ycoord = ycoord;
    }

    /**
     *
     * @return the description of restaurant
     */
    public String getDescription() {
        return description;
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
     * @return the id of restaurant
     */
    public int getRid() {
        return rid;
    }

    /**
     *
     * @param rid: the id of restaurant
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    /**
     *
     * @return the rating of restaurant
     */
    public float getRating() {
        return rating;
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
     * @return the arraylist of restaurant's menu
     */
    public ArrayList<Menu> getMenus() {
        return menus;
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
     * @return the arraylist of restaurant's orders
     */
    public ArrayList<Order> getOrders() {
        return orders;
    }

    /**
     *
     * @param orders:the arraylist of restaurant's orders
     */
    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

}
