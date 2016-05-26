package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

/**
 * @author Marco Ardizzone
 * @class User
 * @date 2016-04-23
 * @brief User class
 *
 */
public abstract class User {

    transient protected Context appContext;

    protected String uid;
    protected String name;
    protected String surname;
    protected String userName;
    protected String password;
    protected String address;
    protected String email;
    protected byte[] image;
    protected ArrayList<Review> reviews = new ArrayList<>();

    /**
     * Reads the JSON file corresponding to this user and fills this class with the data found
     * in the file.
     * If the file doesn't exist CreateJSONFile is called and the file is created. Then the reading
     * is performed again: this time the file will be found and fields of this class will be filled
     * with null values (because the created file is empty).
     *
     * If a fail occurs, the error message is logged and this method returns null.
     *
     * @return A User object or null if fails.
     */
    protected abstract User readJSONFile();

    /**
     * Create the JSON file corresponding to this object. The JSON file is identified by the
     * user ID set at instantiation time.
     * If an error occurs, the error is logged.
     */
    protected abstract void createJSONFile();

    /**
     * Copy all the data took from the JSON file on this object.
     * @param dummy A dummy User object, on which the JSON data is written to.
     */
    protected abstract void copyData(User dummy);

    /**
     * Saves the current data store in this object to its JSON file.
     * Note that this method also saves the data for the custom sub-objects embedded inside this
     * class (Review).
     * In case of fail, the error is logged and a UserException is thrown.
     *
     * @throws UserException If writing JSON file fails
     */
    protected abstract void saveData() throws UserException;

    /**
     * Fetch the data corresponding to the User ID of this object from the JSON file.
     * This method is in charge of filling all the other classes of this data set (Review).
     * If an error occurs, it is logged and a UserException is thrown.
     *
     * @throws UserException If fetch fails
     */
    protected abstract void getData() throws UserException;

    /**
     *
     * @return The unique identifier of this User object
     */
    public String getUid() {
        return this.uid;
    }

    /**
     *
     * @return The name of user
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return The surname of user
     */
    public String getSurname() {
        return this.surname;
    }

    /**
     *
     * @return The address of user
     */
    public String getAddress() {
        return this.address;
    }

    /**
     *
     * @return The userName of the user
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     *
     * @return The password of the user
     */
    public String getPassword(){ return this.password; }

    /**
     *
     * @return The email of the user
     */
    public String getEmail(){ return this.email; }

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
     * @return An ArrayList of reviews belonging to this user
     */
    public ArrayList<Review> getReviews(){ return this.reviews; }

    /**
     * Get the Review object corresponding to the supplied ID.
     * If no Review object is matching the input id, this method returns null.
     * An exception is thrown in case of negative review id.
     *
     * @param revID A positive integer uniquely identifying the review object
     * @return A review object matching the input id, or null if nothing is found.
     * @throws UserException If review id is negative
     */
    public Review getReviewByID(String revID) throws UserException {
        //TODO Fix this
        for(Review r : this.reviews)
            if(r.getRevID() == revID)
                return r;
        return null;
    }

    /**
     * Get the ArrayList of Review objects made by this user matching the supplied restaurant ID.
     * If no Review matches the input restaurant ID, an empty ArrayList is returned
     *
     * @param rid A positive integer uniquely identifying a restaurant.
     * @return An ArrayList of Review objects
     * @throws UserException If restaurant id is negative.
     */
    public ArrayList<Review> getReviewsByRestaurantID(String rid) throws UserException {
        ArrayList<Review> returnRes = new ArrayList<>();
        for(Review r : this.reviews)
            if(r.getRid() == rid) //TODO Fix this
                returnRes.add(r);
        return returnRes;
    }

    /**
     *
     * @param name Name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param surname Surname of the user
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     *
     * @param address Address of the user
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @param userName of the user
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @param password Password of the user
     */
    public void setPassword(String password){
        //TODO Implement some encryption for password - Sad LIOY is sad;
        this.password = password;
    }

    /**
     *
     * @param email A string representing an email address
     */
    public void setEmail(String email){ this.email = email; }

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
     * @param r ArrayList of reviews made by the user.
     */
    public void setReviews(ArrayList<Review> r){ this.reviews = r; }
}
