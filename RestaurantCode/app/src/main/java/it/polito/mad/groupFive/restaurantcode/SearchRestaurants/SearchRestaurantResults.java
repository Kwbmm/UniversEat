package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.listeners.GetRestaurantListener;

public class SearchRestaurantResults extends NavigationDrawer {

    private String query;
    private RecyclerView rv;
    private ProgressBar pb;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    private Context context;
    private double inputLatitude, inputLongitude;
    private ArrayList<RestaurantAdapter.DistanceRestaurant> restaurants;

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
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        this.context = this;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_data, menu);
        //Setup the buttons of the toolbar for the menu
        final MenuItem filterButton = menu.findItem(R.id.filterButton);
        filterButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                final Dialog customADB = new Dialog(SearchRestaurantResults.this);
                customADB.setContentView(R.layout.custom_search_restaurant_alert_dialog);
                customADB.setCancelable(true);

                /*
                This lines of code are for adjusting the size of the dialog box.
                See: http://stackoverflow.com/a/8836169
                 */
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(customADB.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                customADB.show();
                customADB.getWindow().setAttributes(lp);
                AppCompatSeekBar distanceSlider = (AppCompatSeekBar) customADB.findViewById(R.id.distanceSlider);
                distanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        TextView distanceValue = (TextView) customADB.findViewById(R.id.textView_distanceValue);
                        distanceValue.setText(progress+1+" Km");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                TextView cancelButton = (TextView) customADB.findViewById(R.id.button_Cancel);
                final CheckBox cbDistance = (CheckBox) customADB.findViewById(R.id.distanceCustomADB);
                cbDistance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cbDistance.isChecked())
                            customADB.findViewById(R.id.linearLayout_Distance).setVisibility(View.VISIBLE);
                        else
                            customADB.findViewById(R.id.linearLayout_Distance).setVisibility(View.INVISIBLE);
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customADB.dismiss();
                    }
                });
                TextView filterButton = (TextView) customADB.findViewById(R.id.button_Filter);
                filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isFiltered = false;
                        CheckBox cbTicket = (CheckBox) customADB.findViewById(R.id.ticketCustomADB);
                        CheckBox cbRating = (CheckBox) customADB.findViewById(R.id.ratingCustomADB);
                        CheckBox cbOpen = (CheckBox) customADB.findViewById(R.id.openCustomADB);
                        CheckBox cbDistance = (CheckBox) customADB.findViewById(R.id.distanceCustomADB);
                        Integer distance = null;

                        boolean[] selectedItems = new boolean[]{
                                cbTicket.isChecked(),
                                cbRating.isChecked(),
                                cbOpen.isChecked(),
                                cbDistance.isChecked()
                        };

                        if(restaurants == null){ //Save the original set
                            restaurants = new ArrayList<>();
                            for (int i = 0; i < ((RestaurantAdapter)rv.getAdapter()).getRestaurants().size(); i++) {
                                restaurants.add(((RestaurantAdapter)rv.getAdapter()).getRestaurants().get(i));
                            }
                        }
                        RestaurantAdapter ra = new RestaurantAdapter(rv,pb,context);
                        for (int i = 0; i < restaurants.size(); i++) {
                            RestaurantAdapter.DistanceRestaurant dr = restaurants.get(i);
                            ra.addChildWithDistance(dr,dr.getDistance());
                        }
                        for (int i = 0; i < selectedItems.length; i++) {
                            if(selectedItems[i]){
                                if(i == 3){ //Filter by distance, get the distance
                                    distance = ((SeekBar) customADB.findViewById(R.id.distanceSlider)).getProgress();
                                    distance++; //Fix for the slider range
                                    distance*= 1000; //Transform to meters
                                }
                                ra.filter(i,distance);
                                isFiltered = true;
                            }
                        }
                        if(isFiltered){
                            ra.updateEntries();
                        }
                        rv.setAdapter(ra);
                        customADB.dismiss();
                    }
                });

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
