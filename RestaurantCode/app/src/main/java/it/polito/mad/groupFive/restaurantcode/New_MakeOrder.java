package it.polito.mad.groupFive.restaurantcode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import it.polito.mad.groupFive.restaurantcode.datastructures.Order;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

/**
 * Created by Cristiano on 12/06/2016.
 */
public class New_MakeOrder extends Fragment implements TimePickerFragment.Listener, DatePickerFragment.Listener {

    private String rid;
    private String mid;
    private String uid;
    private String name;
    private String menuName;
    private TextView date;
    private TextView time;
    private EditText notes;
    private TextView button;
    private Boolean dateSet;
    private Boolean timeSet;
    private Calendar chosenDate;
    FirebaseDatabase db;

    public New_MakeOrder() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.make_order, container, false);
        rid=getArguments().getString("rid");
        mid=getArguments().getString("mid");
        menuName=getArguments().getString("menuName");
        db = FirebaseDatabase.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user = new User(uid);
        DatabaseReference Myref = db.getReference("User");
        Myref.orderByChild("uid").equalTo(uid).addChildEventListener(new DataList(user));
        dateSet=false;
        timeSet=false;
        chosenDate=Calendar.getInstance();
        final TimePickerFragment timePickerFragment = new TimePickerFragment();
        final DatePickerFragment datePickerFragment = new DatePickerFragment();
        timePickerFragment.setListener(this);
        datePickerFragment.setListener(this);
        date=(TextView)v.findViewById(R.id.date);
        time=(TextView)v.findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragment.show(getActivity().getFragmentManager(),"timePicker");
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getActivity().getFragmentManager(),"datePicker");
            }
        });
        notes=(EditText)v.findViewById(R.id.notes);
        RelativeLayout background=(RelativeLayout)v.findViewById(R.id.background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        button=(TextView)v.findViewById(R.id.pay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now=Calendar.getInstance();
                if(!dateSet||!timeSet)
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.select_date_time), Toast.LENGTH_SHORT).show();
                else if(chosenDate.before(now))
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.MakeOrder_past), Toast.LENGTH_SHORT).show();
                else{
                    Order order=new Order();
                    order.setRid(rid);
                    order.setMid(mid);
                    order.setDate(chosenDate.get(Calendar.HOUR_OF_DAY)+":"+String.format("%02d",chosenDate.get(Calendar.MINUTE))
                        +" "+(chosenDate.get(Calendar.DAY_OF_MONTH)+1)+"/"+chosenDate.get(Calendar.MONTH)+"/"+chosenDate.get(Calendar.YEAR));
                    order.setName(name);
                    order.setNotes(notes.getText().toString());
                    order.setUid(uid);
                    order.setMenuName(menuName);
                    order.setNotified(false);
                    DatabaseReference reference=db.getReference("order");
                    String key= reference.push().getKey();
                    reference.child(key).setValue(order.toMap());
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.MakeOrder_successful)+" "+name, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return v;
    }
    public void setTime(int hourOfDay, int minute) {
        time.setText(hourOfDay+":"+String.format("%02d",minute));
        chosenDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
        chosenDate.set(Calendar.MINUTE,minute);
        timeSet=true;
    }
    public void setDate(int year, int month, int day) {
        date.setText(day+"/"+month+"/"+year);
        chosenDate.set(Calendar.DAY_OF_MONTH,day);
        chosenDate.set(Calendar.MONTH,month);
        chosenDate.set(Calendar.YEAR,year);
        dateSet=true;

    }
    public class DataList implements ChildEventListener {
        public User user;

        public DataList(User user) {
            this.user = user;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            this.user = dataSnapshot.getValue(user.getClass());
            name = user.getName() + " " + user.getSurname();
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
    }
}
