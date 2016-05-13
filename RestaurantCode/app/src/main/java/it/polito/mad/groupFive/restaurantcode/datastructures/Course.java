package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
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
    private byte[] image;
    private boolean glutenFree;
    private boolean vegan;
    private boolean vegetarian;
    private boolean spicy;
    private ArrayList<String> tags = new ArrayList<>();

    /**
     * Create an instance of Course: requires, as parameter, its restaurant object.
     * The ID of the course is generated automatically.
     *
     * @param restaurant The restaurant object whose this course belongs to.
     * @throws CourseException If course id is negative.
     */
    public Course(Restaurant restaurant) throws CourseException {
        this(restaurant,Course.randInt());
    }

    /**
     * Create an instance of Course: requires, as parameters, its restaurant object and an integer
     * positive ID to uniquely identifying this object.
     *
     * @param restaurant The restaurant object whose course belongs to
     * @param cid A positive integer unique identifier.
     * @throws CourseException If course id is negative.
     */
    public Course(Restaurant restaurant, int cid) throws CourseException {
        if(cid < 0)
            throw new CourseException("Course ID must be positive");
        this.cid = cid;
        this.r = restaurant;
    }

    /**
     * Generate a random integer in the range [1, Integer.MAX_VALUE]
     * @return In integer in the range [1, Integer.MAX_VALUE]
     */
    private static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            int result;
            if((result=rand.nextInt(Integer.MAX_VALUE)) == 0)
                return Course.randInt();
            return result;
        }
    }

    /**
     * Fetch the data corresponding to the Course ID of this object from the JSON file.
     * Fetch operations are always performed inside the restaurant object, this is just a call to
     * that method.
     *
     * @throws CourseException If fetch fails
     */
    public void getData() throws CourseException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            this.r.getData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new CourseException(e.getMessage());
        }
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
        this.image = d.getImageByteArray();
        this.glutenFree = d.isGlutenFree();
        this.vegan = d.isVegan();
        this.vegetarian = d.isVegetarian();
        this.spicy = d.isSpicy();
        this.tags = d.getTags();
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
     * @return The byte representation of the image
     */
    public byte[] getImageByteArray() {
        return this.image;
    }

    /**
     * Returns the Bitmap of the image.
     * If you can, use getImageByteArray instead of this one as it is more efficient.
     *
     * @return The Bitmap representing the image.
     */
    public Bitmap getImageBitmap(){
        final String METHOD_NAME = this.getClass().getName()+" - getImageBitmap";
        return BitmapFactory.decodeByteArray(this.image,0,this.image.length);
    }

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
     * Returns the tags assigned to this Course object.
     *
     * @return An ArrayList of Strings representing the tag.
     */
    public ArrayList<String> getTags(){ return this.tags; }

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
     *
     * @param image Byte array representing the image
     */
    public void setImageFromByteArray(byte[] image){ this.image = image; }

    /**
     * Sets the byte array representation of the image from a given input Bitmap.
     * If you can, use setImageFromByteArray instead of this one as it is more efficient.
     *
     * @param image Bitmap representing the image
     */
    public void setImageFromBitmap(Bitmap image){
        final String METHOD_NAME = this.getClass().getName()+" - setImageFromBitmap";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, output);
        this.image = output.toByteArray();
    }

    /**
     * Sets the byte array representation of the image from a given input Drawable. The drawable
     * is first converted to a Bitmap and then setImageFromBitmap is called.
     * If you can, use setImageFromByteArray instead of this one as it is more efficient.
     *
     * @param image Drawable representing the image
     */
    public void setImageFromDrawable(Drawable image){
        Bitmap bitmap = null;

        if (image instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) image;
            if(bitmapDrawable.getBitmap() != null) {
                this.setImageFromBitmap(bitmapDrawable.getBitmap());
            }
        }

        if(image.getIntrinsicWidth() <= 0 || image.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(image.getIntrinsicWidth(), image.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        image.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        image.draw(canvas);
        this.setImageFromBitmap(bitmap);
    }

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

    /**
     * Sets the tags of the Course object.
     *
     * @param tags An ArrayList of Strings representing the tag.
     */
    public void setTags(ArrayList<String> tags){ this.tags = tags; }

}
