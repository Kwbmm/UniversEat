package it.polito.mad.groupFive.restaurantcode;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class SearchResult extends NavigationDrawer {
    public static final String RESTAURANT_SEARCH = "restaurant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_result, mlay);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            boolean isRestaurant = intent.getBooleanExtra(SearchResult.RESTAURANT_SEARCH,false);
            if(isRestaurant){
                String query = intent.getStringExtra(SearchManager.QUERY);
                Log.d(METHOD_NAME,"Rest: "+query);
            }
            else{
                String query = intent.getStringExtra(SearchManager.QUERY);
                Log.d(METHOD_NAME,query);
            }
        }
    }
}
