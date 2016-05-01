package it.polito.mad.groupFive.restaurantcode;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;


import it.polito.mad.groupFive.restaurantcode.datastructures.Order;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * Created by Cristiano on 24/04/2016.
 */
public class MakeOrder extends NavigationDrawer implements TimePickerFragment.Listener, DatePickerFragment.Listener{

    private int rid;
    private int mid;
    private int uid;
    private Restaurant restaurant;
    private String name;
    private Calendar date;
    private String notes;
    private String[] months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.make_order, mlay);
        rid=getIntent().getExtras().getInt("rid");
        mid=getIntent().getExtras().getInt("mid");
        uid=getIntent().getExtras().getInt("uid");
        Log.e("RID MID UID",rid+" "+mid+" "+uid);
        date=Calendar.getInstance();
        months=getResources().getStringArray(R.array.months);
        try {
            restaurant=new Restaurant(getBaseContext(),rid);
            restaurant.getData();
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        setupview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    public void setTime(int hourOfDay, int minute) {
        TextView setHour = (TextView) findViewById(R.id.setHour);
        setHour.setText(hourOfDay+":"+String.format("%02d",minute));
        date.set(Calendar.HOUR_OF_DAY,hourOfDay);
        date.set(Calendar.MINUTE,minute);
    }
    public void setDate(int year, int month, int day) {
        TextView setDate = (TextView) findViewById(R.id.setDate);
        setDate.setText(day+" "+months[month]);
        date.set(Calendar.YEAR,year);
        date.set(Calendar.MONTH,month);
        date.set(Calendar.DAY_OF_MONTH,day);

    }
    private void setupview(){
        final EditText nameTextBox = (EditText) findViewById(R.id.reservation_name);
        final EditText notesTextBox = (EditText) findViewById(R.id.reservation_notes);
        final
        CardView dateCard = (CardView) findViewById(R.id.dateCard);
        CardView hourCard = (CardView) findViewById(R.id.hourCard);
        Button paymentbutton = (Button) findViewById(R.id.paymentbutton);
        paymentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=nameTextBox.getText().toString();
                notes=notesTextBox.getText().toString();
                if(name.equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please insert a name", Toast.LENGTH_SHORT).show();
                }
                else if(date.before(Calendar.getInstance())){
                    Toast.makeText(getBaseContext(), "You can't order in the past!", Toast.LENGTH_SHORT).show();
                }
                else try {
                        Order order = new Order(restaurant);
                        order.setRid(rid);
                        order.setMid(mid);
                        order.setDate(date.getTime());
                        order.setName(name);
                        order.setNotes(notes);
                        order.setUid(uid);
                        restaurant.getOrders().add(order);
                        restaurant.saveData();
                        Toast.makeText(getBaseContext(), "Order successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (OrderException e) {
                        e.printStackTrace();
                    } catch (RestaurantException e) {
                        e.printStackTrace();
                    }
                }
            });
        final TimePickerFragment timePickerFragment = new TimePickerFragment();
        final DatePickerFragment datePickerFragment = new DatePickerFragment();
        timePickerFragment.setListener(this);
        datePickerFragment.setListener(this);
        hourCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragment.show(getFragmentManager(),"timePicker");
            }
        });
        dateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(),"datePicker");
            }
        });



    }
}

