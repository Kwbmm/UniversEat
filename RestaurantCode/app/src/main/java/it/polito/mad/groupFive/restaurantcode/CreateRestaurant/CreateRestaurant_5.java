package it.polito.mad.groupFive.restaurantcode.CreateRestaurant;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;


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
    private TextView btnFinish;
    private ProgressBar progressBar;
    private String rid,uid;

    private onFragInteractionListener mListener;

    private View parentView = null;
    private Restaurant restaurant = null;

    public CreateRestaurant_5() {
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
        this.rid = getR.getRest().getRid();
        this.uid = getR.getRest().getUid();
        this.restaurant=getR.getRest();
        progressBar=(ProgressBar)parentView.findViewById(R.id.progressBar6);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        btnFinish = (TextView) this.parentView.findViewById(R.id.Button_Finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFinish.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Activity a = getActivity();
                if(a instanceof onFragInteractionListener) {
                    if(setRestaurantData()){
                        onFragInteractionListener obs = (onFragInteractionListener) a;
                        obs.onChangeFrag5(restaurant);
                    }
                    else{
                        Toast.makeText(getContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                                .show();
                        progressBar.setVisibility(View.INVISIBLE);
                        btnFinish.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        return this.parentView;
    }

    private boolean setRestaurantData() {
        final String METHOD_NAME = this.getClass().getName()+" - setRestaurantData";



            //Get all the tickets
            ArrayList<CheckBox> ticketCBs = new ArrayList<>();
            //TODO wanna add more tickets??
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_1));
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_2));
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_3));
            ticketCBs.add((CheckBox) this.parentView.findViewById(R.id.ticket_4));
            Map<String,Boolean> ticketMap = new HashMap<>();
            for(CheckBox cb : ticketCBs){
                if(cb.isChecked()) ticketMap.put(cb.getText().toString(),true);
            }
            restaurant.setTickets(ticketMap);
            return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFragInteractionListener) {
            mListener = (onFragInteractionListener) context;
            getR=(getRestaurant) context;
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
