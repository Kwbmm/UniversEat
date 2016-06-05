package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;

public class SearchRestaurantResults extends NavigationDrawer {

    private String query;
    private RecyclerView rv;
    private ProgressBar pb;
    private int lastSortMethod;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_result, mlay);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            SearchRecentSuggestions srs = new SearchRecentSuggestions(this,RestaurantSearchSuggestionProvider.AUTHORITY,RestaurantSearchSuggestionProvider.MODE);
            this.query = intent.getStringExtra(SearchManager.QUERY).trim().toLowerCase();
            if(!this.query.equals("current location"))
                srs.saveRecentQuery(this.query,null);
        }
    }
}
