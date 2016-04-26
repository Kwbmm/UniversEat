package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;
import it.polito.mad.groupFive.restaurantcode.libs.CustomUriDeserializer;
import it.polito.mad.groupFive.restaurantcode.libs.CustomUriSerializer;

/**
 * @author Marco Ardizzone
 * @class RestaurantOwner
 * @date 2016-04-22
 * @brief Restaurant Owner class
 *
 */
public class RestaurantOwner extends User{

    private ArrayList<Integer> restaurantIDs = new ArrayList<>();

    /**
     * Create a RestaurantOwner object. Requires, as parameter, the Android Application Context of
     * the activity instantiating this class.
     * The ID uniquely identifying this restaurant owner is generated automatically.
     *
     * @param c Application Context
     * @throws RestaurantOwnerException If restaurant owner ID is negative or JSON file read fails.
     */
    public RestaurantOwner(Context c) throws RestaurantOwnerException {
        this(c,RestaurantOwner.randInt());
    }

    /**
     * Create a RestaurantOwner object. Requires, as parameters, the Android Application Context of
     * the activity instantiating this class and a positive integer uniquely identifying the
     * restaurant owner object.
     *
     * @param c Application Context
     * @param uid Positive Integer unique identifier
     * @throws RestaurantOwnerException If restaurant owner id is negative or instantiation fails.
     */
    public RestaurantOwner(Context c, int uid) throws RestaurantOwnerException {
        final String METHOD_NAME = this.getClass().getName()+" - constructor";

        if(uid < 0)
            throw new RestaurantOwnerException("Restaurant Owner ID must be positive");
        this.uid = uid;
        this.appContext = c;

        RestaurantOwner dummy;
        if((dummy=this.readJSONFile())== null){
            Log.e(METHOD_NAME, "Dummy is null");
            throw new RestaurantOwnerException("RestaurantOwner dummy object used to fill the current object is null");
        }
        else
            this.copyData(dummy);
    }

    /**
     * Reads the JSON file corresponding to this user and fills this class with the data found
     * in the file.
     * If the file doesn't exist CreateJSONFile is called and the file is created. Then the reading
     * is performed again: this time the file will be found and fields of this class will be filled
     * with null values (because the created file is empty).
     *
     * If a fail occurs, the error message is logged and this method returns null.
     *
     * @return A RestaurantOwner object or null if fails.
     */
    @Override
    protected RestaurantOwner readJSONFile(){
        final String METHOD_NAME = this.getClass().getName()+" - readJSONFile";

        InputStream is;
        try {
            is = appContext.openFileInput("ro"+this.uid+".json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputString = br.readLine()) != null ) {
                stringBuilder.append(inputString);
            }

            is.close();
            Gson root = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new CustomUriDeserializer())
                    .create();
            Log.i(METHOD_NAME, "Loading data into structure...");
            return root.fromJson(stringBuilder.toString(), RestaurantOwner.class);
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
     * user ID set at instantiation time.
     * If an error occurs, the error is logged.
     */
    @Override
    protected void createJSONFile() {
        final String METHOD_NAME = this.getClass().getName()+" - createJSONFile";

        File file = new File(appContext.getFilesDir(),"ro"+this.uid+".json");
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            Gson root = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            root.toJson(this, writer);
            writer.close();
            Log.i(METHOD_NAME,"Wrote to file:\n"+root.toJson(this));
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy User object, on which the JSON data is written to.
     */
    @Override
    protected void copyData(User dummy) {
        this.uid = dummy.getUid();
        this.name = dummy.getName();
        this.surname = dummy.getSurname();
        this.userName = dummy.getUserName();
        this.password = dummy.getPassword();
        this.address = dummy.getAddress();
        this.email = dummy.getEmail();
        this.image = dummy.getImageUri();
        this.reviews = dummy.getReviews();
        this.restaurantIDs = ((RestaurantOwner)dummy).getRestaurantIDs();
    }

    /**
     * Saves the current data stored in this object to its JSON file.
     * Note that this method also saves the data for the custom sub-objects embedded inside this
     * class (Review).
     * In case of fail, the error is logged and a RestaurantOwnerException is thrown.
     *
     * @throws RestaurantOwnerException If writing JSON file fails
     */
    @Override
    public void saveData() throws RestaurantOwnerException {
        final String METHOD_NAME = this.getClass().getName()+" - saveData";

        Gson root = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new CustomUriSerializer())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        String output = root.toJson(this);

        try {
            FileOutputStream fos = this.appContext.openFileOutput("ro"+this.uid+".json",Context.MODE_PRIVATE);
            fos.write(output.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new RestaurantOwnerException(e.getMessage());
        }
    }

    /**
     * Fetch the data corresponding to the User ID of this object from the JSON file.
     * This method is in charge of filling all the other classes of this data set (Review).
     * If an error occurs, it is logged and a RestaurantOwnerException is thrown.
     *
     * @throws RestaurantOwnerException If fetch fails
     */
    @Override
    public void getData() throws RestaurantOwnerException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            FileInputStream fis = appContext.openFileInput("ro"+this.uid+".json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Gson root = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new CustomUriDeserializer())
                    .create();
            RestaurantOwner dummy = root.fromJson(json,RestaurantOwner.class);
            this.copyData(dummy);
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new RestaurantOwnerException(e.getMessage());
        }
    }

    /**
     * Get the ArrayList of Restaurant IDs belonging to this restaurant owner.
     * @return ArrayList of Restaurant IDs
     */
    public ArrayList<Integer> getRestaurantIDs(){ return this.restaurantIDs; }

    /**
     * Returns true if the supplied input ID matches a restaurant id owned by this restaurant owner.
     * Otherwise this method returns false.
     *
     * @param rid A positive integer uniquely identifying a restaurant
     * @return True if restaurant belongs to this restaurant owner, false otherwise.
     * @throws RestaurantOwnerException If supplied restaurant id is negative.
     */
    public boolean ownsRestaurant(int rid) throws RestaurantOwnerException {
        if(rid < 0)
            throw new RestaurantOwnerException("Restaurant ID must be positive");
        for(int i : this.restaurantIDs)
            if(i == rid)
                return true;
        return false;
    }

    /**
     *
     * @param restaurantIDs An ArrayList of Restaurant IDs (Integers) belonging to this
     *                      Restaurant Owner.
     */
    public void setRestaurants(ArrayList<Integer> restaurantIDs) throws RestaurantOwnerException {
        for(int i : restaurantIDs)
            if(i < 0)
                throw new RestaurantOwnerException("Restaurant ID "+i+" must be positive");
        this.restaurantIDs = restaurantIDs;
    }
}
