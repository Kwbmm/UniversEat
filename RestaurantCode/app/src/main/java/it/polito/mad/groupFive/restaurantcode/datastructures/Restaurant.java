package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.libs.CustomByteArrayAdapter;
import it.polito.mad.groupFive.restaurantcode.libs.CustomUriAdapter;

/**
 * @author Marco Ardizzone
 * @class Restaurant
 * @date 2016-04-17
 * @brief Restaurant class
 */
public class Restaurant {

    private String rid;
    private String uid;
    private String name;
    private String description;
    private String address;
    private String state;
    private String city;
    private String website;
    private String telephone;
    private String zip;
    private String imageLocal;
    private float rating;
    private double xcoord;
    private double ycoord;
    private ArrayList<Menu> menus=new ArrayList<>();
    private ArrayList<Order> orders=new ArrayList<>();
    private Map<String,Boolean> tickets = new HashMap<>();
    private Map<String, Map<String,String>> timetableLunch = new HashMap<>();
    private Map<String, Map<String,String>> timetableDinner = new HashMap<>();
    private ArrayList<Review> reviews = new ArrayList<>();

    public Restaurant(){
    }

    /**
     * Generate a random integer in the range [1, Integer.MAX_VALUE]
     *
     * @return In integer in the range [1, Integer.MAX_VALUE]
     */
    public static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            int result;
            if((result=rand.nextInt(Integer.MAX_VALUE)) == 0)
                return Restaurant.randInt();
            return result;
        }
    }

    public Map<String, Object> toMap() {
        HashMap<String,Object> output = new HashMap<>();
        output.put("rid",this.rid);
        output.put("uid",this.uid);
        output.put("name",this.name);
        output.put("description",this.description);
        output.put("address",this.address);
        output.put("state",this.state);
        output.put("city",this.city);
        output.put("website",this.website);
        output.put("telephone",this.telephone);
        output.put("zip",this.zip);
        output.put("imageLocal",this.imageLocal);
        output.put("rating",this.rating);
        output.put("xcoord",this.xcoord);
        output.put("ycoord",this.ycoord);

        HashMap<String, Object> menuMap = new HashMap<>();
        for (Menu m : this.menus){
            menuMap.put(m.getMid(),m.toMap());
        }
        output.put("menus",menuMap);

        return output;
    }

    /**
     *
     * @return the id of restaurant
     */
    public String getRid() { return this.rid; }

    /**
     *
     * @return The id of the restaurant owner
     */
    public String getUid() { return this.uid; }

    /**
     * @return string Name of restaurant
     */
    public String getName() { return this.name; }

    /**
     *
     * @return The description of restaurant
     */
    public String getDescription() { return this.description; }

    /**
     *
     * @return Address of restaurant
     */
    public String getAddress() { return this.address; }

    /**
     *
     * @return State of restaurant
     */
    public String getState() { return this.state; }

    /**
     *
     * @return City of Restaurant
     */
    public String getCity() { return this.city; }

    /**
     *
     * @return The latitude coordinate of restaurant
     */
    public double getXcoord() { return this.xcoord; }

    /**
     *
     * @return The longitude coordinate of restaurant
     */
    public double getYcoord() { return this.ycoord; }

    /**
     *
     * @return the rating of restaurant
     */
    public float getRating() {
        float value=0,total;
        for (Review rev:reviews){
            value=value+rev.getRating();
        }
        total=value/reviews.size();
        this.rating=total;
        return this.rating; }

    /**
     *
     * @return a string with the restaurant's telephone number
     */
    public String getTelephone() { return this.telephone; }

    /**
     *
     * @return a string with the ZIP code
     */
    public String getZip() { return this.zip; }

    /**
     *
     * @return a string with the restaurant's website
     */
    public String getWebsite() { return this.website; }

    /**
     *
     * @return the arraylist of restaurant's menu
     */
    public ArrayList<Menu> getMenus() { return this.menus; }

    /**
     * The method takes as input an integer representing the menu type as follows:
     *  0: Menu of the day
     *  1: Fixed menu
     *  2: Fixed menu with options
     *  3: Complete menu
     * The method returns an ArrayList of Menus or null if nothing is found.
     *
     * @param type The type of menu, must be an integer between 0 and 3.
     * @return An ArrayList of all the menus of the requested type, or null if nothing is found.
     */
    public ArrayList<Menu> getMenusByType(int type){
        ArrayList<Menu> output = new ArrayList<>();
        for(Menu m : this.menus)
            if(m.getType() == type)
                output.add(m);
        return output.isEmpty() ? null : output;
    }

    /**
     *
     * @param mid The id of the menu to search for.
     * @return The Menu object or null if nothing is found.
     */
    public Menu getMenuByID(String mid) {
        for(Menu m : this.menus)
            if(m.getMid().equals(mid))
                return m;
        return null;
    }

    /**
     *
     * @return the arraylist of restaurant's orders
     */
    public ArrayList<Order> getOrders() { return orders; }

    /**
     *
     * @param oid The id of the order to search for.
     * @return The requested order or null if nothing is found.
     */
    public Order getOrderByID(String oid) {
        for(Order o : this.orders)
            if(o.getOid().equals(oid))
                return o;
        return null;
    }

    /**
     *
     * @param uid The user id who submitted
     * @return An ArrayList of Orders or null if nothing is found.
     */
    public ArrayList<Order> getOrdersByUserID(String uid) {
        ArrayList<Order> output = new ArrayList<>();
        for(Order o : this.orders)
            if(o.getUid().equals(uid))
                output.add(o);
        return output.isEmpty()? null : output;
    }

    /**
     * Returns a Map of tickets, where the Key is the name of the ticket; the value indicates if
     * this ticket is accepted by this restaurant or not.
     *
     * @return Map of String:Bool where String = Ticket Name, Bool = accepted by this restaurant
     */
    public Map<String,Boolean> getTickets(){ return this.tickets; }

    /**
     * Returns an ArrayMap with keys the number of the day of the week (from 0 [Monday] to 6
     * [Sunday]) and values a Duration object representing the shift of the LUNCH.
     *
     * @return Map of the LUNCH timetable.
     */
    public Map<String,Map<String,String>> getTimetableLunch() throws RestaurantException {
        return this.timetableLunch;
    }

    /**
     * Returns an ArrayMap with keys the number of the day of the week (from 0 [Monday] to 6
     * [Sunday]) and values a Duration object representing the shift of the DINNER.
     *
     * @return ArrayMap of the DINNER timetable.
     */
    public Map<String,Map<String,String>> getTimetableDinner() throws RestaurantException {
        return this.timetableDinner;
    }

    /**
     *
     * @return An ArrayList of the Review(s) associated to this restaurant.
     */
    public ArrayList<Review> getReviews() { return this.reviews; }

    /**
     * Returns a Review object corresponding to the supplied review ID.
     * If no Review object matches the input review ID, this method returns null.
     *
     * @param revID The ID corresponding to a Review object
     * @return The Review objectd corresponding to the supplied review ID.
     */
    public Review getReviewByRevID(String revID) {
        for(Review r : this.reviews)
            if(r.getRevID().equals(revID))
                return r;
        return null;
    }

    /**
     * Get an ArrayList of Reviews made by the user matching the specified user id.
     * If the supplied ID returns no results, an empty ArrayList is returned.
     *
     * @param uid A user ID.
     * @return An ArrayList of Review objects matching the specified user id.
     */
    public ArrayList<Review> getReviewsByUserID(String uid) {
        ArrayList<Review> returnRes = new ArrayList<>();
        for(Review r : this.reviews)
            if(r.getUid().equals(uid))
                returnRes.add(r);
        return returnRes;
    }

    /**
     * Returns true if the restaurant is open at the current time, false otherwise.
     * In case of error, a warning is logged and the method returns false.
     *
     * @return True if restaurant is open, false otherewise.
     */
    public boolean isOpen(){
        final String METHOD_NAME = this.getClass().getName()+" - isOpen";
        Calendar now = Calendar.getInstance();
        long nowMS = now.getTimeInMillis();
        int dow = now.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.getDefault());
        try {
            switch (dow){
                case 1:{ //Sunday
                    Date startLunch = sdf.parse(this.timetableLunch.get(6).get("start"));
                    Date endLunch = sdf.parse(this.timetableLunch.get(6).get("end"));
                    Date startDinner = sdf.parse(this.timetableDinner.get(6).get("start"));
                    Date endDinner = sdf.parse(this.timetableDinner.get(6).get("end"));
                    if(nowMS >= startLunch.getTime() && nowMS < endLunch.getTime())
                        return true;
                    if(nowMS >= startDinner.getTime() && nowMS < endDinner.getTime())
                        return true;
                    return false;
                }
                case 2:{ //Monday
                    Date startLunch = sdf.parse(this.timetableLunch.get(0).get("start"));
                    Date endLunch = sdf.parse(this.timetableLunch.get(0).get("end"));
                    Date startDinner = sdf.parse(this.timetableDinner.get(0).get("start"));
                    Date endDinner = sdf.parse(this.timetableDinner.get(0).get("end"));
                    if(nowMS >= startLunch.getTime() && nowMS < endLunch.getTime())
                        return true;
                    if(nowMS >= startDinner.getTime() && nowMS < endDinner.getTime())
                        return true;
                    return false;
                }
                case 3:{ //Tuesday
                    Date startLunch = sdf.parse(this.timetableLunch.get(1).get("start"));
                    Date endLunch = sdf.parse(this.timetableLunch.get(1).get("end"));
                    Date startDinner = sdf.parse(this.timetableDinner.get(1).get("start"));
                    Date endDinner = sdf.parse(this.timetableDinner.get(1).get("end"));
                    if(nowMS >= startLunch.getTime() && nowMS < endLunch.getTime())
                        return true;
                    if(nowMS >= startDinner.getTime() && nowMS < endDinner.getTime())
                        return true;
                    return false;
                }
                case 4:{ //Wednesday
                    Date startLunch = sdf.parse(this.timetableLunch.get(2).get("start"));
                    Date endLunch = sdf.parse(this.timetableLunch.get(2).get("end"));
                    Date startDinner = sdf.parse(this.timetableDinner.get(2).get("start"));
                    Date endDinner = sdf.parse(this.timetableDinner.get(2).get("end"));
                    if(nowMS >= startLunch.getTime() && nowMS < endLunch.getTime())
                        return true;
                    if(nowMS >= startDinner.getTime() && nowMS < endDinner.getTime())
                        return true;
                    return false;
                }
                case 5:{ //Thursday
                    Date startLunch = sdf.parse(this.timetableLunch.get(3).get("start"));
                    Date endLunch = sdf.parse(this.timetableLunch.get(3).get("end"));
                    Date startDinner = sdf.parse(this.timetableDinner.get(3).get("start"));
                    Date endDinner = sdf.parse(this.timetableDinner.get(3).get("end"));
                    if(nowMS >= startLunch.getTime() && nowMS < endLunch.getTime())
                        return true;
                    if(nowMS >= startDinner.getTime() && nowMS < endDinner.getTime())
                        return true;
                    return false;
                }
                case 6:{ //Friday
                    Date startLunch = sdf.parse(this.timetableLunch.get(4).get("start"));
                    Date endLunch = sdf.parse(this.timetableLunch.get(4).get("end"));
                    Date startDinner = sdf.parse(this.timetableDinner.get(4).get("start"));
                    Date endDinner = sdf.parse(this.timetableDinner.get(4).get("end"));
                    if(nowMS >= startLunch.getTime() && nowMS < endLunch.getTime())
                        return true;
                    if(nowMS >= startDinner.getTime() && nowMS < endDinner.getTime())
                        return true;
                    return false;
                }
                case 7:{ //Saturday
                    Date startLunch = sdf.parse(this.timetableLunch.get(5).get("start"));
                    Date endLunch = sdf.parse(this.timetableLunch.get(5).get("end"));
                    Date startDinner = sdf.parse(this.timetableDinner.get(5).get("start"));
                    Date endDinner = sdf.parse(this.timetableDinner.get(5).get("end"));
                    if(nowMS >= startLunch.getTime() && nowMS < endLunch.getTime())
                        return true;
                    if(nowMS >= startDinner.getTime() && nowMS < endDinner.getTime())
                        return true;
                    return false;
                }
            }
        } catch (ParseException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return false;
        }
        Log.w(METHOD_NAME,"Switch not entered, returning wrong value");
        return false;
    }

    public void setRid(String rid){
        this.rid = rid;
    }

    /**
     *
     * @param name Name of restaurant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param address Address of restaurant
     */
    public void setAddress(String address) { this.address = address; }

    /**
     *
     * @param state The state of restaurant
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @param city City of restaurant
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @param xcoord The latitude coordinate of restaurant
     */
    public void setXcoord(double xcoord) {
        this.xcoord = xcoord;
    }

    /**
     *
     * @param ycoord The longitude coordinate of restaurant
     */
    public void setYcoord(double ycoord) {
        this.ycoord = ycoord;
    }

    /**
     *
     * @param description of the restaurant
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @param rating of restaurant
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     *
     * @param menus: the arraylist of restaurant's menu
     */
    public void setMenus(ArrayList<Menu> menus) {
        this.menus = menus;
    }

    /**
     *
     * @param orders:the arraylist of restaurant's orders
     */
    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     *
     * @param website of the restaurant
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     *
     * @param telephone the telephone number of the restaurant
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     *
     * @param zip the zip code of the restaurant's city
     */
    public void setZip(String zip){ this.zip = zip; }

    /**
     *
     * @param tickets A HashMap containing the names of the tickets.
     */
    public void setTickets(Map<String,Boolean> tickets){ this.tickets = tickets; }

    /**
     * Sets the amount of time during which the restaurant is open AT LUNCH.
     * Returns true in case of success, false otherwise.
     * If false is returned, an error message is logged.
     * Parameter dayOfWeek should range between 0 (Monday) and 6(Sunday).
     *
     * @param dayOfWeek Day of the week during which this shift takes place. Must be between 0 and 6
     * @param timeStart Start hour of the shift.
     * @param timeEnd End hour of the shift.
     */
    public void setDurationLunch(String dayOfWeek,String timeStart, String timeEnd) {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationLunch";

        Map<String,String> entry = new HashMap<>();
        entry.put("start",timeStart);
        entry.put("end",timeEnd);
        this.timetableLunch.put(String.valueOf(dayOfWeek),entry);
    }

    /**
     * Sets the amount of time during which the restaurant is open AT DINNER.
     * Returns true in case of success, false otherwise.
     * If false is returned, an error message is logged.
     * Parameter dayOfWeek should range between 0 (Monday) and 6(Sunday).
     *
     * @param dayOfWeek Day of the week during which this shift takes place. Must be between 0 and 6
     * @param timeStart Start hour of the shift.
     * @param timeEnd End hour of the shift.
     */
    public void setDurationDinner(String dayOfWeek,String timeStart, String timeEnd) {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationDinner";

        Map<String,String> entry = new HashMap<>();
        entry.put("start",timeStart);
        entry.put("end",timeEnd);
        this.timetableDinner.put(dayOfWeek,entry);
    }

    /**
     * Set the timetable for lunch time of this restaurant.
     * Input parameter is a Map with key the days of the week (from 0 to 6) and as value an array
     * of Date. The array must be of length 2. It should be structured as:
     * [0] startHour
     * [1] endHour
     * @param timetable A(n) (Array)Map of the timetable of the Lunch.
     */
    public void setTimetableLunch(Map<String,Map<String,String>> timetable){
        this.timetableLunch = timetable;
    }

    /**
     * Set the timetable for dinner time of this restaurant.
     * Input parameter is a Map with key the days of the week (from 0 to 6) and as value an array
     * of Date. The array must be of length 2. It should be structured as:
     * [0] startHour
     * [1] endHour
     * @param timetable A(n) (Array)Map of the timetable of the Dinner.
     */
    public void setTimetableDinner(Map<String,Map<String,String>> timetable){
        this.timetableDinner = timetable;
    }

    /**
     *
     * @param reviews An ArrayList of Review(s) to assign to this restaurant object.
     */
    public void setReviews(ArrayList<Review> reviews){ this.reviews = reviews; }

    public void setImageLocal(String imagePath) {
        this.imageLocal = imagePath;
    }

    public String getImageName(){
        String[] arrayString = this.imageLocal.split("/");
        return arrayString[arrayString.length-1];
    }

    public String getImageLocal() {
        return this.imageLocal;
    }
}
