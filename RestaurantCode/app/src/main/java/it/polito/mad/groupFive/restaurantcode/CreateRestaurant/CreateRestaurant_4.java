package it.polito.mad.groupFive.restaurantcode.CreateRestaurant;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.libs.CustomTimePickerDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRestaurant_4.onFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRestaurant_4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRestaurant_4 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String rid,uid;
    private int hourStart=-1, hourEnd=-1;
    private int minuteStart=-1, minuteEnd=-1;

    private onFragInteractionListener mListener;

    private View parentView=null;
    private Restaurant restaurant = null;
    private String[] weekDays = new String[7];
    private ArrayMap<String,Integer> weekdayToRL_IDs=null;

    public CreateRestaurant_4() {
        // Required empty public constructor
    }
    public interface getRestaurant{
        public Restaurant getRest();
        public Boolean editmode();

    }
    public getRestaurant getR;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRestaurant_4.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRestaurant_4 newInstance(String param1, String param2) {
        CreateRestaurant_4 fragment = new CreateRestaurant_4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreateView";
        this.parentView = inflater.inflate(R.layout.fragment_create_restaurant_4, container, false);
        this.uid = getR.getRest().getUid();
        this.rid = getR.getRest().getRid();
        this.restaurant=getR.getRest();
        weekDays[0] = getResources().getString(R.string.monday);
        weekDays[1] = getResources().getString(R.string.tuesday);
        weekDays[2] = getResources().getString(R.string.wednesday);
        weekDays[3] = getResources().getString(R.string.thursday);
        weekDays[4] = getResources().getString(R.string.friday);
        weekDays[5] = getResources().getString(R.string.saturday);
        weekDays[6] = getResources().getString(R.string.sunday);
        this.weekdayToRL_IDs = new ArrayMap<>();
        LinearLayout ll = (LinearLayout) this.parentView.findViewById(R.id.FL_timetableDinner);
        int count=0;
        for(String weekday : this.weekDays){
            LayoutInflater li = LayoutInflater.from(this.parentView.getContext());
            final View timetableItem = li.inflate(R.layout.timetable_item,null);
            CheckBox cb=(CheckBox)timetableItem.findViewById(R.id.checkBox);
            cb.setText(weekday);

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox)v).isChecked()){
                        timetableItem.findViewById(R.id.from).setVisibility(View.VISIBLE);
                        timetableItem.findViewById(R.id.textClockFrom).setVisibility(View.VISIBLE);
                        if(hourStart != -1 && minuteStart != -1){
                            Button btnFrom = (Button) timetableItem.findViewById(R.id.textClockFrom);
                            btnFrom.setText(String.format(Locale.getDefault(),"%02d",hourStart)+
                                    ":" +
                                    String.format(Locale.getDefault(),"%02d",minuteStart));
                        }
                        timetableItem.findViewById(R.id.to).setVisibility(View.VISIBLE);
                        timetableItem.findViewById(R.id.textClockTo).setVisibility(View.VISIBLE);
                        if(hourEnd != -1 && minuteEnd != -1){
                            Button btnTo = (Button) timetableItem.findViewById(R.id.textClockTo);
                            btnTo.setText(String.format(Locale.getDefault(),"%02d",hourEnd)+
                                    ":" +
                                    String.format(Locale.getDefault(),"%02d",minuteEnd));
                        }
                    }
                    else{
                        timetableItem.findViewById(R.id.from).setVisibility(View.INVISIBLE);
                        timetableItem.findViewById(R.id.textClockFrom).setVisibility(View.INVISIBLE);
                        timetableItem.findViewById(R.id.to).setVisibility(View.INVISIBLE);
                        timetableItem.findViewById(R.id.textClockTo).setVisibility(View.INVISIBLE);
                    }
                }
            });

            weekdayToRL_IDs.put(weekday,this.randInt());

            //Set the clock popup for both buttons (to and from)
            final Button btnFrom = (Button) timetableItem.findViewById(R.id.textClockFrom);
            btnFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int startHour = 17;
                    int startMinute = 0;
                    CustomTimePickerDialog ctpd = new CustomTimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    hourStart = hourOfDay;
                                    minuteStart = minute;
                                    btnFrom.setText(
                                            String.format(Locale.getDefault(),"%02d",hourOfDay)+
                                                    ":" +
                                                    String.format(Locale.getDefault(),"%02d",minute));
                                }
                            },
                            startHour,
                            startMinute, true);
                    ctpd.setTitle(getResources().getString(R.string.titleTimePickerFrom));
                    ctpd.setMin(startHour,startMinute);
                    ctpd.setMax(18,59);
                    ctpd.show();
                }
            });

            final Button btnTo = (Button) timetableItem.findViewById(R.id.textClockTo);
            btnTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int startHour = 0;
                    int startMinute = 0;
                    CustomTimePickerDialog ctpd = new CustomTimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    hourEnd = hourOfDay;
                                    minuteEnd = minute;
                                    btnTo.setText(
                                            String.format(Locale.getDefault(),"%02d",hourOfDay)+
                                                    ":" +
                                                    String.format(Locale.getDefault(),"%02d",minute));
                                }
                            },
                            startHour,
                            startMinute, true);
                    ctpd.setTitle(getResources().getString(R.string.titleTimePickerFrom));
                    ctpd.setMin(startHour,startMinute);
                    ctpd.setMax(7,59);
                    ctpd.show();
                }
            });
            if (getR.editmode()){
                if(!restaurant.getTimetableDinner().isEmpty()){
                    if (restaurant.getTimetableDinner().containsKey(weekDays[count])){
                        cb.setChecked(true);
                        timetableItem.findViewById(R.id.from).setVisibility(View.VISIBLE);
                        timetableItem.findViewById(R.id.textClockFrom).setVisibility(View.VISIBLE);
                        timetableItem.findViewById(R.id.to).setVisibility(View.VISIBLE);
                        timetableItem.findViewById(R.id.textClockTo).setVisibility(View.VISIBLE);
                        btnFrom.setText(restaurant.getTimetableDinner().get(weekDays[count]).get("start"));
                        btnTo.setText(restaurant.getTimetableDinner().get(weekDays[count]).get("end"));
                    }
                }
                count++;
            }
            timetableItem.findViewById(R.id.RL_timeTableItem).setId(weekdayToRL_IDs.get(weekday));
            ll.addView(timetableItem);
        }
        Button btnNext = (Button) this.parentView.findViewById(R.id.Button_Next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity a = getActivity();
                if(a instanceof onFragInteractionListener) {
                    if(setRestaurantData()){
                        onFragInteractionListener obs = (onFragInteractionListener) a;
                        obs.onChangeFrag4(restaurant);
                    }
                    else{
                        Toast.makeText(getContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
        return this.parentView;
    }

    private boolean setRestaurantData() {
        final String METHOD_NAME = this.getClass().getName()+" - setRestaurantData";
            this.restaurant = getR.getRest();
            for (String weekDay : this.weekDays) {
                CheckBox cb = (CheckBox) this.parentView.findViewById(this.weekdayToRL_IDs.get(weekDay)).findViewById(R.id.checkBox);
                if (cb.isChecked()) {
                    Button bFrom = (Button) this.parentView.findViewById(this.weekdayToRL_IDs.get(weekDay)).findViewById(R.id.textClockFrom);
                    Button bTo = (Button) this.parentView.findViewById(this.weekdayToRL_IDs.get(weekDay)).findViewById(R.id.textClockTo);
                    restaurant.setDurationDinner(weekDay, bFrom.getText().toString(), bTo.getText().toString());
                }
            }
            return true;
    }
    private int randInt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        else{
            Random rand= new Random();
            int result;
            if((result=rand.nextInt(Integer.MAX_VALUE)) == 0)
                return this.randInt();
            return result;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFragInteractionListener) {
            mListener = (onFragInteractionListener) context;
            getR=(getRestaurant)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFragInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface onFragInteractionListener {
        void onChangeFrag4(Restaurant r);
    }
}
