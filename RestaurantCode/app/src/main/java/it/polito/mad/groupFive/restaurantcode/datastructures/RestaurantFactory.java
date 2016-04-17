package it.polito.mad.groupFive.restaurantcode.datastructures;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * @author Marco
 * @class RestaurantV2
 * @date 2015-04-17
 * @brief Restaurant Factory class
 */
public class RestaurantFactory {

    /**
     * Creates a Restaurant object: either reads from an existing JSON file or creates
     * a new JSON file and returns an empty Restaurant object.
     * @param appContext Android Application Context
     * @param rid ID of the restaurant, use a know restaurant id to fetch it from JSON file.
     * @return An instance of Restaurant
     */
    public static RestaurantV2 createRestaurant(Context appContext, int rid){
        return readJSONFile(appContext, rid);
    }

    private static void createJSONFile(Context appContext,int rid) throws IOException {
        final String METHOD_NAME = RestaurantFactory.class.getName()+" - createJSONFile";

        File file = new File(appContext.getFilesDir(),"r"+rid);
        Writer writer = new FileWriter(file);

        Gson root = new Gson();
        root.toJson(RestaurantV2.class, writer);
        Log.i(METHOD_NAME,"Wrote to file:\n"+root.toJson(RestaurantV2.class));
    }

    private static RestaurantV2 readJSONFile(Context appContext, int rid){
        final String METHOD_NAME = RestaurantFactory.class.getName()+" - readJSONFile";
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
                createJSONFile(appContext, rid);
                return readJSONFile(appContext,rid);
            } catch (IOException e1) {
                Log.e(METHOD_NAME,e1.getMessage());
            }
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
        return null;
    }
}
