package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;


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

    private onFragInteractionListener mListener;

    private View parentView=null;
    private Restaurant restaurant = null;
    private String[] weekDays = new String[7];
    private ArrayMap<String,Integer> weekdayToRL_IDs=null;

    public CreateRestaurant_4() {
        // Required empty public constructor
    }

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
        weekDays[0] = getResources().getString(R.string.monday);
        weekDays[1] = getResources().getString(R.string.tuesday);
        weekDays[2] = getResources().getString(R.string.wednesday);
        weekDays[3] = getResources().getString(R.string.thursday);
        weekDays[4] = getResources().getString(R.string.friday);
        weekDays[5] = getResources().getString(R.string.saturday);
        weekDays[6] = getResources().getString(R.string.sunday);
        this.weekdayToRL_IDs = new ArrayMap<>();

        LinearLayout ll = (LinearLayout) this.parentView.findViewById(R.id.FL_timetableDinner);

        for(String weekday : this.weekDays){
            LayoutInflater li = LayoutInflater.from(this.parentView.getContext());
            View timetableItem = li.inflate(R.layout.timetable_item,null);
            ((CheckBox)timetableItem.findViewById(R.id.checkBox)).setText(weekday);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                weekdayToRL_IDs.put(weekday, ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE));
            else //TODO Move randInt inside dataStructures classes
                weekdayToRL_IDs.put(weekday,randInt());
            //Set the clock popup for both buttons (to and from)
            final Button btnFrom = (Button) timetableItem.findViewById(R.id.textClockFrom);
            btnFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog tpd = new TimePickerDialog(
                            getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    btnFrom.setText(
                                            String.format(Locale.getDefault(),"%02d",hourOfDay)+
                                                    ":" +
                                                    String.format(Locale.getDefault(),"%02d",minute));
                                }
                            },
                            hour,
                            minute,
                            true
                    );
                    tpd.setTitle(getResources().getString(R.string.titleTimePickerFrom));
                    tpd.show();
                }
            });

            final Button btnTo = (Button) timetableItem.findViewById(R.id.textClockTo);
            btnTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog tpd = new TimePickerDialog(
                            getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    btnTo.setText(
                                            String.format(Locale.getDefault(),"%02d",hourOfDay)+
                                                    ":" +
                                                    String.format(Locale.getDefault(),"%02d",minute));
                                }
                            },
                            hour,
                            minute,
                            true
                    );
                    tpd.setTitle(getResources().getString(R.string.titleTimePickerTo));
                    tpd.show();
                }
            });


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

        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateRestaurant.MODE_PRIVATE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                restaurant = new Restaurant(getActivity(), ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE),sp.getInt("uid",-1));
            else //TODO Move randInt inside dataStructures classes
                restaurant = new Restaurant(getActivity(),randInt(),sp.getInt("uid",-1));
            for(int i = 0; i < this.weekDays.length; i++) {
                CheckBox cb = (CheckBox) this.parentView.findViewById(this.weekdayToRL_IDs.get(this.weekDays[i])).findViewById(R.id.checkBox);
                if(cb.isChecked()){
                    Button bFrom = (Button) this.parentView.findViewById(this.weekdayToRL_IDs.get(this.weekDays[i])).findViewById(R.id.textClockFrom);
                    /**
                     * TODO Getter/Setter in restaurant class for duration.
                     * Create an ArrayMap<Integer,Duration>
                     *      The Integer key is the number of the weekday (from 0 to 6)
                     *      The Duration value is an object of type Duration
                     *      See: https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html
                     *
                     * Methods will be:
                     *      void setDuration(int dayOfWeek,String timeStart, String timeEnd);
                     *      void setDuration(Duration d);
                     *      void setTimetable(ArrayMap<Integer,Duration>);
                     *
                     *      Duration getDuration(int dayOfWeek);
                     *      ArrayMap<Integer,Duration> getTimetable();
                     */
                    Button bTo = (Button) this.parentView.findViewById(this.weekdayToRL_IDs.get(this.weekDays[i])).findViewById(R.id.textClockTo);
                    //restaurant.setDuration(i,bFrom.getText(),bTo.getText());
                }
            }
            return true;
        } catch (RestaurantException |
                UserException |
                JSONException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
            return false;
        }
    }

    //TODO Move randInt inside the dataStructures classes
    public static int randInt() {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand= new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt(Integer.MAX_VALUE -1 );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFragInteractionListener) {
            mListener = (onFragInteractionListener) context;
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
        // TODO: Update argument type and name
        void onChangeFrag4(Restaurant r);
    }
}