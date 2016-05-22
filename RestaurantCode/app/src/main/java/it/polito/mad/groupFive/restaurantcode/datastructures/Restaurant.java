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

    private DatabaseReference dbRoot;
    private StorageReference storageRoot;

    private String rid;
    private String uid;
    private String name;
    private String description;
    private String address;
    private String state;
    private String city;
    private String website;
    private String telephone;
    private String ZIPCode;
    private String imageName;
    private String imagePath;
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
        this(null);
    }

    public Restaurant(String rid) {
        final String METHOD_NAME = this.getClass().getName()+" - constructor";
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference("restaurant");

        this.rid = rid == null ? this.dbRoot.push().getKey() : rid;

        //Change the dbRoot to the tree specific to this object
        this.dbRoot = this.dbRoot.child(this.rid);
        //Setup the storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
        //Change the storageRoot to the tree specific to this object
        this.storageRoot = this.storageRoot.child(this.rid);
        this.imageName = "restaurant_"+this.rid+".png";
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

    public void saveData() {
        final String METHOD_NAME = this.getClass().getName()+" - saveData";

        this.dbRoot.child("restaurant-id").setValue(this.uid);
        this.dbRoot.child("user-id").setValue(this.uid);
        this.dbRoot.child("name").setValue(this.name);
        this.dbRoot.child("description").setValue(this.description);
        this.dbRoot.child("address").setValue(this.address);
        this.dbRoot.child("city").setValue(this.city);
        this.dbRoot.child("zip-code").setValue(this.ZIPCode);
        this.dbRoot.child("state").setValue(this.state);
        this.dbRoot.child("website").setValue(this.website);
        this.dbRoot.child("telephone").setValue(this.telephone);
        this.dbRoot.child("rating").setValue(this.rating);
        this.dbRoot.child("x-coord").setValue(this.xcoord); //TODO Fix this
        this.dbRoot.child("y-coord").setValue(this.ycoord); //TODO also this

        //Set the menu IDs
        for(Menu m : this.menus){
            this.dbRoot.child("menu").child(m.getMid()).setValue(true);
        }

        for(Order o : this.orders){
            this.dbRoot.child("order").child(o.getOid()).setValue(true);
        }

        this.dbRoot.child("timetable-lunch").setValue(this.timetableLunch);
        this.dbRoot.child("timetable-dinner").setValue(this.timetableDinner);
        this.dbRoot.child("ticket").setValue(this.tickets);

        for(Review r : this.reviews){
            this.dbRoot.child("review").child(r.getRevID()).setValue(true);
        }
        this.dbRoot.child("image-name").setValue(this.imageName);

        Uri file = Uri.fromFile(new File(this.imagePath,this.imageName));
        UploadTask ut = storageRoot.child(this.imageName).putFile(file);
        ut.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("onFailure", e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("onSuccess","Image is located at: "+taskSnapshot.getDownloadUrl());
            }
        });
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
     * @return The name of the restaurant image
     */
    public String getImageName() { return this.imageName; }

    public Bitmap getImageBitmap() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - getImageBitmap";
        try {
            File f=new File(this.imagePath, this.imageName);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new RestaurantException(e.getMessage());
        }
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
    public Map<String,Date[]> getTimetableLunch() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - getTimetableLunch";
        Map<String,Date[]> output = new HashMap<>();
        for(Map.Entry<String,Map<String,String>> entry : this.timetableLunch.entrySet()){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.getDefault());
            try {
                output.put(entry.getKey(),new Date[]{sdf.parse(entry.getValue().get("start")),sdf.parse(entry.getValue().get("end"))});
            } catch (ParseException e) {
                Log.e(METHOD_NAME,e.getMessage());
                throw new RestaurantException(e.getMessage());
            }
        }
        return output;
    }

    /**
     * Returns an ArrayMap with keys the number of the day of the week (from 0 [Monday] to 6
     * [Sunday]) and values a Duration object representing the shift of the DINNER.
     *
     * @return ArrayMap of the DINNER timetable.
     */
    public Map<String,Date[]> getTimetableDinner() throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - getTimetableLunch";
        Map<String,Date[]> output = new HashMap<>();
        for(Map.Entry<String,Map<String,String>> entry : this.timetableDinner.entrySet()){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.getDefault());
            try {
                output.put(entry.getKey(),new Date[]{sdf.parse(entry.getValue().get("start")),sdf.parse(entry.getValue().get("end"))});
            } catch (ParseException e) {
                Log.e(METHOD_NAME,e.getMessage());
                throw new RestaurantException(e.getMessage());
            }
        }
        return output;
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
     * Sets the byte array representation of the image from a given input Bitmap.
     * If you can, use setImageFromByteArray instead of this one as it is more efficient.
     *
     * @param image Bitmap representing the image
     */
    public void setImageFromBitmap(Bitmap image, Context appContext) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - setImageFromBitmap";
        ContextWrapper cw = new ContextWrapper(appContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,this.imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException fnfe) {
            Log.e(METHOD_NAME,fnfe.getMessage());
            throw new RestaurantException(fnfe.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(METHOD_NAME,e.getMessage());
                throw new RestaurantException(e.getMessage());
            }
        }
        this.imagePath = directory.getAbsolutePath();
    }

    /**
     * Sets the byte array representation of the image from a given input Drawable. The drawable
     * is first converted to a Bitmap and then setImageFromBitmap is called.
     * If you can, use setImageFromByteArray instead of this one as it is more efficient.
     *
     * @param image Drawable representing the image
     */
    public void setImageFromDrawable(Drawable image, Context appContext) throws RestaurantException {
        Bitmap bitmap = null;

        if (image instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) image;
            if(bitmapDrawable.getBitmap() != null) {
                this.setImageFromBitmap(bitmapDrawable.getBitmap(),appContext);
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
        this.setImageFromBitmap(bitmap,appContext);
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
     * @throws RestaurantException if user id null
     */
    public void setUid(String uid) throws RestaurantException {
        if(uid == null)
            throw new RestaurantException("User ID cannot be null");
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
     * @throws RestaurantException if day of the week is not in range, or date parsing fails.
     */
    public void setDurationLunch(int dayOfWeek,String timeStart, String timeEnd) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationLunch";

        if(dayOfWeek < 0 || dayOfWeek > 6){
            Log.e(METHOD_NAME, "Day of week is not in the expected range 0-6");
            throw new RestaurantException("Day of week is not in the expected range 0-6");
        }

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
     * @throws RestaurantException if day of the week is not in range, or date parsing fails.
     */
    public void setDurationDinner(int dayOfWeek,String timeStart, String timeEnd) throws RestaurantException {
        final String METHOD_NAME = this.getClass().getName()+" - setDurationDinner";

        if(dayOfWeek < 0 || dayOfWeek > 6){
            Log.e(METHOD_NAME, "Day of week is not in the expected range 0-6");
            throw new RestaurantException("Day of week is not in the expected range 0-6");
        }
        Map<String,String> entry = new HashMap<>();
        entry.put("start",timeStart);
        entry.put("end",timeEnd);
        this.timetableDinner.put(String.valueOf(dayOfWeek),entry);
    }

    /**
     * Set the timetable for lunch time of this restaurant.
     * Input parameter is a Map with key the days of the week (from 0 to 6) and as value an array
     * of Date. The array must be of length 2. It should be structured as:
     * [0] startHour
     * [1] endHour
     * @param timetable A(n) (Array)Map of the timetable of the Lunch.
     */
    public void setTimetableLunch(Map<String,Date[]> timetable){
        for(Map.Entry<String,Date[]> entry : timetable.entrySet()){
            Map<String,String> duration = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.getDefault());
            duration.put("start",sdf.format(entry.getValue()[0]));
            duration.put("end",sdf.format(entry.getValue()[1]));
            this.timetableLunch.put(entry.getKey(),duration);
        }
    }

    /**
     * Set the timetable for dinner time of this restaurant.
     * Input parameter is a Map with key the days of the week (from 0 to 6) and as value an array
     * of Date. The array must be of length 2. It should be structured as:
     * [0] startHour
     * [1] endHour
     * @param timetable A(n) (Array)Map of the timetable of the Dinner.
     */
    public void setTimetableDinner(Map<String,Date[]> timetable){
        for(Map.Entry<String,Date[]> entry : timetable.entrySet()){
            Map<String,String> duration = new HashMap<>();
            SimpleDateFormat start = new SimpleDateFormat("HH:mm",Locale.getDefault());
            SimpleDateFormat end = new SimpleDateFormat("HH:mm",Locale.getDefault());
            duration.put("start",start.format(entry.getValue()[0]));
            duration.put("end",end.format(entry.getValue()[1]));
            this.timetableDinner.put(entry.getKey(),duration);
        }
    }

    /**
     *
     * @param reviews An ArrayList of Review(s) to assign to this restaurant object.
     */
    public void setReviews(ArrayList<Review> reviews){ this.reviews = reviews; }
}
