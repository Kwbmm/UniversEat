package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.ReviewException;

/**
 * @author Marco Ardizzone
 * @class Review
 * @date 2016-04-22
 * @brief Review class
 */
public class Review {

    private String uid;
    private String rid;
    private String reviewText;
    private String title;
    private Date date;
    private float rating;
    private float food;
    private float place;
    private float service;
    private float pricequality;
public Review(String uid,String rid){
    this.rid=rid;
    this.uid=uid;

}
    public Review(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getFood() {
        return food;
    }

    public void setFood(float food) {
        this.food = food;
    }

    public float getPlace() {
        return place;
    }

    public void setPlace(float place) {
        this.place = place;
    }

    public float getService() {
        return service;
    }

    public void setService(float service) {
        this.service = service;
    }

    public float getPricequality() {
        return pricequality;
    }

    public void setPricequality(float pricequality) {
        this.pricequality = pricequality;
    }
}
