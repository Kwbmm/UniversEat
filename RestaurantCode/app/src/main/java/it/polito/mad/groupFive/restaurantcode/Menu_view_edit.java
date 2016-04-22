package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;


/**
 * Created by MacBookRetina on 12/04/16.
 */
public class Menu_view_edit extends NavigationDrawer {
    private ArrayList<Menu> menusshared;
    private MenuAdapter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readdata();
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.menulist, mlay);
        adp= new MenuAdapter(menusshared);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        readdata();
        adp.update();
        adp.notifyItemInserted(menusshared.size());
        adp.notifyDataSetChanged();
    }

    public void readdata() {
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


    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nfpm: {
                        Intent intent = new Intent(getBaseContext(), Create_menu.class);
                        startActivityForResult(intent,1);
                        break;
                    }
                    case R.id.ndm: {
                        Intent intent = new Intent(getBaseContext(), Create_menu.class);
                        startActivityForResult(intent,2);

                        break;

                    }
                }
                return true;
            }
        });
        popup.inflate(R.menu.popup);
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_ab) {
            showMenu(findViewById(R.id.add_ab));
        }
        return true;
    }

    public static class MenuViewHoder extends RecyclerView.ViewHolder {
        protected TextView menu_name;
        protected TextView menu_desctiprion;
        protected TextView menu_price;
        protected ImageButton edit;
        protected CardView card;

        public MenuViewHoder(View itemView) {
            super(itemView);
            this.menu_name =(TextView)itemView.findViewById(R.id.menu_name);
            this.menu_desctiprion=(TextView)itemView.findViewById(R.id.menu_description);
            this.menu_price=(TextView)itemView.findViewById(R.id.menu_price);
            this.edit=(ImageButton) itemView.findViewById(R.id.menu_edit);
            this.card= (CardView) itemView.findViewById(R.id.menu_card);
        }
    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuViewHoder>{
        private ArrayList<Menu> menus;

        public MenuAdapter(ArrayList<Menu> menus){
            this.menus=menus;
            sort();

        }
        public void sort(){
            Collections.sort(this.menus, new Comparator<Menu>() {
                @Override
                public int compare(Menu lhs, Menu rhs) {

                    return rhs.getType()-lhs.getType();
                }});
        }
        public void update(){
            menus=menusshared;
            sort();

        }

        @Override
        public MenuViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view_edit,null);
            return new MenuViewHoder(menu_view);
        }

        @Override
        public void onBindViewHolder(MenuViewHoder holder,int position) {
            Menu menu =menus.get(position);
            holder.menu_desctiprion.setText(menu.getDescription());
            holder.menu_name.setText(menu.getName());
            holder.menu_price.setText(menu.getPrice()+"€");
            holder.edit.setOnClickListener(new onEditclick(position));


        }

        @Override
        public int getItemCount() {
            return menus.size();
        }

        public void remove(int position){
            menus.remove(position);
            rest.setMenus(menus);
            try {
                rest.saveData();
            } catch (RestaurantException e) {
                e.printStackTrace();
            }
            menusshared=menus;
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, menus.size());
        }

        public class onEditclick implements View.OnClickListener{
            private int position;
            public onEditclick(int position){
                this.position=position;
            }

            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(Menu_view_edit.this);
                final CharSequence[] items = { "Edit", "Delete","Cancel" };
                dialog.setTitle("Edit Options");
                dialog.setItems(items,new onPositionClickDialog(position));
                dialog.show();

            }
        }

        public class onPositionClickDialog implements DialogInterface.OnClickListener{
            private int position;

            public onPositionClickDialog(int position){
                this.position=position;
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:{
                        //TODO:edit intent
                        break;
                    }
                    case 1:{
                        //TODO:delete item
                        remove(position);
                        break;
                    }
                    default:{
                        dialog.cancel();
                    }
                }

            }
        }
    }

}

