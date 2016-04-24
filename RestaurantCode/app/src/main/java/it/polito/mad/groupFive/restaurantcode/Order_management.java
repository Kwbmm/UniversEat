package it.polito.mad.groupFive.restaurantcode;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Order;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

public class Order_management extends NavigationDrawer {
    private User user;
    private Restaurant restaurant;
    private SharedPreferences sharedPreferences;
    private ArrayList<Order> orders;
    private String[] months;
    private int deletecheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        months=getResources().getStringArray(R.array.months);
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        int uid=sharedPreferences.getInt("uid",-1);
        int rid=sharedPreferences.getInt("rid",-1);
        Log.e("RID E UID",""+rid+" "+uid);
        try {
            user=new User(this,rid,uid);
            restaurant=user.getRestaurant();
            restaurant.getData();
        } catch (RestaurantException e) {
            Log.e("Exception",e.toString());
        } catch (UserException e) {
            Log.e("Exception",e.toString());
        }
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.reservationlist, mlay);
        //generaordinifittizi();
        showOrders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    private boolean showOrders() {
        TextView noitems = (TextView)findViewById(R.id.noItemsTextView);
        ListView lview = (ListView) findViewById(R.id.listView);
        orders=restaurant.getOrders();
        Log.v("Order",String.valueOf(orders.size()));
        if (orders.size()>0) {
            noitems.setVisibility(View.INVISIBLE);
            lview.setVisibility(View.VISIBLE);
            OrderAdapter orderAdapter = new OrderAdapter(this, orders);
            lview.setAdapter(orderAdapter);
            return true;
        } else {
            noitems.setVisibility(View.VISIBLE);
            lview.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public class OrderAdapter extends BaseAdapter {
        ArrayList<Order> orderlist;
        Context context;
        public OrderAdapter(Context context,ArrayList<Order> orders){
            this.orderlist=orders;
            this.context=context;
        }

        @Override
        public int getCount() { return orderlist.size(); }

        @Override
        public Object getItem(int position) {
            return orderlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView= LayoutInflater.from(context).inflate(R.layout.reservation,null);
            TextView username= (TextView) convertView.findViewById(R.id.username);
            TextView hour=(TextView) convertView.findViewById(R.id.hour);
            TextView minutes = (TextView) convertView.findViewById(R.id.minutes);
            TextView date=(TextView) convertView.findViewById(R.id.date);
            TextView oid = (TextView)convertView.findViewById(R.id.orderID);
            TextView meal=(TextView) convertView.findViewById(R.id.meal);
            ImageButton delete= (ImageButton) convertView.findViewById(R.id.imageView);
            deletecheck=position;
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog=new AlertDialog.Builder(Order_management.this);
                    final CharSequence[] items = { getString(R.string.menu_view_edit_yes), getString(R.string.menu_view_edit_no) };
                    dialog.setTitle(getString(R.string.menu_view_edit_delete));
                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0){
                                orders.remove(deletecheck);
                                restaurant.setOrders(orders);
                                try {
                                    restaurant.saveData();
                                } catch (RestaurantException e) {
                                    e.printStackTrace();
                                }
                                showOrders();
                            }else{
                                deletecheck=0;
                            }

                        }

                    });

                    dialog.show();
                    try {
                        restaurant.saveData();
                        showOrders();
                    } catch (RestaurantException e) {
                        e.printStackTrace();
                    }

                }
            });

            Order order= orderlist.get(position);
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(order.getDate());
            username.setText(String.valueOf(order.getUid()));
            hour.setText(String.format(Locale.getDefault(),"%02d",calendar.get(Calendar.HOUR_OF_DAY)));
            minutes.setText(String.format(Locale.getDefault(),"%02d",calendar.get(Calendar.MINUTE)));
            date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+" "+months[calendar.get(Calendar.MONTH)]);
            oid.setText(String.valueOf(order.getOid()));
            meal.setText(String.valueOf(order.getMid()));
            return convertView;
        }
    }

}
