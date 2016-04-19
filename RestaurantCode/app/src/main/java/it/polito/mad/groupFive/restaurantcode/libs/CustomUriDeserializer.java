package it.polito.mad.groupFive.restaurantcode.libs;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author Marco Ardizzone
 * @class CustomUriDeserializer
 * @date 2016-04-19
 * @brief Custom Uri deserializer class for GSon
 */
public class CustomUriDeserializer implements JsonDeserializer<Uri> {
    @Override
    public Uri deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Uri.parse(json.getAsString());
    }
}
