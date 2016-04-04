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


}
