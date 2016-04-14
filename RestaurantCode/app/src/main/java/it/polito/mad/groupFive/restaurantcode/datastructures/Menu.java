package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

/**
 * @author Marco
 * @class Menu
 * @date 04/04/16
 * @brief Menu class
 */
public class Menu {

    private JSONObject JSONFile = null;

    private int mid;
    private String name=null;
    private String description=null;
    private float price;
    private byte[] image=null;
    private int type;
    private int numberchoice;
    private ArrayList<Course> courses=null;
    private boolean ticket;
    private Restaurant r=null;
    private boolean beverage;
    private boolean servicefee;

    public Menu(Restaurant restaurant){
        this.r = restaurant;
        this.JSONFile = restaurant.getJSONFile();
        this.courses = new ArrayList<Course>();
    }

    /**
     *
     * @param restaurant Restaurant instance.
     * @param mid The ID of the menu.
     * @throws MenuException Thrown if menu ID is negative.
     */
    public Menu(Restaurant restaurant, int mid) throws MenuException {
        this.r = restaurant;
        this.JSONFile = restaurant.getJSONFile();
        if(mid < 0)
            throw new MenuException("Menu ID must be positive");
        this.mid = mid;
        this.courses = new ArrayList<Course>();
    }

    /**
     * Reads data from JSON configuration file.
     * If some field is missing, it throws JSONException.
     * Please note that course objects read like this are just filled with their
     * own id. The other data must be filled through the methods provided in the Course class.
     *
     * @throws JSONException if some field is missing.
     */
    public void getData() throws JSONException, CourseException {
        JSONArray menus = this.JSONFile.getJSONArray("menus");
        for (int i = 0; i < menus.length(); i++) {
            if(menus.getJSONObject(i).getInt("id") == this.mid){
                JSONObject jsonMenu = menus.getJSONObject(i);
                this.name = jsonMenu.getString("name");
                this.description = jsonMenu.getString("description");
                this.price = (float) jsonMenu.getDouble("price");
                //this.image = jsonMenu.getString("image").getBytes();
                this.type = jsonMenu.getInt("type");
                this.ticket = jsonMenu.getBoolean("ticket");
                this.numberchoice = jsonMenu.getInt("numberchoice");
                this.beverage = jsonMenu.getBoolean("beverage");
                this.servicefee = jsonMenu.getBoolean("servicefee");

                JSONArray courses = jsonMenu.getJSONArray("courses");
                for (int j = 0; j < courses.length(); j++){
                    Course c = new Course(this.r, courses.getJSONObject(j).getInt("id"), this.mid);
                    c.getData();
                    this.courses.add(c);
                }
                break; //Exit from the outer loop
            }
        }
    }

    /**
     * THIS METHOD SHOULD NEVER BE CALLED ON IT'S OWN!
     * Returns a JSONObject to saveData method of Restaurant class.
     *
     * @throws JSONException
     */
    public JSONObject saveData() throws JSONException{
        JSONObject menu = new JSONObject();
        menu.put("id",this.mid);
        menu.put("name",this.name);
        menu.put("description",this.description);
        menu.put("price",this.price);
       // menu.put("image",this.image.toString());
        menu.put("type",this.type);
        menu.put("ticket",this.ticket);
        menu.put("numberchoice",this.numberchoice);
        menu.put("beverage",this.beverage);
        menu.put("servicefee",this.servicefee);
        //Add a new array for the courses, and fill it!
        menu.put("courses",new JSONArray());
        for(Course c : this.courses)
            menu.getJSONArray("courses").put(c.saveData());

        return menu;
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
     * @return The image of the course, in base 64 format
     */
    public byte[] getImage64(){return this.image;}

    /**
     *
     * @return The image of the course, in Bitmap format
     */
    public Bitmap getImageBitmap(){
        return BitmapFactory.decodeByteArray(this.image, 0, this.image.length);
    }

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
    public void setImage64(byte[] image){ this.image = image;}

    /**
     * Sets the base 64 encoding of the image from an input Bitmap
     * @param image Bitmap image to save
     */
    public void setImage64FromBitmap(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        this.image = baos.toByteArray();
    }

    /**
     * Sets the base 64 encoding of the image from an input Drawable
     * @param image Drawable image to save
     */
    public void setImage64FromDrawable(Drawable image){
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        this.setImage64FromBitmap(b);
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
