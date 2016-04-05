package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantIDException;

/**
 * @author Giovanni
 * @class Restaurant
 * @date 04/04/16
 * @brief Restaurant class
 */
public class Restaurant {

    private Context appContext=null;
    private JSONObject JSONFile=null;

    private int rid;
    private int uid;
    private String name;
    private String description;
    private String address;
    private String state;
    private String city;
    private byte[] image;
    private float rating;
    private ArrayList<Menu> menus;
    private ArrayList<Order> orders;
    private double xcoord;
    private double ycoord;

    /**
     * Instantiates a new Restaurant object.
     * The constructor requires the Application Context to read the JSON configuration file
     * from assets folder and the ID of the restaurant because it's the only way to identify
     * the restaurant file uniquely.
     *
     * @param c Application Context.
     * @param restaurantID Unique ID of the restaurant.
     * @throws IOException Thrown if read errors occur.
     * @throws RestaurantIDException Thrown if ID is negative.
     * @throws JSONException Thrown if JSON parsing fails.
     */
    public Restaurant(Context c, int restaurantID) throws IOException, RestaurantIDException, JSONException {
        this.appContext = c;
        if(restaurantID < 0)
            throw new RestaurantIDException("Restaurant ID must be positive");
        this.rid = restaurantID;
        this.JSONFile = new JSONObject(this.loadJSONFromAsset());
    }

    /**
     * This method reads the restaurant JSON file.
     *
     * @return String representation of the JSON file
     * @throws IOException
     */
    public String loadJSONFromAsset() throws IOException {
        String json = null;
        InputStream is = this.appContext.getAssets().open("r"+this.rid);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        return json;
    }

    /**
     * Reads data from JSON configuration file.
     * If some field is missing, it throws JSONException
     * Please note that menus and orders objects read like this are just filled with their
     * own id. The other data must be filled through the methods provided in their classes.
     *
     * @throws JSONException if some field is missing.
     */
    public void getData() throws JSONException {
        this.uid = this.JSONFile.getInt("uid");
        this.name = this.JSONFile.getString("name");
        this.description = this.JSONFile.getString("description");
        this.address = this.JSONFile.getString("address");
        this.city = this.JSONFile.getString("city");
        this.state = this.JSONFile.getString("state");
        this.xcoord = this.JSONFile.getDouble("xcoord");
        this.ycoord = this.JSONFile.getDouble("ycoord");
        this.image = this.JSONFile.getString("image").getBytes();
        this.rating = (float)this.JSONFile.getDouble("rating");

        JSONArray menus = this.JSONFile.getJSONArray("menus");
        for(int i=0; i < menus.length(); i++)
            this.menus.add(new Menu(this.JSONFile, menus.getJSONObject(i).getInt("id")));

        JSONArray orders = this.JSONFile.getJSONArray("orders");
        for(int i=0; i <orders.length(); i++)
            this.orders.add(new Order(this.appContext,orders.getJSONObject(i).getInt("id")));
    }

    /**
     * Saves data to JSON restaurant file.
     * If some field is missing, it throws JSONException.
     * Please note that menus and orders objects saved like this are just filled with their
     * own id. The other data must be filled through the methods provided in their classes.
     *
     * @throws JSONException
     */
    public void saveData() throws JSONException {
        this.JSONFile.put("id",this.rid);
        this.JSONFile.put("uid",this.uid);
        this.JSONFile.put("name",this.name);
        this.JSONFile.put("address",this.address);
        this.JSONFile.put("city",this.city);
        this.JSONFile.put("state",this.state);
        this.JSONFile.put("xcoord",this.xcoord);
        this.JSONFile.put("ycoord",this.ycoord);
        this.JSONFile.put("image",this.image.toString());
        this.JSONFile.put("rating",this.rating);

        JSONArray menus = new JSONArray();
        for (int i = 0; i < this.menus.size(); i++) {
            JSONObject menu = new JSONObject();
            menu.put("id",this.menus.get(i).getMid());
            menus.put(i,menu);
        }
        this.JSONFile.put("menus",menus);

        JSONArray orders = new JSONArray();
        for (int i = 0; i <this.orders.size(); i++) {
            JSONObject order = new JSONObject();
            order.put("id",this.orders.get(i).getOid());
            orders.put(i,order);
        }
        this.JSONFile.put("orders",orders);
    }

    /**
     * @return string: name of restaurant
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name of restaurant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return address of restaurant
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address of restaurant
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return State of restaurant
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state: the State of restaurant
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return city of Restaurant
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city of restaurant
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return byte of restaurant's image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     *
     * @param image the byte of image of restaurant
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     *
     * @return the latitude coordinate of restaurant
     */
    public double getXcoord() {
        return xcoord;
    }

    /**
     *
     * @param xcoord: the latitude coordinate of restaurant
     */
    public void setXcoord(double xcoord) {
        this.xcoord = xcoord;
    }

    /**
     *
     * @return the longitude coordinate of restaurant
     */
    public double getYcoord() {
        return ycoord;
    }

    /**
     *
     * @param ycoord: the longitude coordinate of restaurant
     */
    public void setYcoord(double ycoord) {
        this.ycoord = ycoord;
    }

    /**
     *
     * @return the description of restaurant
     */
    public String getDescription() {
        return description;
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
     * @return the id of restaurant
     */
    public int getRid() {
        return rid;
    }

    /**
     *
     * @param rid: the id of restaurant
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    /**
     *
     * @return the rating of restaurant
     */
    public float getRating() {
        return rating;
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
     * @return the arraylist of restaurant's menu
     */
    public ArrayList<Menu> getMenus() {
        return menus;
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
     * @return the arraylist of restaurant's orders
     */
    public ArrayList<Order> getOrders() {
        return orders;
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
     * @return uid: id of user that is the restaurant owner
     */
    public int getUid() {
        return uid;
    }

    /**
     *
     * @param uid: set the id of the restaurant owner
     */
    public void setUid(int uid) {
        this.uid = uid;
    }
}
