package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.DataManagerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * @author Marco Ardizzone
 * @class Restaurant
 * @date 2016-04-17
 * @brief Restaurant class
 */
public class DataManager {

    transient private Context appContext;

    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private ArrayList<Menu> menus = new ArrayList<>();
    private ArrayList<Course> courses = new ArrayList<>();

    /**
     * Create a new DataManager. Requires, as parameter, the Android Application Context to open
     * the JSON files.
     * In case of fail, the error is logged and an exception is thrown.
     *
     * @param c Android Application Context
     * @throws DataManagerException If read of the single JSON restaurant file fails.
     */
    public DataManager(Context c) throws DataManagerException {
        this.appContext = c;
        this.readFiles();
    }

    /**
     * Reads the list of files in the internal storage and compiles an array of File objects.
     *
     * @throws DataManagerException If reading fails.
     */
    private void readFiles() throws DataManagerException {
        final String METHOD_NAME = this.getClass().getName()+" - readFiles";

        File filesDir = this.appContext.getFilesDir();
        File[] fileList = filesDir.listFiles();
        for(File f : fileList)
            this.setRestaurant(f.getName());
    }

    /**
     * Given the name of file, this method tries to understand if it's a JSON restaurant file.
     * If so, a new Restaurant object is created by using the ID coded in the filename.
     * The created object is put inside an ArrayList to ease the search among restaurants.
     * In case of fail, the error is logged and an exception is thrown.
     *
     * @param name Filename
     * @throws DataManagerException If setting Restaurant object creation fails.
     */
    private void setRestaurant(String name) throws DataManagerException {
        final String METHOD_NAME = this.getClass().getName()+" - setRestaurant";
        String regex = "^(r)(\\d+)(\\.json)";
        Pattern restaurantFilenamePattern = Pattern.compile(regex);
        Matcher m = restaurantFilenamePattern.matcher(name);
        if(m.matches()){
            Integer intObj = Integer.valueOf(m.group(2));
            try {
                Restaurant r = new Restaurant(this.appContext, intObj);
                this.restaurants.add(r);
                this.setMenus(r);
            } catch (RestaurantException e) {
                Log.e(METHOD_NAME,e.getMessage());
                throw new DataManagerException("Error on file "+
                        name+"\n"+
                        e.getMessage());
            }
        }
    }

    /**
     * Given a Restaurant object, this method adds all the menus of that Restaurant to an ArrayList
     * to ease the search among menus.
     *
     * @param r Restaurant object
     */
    private void setMenus(Restaurant r){
        for (Menu m : r.getMenus()) {
            this.menus.add(m);
            this.setCourses(m);
        }
    }

    /**
     * Given a Menu object, this method adds all the courses of that Menu to an ArrayList
     * to ease the search among courses.
     *
     * @param m Menu object
     */
    private void setCourses(Menu m){
        for(Course c : m.getCourses())
            this.courses.add(c);
    }

    /**
     * Get the list of all restaurants. The returned ArrayList may be empty.
     *
     * @return ArrayList of Restaurant(s)
     */
    public ArrayList<Restaurant> getRestaurants(){
        return this.restaurants;
    }

    /**
     * Get a Restaurant object corresponding to the supplied ID.
     * Returns null if no restaurant matched that ID.
     *
     * @param rid Restaurant ID
     * @return Restaurant object or null if no match.
     */
    public Restaurant getRestaurantByID(int rid){
        for(Restaurant r : this.restaurants)
            if(r.getRid() == rid)
                return r;
        return null;
    }

    /**
     * Get an ArrayList of Restaurant(s) matching a given name.
     * If no restaurant matched the supplied name, an empty ArrayList is returned.
     *
     * @param restaurantName Name to look for.
     * @return The ArrayList of the restaurants matching the supplied name.
     */
    public ArrayList<Restaurant> getRestaurantsByName(String restaurantName){
        ArrayList<Restaurant> returnRes = new ArrayList<>();
        for(Restaurant r : this.restaurants)
            if(r.getName().equals(restaurantName))
                returnRes.add(r);
        return returnRes;
    }

    /**
     * Get an ArrayList of Menu(s). The returned ArrayList may be empty.
     * @return ArrayList of Menu objects
     */
    public ArrayList<Menu> getMenus(){
        return this.menus;
    }

    /**
     * Get a Menu object corresponding to the supplied ID.
     * Returns null if no Menu object matches the supplied ID.
     *
     * @param mid ID to look for
     * @return The Menu object matching the ID or null if no match is found.
     */
    public Menu getMenuByID(int mid){
        for(Menu m : this.menus)
            if(m.getMid() == mid)
                return m;
        return null;
    }

    /**
     * Get an ArrayList of Menu objects matching the supplied menu name.
     * If no Menu object matches the input name, an empty ArrayList is returned.
     *
     * @param menuName The menu name to look for
     * @return An ArrayList of Menu objects
     */
    public ArrayList<Menu> getMenusByName(String menuName){
        ArrayList<Menu> returnRes = new ArrayList<>();
        for(Menu m : this.menus)
            if(m.getName().equals(menuName))
                returnRes.add(m);
        return returnRes;
    }

    /**
     * Get an ArrayList of Course objects. The returned ArrayList may be empty.
     * @return
     */
    public ArrayList<Course> getCourses(){
        return this.courses;
    }

    /**
     * Get the Course matching the supplied Course ID.
     * If no Course object matches the input ID, the method returns null.
     *
     * @param cid Course ID to look for.
     * @return Course object or null if no match is found.
     */
    public Course getCourseByID(int cid){
        for(Course c : this.courses)
            if(c.getCid() == cid)
                return c;
        return null;
    }

    /**
     * Get an ArrayList of Course objects matching a supplied name.
     * If no Course object matches the input name, an empty ArrayList is returned.
     *
     * @param courseName The course name to look for.
     * @return An ArrayList of Course objects.
     */
    public ArrayList<Course> getCoursesByName(String courseName){
        ArrayList<Course> returnRes = new ArrayList<>();
        for(Course c : this.courses)
            if(c.getName().equals(courseName))
                returnRes.add(c);
        return returnRes;
    }
}
