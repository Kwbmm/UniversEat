package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

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
    private String imageLocalPath;
    private float rating;
    private float ratingNumber;
    private double xcoord;
    private double ycoord;
    private ArrayList<Menu> menus=new ArrayList<>();
    private ArrayList<Order> orders=new ArrayList<>();
    private Map<String,Boolean> tickets = new HashMap<>();
    private Map<String, Map<String,String>> timetableLunch = new HashMap<>();
    private Map<String, Map<String,String>> timetableDinner = new HashMap<>();
    private ArrayList<Review> reviews = new ArrayList<>();

    public void setRatingNumber(float ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    public Restaurant(String uid, String rid) throws RestaurantException {
        if(uid == null)
            throw new RestaurantException("User ID cannot be null");
        if(rid == null)
            throw new RestaurantException("Restaurant canno be null");

        this.uid = uid;
        this.rid = rid;
    }

    public Restaurant(String rid) throws RestaurantException {
        if(rid == null)
            throw new RestaurantException("Restaurant canno be null");
        this.rid = rid;

    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    /**
     * Creates a Map of this Object, ready to be put as value inside Firebase DB.
     *
     * @return A Map representing this object.
     */
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
        output.put("imageLocalPath",this.imageLocalPath);
        output.put("rating",this.rating);
        output.put("xcoord",this.xcoord);
        output.put("ycoord",this.ycoord);
        output.put("timetableLunch",this.timetableLunch);
        output.put("timetableDinner",this.timetableDinner);
        output.put("tickets",this.tickets);
        output.put("ratingNumber",this.ratingNumber);

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
     * Get the filename of this restaurant's image.
     * @return The name of the image.
     */
    public String getImageName(){
        String[] arrayString = this.imageLocalPath.split("/");
        return arrayString[arrayString.length-1];
    }

    /**
     * Get the local path of where the restaurant's image is stored.
     * @return The location of the image.
     */
    public String getImageLocalPath() {
        return this.imageLocalPath;
    }

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
    public double getXCoord() { return this.xcoord; }

    /**
     *
     * @return The longitude coordinate of restaurant
     */
    public double getYCoord() { return this.ycoord; }

    /**
     *
     * @return the rating of restaurant
     */
    public float getRating() {
        return rating/ratingNumber;}

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

    public float getRatingNumber() {
        return ratingNumber;
    }

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
    public Map<String,Map<String,String>> getTimetableLunch() {
        return this.timetableLunch;
    }

    /**
     * Returns an ArrayMap with keys the number of the day of the week (from 0 [Monday] to 6
     * [Sunday]) and values a Duration object representing the shift of the DINNER.
     *
     * @return ArrayMap of the DINNER timetable.
     */
    public Map<String,Map<String,String>> getTimetableDinner() {
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
     * @return True if restaurant is open, false otherwise.
     */
    public boolean isOpen(){
        final String METHOD_NAME = this.getClass().getName()+" - isOpen";
        Calendar now = Calendar.getInstance();
        long nowMS = now.getTimeInMillis();
        int dow = now.get(Calendar.DAY_OF_WEEK);
        Calendar startLunch= Calendar.getInstance();
        Calendar endLunch=Calendar.getInstance();
        Calendar startDinner=Calendar.getInstance();
        Calendar endDinner=Calendar.getInstance();
        String time[];
        try {
            switch (dow){
                case 1:{ //Sunday
                    if(this.timetableLunch.containsKey("Sun")) {
                        time = this.timetableLunch.get("Sun").get("start").split(":");
                        startLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        startLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        time = (this.timetableLunch.get("Sun").get("end").split(":"));
                        endLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        endLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        if(nowMS >= startLunch.getTimeInMillis() && nowMS < endLunch.getTimeInMillis())
                            return true;
                    }
                    time = this.timetableDinner.get("Sun").get("start").split(":");
                    startDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    startDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    time = (this.timetableDinner.get("Sun").get("end").split(":"));
                    endDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    endDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    if(endDinner.getTimeInMillis()<startDinner.getTimeInMillis())
                        endDinner.set(Calendar.DAY_OF_MONTH,endDinner.get(Calendar.DAY_OF_MONTH)+1);
                    if(nowMS >= startDinner.getTimeInMillis() && nowMS < endDinner.getTimeInMillis())
                        return true;
                    return false;
                }
                case 2:{ //Monday
                    if(this.timetableLunch.containsKey("Mon")) {
                        time = this.timetableLunch.get("Mon").get("start").split(":");
                        startLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        startLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        time = (this.timetableLunch.get("Mon").get("end").split(":"));
                        endLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        endLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        if(nowMS >= startLunch.getTimeInMillis() && nowMS < endLunch.getTimeInMillis())
                            return true;
                    }
                    time = this.timetableDinner.get("Mon").get("start").split(":");
                    startDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    startDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    time = (this.timetableDinner.get("Mon").get("end").split(":"));
                    endDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    endDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    if(endDinner.getTimeInMillis()<startDinner.getTimeInMillis())
                        endDinner.set(Calendar.DAY_OF_MONTH,endDinner.get(Calendar.DAY_OF_MONTH)+1);
                    if(nowMS >= startDinner.getTimeInMillis() && nowMS < endDinner.getTimeInMillis())
                        return true;
                    return false;
                }
                case 3:{ //Tuesday
                    if(this.timetableLunch.containsKey("Tue")) {
                        time = this.timetableLunch.get("Tue").get("start").split(":");
                        startLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        startLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        time = (this.timetableLunch.get("Tue").get("end").split(":"));
                        endLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        endLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        if(nowMS >= startLunch.getTimeInMillis() && nowMS < endLunch.getTimeInMillis())
                            return true;
                    }
                    time = this.timetableDinner.get("Tue").get("start").split(":");
                    startDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    startDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    time = (this.timetableDinner.get("Tue").get("end").split(":"));
                    endDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    endDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    if(endDinner.getTimeInMillis()<startDinner.getTimeInMillis())
                        endDinner.set(Calendar.DAY_OF_MONTH,endDinner.get(Calendar.DAY_OF_MONTH)+1);
                    if(nowMS >= startDinner.getTimeInMillis() && nowMS < endDinner.getTimeInMillis())
                        return true;
                    return false;
                }
                case 4:{ //Wednesday
                    if(this.timetableLunch.containsKey("Wed")) {
                        time = this.timetableLunch.get("Wed").get("start").split(":");
                        startLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        startLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        time = (this.timetableLunch.get("Wed").get("end").split(":"));
                        endLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        endLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        if(nowMS >= startLunch.getTimeInMillis() && nowMS < endLunch.getTimeInMillis())
                            return true;
                    }
                    time = this.timetableDinner.get("Wed").get("start").split(":");
                    startDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    startDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    time = (this.timetableDinner.get("Wed").get("end").split(":"));
                    endDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    endDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    if(endDinner.getTimeInMillis()<startDinner.getTimeInMillis())
                        endDinner.set(Calendar.DAY_OF_MONTH,endDinner.get(Calendar.DAY_OF_MONTH)+1);
                    if(nowMS >= startDinner.getTimeInMillis() && nowMS < endDinner.getTimeInMillis())
                        return true;
                    return false;
                }
                case 5:{ //Thursday
                    if(this.timetableLunch.containsKey("Thu")) {
                        time = this.timetableLunch.get("Thu").get("start").split(":");
                        startLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        startLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        time = (this.timetableLunch.get("Thu").get("end").split(":"));
                        endLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        endLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        if(nowMS >= startLunch.getTimeInMillis() && nowMS < endLunch.getTimeInMillis())
                            return true;
                    }
                    time = this.timetableDinner.get("Thu").get("start").split(":");
                    startDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    startDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    time = (this.timetableDinner.get("Thu").get("end").split(":"));
                    endDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    endDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    if(endDinner.getTimeInMillis()<startDinner.getTimeInMillis())
                        endDinner.set(Calendar.DAY_OF_MONTH,endDinner.get(Calendar.DAY_OF_MONTH)+1);
                    if(nowMS >= startDinner.getTimeInMillis() && nowMS < endDinner.getTimeInMillis())
                        return true;
                    return false;
                }
                case 6:{ //Friday
                    if(this.timetableLunch.containsKey("Fri")) {
                        time = this.timetableLunch.get("Fri").get("start").split(":");
                        startLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        startLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        time = (this.timetableLunch.get("Fri").get("end").split(":"));
                        endLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        endLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        if(nowMS >= startLunch.getTimeInMillis() && nowMS < endLunch.getTimeInMillis())
                            return true;
                    }
                    time = this.timetableDinner.get("Fri").get("start").split(":");
                    startDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    startDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    time = (this.timetableDinner.get("Fri").get("end").split(":"));
                    endDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    endDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    if(endDinner.getTimeInMillis()<startDinner.getTimeInMillis())
                        endDinner.set(Calendar.DAY_OF_MONTH,endDinner.get(Calendar.DAY_OF_MONTH)+1);
                    if(nowMS >= startDinner.getTimeInMillis() && nowMS < endDinner.getTimeInMillis())
                        return true;
                    return false;
                }
                case 7:{ //Saturday
                    if(this.timetableLunch.containsKey("Sat")) {
                        time = this.timetableLunch.get("Sat").get("start").split(":");
                        startLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        startLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        time = (this.timetableLunch.get("Sat").get("end").split(":"));
                        endLunch.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                        endLunch.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                        if(nowMS >= startLunch.getTimeInMillis() && nowMS < endLunch.getTimeInMillis())
                            return true;
                    }
                    time = this.timetableDinner.get("Sat").get("start").split(":");
                    startDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    startDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    time = (this.timetableDinner.get("Sat").get("end").split(":"));
                    endDinner.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
                    endDinner.set(Calendar.MINUTE,Integer.parseInt(time[1]));
                    if(endDinner.getTimeInMillis()<startDinner.getTimeInMillis())
                        endDinner.set(Calendar.DAY_OF_MONTH,endDinner.get(Calendar.DAY_OF_MONTH)+1);
                    if(nowMS >= startDinner.getTimeInMillis() && nowMS < endDinner.getTimeInMillis())
                        return true;
                    return false;
                }
            }
        } catch (NullPointerException e){
            return false;
        }
        Log.w(METHOD_NAME,"Switch not entered, returning wrong value");
        return false;
    }

    /**
     *
     * @param name Name of restaurant
     */
    public Restaurant setName(String name) {
        this.name = name; return this;
    }

    /**
     *
     * @param address Address of restaurant
     */
    public Restaurant setAddress(String address) { this.address = address; return this; }

    /**
     *
     * @param state The state of restaurant
     */
    public Restaurant setState(String state) {
        this.state = state; return this;
    }

    /**
     *
     * @param city City of restaurant
     */
    public Restaurant setCity(String city) {
        this.city = city; return this;
    }

    /**
     *
     * @param xcoord The latitude coordinate of restaurant
     */
    public Restaurant setXCoord(double xcoord) {
        this.xcoord = xcoord; return this;
    }

    /**
     *
     * @param ycoord The longitude coordinate of restaurant
     */
    public Restaurant setYCoord(double ycoord) {
        this.ycoord = ycoord; return this;
    }

    /**
     *
     * @param description of the restaurant
     */
    public Restaurant setDescription(String description) {
        this.description = description; return this;
    }

    /**
     * Sets the local path of this restaurants's image.
     * @param imagePath The local path of the image
     */
    public Restaurant setImageLocalPath(String imagePath) {
        this.imageLocalPath = imagePath; return this;
    }

    /**
     *
     * @param rating of restaurant
     */
    public Restaurant setRating(float rating) {
        this.rating = rating; return this;
    }

    /**
     *
     * @param menus: the arraylist of restaurant's menu
     */
    public Restaurant setMenus(ArrayList<Menu> menus) {
        this.menus = menus; return this;
    }

    /**
     *
     * @param orders:the arraylist of restaurant's orders
     */
    public Restaurant setOrders(ArrayList<Order> orders) {
        this.orders = orders; return this;
    }

    /**
     *
     * @param website of the restaurant
     */
    public Restaurant setWebsite(String website) {
        this.website = website; return this;
    }

    /**
     *
     * @param telephone the telephone number of the restaurant
     */
    public Restaurant setTelephone(String telephone) {
        this.telephone = telephone; return this;
    }

    /**
     *
     * @param zip the zip code of the restaurant's city
     */
    public Restaurant setZip(String zip){ this.zip = zip; return this; }

    /**
     *
     * @param tickets A HashMap containing the names of the tickets.
     */
    public Restaurant setTickets(Map<String,Boolean> tickets){ this.tickets = tickets; return this; }

    /**
     * Set the duration of the lunch time.
     *
     * @param dayOfWeek The day of the week, can be: Mon, Tue, Wed, Thu, Fri, Sat, Sun
     * @param timeStart Start time of the lunch
     * @param timeEnd End time of the lunch
     */
    public Restaurant setDurationLunch(String dayOfWeek,String timeStart, String timeEnd) {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationLunch";

        Map<String,String> entry = new HashMap<>();
        entry.put("start",timeStart);
        entry.put("end",timeEnd);
        this.timetableLunch.put(dayOfWeek,entry);
        return this;
    }

    /**
     * Set the duration of the dinner time.
     *
     * @param dayOfWeek The day of the week, can be: Mon, Tue, Wed, Thu, Fri, Sat, Sun
     * @param timeStart Start time of the dinner
     * @param timeEnd End time of the dinner
     */
    public Restaurant setDurationDinner(String dayOfWeek,String timeStart, String timeEnd) {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationDinner";

        Map<String,String> entry = new HashMap<>();
        entry.put("start",timeStart);
        entry.put("end",timeEnd);
        this.timetableDinner.put(dayOfWeek,entry);
        return this;
    }

    /**
     * Set the whole timetable for the lunch of this restaurant.
     * The timetable is formatted as follows:
     *      Mon-
     *          |-start => startTime
     *          |-end => endTime
     *      ...
     *      Sun-
     *          |-start => startTime
     *          |-end => endTime
     *
     * @param timetable A map representing the timetable formatted as shown.
     */
    public Restaurant setTimetableLunch(Map<String,Map<String,String>> timetable){
        this.timetableLunch = timetable;
        return this;
    }

    /**
     * Set the whole timetable for the dinner of this restaurant.
     * The timetable is formatted as follows:
     *      Mon-
     *          |-start => startTime
     *          |-end => endTime
     *      ...
     *      Sun-
     *          |-start => startTime
     *          |-end => endTime
     *
     * @param timetable A map representing the timetable formatted as shown.
     */
    public Restaurant setTimetableDinner(Map<String,Map<String,String>> timetable){
        this.timetableDinner = timetable;
        return this;
    }

    /**
     *
     * @param reviews An ArrayList of Review(s) to assign to this restaurant object.
     */
    public Restaurant setReviews(ArrayList<Review> reviews){ this.reviews = reviews; return this; }
}
