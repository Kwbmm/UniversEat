package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

/**
 * @author Marco
 * @class Course
 * @date 04/04/16
 * @brief Course class
 */
public class Course {
    private int cid;
    private String name;
    private String description;
    private float price;
    private byte[] image;
    private boolean glutenFree;
    private boolean vegan;
    private boolean vegetarian;
    private boolean spicy;

    /**
     *
     * @return The Course ID
     */
    public int getCid(){return this.cid;}

    /**
     *
     * @return The name of the course
     */
    public String getName(){return this.name;}

    /**
     *
     * @return The description of the course
     */
    public String getDescription(){return this.description;}

    /**
     *
     * @return The price of the course
     */
    public float getPrice(){return this.price;}

    /**
     *
     * @return The image of the course, in base 64 format
     */
    public byte[] getImage64(){return this.image;}

    /**
     *
     * @return The image of the course, in Bitmap format
     */
    public Bitmap getImageBitmap(){
        return BitmapFactory.decodeByteArray(this.image,0,this.image.length);
    }

    /**
     * Returns true if course is gluten-free. False otherwise.
     * @return true or false
     */
    public boolean isGlutenFree(){ return this.glutenFree;}

    /**
     * Returns true if course is vegan. False otherwise.
     * @return true or false
     */
    public boolean isVegan(){ return this.vegan;}

    /**
     * Returns true if course is vegetarian. False otherwise.
     * @return true or false
     */
    public boolean isVegetarian(){ return this.vegetarian;}

    /**
     * Returns true if course is spicy. False otherwise.
     * @return true or false
     */
    public boolean isSpicy(){ return this.spicy;}

    /**
     * Sets the Course ID
     * @param cid Course ID
     */
    public void setCid(int cid){ this.cid = cid;}
    /**
     * Sets the name of the course
     * @param name Name of the course
     */
    public void setName(String name){ this.name = name;}

    /**
     * Sets the description of the course
     * @param description Description of the course
     */
    public void setDescription(String description){ this.description = description;}

    /**
     * Sets the price of the course
     * @param price Price of the course
     */
    public void setPrice(float price){ this.price = price;}

    /**
     * Sets the base 64 encoding of the image
     * @param image byte array of image, encoded in base 64
     */
    public void setImage64(byte[] image){ this.image = image;}

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
     * Sets if the course is gluten-free
     * @param gf true or false
     */
    public void setGlutenFree(boolean gf){ this.glutenFree = gf;}

    /**
     * Sets if the course is vegan
     * @param vegan true or false
     */
    public void setVegan(boolean vegan){ this.vegan = vegan;}

    /**
     * Sets if the course is vegetarian
     * @param vegetarian true or false
     */
    public void setVegetarian(boolean vegetarian){ this.vegetarian = vegetarian;}

    /**
     * Sets if the course is spicy
     * @param spicy true or false
     */
    public void setSpicy(boolean spicy){ this.spicy = spicy;}
}
