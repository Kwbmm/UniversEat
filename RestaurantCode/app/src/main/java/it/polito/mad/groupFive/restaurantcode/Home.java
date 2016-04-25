package it.polito.mad.groupFive.restaurantcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
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
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.datastructures.Order;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

public class Home extends NavigationDrawer {
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menusshared;
    private MenuAdapter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;
    private View parent;
    private int user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Every activity should extend navigation drawer activity, no set content view must be called, layout must be inflated using inflate function

        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_home, mlay);
        parent=mlay;
        getMenus();

        if(menusshared!=null){
        adp= new MenuAdapter(menusshared);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.home_menu_rw);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        }
        SearchView searchView= (SearchView) findViewById(R.id.search);
        hideSoftKeyboard();
        ImageButton option= (ImageButton)findViewById(R.id.opt);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count=1;
                try {

                    Restaurant rest = new Restaurant(v.getContext());
                    Order order =new Order(rest,2);
                    order.setDate(new Date());
                    order.setMid(14);
                    order.setUid(22);

                    rest.setUid(2);
                    rest.setXcoord(0.0f);
                    rest.setYcoord(0.0f);
                    rest.setName("Pippo");
                    rest.setDescription("Figo");
                    rest.setState("Bello");
                    rest.setRating(3.5f);
                    rest.setCity("Politia");
                    rest.setAddress("Via vai");
                    rest.setTelephone("011667788");
                    rest.getOrders().add(order);
                    Log.v("create",String.valueOf(rest.getOrders().size()));
                    //rest.setImage64FromDrawable(drawable);
                    rest.saveData();

                    ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> ms = rest.getMenus();
                    for (int i = 0; i < 5; i++) {

                        it.polito.mad.groupFive.restaurantcode.datastructures.Menu mn = new it.polito.mad.groupFive.restaurantcode.datastructures.Menu(rest);
                        mn.setName("Menu");
                        mn.setDescription("Description");
                        mn.setPrice(1.5f);
                        mn.setTicket(true);
                        mn.setType(1);
                        //mn.setImage64FromDrawable(drawable);
                        //ms.add(mn);
                        rest.addMenu(mn);
                        //count++;

                    }
                    it.polito.mad.groupFive.restaurantcode.datastructures.Menu mn = new it.polito.mad.groupFive.restaurantcode.datastructures.Menu(rest);
                    mn.setName("Orecchiette tris");
                    mn.setDescription("orecchiette, patate, pollo");
                    mn.setPrice(1.5f);
                    mn.setTicket(true);
                    mn.setType(2);
                    rest.addMenu(mn);
                    //ms.add(mn);
                    //rest.setMenus(ms);
                    rest.saveData();
                    SharedPreferences sharedPreferences=v.getContext().getSharedPreferences(getString(R.string.user_pref),v.getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt("uid",2);
                    editor.putInt("rid",rest.getRid());
                    editor.commit();
                }catch (Exception e){
                    e.printStackTrace();
                }

                //parent.invalidate();
            }
        });

    }

private void getMenus(){
    sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
    int rid, uid;
    uid = sharedPreferences.getInt("uid", -1);
    rid = sharedPreferences.getInt("rid", -1);
    user=uid;
    Log.v("uid",uid+" ");
        try {if(uid>0){
                menusshared = new Restaurant(getBaseContext(),rid).getMenus();}else menusshared=null;
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public void onBindViewHolder(final MenuViewHoder holder, int position) {
        it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu =menus.get(position);
        holder.menu_desctiprion.setText(menu.getDescription());
        holder.menu_name.setText(menu.getName());
        holder.menu_price.setText(menu.getPrice()+"€");
        int rid =menus.get(position).getRid();
        holder.card.setOnClickListener(new onCardClick(position,rid));
    }



    @Override
    public int getItemCount() {
        return menus.size();
    }


    }
public class onCardClick implements View.OnClickListener{
    private int position;
    private int rid;

    public onCardClick(int position,int rid){
        this.position=position;
        this.rid=rid;
    }

    @Override
    public void onClick(View v) {

        Intent restinfo=new Intent(getBaseContext(),User_info_view.class);
        Log.v("rid",rid+"");
        restinfo.putExtra("rid",this.rid);
        startActivity(restinfo);

    }
}


}



