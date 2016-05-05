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

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * @author Marco Ardizzone
 * @class Menu
 * @date 2016-04-18
 * @brief Menu class
 */
public class Menu {

    transient private Restaurant r=null;

    private int mid;
    private String name=null;
    private String description=null;
    private float price;
    private byte[] image;
    private int type;
    private int choiceAmount;
    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<Option> options = new ArrayList<>();
    private boolean ticket;
    private boolean beverage;
    private boolean serviceFee;
    private int rid;

    /**
     * Create an instance of Menu: requires, as parameter, its restaurant object.
     * The ID of the menu is generated automatically.
     *
     * @param restaurant The restaurant object whose this menu belongs to.
     * @throws MenuException If menu id is negative
     */
    public Menu(Restaurant restaurant) throws MenuException {
        this(restaurant, Menu.randInt());
    }

    /**
     * Create an instance of Menu: requires, as parameters, its restaurant object and an integer
     * positive ID to uniquely identifying this object.
     *
     * @param restaurant The restaurant object whose course belongs to
     * @param mid A positive integer unique identifier.
     * @throws MenuException If menu id is negative
     */
    public Menu(Restaurant restaurant, int mid) throws MenuException {
        this.r = restaurant;
        if(mid < 0)
            throw new MenuException("Menu ID must be positive");
        this.mid = mid;
        try {
            r.getData();
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        this.rid=r.getRid();
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
                return Menu.randInt();
            return result;
        }
    }

    /**
     * Fetch the data corresponding to the Menu ID of this object from the JSON file.
     * Fetch operations are always performed inside the restaurant object, this is just a call to
     * that method.
     *
     * @throws MenuException If fetch fails
     */
    public void getData() throws MenuException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            this.r.getData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new MenuException(e.getMessage());
        }
        Menu dummy = null;
        try {
            dummy = this.r.getMenuByID(this.mid);
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new MenuException(e.getMessage());
        }
        this.copyData(dummy);
    }

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy Menu object, on which the JSON data is written to.
     */
    private void copyData(Menu dummy) {
        this.mid = dummy.getMid();
        this.name = dummy.getName();
        this.description = dummy.getDescription();
        this.price = dummy.getPrice();
        this.image = dummy.getImageByteArray();
        this.type = dummy.getType();
        this.choiceAmount = dummy.getChoiceAmount();
        this.courses = dummy.getCourses();
        this.ticket = dummy.acceptTicket();
        this.beverage = dummy.isBeverage();
        this.serviceFee = dummy.isServiceFee();
        this.options = dummy.getOptions();

    }

    /**
     *
     * @return The ID
     */
    public int getMid(){ return this.mid; }

    /**
     *
     * @return The name of the menu
     */
    public String getName(){ return this.name; }

    /**
     *
     * @return The description of the menu
     */
    public String getDescription(){ return this.description; }

    /**
     *
     * @return The price of the menu
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
     * This method returns a textual representation of the type of menu. It can be:
     *  0 = "day" = Menu of the day
     *  1 = "fixed" = Fixed menu
     *  2 = "fixed options" = Fixed menu with options
     *  3 = "complete" = Complete menu
     * @return Textual representation of the type of menu
     */
    public String getTypeAsString(){
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
    public int getType(){ return this.type; }

    /**
     *
     * @return The list of all the courses in the menu
     */
    public ArrayList<Course> getCourses(){ return this.courses; }

    /**
     * This method returns a particular course, given its ID.
     *
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
     *
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
     * This method returns the list of all gluten-free courses.
     *
     * @return The list of gluten-free courses
     */
    public ArrayList<Course> getGlutenFreeCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses){
            if(course.isGlutenFree())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the vegan courses.
     *
     * @return The list of vegan courses
     */
    public ArrayList<Course> getVeganCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses){
            if(course.isVegan())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the vegetarian courses.
     *
     * @return The list of vegetarian courses
     */
    public ArrayList<Course> getVegetarianCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses){
            if(course.isVegetarian())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the spicy courses.
     *
     * @return The list of spicy courses
     */
    public ArrayList<Course> getSpicyCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses)
            if(course.isSpicy())
                output.add(course);

        return output;
    }

    /**
     * Returns true if the menu can be bought with tickets. False otherwise.
     *
     * @return true or false
     */
    public boolean acceptTicket(){ return this.ticket; }

    /**
     *
     * @return The number of multiple choice menu
     */
    public int getChoiceAmount(){ return this.choiceAmount; }

    /**
     *
     * @return True if beverage is included, false otherwise.
     */
    public boolean isBeverage() {
        return beverage;
    }

    /**
     *
     * @return True if there is an extra fee to pay for the service, false otherwise.
     */
    public boolean isServiceFee() {
        return serviceFee;
    }

    /**
     *
     * @return An ArrayList with all the options for this menu.
     */
    public ArrayList<Option> getOptions(){ return this.options; }

    /**
     * Looks for an Option object with the given ID in the set of available Options.
     * If a match is found, the corresponding Option object is returned, otherwise the method returns
     * null.
     *
     * @param optID The ID to look for.
     * @return Option object if successful, null otherwise
     */
    public Option getOptionByID(int optID) {
        for(Option o : this.options)
            if(o.getOptID() == optID)
                return o;
        return null;
    }

    /**
     * Returns the tags associated to all the Courses in this menu. If no tags are added to the
     * Course objects of this Menu object, the returned ArrayMap is empty.
     *
     * @return An ArrayList of Strings representing the tag.
     */
    public ArrayList<String> getTags(){
        ArrayList<String> output = new ArrayList<>();
        for(Course c : this.courses)
            if(c.getTags().size() != 0)
                output.addAll(c.getTags());
        return output;
    }

    /**
     *
     * @param mid The ID of the menu
     * @throws MenuException if menu id is negative.
     */
    public void setMid(int mid) throws MenuException {
        if(mid < 0)
            throw new MenuException("Menu ID must be positive");
        this.mid = mid;
    }

    /**
     *
     * @param name The name of the menu
     */
    public void setName(String name){ this.name = name; }

    /**
     *
     * @param description The description of the menu
     */
    public void setDescription(String description){ this.description = description; }

    /**
     *
     * @return Restaurant reference to fetch data
     */

    public int getRid() {
        return rid;
    }

    /**
     *
     * @param price The price of the menu

     */
    public void setPrice(float price){ this.price = price; }

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
    public void setCourses(ArrayList<Course> courses){ this.courses = courses; }

    /**
     *
     * @param v true or false.
     */
    public void setTicket(boolean v){ this.ticket = v; }

    /**
     *
     * @param choiceAmount The number of multiple choice menu
     */
    public void setChoiceAmount(int choiceAmount){ this.choiceAmount=choiceAmount; }

    /**
     * Sets if beverage is included.
     *
     * @param beverage true or false
     */
    public void setBeverage(boolean beverage) {
        this.beverage = beverage;
    }

    /**
     * Sets if there is an extra fee to pay for the service.
     *
     * @param serviceFee true or false
     */
    public void setServiceFee(boolean serviceFee) { this.serviceFee = serviceFee; }

    /**
     *
     * @param opts An ArrayList of options to set on this menu.
     */
    public void setOptions(ArrayList<Option> opts){ this.options = opts; }
}
