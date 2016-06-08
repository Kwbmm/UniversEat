package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.listeners.GetRestaurantListener;

public class SearchRestaurantResults extends NavigationDrawer {

    private String query;
    private RecyclerView rv;
    private ProgressBar pb;
    private int lastSortMethod;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    private Context context;
    private double inputLatitude, inputLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_restaurant_results, mlay);
        Bundle b = getIntent().getExtras();
        this.inputLatitude = b.getDouble("lat");
        this.inputLongitude = b.getDouble("lon");
        this.rv = (RecyclerView)findViewById(R.id.recyclerView_DataView);
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingSearchViewData);
        if(this.rv != null)
            this.showRestaurant();
    }

    private void showRestaurant() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRoot = db.getReference("restaurant");
        Location here = new Location("dummy provider");
        here.setLatitude(this.inputLatitude);
        here.setLongitude(this.inputLongitude);
        RestaurantAdapter ra = new RestaurantAdapter(this.rv,this.pb,this);
        this.rv.setAdapter(ra);
        LinearLayoutManager llmVertical = new LinearLayoutManager(this);
        llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
        this.rv.setLayoutManager(llmVertical);
        Query restaurantQuery = dbRoot.orderByChild("rating").limitToLast(30); //Pick the 30 best restaurants
        restaurantQuery.addListenerForSingleValueEvent(new GetRestaurantListener(ra,here,this));
    }
}
