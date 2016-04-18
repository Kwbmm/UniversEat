package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * @author Marco
 * @class Course
 * @date 2016-04-18
 * @brief Course class
 */
public class Course {

    transient private RestaurantV2 r=null;

    private int cid;
    private int mid;
    private String name;
    private String description;
    private float price;
    private Uri image;
    private boolean glutenFree;
    private boolean vegan;
    private boolean vegetarian;
    private boolean spicy;

    public Course(RestaurantV2 restaurant){
        this.r = restaurant;
    }

    public Course(RestaurantV2 restaurant, int cid, int mid) throws CourseException, MenuException {
        if(cid < 0)
            throw new CourseException("Course ID must be positive");
        this.cid = cid;
        if(mid < 0)
            throw new MenuException("Menu ID must be positive");
        this.mid = mid;
        this.r = restaurant;
    }

    public void getData() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        this.r.getData();
        for(Menu m :this.r.getMenus()){
            if(m.getCourseByID(this.cid) != null) {
                Course dummy = m.getCourseByID(this.cid);
                this.copyData(dummy);
            }
        }
    }

    private void copyData(Course d){
        this.cid = d.cid;
        this.mid = d.mid;
        this.name = d.name;
        this.description = d.description;
        this.price = d.price;
        this.image = d.image;
        this.glutenFree = d.glutenFree;
        this.vegan = d.vegan;
        this.vegetarian = d.vegetarian;
        this.spicy = d.spicy;
    }

    /**
     *
     * @return The Course ID
     */
    public int getCid(){return this.cid;}

    /**
     *
     * @return The name of the course
     */
    public String getName(){return this.name;}

    /**
     *
     * @return The description of the course
     */
    public String getDescription(){return this.description;}

    /**
     *
     * @return The price of the course
     */
    public float getPrice(){return this.price;}

    /**
     *
     * @return The Uri of the course image
     */
    public Uri getImageUri(){return this.image;}

    /**
     * Returns true if course is gluten-free. False otherwise.
     * @return true or false
     */
    public boolean isGlutenFree(){ return this.glutenFree;}

    /**
     * Returns true if course is vegan. False otherwise.
     * @return true or false
     */
    public boolean isVegan(){ return this.vegan;}

    /**
     * Returns true if course is vegetarian. False otherwise.
     * @return true or false
     */
    public boolean isVegetarian(){ return this.vegetarian;}

    /**
     * Returns true if course is spicy. False otherwise.
     * @return true or false
     */
    public boolean isSpicy(){ return this.spicy;}

    /**
     * Sets the Course ID
     * @param cid Course ID
     */
    public void setCid(int cid){ this.cid = cid;}
    /**
     * Sets the name of the course
     * @param name Name of the course
     */
    public void setName(String name){ this.name = name;}

    /**
     * Sets the description of the course
     * @param description Description of the course
     */
    public void setDescription(String description){ this.description = description;}

    /**
     * Sets the price of the course
     * @param price Price of the course
     */
    public void setPrice(float price){ this.price = price;}

    /**
     * Sets the Uri of the image.
     * @param image Uri of the image.
     */
    public void setImageUri(Uri image){ this.image = image;}

    /**
     * Sets if the course is gluten-free
     * @param gf true or false
     */
    public void setGlutenFree(boolean gf){ this.glutenFree = gf;}

    /**
     * Sets if the course is vegan
     * @param vegan true or false
     */
    public void setVegan(boolean vegan){ this.vegan = vegan;}

    /**
     * Sets if the course is vegetarian
     * @param vegetarian true or false
     */
    public void setVegetarian(boolean vegetarian){ this.vegetarian = vegetarian;}

    /**
     * Sets if the course is spicy
     * @param spicy true or false
     */
    public void setSpicy(boolean spicy){ this.spicy = spicy;}
}
