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

import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Order;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class Order_management extends NavigationDrawer {
    private SharedPreferences sharedPreferences;
    private ArrayList<Order> orders;
    private String[] months;
    private int deletecheck;
    private  String uid;
    private String rid;
    private OrderAdapter orderAdapter;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_orders);
        months=getResources().getStringArray(R.array.months);
        orders=new ArrayList<>();
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        uid=sharedPreferences.getString("uid",null);
        rid=sharedPreferences.getString("rid",null);
        Log.e("RID E UID",""+rid+" "+uid);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.reservationlist, mlay);

        db=FirebaseDatabase.getInstance();
        ref=db.getReference("order");
        ref.orderByChild("rid").equalTo(rid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Order order=new Order();
                order.setOid(dataSnapshot.getKey());
                order.setUid(dataSnapshot.child("uid").getValue().toString());
                order.setMid(dataSnapshot.child("mid").getValue().toString());
                order.setRid(rid);

                order.setDate(dataSnapshot.child("date").getValue().toString());
                order.setMenuName(dataSnapshot.child("menuname").getValue().toString());
                order.setName(dataSnapshot.child("name").getValue().toString());
                order.setNotes(dataSnapshot.child("notes").getValue().toString());
                orders.add(order);
                showOrders();
                orderAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_refresh,menu);
        return true;
    }

    private boolean showOrders() {
        TextView noitems = (TextView)findViewById(R.id.noItemsTextView);
        ListView lview = (ListView) findViewById(R.id.listView);
        Log.v("Order",String.valueOf(orders.size()));
        if (orders.size()>0) {
            noitems.setVisibility(View.INVISIBLE);
            lview.setVisibility(View.VISIBLE);
            orderAdapter= new OrderAdapter();
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
        if(item.getItemId()==R.id.refresh_ab){
            showOrders();
        }
        return super.onOptionsItemSelected(item);
    }

    public class OrderAdapter extends BaseAdapter {

        public OrderAdapter(){

        }

        @Override
        public int getCount() { return orders.size(); }

        @Override
        public Object getItem(int position) {
            return orders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView= LayoutInflater.from(getBaseContext()).inflate(R.layout.reservation,null);
            TextView username= (TextView) convertView.findViewById(R.id.order_username);
            TextView userID= (TextView) convertView.findViewById(R.id.order_userID);
            TextView date=(TextView) convertView.findViewById(R.id.date);
            TextView oid = (TextView)convertView.findViewById(R.id.order_OID);
            TextView meal=(TextView) convertView.findViewById(R.id.order_meal);
            TextView notes = (TextView) convertView.findViewById(R.id.order_notes);
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
                                ref.child(orders.get(deletecheck).getOid()).removeValue();
                                orders.remove(deletecheck);
                                orderAdapter.notifyDataSetChanged();
                                showOrders();
                            }else{
                                deletecheck=0;
                            }

                        }

                    });

                    dialog.show();

                        showOrders();


                }
            });

            Order order= orders.get(position);
            username.setText(order.getName());
            userID.setText(" (User #"+String.valueOf(order.getUid())+")");
            notes.setText("\""+order.getNotes()+"\"");
            if(order.getNotes().length()==0)
                notes.setVisibility(View.GONE);
            date.setText(order.getDate());
            oid.setText("Order ID: "+String.valueOf(order.getOid()));
            String mealID=String.valueOf(order.getMenuName());

            meal.setText("Menu: "+mealID);
            return convertView;
        }
    }

}
