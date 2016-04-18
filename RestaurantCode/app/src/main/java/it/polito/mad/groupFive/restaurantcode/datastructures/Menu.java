package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * @author Marco
 * @class Menu
 * @date 2016-04-18
 * @brief Menu class
 */
public class Menu {

    transient private RestaurantV2 r=null;

    private int mid;
    private String name=null;
    private String description=null;
    private float price;
    private Uri image;
    private int type;
    private int numberchoice;
    private ArrayList<Course> courses = new ArrayList<>();
    private boolean ticket;
    private boolean beverage;
    private boolean servicefee;

    public Menu(RestaurantV2 restaurant){
        this.r = restaurant;
        this.mid = Menu.randInt();
    }

    /**
     *
     * @param restaurant Restaurant instance.
     * @param mid The ID of the menu.
     * @throws MenuException Thrown if menu ID is negative.
     */
    public Menu(RestaurantV2 restaurant, int mid) throws MenuException {
        this.r = restaurant;
        if(mid < 0)
            throw new MenuException("Menu ID must be positive");
        this.mid = mid;
    }

    public static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            return rand.nextInt(Integer.MAX_VALUE -1 );
        }
    }

    /**
     * Reads data from JSON configuration file.
     * If some field is missing, it throws JSONException.
     * Please note that course objects read like this are just filled with their
     * own id. The other data must be filled through the methods provided in the Course class.
     *
     * @throws JSONException if some field is missing.
     */
    public void getData() throws MenuException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            this.r.getData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new MenuException(e.getMessage());
        }
        Menu dummy = this.r.getMenuByID(this.mid);
        this.copyData(dummy);
    }

    private void copyData(Menu dummy) {
         this.mid = dummy.mid;
         this.name = dummy.name;
         this.description = dummy.description;
         this.price = dummy.price;
         this.image = dummy.image;
         this.type = dummy.type;
         this.numberchoice = dummy.numberchoice;
         this.courses = dummy.courses;
         this.ticket = dummy.ticket;
         this.beverage = dummy.beverage;
         this.servicefee = dummy.servicefee;
    }

    /**
     *
     * @return The ID
     */
    public int getMid(){ return this.mid;}

    /**
     *
     * @return The name of the menu
     */
    public String getName(){ return this.name;}

    /**
     *
     * @return The description of the menu
     */
    public String getDescription(){ return this.description;}

    /**
     *
     * @return The price of the menu
     */
    public float getPrice(){ return this.price;}

    /**
     *
     * @return The image Uri of the course
     */
    public Uri getImageUri(){return this.image;}

    /**
     * This method returns a textual representation of the type of menu. It can be:
     *  0 = "day" = Menu of the day
     *  1 = "fixed" = Fixed menu
     *  2 = "fixed options" = Fixed menu with options
     *  3 = "complete" = Complete menu
     * @return Textual representation of the type of menu
     */
    public String getTypeString(){
        switch(this.type){
            case 0:
                return "day"; //Menu of the day
            case 1:
                return "fixed";
            case 2:
                return "fixed options"; //Menu fixed with options
            case 3:
                return "complete"; //Complete menu
            default:
                return "ERROR";
        }
    }

    /**
     *
     * @return The integer associated with the menu type
     */
    public int getType(){return this.type;}

    /**
     *
     * @return The list of all the courses in the menu
     */
    public ArrayList<Course> getCourses(){ return this.courses;}

    /**
     * This method returns a particular course, given its ID.
     * @param id The id of the course
     * @return The requested course or null if nothing is found.
     */
    public Course getCourseByID(int id){
        for(Course course : this.courses)
            if(course.getCid() == id)
                return course;
        return null;
    }

    /**
     * This method returns a particular course, given its name.
     * If the course is not found, it returns null.
     * @param name Name of the course to search for.
     * @return The requested course or null if nothing is found.
     */
    public Course getCourseByName(String name){
        for(Course course : this.courses)
            if(course.getName().equals(name))
                return course;
        return null;
    }

    /**
     * This method returns the list of all gluten-free courses
     * @return The list of gluten-free courses
     */
    public ArrayList<Course> getGlutenFreeCourses(){
        ArrayList<Course> output = new ArrayList<Course>();
        for(Course course: this.courses){
            if(course.isGlutenFree())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the vegan courses.
     * @return The list of vegan courses
     */
    public ArrayList<Course> getVeganCourses(){
        ArrayList<Course> output = new ArrayList<Course>();
        for(Course course: this.courses){
            if(course.isVegan())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the vegetarian courses.
     * @return The list of vegetarian courses
     */
    public ArrayList<Course> getVegetarianCourses(){
        ArrayList<Course> output = new ArrayList<Course>();
        for(Course course: this.courses){
            if(course.isVegetarian())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the spicy courses.
     * @return The list of spicy courses
     */
    public ArrayList<Course> getSpicyCourses(){
        ArrayList<Course> output = new ArrayList<Course>();
        for(Course course: this.courses)
            if(course.isSpicy())
                output.add(course);

        return output;
    }

    /**
     * Returns true if the menu can be bought with tickets. False otherwise.
     * @return true or false
     */
    public boolean acceptTicket(){ return this.ticket; }

    /**
     *
     * @param mid The ID of the menu
     */
    public void setMid(int mid){ this.mid = mid;}

    /**
     *
     * @param name The name of the menu
     */
    public void setName(String name){ this.name = name;}

    /**
     *
     * @param description The description of the menu
     */
    public void setDescription(String description){ this.description = description;}

    /**
     *
     * @param price The price of the menu
     */
    public void setPrice(float price){ this.price = price;}

    /**
     * Sets the base 64 encoding of the image
     * @param image Byte array of image, encoded in base 64
     */
    public void setImageUri(Uri image){ this.image = image;}

    /**
     * This method sets the type of the menu. The type can be:
     *  0 = Menu of the day
     *  1 = Fixed menu
     *  2 = Fixed menu with options
     *  3 = Complete menu
     *
     *  Returns true in case of success, false otherwise.
     *
     * @param type The type of the menu, must be between 0 and 3
     * @return true or false.
     */
    public boolean setType(int type){
        switch(type){
            case 0:
                this.type = 0;
                return true;
            case 1:
                this.type = 1;
                return true;
            case 2:
                this.type = 2;
                return true;
            case 3:
                this.type = 3;
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param courses The list of courses of this menu
     */
    public void setCourses(ArrayList<Course> courses){ this.courses = courses;}

    /**
     *
     * @param v true or false.
     */
    public void setTicket(boolean v){ this.ticket = v;}

    /**
     *
     * @param numberchoice set the number of multiple choice menu
     */
    public void setNumberchoice(int numberchoice){ this.numberchoice=numberchoice; }

    /**
     *
     * @return the number of multiple choice menu
     */
    public int getNumberchoice(){ return this.numberchoice; }

    public boolean isBeverage() {
        return beverage;
    }

    public void setBeverage(boolean beverage) {
        this.beverage = beverage;
    }

    public boolean isServicefee() {
        return servicefee;
    }

    public void setServicefee(boolean servicefee) {
        this.servicefee = servicefee;
    }
}
