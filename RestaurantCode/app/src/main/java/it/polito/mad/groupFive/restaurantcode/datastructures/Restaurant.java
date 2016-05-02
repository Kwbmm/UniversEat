package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.util.Log;

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
import java.util.Date;
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

    transient private Context appContext;

    private int rid;
    private int uid;
    private String name;
    private String description;
    private String address;
    private String state;
    private String city;
    private String website;
    private String telephone;
    private String ZIPCode;
    private byte[] image;
    private float rating;
    private double xcoord;
    private double ycoord;
    private ArrayList<Menu> menus=new ArrayList<>();
    private ArrayList<Order> orders=new ArrayList<>();
    private ArrayMap<Integer, String> tickets = new ArrayMap<>();
    private ArrayMap<Integer, Date[]> timetableLunch = new ArrayMap<>();
    private ArrayMap<Integer, Date[]> timetableDinner = new ArrayMap<>();
    private ArrayList<Review> reviews = new ArrayList<>();

    /**
     * Create a Restaurant object. Requires, as parameter, the Android Application Context of the
     * activity instantiating this class.
     * The ID uniquely identifying this restaurant is generated automatically.
     *
     * @param appContext Application Context
     * @throws RestaurantException if restaurant ID is negative or JSON file read fails.
     */
    public Restaurant(Context appContext) throws RestaurantException {
        this(appContext,Restaurant.randInt());
    }

    /**
     * Create a Restaurant object. Requires, as parameters, the Android Application Context of the
     * activity instantiating this class and a positive integer uniquely identifying the restaurant
     * object.
     *
     * @param appContext Application Context
     * @param rid Positive Integer unique identifier
     * @throws RestaurantException
     */
    public Restaurant(Context appContext, int rid) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - constructor";
        if(rid < 0)
            throw new RestaurantException("Restaurant ID must be positive");
        this.rid = rid;
        this.appContext = appContext;
        Restaurant dummy;
        if((dummy=this.readJSONFile())== null){
            Log.e(METHOD_NAME, "Dummy is null");
            throw new RestaurantException("Restaurant dummy object used to fill the current object is null");
        }
        else
            this.copyData(dummy);
    }

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy Restaurant object, on which the JSON data is written to.
     */
    private void copyData(Restaurant dummy) {
        final String METHOD_NAME = this.getClass().getName()+" - copyData";

        this.uid = dummy.getUid();
        this.name = dummy.getName();
        this.description = dummy.getDescription();
        this.address = dummy.getAddress();
        this.state = dummy.getState();
        this.city = dummy.getCity();
        this.website = dummy.getWebsite();
        this.telephone = dummy.getTelephone();
        this.ZIPCode = dummy.getZIPCode();
        this.image = dummy.getImageByteArray();
        this.xcoord = dummy.getXcoord();
        this.ycoord = dummy.getYcoord();
        this.menus = dummy.getMenus();
        this.orders = dummy.getOrders();
        this.tickets = dummy.getTickets();
        this.timetableLunch = dummy.getTimetableLunch();
        this.timetableDinner = dummy.getTimetableDinner();
        this.reviews = dummy.getReviews();
    }

    /**
     * Reads the JSON file corresponding to this restaurant and fills this class with the data found
     * in the file.
     * If the file doesn't exist CreateJSONFile is called and the file is created. Then the reading
     * is performed again: this time the file will be found and fields of this class will be filled
     * with null values (because the created file is empty).
     *
     * If a fail occurs, the error message is logged and this method returns null.
     *
     * @return A Restaurant object or null if fails.
     */
    private Restaurant readJSONFile(){
        final String METHOD_NAME = this.getClass().getName()+" - readJSONFile";

        InputStream is;
        try {
            is = appContext.openFileInput("r"+this.rid+".json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputString = br.readLine()) != null ) {
                stringBuilder.append(inputString);
            }

            is.close();
            Gson root = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new CustomUriAdapter())
                    .registerTypeHierarchyAdapter(byte[].class, new CustomByteArrayAdapter())
                    .create();
            return root.fromJson(stringBuilder.toString(), Restaurant.class);
        } catch (FileNotFoundException e) {
            this.createJSONFile();
            return this.readJSONFile();
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return null;
        }
    }

    /**
     * Create the JSON file corresponding to this object. The JSON file is identified by the
     * restaurant ID set at instantiation time.
     * If an error occurs, the error is logged.
     */
    private void createJSONFile() {
        final String METHOD_NAME = this.getClass().getName()+" - createJSONFile";

        File file = new File(appContext.getFilesDir(),"r"+rid+".json");
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            Gson root = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            root.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    /**
     * Generate a random integer in the range [1, Integer.MAX_VALUE]
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

    /**
     * Fetch the data corresponding to the Restaurant ID of this object from the JSON file.
     * This method is in charge of filling all the other classes of this data set (Menu, Order and
     * Course).
     * If an error occurs, it is logged and a RestaurantException is thrown.
     *
     * @throws RestaurantException If fetch fails
     */
    public void getData() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            FileInputStream fis = appContext.openFileInput("r"+this.rid+".json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Gson root = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new CustomUriAdapter())
                    .registerTypeHierarchyAdapter(byte[].class, new CustomByteArrayAdapter())
                    .create();
            Restaurant dummy = root.fromJson(json,Restaurant.class);
            this.copyData(dummy);
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
    }

    /**
     * Saves the current data store in this object to its JSON file.
     * Note that this method also saves the data for the custom sub-objects embedded inside this
     * class (Order, Menu and Course).
     * In case of fail, the error is logged and a RestaurantException is thrown.
     *
     * @throws RestaurantException If writing JSON file fails
     */
    public void saveData() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - saveData";

        Gson root = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new CustomUriAdapter())
                .registerTypeHierarchyAdapter(byte[].class, new CustomByteArrayAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        String output = root.toJson(this);

        try {
            FileOutputStream fos = this.appContext.openFileOutput("r"+this.rid+".json",Context.MODE_PRIVATE);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(output.getBytes());
            bos.close();
            fos.close();
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
    }

    /**
     *
     * @return the id of restaurant
     */
    public int getRid() { return this.rid; }

    /**
     *
     * @return The id of the restaurant owner
     */
    public int getUid() { return this.uid; }

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
    public String getZIPCode() { return this.ZIPCode; }

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
     * @throws RestaurantException if menu id is negative.
     */
    public Menu getMenuByID(int mid) throws RestaurantException {
        if (mid <0)
            throw new RestaurantException("Menu ID must be positive");
        for(Menu m : this.menus)
            if(m.getMid() == mid)
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
     * @throws RestaurantException if order is negative.
     */
    public Order getOrderByID(int oid) throws RestaurantException {
        if(oid <0)
            throw new RestaurantException("Order ID must be positive");
        for(Order o : this.orders)
            if(o.getOid() == oid)
                return o;
        return null;
    }

    /**
     *
     * @param uid The user id who submitted
     * @return An ArrayList of Orders or null if nothing is found.
     * @throws RestaurantException if user id is negative.
     */
    public ArrayList<Order> getOrdersByUserID(int uid) throws RestaurantException {
        if(uid <0)
            throw new RestaurantException("User ID must be positive");
        ArrayList<Order> output = new ArrayList<>();
        for(Order o : this.orders)
            if(o.getUid() == uid)
                output.add(o);
        return output.isEmpty()? null : output;
    }

    /**
     * Returns an array map where the Integer ID of the ticket is used as key. The value is the
     * name of the ticket type.
     * @return An ArrayMap<Integer, String> of tickets.
     */
    public ArrayMap<Integer,String> getTickets(){ return this.tickets; }

    /**
     * This methods takes as input an integer ID, representing the key to look for. If the key is
     * found in the map, it returns the corresponding value name of the ticket type.
     * If the search produces no results, it returns null.
     *
     * @param key The key to look for
     * @return String representing a ticket type, or null if not found.
     */
    public String getTicketNameByKey(Integer key){
        for(Integer i : this.tickets.keySet()){
            if(i.equals(key))
                return this.tickets.get(i);
        }
        return null;
    }

    /**
     * Returns an ArrayMap with keys the number of the day of the week (from 0 [Monday] to 6
     * [Sunday]) and values a Duration object representing the shift of the LUNCH.
     *
     * @return ArrayMap of the LUNCH timetable.
     */
    public ArrayMap<Integer,Date[]> getTimetableLunch(){ return this.timetableLunch; }

    /**
     * Returns an ArrayMap with keys the number of the day of the week (from 0 [Monday] to 6
     * [Sunday]) and values a Duration object representing the shift of the DINNER.
     *
     * @return ArrayMap of the DINNER timetable.
     */
    public ArrayMap<Integer,Date[]> getTimetableDinner(){ return this.timetableDinner; }

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
     * @throws RestaurantException If review id is negative.
     */
    public Review getReviewByRevID(int revID) throws RestaurantException {
        if(revID < 0)
            throw new RestaurantException("Review ID must be positive");
        for(Review r : this.reviews)
            if(r.getRevID() == revID)
                return r;
        return null;
    }

    /**
     * Get an ArrayList of Reviews made by the user matching the specified user id.
     * If the supplied ID returns no results, an empty ArrayList is returned.
     *
     * @param uid A user ID.
     * @return An ArrayList of Review objects matching the specified user id.
     * @throws RestaurantException If user id is negative.
     */
    public ArrayList<Review> getReviewsByUserID(int uid) throws RestaurantException {
        if(uid < 0)
            throw new RestaurantException("User ID must be positive");
        ArrayList<Review> returnRes = new ArrayList<>();
        for(Review r : this.reviews)
            if(r.getUid() == uid)
                returnRes.add(r);
        return returnRes;
    }

    /**
     * Get the Android Application Context stored in this object.
     * @return Context
     */
    Context getAppContext(){ return this.appContext; }

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
     * @param rid: the id of restaurant.
     * @throws RestaurantException Thrown if restaurant ID is negative.
     */
    public void setRid(int rid) throws RestaurantException {
        if(rid < 0)
            throw new RestaurantException("Restaurant ID must be positive");
        this.rid = rid;
    }

    /**
     *
     * @param rating of restaurant
     */
    public void setRating(float rating) throws RestaurantException {
        if(rating < 0)
            throw new RestaurantException("Rating must be positive");
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

    /**
     *
     * @param uid Set the id of the restaurant owner
     * @throws RestaurantException if user id is negative
     */
    public void setUid(int uid) throws RestaurantException {
        if(uid < 0)
            throw new RestaurantException("User ID must be positive");
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
     * @param ZIPCode the zip code of the restaurant's city
     */
    public void setZIPCode(String ZIPCode){ this.ZIPCode = ZIPCode; }

    /**
     *
     * @param tickets An ArrayMap where the Integer representing the ticket is the key, while the
     *                string is the name of the ticket.
     */
    public void setTickets(ArrayMap<Integer, String> tickets){ this.tickets = tickets; }

    /**
     * Sets the amount of time during which the restaurant is open AT LUNCH.
     * Returns true in case of success, false otherwise.
     * If false is returned, an error message is logged.
     * Parameter dayOfWeek should range between 0 (Monday) and 6(Sunday).
     *
     * @param dayOfWeek Day of the week during which this shift takes place. Must be between 0 and 6
     * @param timeStart Start hour of the shift.
     * @param timeEnd End hour of the shift.
     * @throws RestaurantException if day of the week is not in range, or date parsing fails.
     */
    public void setDurationLunch(int dayOfWeek,String timeStart, String timeEnd) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationLunch";

        if(dayOfWeek < 0 || dayOfWeek > 6){
            Log.e(METHOD_NAME, "Day of week is not in the expected range 0-6");
            throw new RestaurantException("Day of week is not in the expected range 0-6");
        }

        SimpleDateFormat sdfStart = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdfEnd = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date startDate = sdfStart.parse(timeStart);
            Date endDate = sdfEnd.parse(timeEnd);

            this.timetableLunch.put(dayOfWeek,new Date[]{startDate,endDate});
        } catch (ParseException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
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
     * @throws RestaurantException if day of the week is not in range, or date parsing fails.
     */
    public void setDurationDinner(int dayOfWeek,String timeStart, String timeEnd) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationDinner";

        if(dayOfWeek < 0 || dayOfWeek > 6){
            Log.e(METHOD_NAME, "Day of week is not in the expected range 0-6");
            throw new RestaurantException("Day of week is not in the expected range 0-6");
        }
        SimpleDateFormat sdfStart = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdfEnd = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date startDate = sdfStart.parse(timeStart);
            Date endDate = sdfEnd.parse(timeEnd);
            this.timetableDinner.put(dayOfWeek,new Date[]{startDate,endDate});
        } catch (ParseException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
    }

    /**
     * Set the timetable for lunch time of this restaurant.
     * Input parameter is a Map with key the days of the week (from 0 to 6) and as value an array
     * of Date. The array must be of length 2. It should be structured as:
     * [0] startHour
     * [1] endHour
     * @param timetable A(n) (Array)Map of the timetable of the Lunch.
     */
    public void setTimetableLunch(Map<Integer,Date[]> timetable){
        this.timetableLunch.putAll(timetable);
    }

    /**
     * Set the timetable for dinner time of this restaurant.
     * Input parameter is a Map with key the days of the week (from 0 to 6) and as value an array
     * of Date. The array must be of length 2. It should be structured as:
     * [0] startHour
     * [1] endHour
     * @param timetable A(n) (Array)Map of the timetable of the Dinner.
     */
    public void setTimetableDinner(Map<Integer,Date[]> timetable){
        this.timetableDinner.putAll(timetable);
    }

    public void addMenu(Menu menu) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+"- add menu to list";
        this.menus.add(menu);
        try {
            saveData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
    }

    public void addReview(Review review) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+"- add menu to list";
        this.reviews.add(review);
        try {
            saveData();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
    }
    /**
     *
     * @param reviews An ArrayList of Review(s) to assign to this restaurant object.
     */
    public void setReviews(ArrayList<Review> reviews){ this.reviews = reviews; }
}
