package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
     * from internal storage and the ID of the restaurant to identify the restaurant uniquely.
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
        this.JSONFile = this.readJSONFile();
    }

    /**
     * Reads the JSON file from internal storage and returns the corresponding JSONObject.
     * If the file is not found, FileNotFound exception is caught and createJSONFile method
     * is called.
     *
     * @return Parsed JSON restaurant file.
     * @throws IOException When read/write errors to file occur.
     * @throws JSONException When JSON file cannot be parsed properly.
     */
    public JSONObject readJSONFile() throws IOException, JSONException {
        InputStream is=null;
        try {
            is = this.appContext.openFileInput("r"+this.rid);
            if(is != null){
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                is.close();
                return new JSONObject(stringBuilder.toString());
            }
        } catch (FileNotFoundException e) {
            return this.createJSONFile();
        }
        return null;
    }

    /**
     * This is called when the JSON file to be read is not found. A new EMPTY file is
     * initialized. The file is then filled with all the required keys. The corresponding
     * values are left empty.
     *
     * @return The JSONOBject to be used by the class to access the key-value pairs.
     * @throws JSONException When JSON parsing fails.
     */
    private JSONObject createJSONFile() throws JSONException {
        final String methodName = "createJSONFile";

        File file = new File(this.appContext.getFilesDir(),"r"+this.rid);
        FileOutputStream fos = null;
        JSONObject newFile = new JSONObject();

        newFile.put("id", this.rid);
        newFile.put("uid","" );
        newFile.put("name","" );
        newFile.put("address","" );
        newFile.put("city","" );
        newFile.put("state","" );
        newFile.put("xcoord",0.0 );
        newFile.put("ycoord",0.0 );
        newFile.put("image", "" );
        newFile.put("rating", 0.0 );

        newFile.put("menus", new JSONArray());

        newFile.put("orders", new JSONArray());

        try {
            fos = appContext.openFileOutput("r"+this.rid,Context.MODE_PRIVATE);
            fos.write(newFile.toString(2).getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(methodName,"File r"+this.rid+" cannot be found!");
        } catch (IOException e) {
            Log.e(methodName,"Error in writing to r"+this.rid);
        }
        return newFile;
    }

    /**
     * Reads data from JSON object file.
     * If some field is missing, it throws JSONException.
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
            this.orders.add(new Order(this.JSONFile, orders.getJSONObject(i).getInt("id"), orders.getJSONObject(i).getInt("rid")));

    }

    /**
     * Saves data to JSON restaurant file.
     * If some field is missing, it throws JSONException.
     * This method calls, in cascade, the saveData methods of Menu class and Order class.
     *
     * @throws JSONException When JSON parsing fails.
     * @throws IOException When the output JSON file doesn't exist or a write error occurs.
     */
    public void saveData() throws JSONException, IOException {
        FileOutputStream fos = null;

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

        this.JSONFile.put("menus",new JSONArray());
        for(Menu m : this.menus)
            this.JSONFile.getJSONArray("menus").put(m.saveData());

        this.JSONFile.put("orders",new JSONArray());
        for(Order o : this.orders)
            this.JSONFile.getJSONArray("orders").put(o.saveData());

        fos = appContext.openFileOutput("r"+this.rid,Context.MODE_PRIVATE);
        fos.write(this.JSONFile.toString(2).getBytes());
        fos.close();
    }

    /**
     * @return string Name of restaurant
     */
    public String getName() {
        return name;
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
     * @return Address of restaurant
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address Address of restaurant
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
     * @param state The state of restaurant
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return City of Restaurant
     */
    public String getCity() {
        return city;
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
     * @return The image of the course, in base 64 format
     */
    public byte[] getImage64(){return this.image;}

    /**
     * Sets the base 64 encoding of the image
     * @param image Byte array of image, encoded in base 64
     */

    public void setImage64(byte[] image){ this.image = image;}
    /**
     *
     * @return The image of the course, in Bitmap format
     */
    public Bitmap getImageBitmap(){
        return BitmapFactory.decodeByteArray(this.image, 0, this.image.length);
    }

    /**
     * Sets the base 64 encoding of the image from an input Bitmap
     * @param image Bitmap image to save
     */
    public void setImage64FromBitmap(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        this.image = baos.toByteArray();
    }

    /**
     * Sets the base 64 encoding of the image from an input Drawable
     * @param image Drawable image to save
     */
    public void setImage64FromDrawable(Drawable image){
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        this.setImage64FromBitmap(b);
    }

    /**
     *
     * @return The latitude coordinate of restaurant
     */
    public double getXcoord() {
        return xcoord;
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
     * @return The longitude coordinate of restaurant
     */
    public double getYcoord() {
        return ycoord;
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
     * @return The description of restaurant
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
     * @param id The id of the menu to search for.
     * @return The Menu object or null if nothing is found.
     */
    public Menu getMenuByID(int id){
        for(Menu m : this.menus)
            if(m.getMid() == id)
                return m;
        return null;
    }

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
     * @param id The id of the order to search for.
     * @return The requested order or null if nothing is found.
     */
    public Order getOrderByID(int id){
        for(Order o : this.orders)
            if(o.getOid() == id)
                return o;
        return null;
    }

    /**
     *
     * @param uid The user id who submitted
     * @return An ArrayList of Orders or null if nothing is found.
     */
    public ArrayList<Order> getOrdersByUserID(int uid){
        ArrayList<Order> output = new ArrayList<Order>();
        for(Order o : this.orders)
            if(o.getUid() == uid)
                output.add(o);
        return output.isEmpty()? null : output;
    }

    /**
     *
     * @return The id of the restaurant owner
     */
    public int getUid() {
        return uid;
    }

    /**
     *
     * @param uid Set the id of the restaurant owner
     */
    public void setUid(int uid) {
        this.uid = uid;
    }
}
