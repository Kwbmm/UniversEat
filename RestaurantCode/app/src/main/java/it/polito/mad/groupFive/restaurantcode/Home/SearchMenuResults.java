package it.polito.mad.groupFive.restaurantcode.Home;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.New_Menu_details;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusIDFromCourseListener;

public class SearchMenuResults extends NavigationDrawer {
    private ArrayList<MenuAdapter.WeightedMenu> menus;
    private String[] query;
    private RecyclerView rv;
    private ProgressBar pb;
    private DatabaseReference dbRoot;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_menu_results, mlay);

        this.context=this;
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingSearchViewData);
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY).trim().toLowerCase().split(" ");
            showMenus();
        }
    }

    private void showMenus(){
        final String METHOD_NAME = this.getClass().getName()+" - showMenus";
        this.rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
        if (rv != null){
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            this.dbRoot = db.getReference("course");
            MenuAdapter ma = new MenuAdapter(this.rv, this.pb,context);
            rv.setAdapter(ma);
            LinearLayoutManager llmVertical = new LinearLayoutManager(this);
            llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llmVertical);
            Query menuQuery = this.dbRoot.orderByChild(this.query[0]).equalTo(true);
            menuQuery.addListenerForSingleValueEvent(new GetMenusIDFromCourseListener(ma, this.query,getApplicationContext()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_data, menu);
        //Setup the buttons of the toolbar for the menu
        final MenuItem filterButton = menu.findItem(R.id.filterButton);
        filterButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                String[] items = getResources().getStringArray(R.array.filterCurtainMenuItems);
                final boolean[] selectedItems = new boolean[items.length];
                AlertDialog.Builder filterCurtain = new AlertDialog.Builder(SearchMenuResults.this);
                filterCurtain
                        .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                selectedItems[which] = isChecked;
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.SearchResult_filterConfirmButtonText), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean isFiltered = false;
                                if(menus == null){ //Save the original set
                                    menus = new ArrayList<>();
                                    for (int i = 0; i < ((MenuAdapter)rv.getAdapter()).getMenus().size(); i++) {
                                        menus.add(((MenuAdapter)rv.getAdapter()).getMenus().get(i));
                                    }
                                }
                                MenuAdapter ma = new MenuAdapter(rv,pb,context);
                                for (int i = 0; i < menus.size(); i++) {
                                    MenuAdapter.WeightedMenu wm = menus.get(i);
                                    ma.addChild(wm,wm.getWeight());
                                }
                                for (int i = 0; i < selectedItems.length; i++) {
                                    if(selectedItems[i]){
                                        ma.filter(i);
                                        isFiltered = true;
                                    }
                                }
                                if(isFiltered){
                                    ma.updateEntries();
                                }
                                rv.setAdapter(ma);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.SearchResult_filterCancelButtonText), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Arrays.fill(selectedItems,false);
                            }
                        })
                        .show();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public static class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private class WeightedMenu extends Menu{

            private int weight;
            private boolean toKeep;
            public WeightedMenu(Menu m,int weight) throws MenuException {
                super(m.getRid(), m.getMid());
                super.setBeverage(m.isBeverage())
                        .setDescription(m.getDescription())
                        .setName(m.getName())
                        .setPrice(m.getPrice())
                        .setImageLocal(m.getImageLocalPath())
                        .setServiceFee(m.isServiceFee())
                        .setSpicy(m.isSpicy())
                        .setGlutenfree(m.isGlutenfree())
                        .setVegan(m.isVegan())
                        .setVegetarian(m.isVegetarian())
                        .setType(m.getType());
                this.weight = weight;
                this.toKeep = false;
            }

            public int getWeight(){ return this.weight; }
        }

        private SortedList<WeightedMenu> menus;
        private RecyclerView rv;
        private ProgressBar pb;
        private Context context;

        public MenuAdapter(RecyclerView rv,ProgressBar pb,Context context){
            this.rv = rv;
            this.pb = pb;
            this.context=context;
            this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                    new SortedList.Callback<WeightedMenu>() {
                        @Override
                        public int compare(WeightedMenu o1, WeightedMenu o2) {
                            return o1.getWeight() <= o2.getWeight() ? -1 : 1;
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
                        public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                            return oldItem.getMid().equals(newItem.getMid());
                        }

                        @Override
                        public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                            return item1.getMid().equals(item2.getMid());
                        }
                    });
        }

        /**
         * Add a new child to the adapter. Elements are added according to their weights: elements
         * with lower weight are displayed before those with higher weight.
         *
         * @param m Object to insert
         * @param weight Weight of the object
         */
        public void addChild(Menu m,int weight){
            final String METHOD_NAME = this.getClass().getName()+" - addChild";
            try {
                WeightedMenu wm = new WeightedMenu(m,weight);

                //Check if menu is not duplicate
                for(int i =0; i < this.menus.size(); i++)
                    if(this.menus.get(i).getMid().equals(wm.getMid()))
                        return;

                this.menus.add(wm);
                if(rv.getVisibility() == View.GONE){
                    pb.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
                this.rv.scrollToPosition(0);
            } catch (MenuException e) {
                //Log.e(METHOD_NAME,e.getMessage());
            }
        }

        public void filter(int choice){
            switch (choice){
                case 0: //By beverage
                    this.filterByBeverage();
                    break;
                case 1: //By service fee
                    this.filterByServiceFee();
                    break;
                case 2: //Vegan
                    this.filterByVegan();
                    break;
                case 3: //Vegetarian
                    this.filterByVegetarian();
                    break;
                case 4: //Price 1 - 10
                    this.filterByPrice1_10();
                    break;
                case 5: //Price 11 - 25
                    this.filterByPrice11_25();
                    break;
                case 6: //Price > 25
                    this.filterByPrice25();
                    break;
            }
        }

        private void filterByBeverage(){
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(wm.isBeverage())
                    wm.toKeep = true;
            }
        }

        private void filterByServiceFee() {
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(!wm.isServiceFee())
                    wm.toKeep = true;
            }
        }


        private void filterByVegan() {
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(wm.isVegan())
                    wm.toKeep = true;
            }
        }

        private void filterByVegetarian() {
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(wm.isVegetarian())
                    wm.toKeep = true;
            }
        }

        private void filterByPrice1_10() {
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(wm.getPrice() < 11)
                    wm.toKeep = true;
            }
        }

        private void filterByPrice11_25() {
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if (wm.getPrice() >= 11 && wm.getPrice() < 26)
                    wm.toKeep = true;
            }
        }

        private void filterByPrice25() {
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if (wm.getPrice() >= 26)
                    wm.toKeep = true;
            }
        }

        /**
         * Remove the items marked as such.
         */
        public void updateEntries(){
            this.menus.beginBatchedUpdates();
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(!wm.toKeep){
                    this.menus.removeItemAt(i);
                    i--;
                }
                else{
                    wm.toKeep = false;
                }
            }
            this.menus.endBatchedUpdates();
        }

        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,null);
            return new MenuViewHolder(menu_view);
        }

        @Override
        public void onBindViewHolder(final MenuViewHolder holder, int position) {
            final String METHOD_NAME = this.getClass().getName()+" - onBindViewHolder";
            final Menu menu = menus.get(position);
            String s=menu.getDescription();
            holder.menu_description.setText(s);
            holder.menu_name.setText(menu.getName());
            if(menu.getName().length()>18)
                holder.menu_name.setTypeface(Typeface.create("sans-serif-condensed",Typeface.NORMAL));
            holder.menu_price.setText(String.format("%.2f", menu.getPrice())+"€");
            if(!menu.isSpicy()) holder.spicy_icon.setColorFilter(Color.GRAY); else holder.spicy_icon.setColorFilter(Color.parseColor("#FFD32F2F"));
            if(!menu.isVegan()) holder.vegan_icon.setColorFilter(Color.GRAY); else holder.vegan_icon.setColorFilter(Color.parseColor("#388e3c"));
            if(!menu.isVegetarian()) holder.vegetarian_icon.setColorFilter(Color.GRAY); else holder.vegetarian_icon.setColorFilter(Color.parseColor("#7b1fa2"));
            if(!menu.isGlutenfree()) holder.glutenfree_icon.setColorFilter(Color.GRAY); else holder.glutenfree_icon.setColorFilter(Color.parseColor("#FFFBC02D"));
            File img = new File(menu.getImageLocalPath());
            try {
                holder.menu_image.setImageBitmap(new Picture(Uri.fromFile(img),context.getContentResolver(),300,300).getBitmap());
            } catch (IOException e) {
                //Log.e(METHOD_NAME,e.getMessage());
            }
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("rid",menu.getRid());
                    bundle.putString("mid",menu.getMid());
                    New_Menu_details menu_details = new New_Menu_details();
                    menu_details.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_up,R.anim.slide_down,R.anim.slide_up,R.anim.slide_down)
                            .addToBackStack(null).add(R.id.frame,menu_details).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.menus.size();
        }

        public SortedList<WeightedMenu> getMenus(){ return this.menus; }
    }

}
