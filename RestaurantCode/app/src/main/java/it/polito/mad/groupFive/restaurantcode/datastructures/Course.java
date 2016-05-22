package it.polito.mad.groupFive.restaurantcode.datastructures;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;

/**
 * @author Marco Ardizzone
 * @class Course
 * @date 2016-04-18
 * @brief Course class
 */
public class Course {

    private DatabaseReference dbRoot;

    private String cid;
    private String mid;
    private String name;
    private boolean glutenFree;
    private boolean vegan;
    private boolean vegetarian;
    private boolean spicy;
    private ArrayList<String> tags = new ArrayList<>();


    public Course(String mid) throws CourseException {
        this(null,mid);
    }

    public Course(String cid, String mid) throws CourseException {
        if(mid == null){
            throw new CourseException("Menu ID cannot be null");
        }

        this.mid = mid;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference("course");

        this.cid = cid == null ? this.dbRoot.push().getKey() : cid;

        //Change the dbRoot to the tree specific to this object
        this.dbRoot = this.dbRoot.child(this.cid);
    }

    void saveData(){
        this.dbRoot.child("course-id").setValue(this.cid);
        this.dbRoot.child("menu-id").setValue(this.mid);
        this.dbRoot.child("name").setValue(this.name);
        this.dbRoot.child("gluten-free").setValue(this.glutenFree);
        this.dbRoot.child("vegan").setValue(this.vegan);
        this.dbRoot.child("vegetarian").setValue(this.vegetarian);
        this.dbRoot.child("spicy").setValue(this.spicy);
        for(String tag : this.tags){
            this.dbRoot.child("tag").child(tag).setValue(true);
        }
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
     * Sets the name of the course
     * @param name Name of the course
     */
    public void setName(String name){ this.name = name;}

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
