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

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.libs.CustomByteArrayAdapter;
import it.polito.mad.groupFive.restaurantcode.libs.CustomUriAdapter;

/**
 * @author Marco Ardizzone
 * @class User
 * @date 2016-04-23
 * @brief Customer class
 *
 */
public class Customer extends User {

    private ArrayList<Integer> favourites = new ArrayList<>();

    /**
     * Create a Customer object. Requires, as parameter, the Android Application Context of the
     * activity instantiating this class.
     * The ID uniquely identifying this customer is generated automatically.
     *
     * @param c Application Context
     * @throws CustomerException If customer ID is negative or JSON file read fails.
     */
    public Customer(Context c) throws CustomerException {
        this(c,"asdasd");
    }

    /**
     * Create a Customer object. Requires, as parameters, the Android Application Context of the
     * activity instantiating this class and a positive integer uniquely identifying the customer
     * object.
     *
     * @param c Application Context
     * @param uid Positive Integer unique identifier
     * @throws CustomerException If customer id is negative or instantiation fails.
     */
    public Customer(Context c, String uid) throws CustomerException {
        final String METHOD_NAME = this.getClass().getName()+" - constructor";

        if(uid == null)
            throw new CustomerException("Customer ID cannot be null");
        this.uid = uid;
        this.appContext = c;

        Customer dummy;
        if((dummy=this.readJSONFile())== null){
            Log.e(METHOD_NAME, "Dummy is null");
            throw new CustomerException("Customer dummy object used to fill the current object is null");
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
     * @return A Customer object or null if fails.
     */
    @Override
    protected Customer readJSONFile(){
        final String METHOD_NAME = this.getClass().getName()+" - readJSONFile";

        InputStream is;
        try {
            is = appContext.openFileInput("c"+this.uid+".json");
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
            Log.i(METHOD_NAME, "Loading data into structure...");
            return root.fromJson(stringBuilder.toString(), Customer.class);
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

        File file = new File(appContext.getFilesDir(),"c"+this.uid+".json");
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
        this.image = dummy.getImageByteArray();
        this.reviews = dummy.getReviews();
        this.favourites = ((Customer) dummy).getFavourites();
    }

    /**
     * Saves the current data store in this object to its JSON file.
     * Note that this method also saves the data for the custom sub-objects embedded inside this
     * class (Review).
     * In case of fail, the error is logged and a UserException is thrown.
     *
     * @throws CustomerException If writing JSON file fails
     */
    @Override
    public void saveData() throws CustomerException {
        final String METHOD_NAME = this.getClass().getName()+" - saveData";

        Gson root = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new CustomUriAdapter())
                .registerTypeHierarchyAdapter(byte[].class, new CustomByteArrayAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        String output = root.toJson(this);

        try {
            FileOutputStream fos = this.appContext.openFileOutput("c"+this.uid+".json",Context.MODE_PRIVATE);
            fos.write(output.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new CustomerException(e.getMessage());
        }
    }

    /**
     * Fetch the data corresponding to the User ID of this object from the JSON file.
     * This method is in charge of filling all the other classes of this data set (Review).
     * If an error occurs, it is logged and a CustomerException is thrown.
     *
     * @throws CustomerException If fetch fails
     */
    @Override
    public void getData() throws CustomerException {
        final String METHOD_NAME = this.getClass().getName()+" - getData";
        try {
            FileInputStream fis = appContext.openFileInput("c"+this.uid+".json");
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
            Customer dummy = root.fromJson(json,Customer.class);
            this.copyData(dummy);
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
            throw new CustomerException(e.getMessage());
        }
    }

    /**
     *
     * @return ArrayList of user's favourite restaurants IDs
     */
    public ArrayList<Integer> getFavourites() {
        return this.favourites;
    }

    /**
     * Returns true if the supplied restaurant ID matches one of the user's favourite restaurants.
     * False otherwise.
     *
     * @param rid A positive integer uniquely identifying a restaurant.
     * @return True if restaurant id is in the set of favourite restaurant's IDs.
     * @throws CustomerException If supplied restaurant ID is negative
     */
    public boolean isFavourite(int rid) throws CustomerException {
        if(rid < 0 )
            throw new CustomerException("Restaurant ID must be positive");
        for(int i : this.favourites)
            if(i == rid)
                return true;
        return false;
    }

    /**
     *
     * @param favourites: the ArrayList of user's favourite restaurants IDs
     * @throws CustomerException If one of the restaurant IDs is negative.
     */
    public void setFavourites(ArrayList<Integer> favourites) throws CustomerException {
        for(int i : favourites)
            if(i < 0)
                throw new CustomerException("Restaurant ID "+i+" must be positive");
        this.favourites = favourites;
    }

}
