package it.polito.mad.groupFive.restaurantcode;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


import it.polito.mad.groupFive.restaurantcode.datastructures.Order;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * Created by Cristiano on 24/04/2016.
 */
public class MakeOrder extends NavigationDrawer implements TimePickerFragment.Listener, DatePickerFragment.Listener{

    private String rid;
    private String mid;
    private String uid;
    private Restaurant restaurant;
    private String name;
    private Calendar date;
    private String notes;
    private String[] months;
    private String menuName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.make_order, mlay);
        rid=getIntent().getExtras().getString("rid");
        mid=getIntent().getExtras().getString("mid");
        uid=getIntent().getExtras().getString("uid");
        menuName=getIntent().getExtras().getString("name");
        Log.e("RID MID UID",rid+" "+mid+" "+uid);
        date=Calendar.getInstance();
        months=getResources().getStringArray(R.array.months);



        setupview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_pay,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.pay_ab){
            final EditText nameTextBox = (EditText) findViewById(R.id.reservation_name);
            final EditText notesTextBox = (EditText) findViewById(R.id.reservation_notes);
            name=nameTextBox.getText().toString();
            notes=notesTextBox.getText().toString();
            if(name.equalsIgnoreCase("")){
                Toast.makeText(getBaseContext(), getString(R.string.MakeOrder_name), Toast.LENGTH_SHORT).show();
            }
            else if(date.before(Calendar.getInstance())){
                Toast.makeText(getBaseContext(), getString(R.string.MakeOrder_past), Toast.LENGTH_SHORT).show();
            }
            else {
                SimpleDateFormat format=new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                    Order order = new Order();
                    order.setRid(rid);
                    order.setMid(mid);
                    order.setDate(format.format(date.getTime()));
                    order.setName(name);
                    order.setNotes(notes);
                    order.setUid(uid);
                    order.setMenuName(menuName);
                    order.setNotified(false);
                     FirebaseDatabase db;
                     db=FirebaseDatabase.getInstance();
                      DatabaseReference reference=db.getReference("order");
                    String key= reference.push().getKey();
                reference.child(key).setValue(order.toMap());

                    Toast.makeText(getBaseContext(), getString(R.string.MakeOrder_successful), Toast.LENGTH_SHORT).show();
                    finish();

                }
        }
        return super.onOptionsItemSelected(item);
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

        final TimePickerFragment timePickerFragment = new TimePickerFragment();
        final DatePickerFragment datePickerFragment = new DatePickerFragment();
        timePickerFragment.setListener(this);
        datePickerFragment.setListener(this);
        CardView dateCard = (CardView) findViewById(R.id.dateCard);
        CardView hourCard = (CardView) findViewById(R.id.hourCard);
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

