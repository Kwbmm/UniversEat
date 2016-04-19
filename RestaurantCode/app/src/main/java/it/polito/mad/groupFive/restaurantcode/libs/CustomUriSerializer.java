package it.polito.mad.groupFive.restaurantcode.libs;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author Marco Ardizzone
 * @class CustomUriSerializer
 * @date 2016-04-19
 * @brief Custom Uri Serializer class for GSon
 */
public class CustomUriSerializer implements JsonSerializer<Uri> {
    @Override
    public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
