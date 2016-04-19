package it.polito.mad.groupFive.restaurantcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

public class Home extends NavigationDrawer {
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menusshared;
    private MenuAdapter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Every activity should extend navigation drawer activity, no set content view must be called, layout must be inflated using inflate function

        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_home, mlay);
        getMenus();


        adp= new MenuAdapter(menusshared);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.home_menu_rw);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }

private void getMenus(){
    sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
    int rid, uid;
    uid = sharedPreferences.getInt("uid", -1);
    rid = sharedPreferences.getInt("rid", -1);
    try {

        rest = new User(this, rid, uid).getRestaurant();
        rest.getData();
    } catch (Exception e) {
        e.printStackTrace();
    }

    menusshared = rest.getMenus();

}

public static class MenuViewHoder extends RecyclerView.ViewHolder {
    protected TextView menu_name;
    protected TextView menu_desctiprion;
    protected TextView menu_price;
    protected CardView card;

    public MenuViewHoder(View itemView) {
        super(itemView);
        this.menu_name =(TextView)itemView.findViewById(R.id.menu_name);
        this.menu_desctiprion=(TextView)itemView.findViewById(R.id.menu_description);
        this.menu_price=(TextView)itemView.findViewById(R.id.menu_price);
        this.card= (CardView) itemView.findViewById(R.id.menu_card);
    }
}

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHoder>{
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menus;

    public MenuAdapter(ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menus){
        this.menus=menus;
        sort();

    }
    public void sort(){
        Collections.sort(this.menus, new Comparator<it.polito.mad.groupFive.restaurantcode.datastructures.Menu>() {
            @Override
            public int compare(it.polito.mad.groupFive.restaurantcode.datastructures.Menu lhs, it.polito.mad.groupFive.restaurantcode.datastructures.Menu rhs) {

                return rhs.getType()-lhs.getType();
            }});
    }

    public void update(){
        menus=menusshared;
        sort();

    }

    @Override
    public MenuViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,null);
        return new MenuViewHoder(menu_view);
    }

    @Override
    public void onBindViewHolder(MenuViewHoder holder,int position) {
        it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu =menus.get(position);
        holder.menu_desctiprion.setText(menu.getDescription());
        holder.menu_name.setText(menu.getName());
        holder.menu_price.setText(menu.getPrice()+"€");



    }

    @Override
    public int getItemCount() {
        return menus.size();
    }


    }}