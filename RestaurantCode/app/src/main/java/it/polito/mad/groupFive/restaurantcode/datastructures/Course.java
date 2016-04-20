package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.net.Uri;
import android.os.Build;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

    transient private Restaurant r=null;

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

    /**
     * Create an instance of Course: requires, as parameter, its restaurant object.
     * The ID of the course is generated automatically.
     *
     * @param restaurant The restaurant object whose course belongs to.
     */
    public Course(Restaurant restaurant){
        this.r = restaurant;
        this.cid = Course.randInt();
    }

    /**
     * Create an instance of Course: requires, as parameters, its restaurant object and an integer
     * positive ID to uniquely identify this object.
     *
     * @param restaurant The restaurant object whose course belongs to
     * @param cid A positive integer unique identifier.
     * @throws CourseException Thrown if cid is negative.
     */
    public Course(Restaurant restaurant, int cid) throws CourseException {
        if(cid < 0)
            throw new CourseException("Course ID must be positive");
        this.cid = cid;
        this.r = restaurant;
    }

    /**
     * Generate a random integer in the range [1, Integer.MAX_VALUE]
     * @return
     */
    private static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            return rand.nextInt(Integer.MAX_VALUE -1 );
        }
    }

    /**
     * Fetch the data corresponding to this Course ID from the JSON file. Fetch operations are
     * always performed inside the restaurant object, this is just a call to that method.
     *
     * @throws RestaurantException If fetch fails inside restaurant object
     */
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

    /**
     * Copy all the data took from the JSON file on this object.
     * @param d A dummy Course object, on which the JSON data is written to.
     */
    private void copyData(Course d){
        this.cid = d.getCid();
        this.mid = d.getMid();
        this.name = d.getName();
        this.description = d.getDescription();
        this.price = d.getPrice();
        this.image = d.getImageUri();
        this.glutenFree = d.isGlutenFree();
        this.vegan = d.isVegan();
        this.vegetarian = d.isVegetarian();
        this.spicy = d.isSpicy();
    }

    /**
     *
     * @return The Course ID
     */
    public int getCid(){ return this.cid;}

    /**
     *
     * @return The ID of the menu whose this course belongs to.
     */
    public int getMid(){ return this.mid; }

    /**
     *
     * @return The name of the course
     */
    public String getName(){ return this.name; }

    /**
     *
     * @return The description of the course
     */
    public String getDescription(){ return this.description; }

    /**
     *
     * @return The price of the course
     */
    public float getPrice(){ return this.price; }

    /**
     *
     * @return The Uri of the course image
     */
    public Uri getImageUri(){ return this.image; }

    /**
     * Returns true if course is gluten-free. False otherwise.
     * @return true or false
     */
    public boolean isGlutenFree(){ return this.glutenFree; }

    /**
     * Returns true if course is vegan. False otherwise.
     * @return true or false
     */
    public boolean isVegan(){ return this.vegan; }

    /**
     * Returns true if course is vegetarian. False otherwise.
     * @return true or false
     */
    public boolean isVegetarian(){ return this.vegetarian; }

    /**
     * Returns true if course is spicy. False otherwise.
     * @return true or false
     */
    public boolean isSpicy(){ return this.spicy; }

    /**
     * Sets the Course ID
     * @param cid Course ID
     * @throws CourseException if Course ID is negative.
     */
    public void setCid(int cid) throws CourseException {
        if(cid < 0)
            throw new CourseException("Course ID must be positive");
        this.cid = cid;
    }

    /**
     * Sets the Menu ID of the Menu whose this course belongs to.
     * @param mid Menu ID
     * @throws CourseException if Menu ID is negative.
     */
    public void setMid(int mid) throws CourseException {
        if(mid < 0)
            throw new CourseException("Menu ID must be positive");
        this.mid = mid;
    }

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
     *
     * @param uri A string representing the Uri of the image
     */
    public void setImageUriFromString(String uri){ this.image = Uri.parse(uri); }

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
