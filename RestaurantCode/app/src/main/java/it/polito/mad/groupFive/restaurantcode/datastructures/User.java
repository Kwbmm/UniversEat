package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

/**
 * @author Giovanni
 * @class User
 * @date 04/04/16
 * @brief User class
 *
 */
public class User {
    private int uid;
    private Restaurant restaurant;
    private String name;
    private String surname;
    private String address;
    private byte[] image;
    private String nickname;
    private ArrayList<Restaurant> favorites;
    private boolean restaurantOwner;

    /**
     * Create a User object. Requires, as parameters, the Android Application Context of the
     * activity instantiating this class, a positive integer uniquely identifying the restaurant
     * object and another positive integer uniquely identifying the user object.
     * In case of fail, the error is logged and a UserException is thrown.
     *
     * @param c The Android Application Context
     * @param rid A positive unique integer identifying a restaurant object
     * @param uid A positive unique integer identifying a user object
     * @throws UserException If user id is negative or if restaurant creation fails for some reason.
     */
    public User(Context c, int rid, int uid) throws UserException {
        final String METHOD_NAME = this.getClass().getName()+" - constructor";
        if(uid < 0)
            throw new UserException("User ID must be positive");
        this.uid = uid;

        try {
            this.restaurant = new Restaurant(c,rid);
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
        this.restaurantOwner = true;
    }

    /**
     *
     * @return the name of user
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the surname of user
     */
    public String getSurname() {
        return surname;
    }

    /**
     *
     * @param surname of the user
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     *
     * @return the address of user
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address of the user
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return the nickname of the user
     */
    public String getNickname() {
        return nickname;
    }

    /**
     *
     * @param nickname of the user
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     *
     * @return byte[] of user's image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     *
     * @param image of user
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     *
     * @return yes if the user is the restaurant owner
     */
    public boolean isRestaurantowner() {
        return this.restaurantOwner;
    }

    /**
     *
     * @param restaurantowner if is or is not a restaurant owner
     */
    public void setRestaurantowner(boolean restaurantowner) { this.restaurantOwner = restaurantowner;}

    /**
     *
     * @return the id of user
     */
    public int getUid() {
        return uid;
    }

    /**
     *
     * @param uid the id of user
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     *
     * @return the arraylist of user's favorite restaurants 
     */
    public ArrayList<Restaurant> getFavorites() {
        return favorites;
    }

    /**
     *
     * @param favorite: the arraylist of user's favorite restaurants
     */
    public void setFavorites(ArrayList<Restaurant> favorite) {
        this.favorites = favorite;
    }

    /**
     *
     * @return the restaurant of the user (restaurant manager)
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     *
     * @param restaurant: the restaurant of the user (restaurant manager)
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

}
