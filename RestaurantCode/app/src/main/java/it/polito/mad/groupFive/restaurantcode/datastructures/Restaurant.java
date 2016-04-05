package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;

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
    private final String configFile = "restaurants.json";
    private JSONObject jsonConfigFile=null;

    private JSONObject myJSONFile=null;

    private int rid;
    private String name;
    private String description;
    private String address;
    private String state;
    private String city;
    private byte[] image;
    private float rating;
    private ArrayList<Menu> menus;
    private ArrayList<Order> orders;
    private float xcoord;
    private float ycoord;

    /**
     * Instantiates a new Restaurant object.
     * The constructor requires the Application Context to read the JSON configuration file
     * from assets folder and the ID of the restaurant because it's the only way to identify
     * the restaurant uniquely.
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
        this.jsonConfigFile = new JSONObject(this.loadJSONFromAsset());

    }

    /**
     * This method reads the list of restaurants from restaurant.json asset file.
     * The restaurant.json file should be structured as follows:
     *  {
     *      "restaurants":[
     *          {"id": 1},
     *          {"id": 2},
     *          ...
     *          {"id": N}
     *      ]
     *  }
     * @return String representation of the JSON file
     * @throws IOException
     */
    public String loadJSONFromAsset() throws IOException {
        String json = null;
        InputStream is = this.appContext.getAssets().open(this.configFile);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        return json;
    }

    /**
     * Reads data from the JSON restaurant file
     */
    public void getData(){}

    /**
     * Saves data to the JSON restaurant file
     */
    public void saveData(){}

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
    public float getXcoord() {
        return xcoord;
    }

    /**
     *
     * @param xcoord: the latitude coordinate of restaurant
     */
    public void setXcoord(float xcoord) {
        this.xcoord = xcoord;
    }

    /**
     *
     * @return the longitude coordinate of restaurant
     */
    public float getYcoord() {
        return ycoord;
    }

    /**
     *
     * @param ycoord: the longitude coordinate of restaurant
     */
    public void setYcoord(float ycoord) {
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

}
