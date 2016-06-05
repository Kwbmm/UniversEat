package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.content.SearchRecentSuggestionsProvider;

public class RestaurantSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "it.polito.mad.groupFive.restaurantcode.SearchRestaurants.RestaurantSearchSuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public RestaurantSearchSuggestionProvider(){
        this.setupSuggestions(AUTHORITY,MODE);
    }
}
