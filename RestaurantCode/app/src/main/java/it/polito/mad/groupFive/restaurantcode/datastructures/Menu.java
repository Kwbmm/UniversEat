package it.polito.mad.groupFive.restaurantcode.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

/**
 * @author Marco Ardizzone
 * @class Menu
 * @date 2016-04-18
 * @brief Menu class
 */
public class Menu {

    private String rid; //The restaurant ID this menu belongs to
    private String mid;
    private String name;
    private String description;
    private float price;
    private String imageLocalPath;
    private int type;
    private boolean beverage;
    private boolean serviceFee;
    private ArrayList<Course> courses = new ArrayList<>();

    /**
     * Creates a new Menu object.
     *
     * @param rid The restaurant ID to which this menu belongs to.
     * @param mid The ID of this Menu object.
     * @throws MenuException If restaurant ID or menu ID are null.
     */
    public Menu(String rid, String mid) throws MenuException {
        if(rid == null)
            throw new MenuException("Restaurant ID cannot be null");
        if(mid == null)
            throw new MenuException("Menu ID cannot be null");
        this.rid = rid;
        this.mid = mid;
    }
    public Menu(String mid) throws MenuException {
        if(mid == null)
            throw new MenuException("Menu ID cannot be null");
        this.mid = mid;
    }

    /**
     * Creates a Map of this Object, ready to be put as value inside Firebase DB.
     *
     * @return A Map representing this object.
     */
    public Map<String, Object> toMap(){
        final String METHOD_NAME = this.getClass().getName()+" - saveData";

        HashMap<String, Object> output = new HashMap<>();
        output.put("mid",this.mid);
        output.put("rid",this.rid);
        output.put("name",this.name);
        output.put("description",this.description);
        output.put("price",this.price);
        output.put("imageLocalPath",this.imageLocalPath);
        output.put("type",this.type);
        output.put("beverage",this.beverage);
        output.put("serviceFee",this.serviceFee);

        HashMap<String,Boolean> tagMap = new HashMap<>();
        for(String tag : this.getTags()){
            tagMap.put(tag,true);
        }
        output.put("tags",tagMap);

        return output;
    }

    /**
     *
     * @return The ID
     */
    public String getMid(){ return this.mid; }

    public String getRid(){ return this.rid; }

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
     * Get the filename of this menu's image.
     * @return The name of the image.
     */
    public String getImageName(){
        String[] arrayString = this.imageLocalPath.split("/");
        return arrayString[arrayString.length-1];
    }

    /**
     * Get the local path of where the menu's image is stored.
     * @return The location of the image.
     */
    public String getImageLocalPath() {
        return this.imageLocalPath;
    }

    /**
     *
     * @return The price of the menu
     */
    public float getPrice(){ return this.price; }

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
    public Course getCourseByID(String id){
        for(Course course : this.courses)
            if(course.getCid().equals(id))
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


    public void setRid(String rid) {
        this.rid = rid;
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
     * @param name The name of the menu
     */
    public Menu setName(String name){ this.name = name; return this;}

    /**
     *
     * @param description The description of the menu
     */
    public Menu setDescription(String description){ this.description = description; return this; }

    /**
     *
     * @param price The price of the menu
     */
    public Menu setPrice(float price){ this.price = price; return this; }

    /**
     * Sets the local path of this menu's image.
     * @param imagePath The local path of the image
     */
    public Menu setImageLocal(String imagePath) {
        this.imageLocalPath = imagePath;
        return this;
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
    public Menu setCourses(ArrayList<Course> courses){ this.courses = courses; return this; }

    /**
     * Sets if beverage is included.
     *
     * @param beverage true or false
     */
    public Menu setBeverage(boolean beverage) {
        this.beverage = beverage; return this;
    }

    /**
     * Sets if there is an extra fee to pay for the service.
     *
     * @param serviceFee true or false
     */
    public Menu setServiceFee(boolean serviceFee) { this.serviceFee = serviceFee; return this; }
}
