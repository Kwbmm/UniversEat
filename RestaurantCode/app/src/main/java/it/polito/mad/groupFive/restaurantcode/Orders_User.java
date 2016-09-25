package it.polito.mad.groupFive.restaurantcode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Order;

/**
 * Created by Cristiano on 25/09/2016.
 */
public class Orders_User extends NavigationDrawer {
    private SharedPreferences sharedPreferences;
    private ArrayList<Order> orders;
    private String uid;
    private String rid;
    private OrderAdapter orderAdapter;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.myOrders);
        orders = new ArrayList<>();
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);
        rid = sharedPreferences.getString("rid", null);
        //Log.e("RID E UID",""+rid+" "+uid);
        FrameLayout mlay = (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.reservationlist, mlay);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("order");
        ref.orderByChild("uid").equalTo(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Order order = new Order();
                order.setOid(dataSnapshot.getKey());
                order.setUid(dataSnapshot.child("uid").getValue().toString());
                order.setMid(dataSnapshot.child("mid").getValue().toString());
                order.setRid(rid);
                order.setRestaurantName(dataSnapshot.child("restaurantName").getValue().toString());
                order.setCreationDate(dataSnapshot.child("creationDate").getValue().toString());
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


    private boolean showOrders() {
        TextView noitems = (TextView) findViewById(R.id.noItemsTextView);
        ListView lview = (ListView) findViewById(R.id.listView);
        //Log.v("Order",String.valueOf(orders.size()));
        if (orders.size() > 0) {
            noitems.setVisibility(View.INVISIBLE);
            lview.setVisibility(View.VISIBLE);
            orderAdapter = new OrderAdapter();
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
        if (item.getItemId() == R.id.refresh_ab) {
            showOrders();
        }
        return super.onOptionsItemSelected(item);
    }

    public class OrderAdapter extends BaseAdapter {

        public OrderAdapter() {

        }

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int position) {
            return orders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.reservation_users, null);
            TextView username = (TextView) convertView.findViewById(R.id.order_username);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView meal = (TextView) convertView.findViewById(R.id.order_meal);
            TextView notes = (TextView) convertView.findViewById(R.id.order_notes);
            ImageView notesimg = (ImageView) convertView.findViewById(R.id.imageView36);
            RelativeLayout card = (RelativeLayout) convertView.findViewById(R.id.card);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog=new AlertDialog.Builder(Orders_User.this);
                    final CharSequence[] items = { getString(R.string.details),getString(R.string.viewMenu),getString(R.string.viewRestaurant),getString(R.string.Menu_view_edit_Delete) };
                    dialog.setItems(items,new onPositionClickDialog(orders.get(position)));
                    dialog.show();
                }
            });

            final Order order = orders.get(position);
            username.setText(order.getRestaurantName());
            notes.setText(order.getNotes());
            if (order.getNotes().length() == 0) {
                notes.setVisibility(View.GONE);
                notesimg.setVisibility(View.GONE);
            }
            date.setText(order.getDate());
            String mealID = String.valueOf(order.getMenuName());

            meal.setText(mealID);
            return convertView;
        }
    }
    public class onPositionClickDialog implements DialogInterface.OnClickListener{

        private Order order;

        public onPositionClickDialog(Order order){
            this.order=order;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:{
                    AlertDialog.Builder detailsDialog=new AlertDialog.Builder(Orders_User.this);
                    detailsDialog.setTitle(R.string.orderDetails);
                    detailsDialog.setMessage(getString(R.string.orderCreatedAt)+order.getCreationDate()+getString(R.string.orderID)+order.getOid()+getString(R.string.userID)+order.getUid()+getString(R.string.menuID)+order.getMid());
                    detailsDialog.show();
                    break;
                }
                case 1:{
                    Bundle bundle = new Bundle();
                    bundle.putString("rid", order.getRid());
                    bundle.putString("mid", order.getMid());
                    New_Menu_details menu_details = new New_Menu_details();
                    menu_details.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
                            .addToBackStack(null).add(R.id.frame, menu_details).commit();
                    break;
                }
                case 2:{
                    Intent restaurant_view = new Intent(getBaseContext(), User_info_view.class);
                    restaurant_view.putExtra("rid", order.getRid());
                    startActivity(restaurant_view);
                    break;
                }
                case 3:{
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(Orders_User.this);
                    final CharSequence[] items = {getString(R.string.menu_view_edit_yes), getString(R.string.menu_view_edit_no)};
                    confirmationDialog.setTitle(getString(R.string.menu_view_edit_delete));
                    confirmationDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                ref.child(order.getOid()).removeValue();
                                orders.remove(order);
                                orderAdapter.notifyDataSetChanged();
                                showOrders();
                            } else {
                            }

                        }

                    });
                    confirmationDialog.show();
                    break;
                }
                default:{
                    dialog.cancel();
                }
            }
        }
    }

}

