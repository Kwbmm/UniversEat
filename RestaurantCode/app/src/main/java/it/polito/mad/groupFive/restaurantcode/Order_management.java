package it.polito.mad.groupFive.restaurantcode;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private String[] months={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        int uid=sharedPreferences.getInt("uid",-1);
        int rid=sharedPreferences.getInt("rid",-1);
        Log.e("RID E UID",""+rid+" "+uid);
        try {
            user=new User(this,rid,uid);
            restaurant=user.getRestaurant();
            restaurant.getData();
        } catch (JSONException e) {
            Log.e("Exception",e.toString());
        } catch (IOException e) {
            Log.e("Exception",e.toString());
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
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        MenuItem item=menu.findItem(R.id.add_ab);
        item.setVisible(false);
        item.setEnabled(false);
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
            lview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
                    orders.remove(position);
                    restaurant.setOrders(orders);
                    try {
                        restaurant.saveData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showOrders();
                }
            });
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

            Order order= orderlist.get(position);
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(order.getDate());
            username.setText(String.valueOf(order.getUid()));
            hour.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
            minutes.setText(String.valueOf(calendar.get(Calendar.MINUTE)));
            date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+" "+months[calendar.get(Calendar.MONTH)]);
            oid.setText(String.valueOf(order.getOid()));
            meal.setText(String.valueOf(order.getMid()));
            return convertView;
        }
    }

    private void generaordinifittizi(){
        try{
            ArrayList<Order> ordini = new ArrayList<Order>();
            Order o = new Order(restaurant,2234,2);
            Order e = new Order(restaurant,23454,2);
            Order i = new Order(restaurant,34545,2);
            Order a = new Order(restaurant,75672,2);
            Order u = new Order(restaurant,4532,2);
            Order og = new Order(restaurant,96782,2);
            Order eg = new Order(restaurant,12,2);
            Order ig = new Order(restaurant,2452,2);
            Order ag = new Order(restaurant,85672,2);
            Order ug = new Order(restaurant,2222,2);
            o.setDate(new Date());
            o.setMid(1);
            o.setUid(45);
            o.setRestaurantID(restaurant.getRid());
            a.setDate(new Date());
            a.setMid(3);
            a.setRestaurantID(restaurant.getRid());
            i.setDate(new Date());
            i.setMid(4);
            i.setRestaurantID(restaurant.getRid());
            u.setDate(new Date());
            u.setMid(66);
            u.setRestaurantID(restaurant.getRid());
            e.setDate(new Date());
            e.setMid(8);
            e.setRestaurantID(restaurant.getRid());
            og.setDate(new Date());
            og.setMid(1);
            og.setUid(45);
            og.setRestaurantID(restaurant.getRid());
            ag.setDate(new Date());
            ag.setMid(3);
            ag.setRestaurantID(restaurant.getRid());
            ig.setDate(new Date());
            ig.setMid(4);
            ig.setRestaurantID(restaurant.getRid());
            ug.setDate(new Date());
            ug.setMid(66);
            ug.setRestaurantID(restaurant.getRid());
            eg.setDate(new Date());
            eg.setMid(8);
            eg.setRestaurantID(restaurant.getRid());
            ordini.add(o);
            ordini.add(a);
            ordini.add(e);
            ordini.add(i);
            ordini.add(u);
            ordini.add(og);
            ordini.add(ag);
            ordini.add(eg);
            ordini.add(ig);
            ordini.add(ug);
            restaurant.setOrders(ordini);
            restaurant.saveData();
        } catch (OrderException e) {
            Log.e("Exception",e.toString());
        } catch (IOException e) {
            Log.e("Exception",e.toString());
        } catch (JSONException e) {
            Log.e("Exception",e.toString());
        }
    }
}
