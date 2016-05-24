package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

/**
 * @author Marco Ardizzone
 * @class Menu
 * @date 2016-04-18
 * @brief Menu class
 */
public class Menu {

    private String rid; //The restaurant ID this menu belongs to
    private String mid;
    private String name=null;
    private String description=null;
    private float price;
    private String imageName;
    private String imagePath;
    private int type;
    private boolean beverage;
    private boolean serviceFee;
    private ArrayList<Course> courses = new ArrayList<>();

    public Menu() {
    }

    public Menu(String rid) {
        this.rid = rid;
    }

    Map<String, Object> toMap(){
        final String METHOD_NAME = this.getClass().getName()+" - saveData";
        HashMap<String, Object> output = new HashMap<>();
        output.put("mid",this.mid);
        output.put("name",this.name);
        output.put("description",this.description);
        output.put("price",this.price);
        output.put("imageName",this.imageName);
        output.put("imagePath",this.imagePath);
        output.put("type",this.type);
        output.put("beverage",this.beverage);
        output.put("serviceFee",this.serviceFee);
        HashMap<String, Object> courseMap = new HashMap<>();
        for (Course c : this.courses){
            courseMap.put(c.getCid(),c.toMap());
        }
        output.put("courses",courseMap);
        return output;
    }

    /**
     *
     * @return The ID
     */
    public String getMid(){ return this.mid; }

    /**
     *
     * @return The name of the menu
     */
    public String getName(){ return this.name; }

    /**
     *
     * @return The description of the menu
     */
    public String getDescription(){ return this.description; }

    /**
     *
     * @return The price of the menu
     */
    public float getPrice(){ return this.price; }

    public Bitmap getImageBitmap() throws MenuException {
        final String METHOD_NAME = this.getClass().getName()+" - getImageBitmap";
        try {
            File f=new File(this.imagePath, this.imageName);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e) {
            Log.e(METHOD_NAME,e.getMessage());
            throw new MenuException(e.getMessage());
        }
    }

    /**
     * This method returns a textual representation of the type of menu. It can be:
     *  0 = "day" = Menu of the day
     *  1 = "fixed" = Fixed menu
     *  2 = "fixed options" = Fixed menu with options
     *  3 = "complete" = Complete menu
     * @return Textual representation of the type of menu
     */
    public String getTypeAsString(){
        switch(this.type){
            case 0:
                return "day"; //Menu of the day
            case 1:
                return "fixed";
            case 2:
                return "fixed options"; //Menu fixed with options
            case 3:
                return "complete"; //Complete menu
            default:
                return "ERROR";
        }
    }

    /**
     *
     * @return The integer associated with the menu type
     */
    public int getType(){ return this.type; }

    /**
     *
     * @return The list of all the courses in the menu
     */
    public ArrayList<Course> getCourses(){ return this.courses; }

    /**
     * This method returns a particular course, given its ID.
     *
     * @param id The id of the course
     * @return The requested course or null if nothing is found.
     */
    public Course getCourseByID(String id){
        for(Course course : this.courses)
            if(course.getCid().equals(id))
                return course;
        return null;
    }

    /**
     * This method returns a particular course, given its name.
     * If the course is not found, it returns null.
     *
     * @param name Name of the course to search for.
     * @return The requested course or null if nothing is found.
     */
    public Course getCourseByName(String name){
        for(Course course : this.courses)
            if(course.getName().equals(name))
                return course;
        return null;
    }

    /**
     * This method returns the list of all gluten-free courses.
     *
     * @return The list of gluten-free courses
     */
    public ArrayList<Course> getGlutenFreeCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses){
            if(course.isGlutenFree())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the vegan courses.
     *
     * @return The list of vegan courses
     */
    public ArrayList<Course> getVeganCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses){
            if(course.isVegan())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the vegetarian courses.
     *
     * @return The list of vegetarian courses
     */
    public ArrayList<Course> getVegetarianCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses){
            if(course.isVegetarian())
                output.add(course);
        }
        return output;
    }

    /**
     * This method returns the list of all the spicy courses.
     *
     * @return The list of spicy courses
     */
    public ArrayList<Course> getSpicyCourses(){
        ArrayList<Course> output = new ArrayList<>();
        for(Course course: this.courses)
            if(course.isSpicy())
                output.add(course);

        return output;
    }

    /**
     *
     * @return True if beverage is included, false otherwise.
     */
    public boolean isBeverage() {
        return beverage;
    }

    /**
     *
     * @return True if there is an extra fee to pay for the service, false otherwise.
     */
    public boolean isServiceFee() {
        return serviceFee;
    }


    /**
     * Returns the tags associated to all the Courses in this menu. If no tags are added to the
     * Course objects of this Menu object, the returned ArrayMap is empty.
     *
     * @return An ArrayList of Strings representing the tag.
     */
    public ArrayList<String> getTags(){
        ArrayList<String> output = new ArrayList<>();
        for(Course c : this.courses)
            if(c.getTags().size() != 0)
                output.addAll(c.getTags());
        return output;
    }

    /**
     *
     * @param name The name of the menu
     */
    public void setName(String name){ this.name = name; }

    /**
     *
     * @param description The description of the menu
     */
    public void setDescription(String description){ this.description = description; }

    /**
     *
     * @return Restaurant reference to fetch data
     */

    public String getRid() {
        return rid;
    }

    /**
     *
     * @param price The price of the menu

     */
    public void setPrice(float price){ this.price = price; }

    /**
     * Sets the byte array representation of the image from a given input Bitmap.
     *
     * @param image Bitmap representing the image
     */
    public void setImageFromBitmap(Bitmap image, Context appContext) throws MenuException {
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
            throw new MenuException(fnfe.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(METHOD_NAME,e.getMessage());
                throw new MenuException(e.getMessage());
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
    public void setImageFromDrawable(Drawable image, Context appContext) throws MenuException {
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
     * This method sets the type of the menu. The type can be:
     *  0 = Menu of the day
     *  1 = Fixed menu
     *  2 = Fixed menu with options
     *  3 = Complete menu
     *
     *  Returns true in case of success, false otherwise.
     *
     * @param type The type of the menu, must be between 0 and 3
     * @return true or false.
     */
    public boolean setType(int type){
        switch(type){
            case 0:
                this.type = 0;
                return true;
            case 1:
                this.type = 1;
                return true;
            case 2:
                this.type = 2;
                return true;
            case 3:
                this.type = 3;
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param courses The list of courses of this menu
     */
    public void setCourses(ArrayList<Course> courses){ this.courses = courses; }

    /**
     * Sets if beverage is included.
     *
     * @param beverage true or false
     */
    public void setBeverage(boolean beverage) {
        this.beverage = beverage;
    }

    /**
     * Sets if there is an extra fee to pay for the service.
     *
     * @param serviceFee true or false
     */
    public void setServiceFee(boolean serviceFee) { this.serviceFee = serviceFee; }
}
