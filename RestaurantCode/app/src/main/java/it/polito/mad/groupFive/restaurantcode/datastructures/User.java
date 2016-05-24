package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

public class User {
    protected String FireID;
    protected String name;
    protected String surname;
    protected String userName;
    protected String password;
    protected String address;
    protected String email;
    protected String ImagePath;
    protected List<String> reviewsIDs = new ArrayList<>();
    protected boolean manager;
    protected List<String> favouriteIDs=new ArrayList<>();
    protected List<String> restaurantIDs=new ArrayList<>();

    public User(){

    }
    public User(boolean manager){
        this.manager=manager;
    }

    public User(String uid){

    }


    public String getUid() {
        return FireID;
    }

    public void setUid(String fireID) {
        FireID = fireID;
    }

    public List<String> getRestaurantIDs() {
        return restaurantIDs;
    }

    public void setRestaurantIDs(List<String> restaurantIDs) {
        this.restaurantIDs = restaurantIDs;
    }

    public List<String> getFavouriteIDs() {
        return favouriteIDs;
    }

    public void setFavouriteIDs(List<String> favouriteIDs) {
        this.favouriteIDs = favouriteIDs;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        manager = manager;
    }

    public List<String> getReviewsIDs() {
        return reviewsIDs;
    }

    public void setReviewsIDs(List<String> reviewsIDs) {
        this.reviewsIDs = reviewsIDs;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }
}
