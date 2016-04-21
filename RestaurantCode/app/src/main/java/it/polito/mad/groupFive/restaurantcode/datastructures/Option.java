package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OptionException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * @author Marco Ardizzone
 * @class Option
 * @date 2016-04-20
 * @brief Option class
 */
public class Option {

    transient private Restaurant r = null;

    private ArrayList<Course> courses = new ArrayList<>();
    private int optID;

    /**
     * Create an instance of Option: requires, as parameter, its restaurant object.
     * The ID of the option is generated automatically.
     *
     * @param r The restaurant object whose this menu belongs to.
     */
    public Option(Restaurant r){
        this.r = r;
        this.optID = Option.randInt();
    }

    /**
     * Create an instance of Option: requires, as parameters, its restaurant object and an integer
     * positive ID to uniquely identifying this object.
     *
     * @param r The restaurant object whose course belongs to
     * @param optID A positive integer unique identifier.
     * @throws OptionException Thrown if mid is negative.
     */
    public Option(Restaurant r, int optID) throws OptionException {
        if(optID < 0)
            throw new OptionException("Option ID must be positive");
        this.optID = optID;
        this.r = r;
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
                return Option.randInt();
            return result;
        }
    }

    /**
     * Fetch the data corresponding to the Option ID of this object from the JSON file.
     * Fetch operations are always performed inside the restaurant object, this is just a call to
     * that method.
     *
     * @throws OptionException If fetch fails
     */
    public void getData() throws OptionException{
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            this.r.getData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new OptionException(e.getMessage());
        }
        for(Menu m :this.r.getMenus()){
            if(m.getOptionByID(this.optID) != null) {
                Option dummy = m.getOptionByID(this.optID);
                this.copyData(dummy);
            }
        }
    }

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy Menu object, on which the JSON data is written to.
     */
    private void copyData(Option dummy) {
        this.courses = dummy.getCourses();
        this.optID = dummy.getOptID();
    }

    /**
     *
     * @return The unique identifier of the Option
     */
    public int getOptID(){ return this.optID; }

    /**
     *
     * @return An ArrayList of courses associated with this Option
     */
    public ArrayList<Course> getCourses(){ return this.courses; }

    /**
     * This method returns a particular course, given its ID.
     *
     * @param cid The id of the course
     * @return The requested course or null if nothing is found.
     */
    public Course getCourseByID(int cid){
        for(Course c : this.courses)
            if(c.getCid() == cid)
                return c;
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
     *
     * @param optID A positive integer unique identifier.
     * @throws OptionException Thrown if option id is negative.
     */
    public void setOptID(int optID) throws OptionException {
        if(optID < 0)
            throw new OptionException("Option ID must be positive");
        this.optID = optID;
    }

    /**
     *
     * @param courses The list of courses of this menu
     */
    public void setCourses(ArrayList<Course> courses){ this.courses = courses; }

}
