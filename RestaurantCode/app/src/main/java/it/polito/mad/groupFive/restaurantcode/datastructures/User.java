package it.polito.mad.groupFive.restaurantcode.datastructures;

import java.util.ArrayList;

/**
 * @author Giovanni
 * @class User
 * @date 04/04/16
 * @brief User class
 *
 */
public class User {
    private int uid;
    private String name;
    private String surname;
    private String address;
    private byte[] image;
    private String nickname;
    private ArrayList<Restaurant> prefers;
    private boolean restaurantowner;
    private Restaurant restaurant;
}
