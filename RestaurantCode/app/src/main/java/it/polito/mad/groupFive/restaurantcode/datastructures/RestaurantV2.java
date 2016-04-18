package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

/**
 * @author Marco Ardizzone
 * @class RestaurantV2
 * @date 2016-04-17
 * @brief Restaurant class
 */
public class RestaurantV2 {

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
    private Uri image;
    private float rating;
    private double xcoord;
    private double ycoord;
    private ArrayList<Menu> menus=new ArrayList<>();
    private ArrayList<Order> orders=new ArrayList<>();
    private ArrayMap<Integer, String> tickets = new ArrayMap<>();
    private ArrayList<ArrayMap<Integer, Duration>> timetable = new ArrayList<>(2);

    public RestaurantV2(Context appContext) {
        this.rid = RestaurantV2.randInt();
        this.appContext = appContext;
        RestaurantV2 dummy = this.readJSONFile();
        this.copyData(dummy);
    }

    public RestaurantV2(Context appContext,int rid) throws RestaurantException {
        if(rid < 0)
            throw new RestaurantException("Restaurant ID must be positive");
        this.rid = rid;
        this.appContext = appContext;
        RestaurantV2 dummy = this.readJSONFile();
        this.copyData(dummy);
    }

    private void copyData(RestaurantV2 dummy) {
        this.uid = dummy.uid;
        this.name = dummy.name;
        this.description = dummy.description;
        this.address = dummy.address;
        this.state = dummy.state;
        this.city = dummy.city;
        this.website = dummy.website;
        this.telephone = dummy.telephone;
        this.image = dummy.image;
        this.xcoord = dummy.xcoord;
        this.ycoord = dummy.ycoord;
        this.menus = dummy.menus;
        this.orders = dummy.orders;
        this.tickets = dummy.tickets;
    }

    private RestaurantV2 readJSONFile(){
        final String METHOD_NAME = this.getClass().getName()+" - readJSONFile";

        InputStream is;
        try {
            is = appContext.openFileInput("r"+rid);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputString = br.readLine()) != null ) {
                stringBuilder.append(inputString);
            }

            is.close();
            Gson root = new Gson();
            return root.fromJson(stringBuilder.toString(), RestaurantV2.class);
        } catch (FileNotFoundException e) {
            try {
                this.createJSONFile();
                return this.readJSONFile();
            } catch (IOException e1) {
                Log.e(METHOD_NAME,e1.getMessage());
                return null;
            }
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return null;
        }
    }

    private void createJSONFile() throws IOException {
        final String METHOD_NAME = this.getClass().getName()+" - createJSONFile";

        File file = new File(appContext.getFilesDir(),"r"+rid);
        Writer writer = new FileWriter(file);

        Gson root = new Gson();
        root.toJson(RestaurantV2.class, writer);
        Log.i(METHOD_NAME,"Wrote to file:\n"+root.toJson(RestaurantV2.class));
    }

    public static int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            return rand.nextInt(Integer.MAX_VALUE -1 );
        }
    }

    public void getData() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            FileInputStream fis = appContext.openFileInput("r"+this.rid);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Gson root = new Gson();
            RestaurantV2 dummy = root.fromJson(json,RestaurantV2.class);
            this.copyData(dummy);
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
    }

    public void saveData() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - saveData";

        Gson root = new Gson();
        String output = root.toJson(this);

        try {
            FileOutputStream fos = this.appContext.openFileOutput("r"+this.rid,Context.MODE_PRIVATE);
            fos.write(output.getBytes());
            fos.close();
        } catch (IOException e) {
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
     * @return Uri of the image
     */
    public Uri getImage(){ return this.image;}

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
    public float getRating() { return this.rating; }

    /**
     *
     * @return a string with the restaurant's telephone number
     */
    public String getPhone() { return this.telephone; }

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
        ArrayList<Menu> output = new ArrayList<Menu>();
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
    public Menu getMenuByID(int mid) throws MenuException {
        if (mid <0)
            throw new MenuException("Menu ID must be positive");
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
     */
    public Order getOrderByID(int oid) throws OrderException {
        if(oid <0)
            throw new OrderException("Order ID must be positive");
        for(Order o : this.orders)
            if(o.getOid() == oid)
                return o;
        return null;
    }

    /**
     *
     * @param uid The user id who submitted
     * @return An ArrayList of Orders or null if nothing is found.
     */
    public ArrayList<Order> getOrdersByUserID(int uid) throws UserException {
        if(uid <0)
            throw new UserException("User ID must be positive");
        ArrayList<Order> output = new ArrayList<Order>();
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
    public ArrayMap<Integer,Duration> getTimetableLunch(){ return this.timetable.get(0); }

    /**
     * Returns an ArrayMap with keys the number of the day of the week (from 0 [Monday] to 6
     * [Sunday]) and values a Duration object representing the shift of the DINNER.
     *
     * @return ArrayMap of the DINNER timetable.
     */
    public ArrayMap<Integer,Duration> getTimetableDinner(){ return this.timetable.get(1); }

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
     * @param uri Uri of the image
     */
    public void setImageUri(Uri uri){ this.image = uri; }

    /**
     *
     * @param uri A string representing the Uri of the image
     */
    public void setImageUriFromString(String uri){ this.image = Uri.parse(uri); }

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
     */
    public void setUid(int uid) throws UserException {
        if(uid < 0)
            throw new UserException("User ID must be positive");
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
    public void setPhone(String telephone) {
        this.telephone = telephone;
    }

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
     * @return true if saved correctly, false otherwise
     */
    boolean setDurationLunch(int dayOfWeek,String timeStart, String timeEnd) {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationLunch";

        if(dayOfWeek < 0 || dayOfWeek > 6){
            Log.e(METHOD_NAME, "Day of week is not in the expected range 0-6");
            return false;
        }

        DateFormat sdfStart = SimpleDateFormat.getTimeInstance();
        DateFormat sdfEnd = SimpleDateFormat.getTimeInstance();
        try {
            Date startDate = sdfStart.parse(timeStart);
            Date endDate = sdfEnd.parse(timeEnd);
            Duration d = DatatypeFactory.newInstance().newDuration(endDate.getTime()-startDate.getTime());
            this.timetable.get(0).put(dayOfWeek,d);
            return true;
        } catch (DatatypeConfigurationException e) {
            Log.e(METHOD_NAME, e.getMessage());
            return false;
        } catch (ParseException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return false;
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
     * @return true if saved correctly, false otherwise
     */
    boolean setDurationDinner(int dayOfWeek,String timeStart, String timeEnd){
        final String METHOD_NAME = this.getClass().getName()+" - setDurationDinner";

        if(dayOfWeek < 0 || dayOfWeek > 6){
            Log.e(METHOD_NAME, "Day of week is not in the expected range 0-6");
            return false;
        }
        DateFormat sdfStart = SimpleDateFormat.getTimeInstance();
        DateFormat sdfEnd = SimpleDateFormat.getTimeInstance();
        try {
            Date startDate = sdfStart.parse(timeStart);
            Date endDate = sdfEnd.parse(timeEnd);
            Duration d = DatatypeFactory.newInstance().newDuration(endDate.getTime()-startDate.getTime());
            this.timetable.get(1).put(dayOfWeek,d);
            return true;
        } catch (DatatypeConfigurationException e) {
            Log.e(METHOD_NAME, e.getMessage());
            return false;
        } catch (ParseException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return false;
        }
    }
}
