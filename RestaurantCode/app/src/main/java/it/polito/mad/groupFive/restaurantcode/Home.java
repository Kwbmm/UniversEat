package it.polito.mad.groupFive.restaurantcode;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusIDFromRestaurantListener;

public class Home extends NavigationDrawer {
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menusshared;
    private MenuAdapter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;
    private View parent;
    private String user;
    private LinearLayout dropdown;
    private boolean drop_visible;
    private RecyclerView rv;
    private ProgressBar pb;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    private  FrameLayout mlay;
    private View home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Every activity should extend navigation drawer activity, no set content view must be called, layout must be inflated using inflate function

        super.onCreate(savedInstanceState);
        mlay= (FrameLayout) findViewById(R.id.frame);
        home=getLayoutInflater().inflate(R.layout.activity_home,null);
        mlay.addView(home);
        //mlay.inflate(this, R.layout.activity_home, mlay);
        parent=mlay;

        this.db = FirebaseDatabase.getInstance();
        getMenus();

        /**
         * These lines of code are for setting up the searchViewMenu and let it know about the activity
         * used to performed searches (SearchResult.java).
         * More info:
         *  http://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget
         */
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchViewMenu = (SearchView) findViewById(R.id.search_viewMenu);
        if(searchViewMenu != null){
            searchViewMenu.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchViewMenu.setIconifiedByDefault(false);
        }

        dropdown= (LinearLayout) findViewById(R.id.dropdown_option);
        drop_visible=false;
        ImageButton option= (ImageButton)findViewById(R.id.opt);
        if(option != null){
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!drop_visible){
                        View options=LayoutInflater.from(getBaseContext()).inflate(R.layout.dropdown_options,null);
                        dropdown.addView(options);
                        drop_visible=true;

                    }else {
                        drop_visible=false;
                        dropdown.removeAllViews();
                    }
                }
            });
        }

        Button searchRestaurantsBtn = (Button) findViewById(R.id.buttonRestaurant);
        if(searchRestaurantsBtn != null ){
            searchRestaurantsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlay.removeView(home);
                    SearchRestaurants sr = new SearchRestaurants();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.frame,sr)
                            .commit();
                }
            });
        }
    }

    @Override
    public void startActivity(Intent intent) {
        final String METHOD_NAME = this.getClass().getName()+" - startActivity";
        /**
         * After spending 3 hours just by trying to send extra parameters to SearchResult activity
         * as explained by the android documentation with no success, I found out that the method
         * onSearchRequested is not available for AppCompat activities. So we need to override
         * startActivity to catch the intent, check if it's an ACTION_SEARCH intent and, if so, add
         * extra data.
         * For more info, see: http://stackoverflow.com/q/26991594/5261306
         */
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            CheckBox cb = (CheckBox) findViewById(R.id.checkBox_searchByRestaurant);
            if(cb != null && cb.isChecked()){}
            intent.putExtra(SearchResult.RESTAURANT_SEARCH,true);
        }
        super.startActivity(intent);
    }

    private void getMenus(){
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingSearchViewData);
        this.rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
        this.dbRoot = this.db.getReference("restaurant");
        if(rv != null){
            MenuAdapter ma = new MenuAdapter(this.rv,this.pb);
            rv.setAdapter(ma);
            LinearLayoutManager llmVertical = new LinearLayoutManager(this);
            llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llmVertical);
            Query menuQuery = this.dbRoot.limitToFirst(10); //Get the first 10 restaurants
            menuQuery.addListenerForSingleValueEvent(new GetMenusIDFromRestaurantListener(ma));
        }
    }

    public static class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private class DistanceMenu extends Menu{

            private float distance;
            public DistanceMenu(Menu m,float distance) throws MenuException {
                super(m.getRid(), m.getMid());
                super.setBeverage(m.isBeverage())
                        .setDescription(m.getDescription())
                        .setName(m.getName())
                        .setPrice(m.getPrice())
                        .setServiceFee(m.isServiceFee()).setType(m.getType());
                this.distance = distance;
            }

            public float getDistance(){ return this.distance; }
        }

        private SortedList<DistanceMenu> menus;
        private RecyclerView rv;
        private ProgressBar pb;

        private MenuAdapter(RecyclerView rv,ProgressBar pb){
            this.rv = rv;
            this.pb = pb;
            this.menus = new SortedList<DistanceMenu>(DistanceMenu.class,
                    new SortedList.Callback<DistanceMenu>() {
                        @Override
                        public int compare(DistanceMenu o1, DistanceMenu o2) {
                            return (o1.getDistance() - o2.getDistance() >= 0) ? -1 : 1;
                        }

                        @Override
                        public void onInserted(int position, int count) {
                            notifyItemRangeInserted(position,count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            notifyItemRangeRemoved(position,count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            notifyItemMoved(fromPosition,toPosition);
                        }

                        @Override
                        public void onChanged(int position, int count) {
                            notifyItemRangeChanged(position,count);
                        }

                        @Override
                        public boolean areContentsTheSame(DistanceMenu oldItem, DistanceMenu newItem) {
                            return oldItem.getMid().equals(newItem.getMid());
                        }

                        @Override
                        public boolean areItemsTheSame(DistanceMenu item1, DistanceMenu item2) {
                            return item1.getMid().equals(item2.getMid());
                        }
                    });
        }

        /**
         * Add a new child to the adapter. Elements are added according to their weights: elements
         * with lower weight are displayed before those with higher weight.
         *
         * @param m Object to insert
         * @param distance Distance of the object with respect to your location
         */
        public void addChild(Menu m,float distance){
            final String METHOD_NAME = this.getClass().getName()+" - filterByTicket";
            try {
                DistanceMenu wm = new DistanceMenu(m,distance);
                this.menus.add(wm);
                if(rv.getVisibility() == View.GONE){
                    pb.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
            } catch (MenuException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }

        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,null);
            return new MenuViewHolder(menu_view);
        }

        @Override
        public void onBindViewHolder(final MenuViewHolder holder, int position) {
            Menu menu = menus.get(position);
            holder.menu_description.setText(menu.getDescription());
            holder.menu_name.setText(menu.getName());
            holder.menu_price.setText(menu.getPrice()+"€");
        }

        @Override
        public int getItemCount() {
            return this.menus.size();
        }

        public SortedList<DistanceMenu> getMenus(){ return this.menus; }
    }

    public class onCardClick implements View.OnClickListener{
        private int position;
        private String rid;
        private String mid;

        public onCardClick(int position, String rid, String mid){
            this.position=position;
            this.rid=rid;
            this.mid=mid;
        }

        @Override
        public void onClick(View v) {
            Intent restinfo=new Intent(getBaseContext(),User_info_view.class);
            Log.v("rid",rid+"");
            restinfo.putExtra("rid",this.rid);
            restinfo.putExtra("mid",this.mid);
            startActivity(restinfo);
        }
    }
}