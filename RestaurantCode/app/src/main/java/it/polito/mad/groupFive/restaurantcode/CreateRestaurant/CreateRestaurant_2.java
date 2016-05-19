package it.polito.mad.groupFive.restaurantcode.CreateRestaurant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRestaurant_2.onFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRestaurant_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRestaurant_2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onFragInteractionListener mListener;
    private View parentView=null;
    private TextView address;
    private TextView city;
    private TextView ZIPCode;
    private TextView state;

    private Restaurant restaurant=null;

    public CreateRestaurant_2() {
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
     * @return A new instance of fragment CreateRestaurant_2.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRestaurant_2 newInstance(String param1, String param2) {
        CreateRestaurant_2 fragment = new CreateRestaurant_2();
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
        this.parentView = inflater.inflate(R.layout.fragment_create_restaurant_2, container, false);
        address = (TextView) parentView.findViewById(R.id.editText_Address);
        city = (TextView) parentView.findViewById(R.id.editText_City);
        ZIPCode = (TextView) parentView.findViewById(R.id.editText_ZIPCode);
        state = (TextView) parentView.findViewById(R.id.editText_State);
        if(getR.editmode()){
        fetchData();}
        Button btnNext = (Button) this.parentView.findViewById(R.id.Button_Next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity a = getActivity();
                if(a instanceof onFragInteractionListener) {
                    if(setRestaurantData()){
                        onFragInteractionListener obs = (onFragInteractionListener) a;
                        obs.onChangeFrag2(restaurant);
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

    private void fetchData(){
        this.restaurant=getR.getRest();
        address.setText(restaurant.getAddress());
        state.setText(restaurant.getState());
        ZIPCode.setText(restaurant.getZIPCode());
        city.setText(restaurant.getCity());
    }

    private boolean setRestaurantData() {
        final String METHOD_NAME = this.getClass().getName()+" - setRestaurantData";

        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateRestaurant.MODE_PRIVATE);

        try {


            if(!getR.editmode())restaurant=new Restaurant(getContext(),sp.getInt("rid",-1));

            if(address.getText().toString().trim().equals("") || address.getText() == null){
                Log.w(METHOD_NAME,"TextView Address is either empty or null");
                return false;
            }
            restaurant.setAddress(address.getText().toString());


            if(city.getText().toString().trim().equals("") || city.getText() == null){
                Log.w(METHOD_NAME,"TextView City is either empty or null");
                return false;
            }
            restaurant.setCity(city.getText().toString());


            if(ZIPCode.getText().toString().trim().equals("") || ZIPCode.getText() == null){
                Log.w(METHOD_NAME,"TextView ZIPCode is either empty or null");
                return false;
            }
            restaurant.setZIPCode(ZIPCode.getText().toString());


            if(state.getText().toString().trim().equals("") || state.getText() == null){
                Log.w(METHOD_NAME,"TextView State is either empty or null");
                return false;
            }
            restaurant.setState(state.getText().toString());

            //TODO Manage GMaps to set this data
            //restaurant.setXcoord();
            //restaurant.setYcoord();
            return true;
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME, e.getMessage());
            return false;
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
        void onChangeFrag2(Restaurant r);
    }
}