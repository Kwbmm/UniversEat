package it.polito.mad.groupFive.restaurantcode;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import it.polito.mad.groupFive.restaurantcode.datastructures.DataManager;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.DataManagerException;

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
            String query = intent.getStringExtra(SearchManager.QUERY);
            try {
                DataManager dm = new DataManager(getApplicationContext());
                if(isRestaurant){ //Restaurant search
                    if(dm.getRestaurants().size() == 0)
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.SearchResult_toastNoRestaurants),
                                Toast.LENGTH_LONG)
                                .show();
                    else{
                        for (Restaurant r : dm.getRestaurants()){
                            if(r.getName().equals(query)){
                                //TODO Show this restaurant
                                Log.i(METHOD_NAME,"Matched restaurant "+r.getName());
                            }
                        }
                    }
                }
                else{ //Menu search
                    if(dm.getMenus().size() == 0)
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.SearchResult_toastNoMenus),
                                Toast.LENGTH_LONG)
                                .show();
                    else{
                        for (Menu m : dm.getMenus()){
                            if(m.getName().equals(query)){
                                //TODO Show this menu
                                Log.i(METHOD_NAME,"Matched menu: "+m.getName());
                            }
                        }
                    }
                }
            } catch (DataManagerException e) {
                Log.e(METHOD_NAME,e.getMessage());
                Toast.makeText(getApplicationContext(),
                        getString(R.string.SearchResult_toastFailLoadDM),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
