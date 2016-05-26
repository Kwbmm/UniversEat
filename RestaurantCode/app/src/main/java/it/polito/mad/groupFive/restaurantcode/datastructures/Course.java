package it.polito.mad.groupFive.restaurantcode.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;

/**
 * @author Marco Ardizzone
 * @class Course
 * @date 2016-04-18
 * @brief Course class
 */
public class Course {

    private String cid;
    private String mid;
    private String name;
    private boolean glutenFree;
    private boolean vegan;
    private boolean vegetarian;
    private boolean spicy;
    private ArrayList<String> tags = new ArrayList<>();

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * Create a new Course object.
     *
     * @param mid The Menu ID to which this Course belongs to.
     * @param cid The ID of this Course
     * @throws CourseException If Menu ID or Course ID are null
     */
    public Course(String mid, String cid) throws CourseException {
        if(mid == null)
            throw new CourseException("Menu ID cannot be null");
        if(cid == null)
            throw new CourseException("Course ID cannot be null");
        this.mid = mid;
        this.cid = cid;

    }
    public Course(){

    }

    /**
     * Creates a Map of this Object, ready to be put as value inside Firebase DB.
     *
     * @return A Map representing this object.
     */
    public Map<String,Object> toMap(){
        HashMap<String, Object> output = new HashMap<>();
        output.put("cid",this.cid);
        output.put("mid",this.mid);
        output.put("name",this.name);
        output.put("glutenFree",this.glutenFree);
        output.put("vegan",this.vegan);
        output.put("vegetarian",this.vegetarian);
        output.put("spicy",this.spicy);

        HashMap<String, Boolean> tagMap = new HashMap<>();
        for(String tag : this.tags){
            tagMap.put(tag,true);
        }
        output.put("tags",tagMap);
        return output;
    }

    /**
     *
     * @return The Course ID
     */
    public String getCid(){ return this.cid;}

    /**
     *
     * @return The ID of the menu whose this course belongs to.
     */
    public String getMid(){ return this.mid; }

    /**
     *
     * @return The name of the course
     */
    public String getName(){ return this.name; }

    /**
     * Returns true if course is gluten-free. False otherwise.
     *
     * @return true or false
     */
    public boolean isGlutenFree(){ return this.glutenFree; }

    /**
     * Returns true if course is vegan. False otherwise.
     *
     * @return true or false
     */
    public boolean isVegan(){ return this.vegan; }

    /**
     * Returns true if course is vegetarian. False otherwise.
     *
     * @return true or false
     */
    public boolean isVegetarian(){ return this.vegetarian; }

    /**
     * Returns true if course is spicy. False otherwise.
     *
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
     * Sets the name of the course
     *
     * @param name Name of the course
     */
    public Course setName(String name){ this.name = name; return this;
    }

    /**
     * Sets if the course is gluten-free
     *
     * @param gf true or false
     */
    public Course setGlutenFree(boolean gf){ this.glutenFree = gf; return this;}

    /**
     * Sets if the course is vegan
     *
     * @param vegan true or false
     */
    public Course setVegan(boolean vegan){ this.vegan = vegan; return this;}

    /**
     * Sets if the course is vegetarian
     *
     * @param vegetarian true or false
     */
    public Course setVegetarian(boolean vegetarian){ this.vegetarian = vegetarian; return this;}

    /**
     * Sets if the course is spicy
     *
     * @param spicy true or false
     */
    public Course setSpicy(boolean spicy){ this.spicy = spicy; return this;}

    /**
     * Sets the tags of the Course object.
     *
     * @param tags An ArrayList of Strings representing the tag.
     */
    public Course setTags(ArrayList<String> tags){ this.tags = tags; return this;}
}
