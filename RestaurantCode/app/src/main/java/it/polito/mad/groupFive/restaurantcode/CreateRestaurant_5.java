package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRestaurant_5.onFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRestaurant_5#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRestaurant_5 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onFragInteractionListener mListener;

    private View parentView = null;
    private Restaurant restaurant = null;

    public CreateRestaurant_5() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRestaurant_5.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRestaurant_5 newInstance(String param1, String param2) {
        CreateRestaurant_5 fragment = new CreateRestaurant_5();
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
        this.parentView = inflater.inflate(R.layout.fragment_create_restaurant_5, container, false);

        Button btnFinish = (Button) this.parentView.findViewById(R.id.Button_Finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity a = getActivity();
                if(a instanceof onFragInteractionListener) {
                    setRestaurantData();

                    onFragInteractionListener obs = (onFragInteractionListener) a;
                    obs.onChangeFrag5(restaurant);
                }
            }
        });

        return this.parentView;
    }

    private void setRestaurantData() {
        final String METHOD_NAME = this.getClass().getName()+" - setRestaurantData";

        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateRestaurant.MODE_PRIVATE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                restaurant = new Restaurant(getActivity(), ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE),sp.getInt("uid",-1));
            else //TODO Move randInt inside dataStructures classes
                restaurant = new Restaurant(getActivity(),randInt(),sp.getInt("uid",-1));
            /**
             * TODO create getter/setter for saving tickets inside Restaurant class.
             * Methods should be:
             *      void setTickets(ArrayList<String> tickets);
             *      void addTicket(String ticket);
             *
             *      ArrayList<String> getTickets();
             *      boolean isTicketAccepted(String ticket);
             */
            //Get all the tickets
            ArrayList<CheckBox> ticketCBs = new ArrayList<>();
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_1));
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_2));
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_3));
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_4));
            for(CheckBox cb : ticketCBs){
                if(cb.isChecked()) {
                    //restaurant.addTicket(cb.getText());
                }
            }
        } catch (RestaurantException |
                UserException |
                JSONException e) {
            Log.e(METHOD_NAME,e.getMessage());
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
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
        void onChangeFrag5(Restaurant r);
    }
}
