package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Restaurant_info_user.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Restaurant_info_user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Restaurant_info_user extends Fragment {

    public interface restaurantData{
        public Restaurant getRestaurant();
    }
    restaurantData data;
    Restaurant restaurant;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Restaurant_info_user() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Restaurant_info_user.
     */
    // TODO: Rename and change types and number of parameters
    public static Restaurant_info_user newInstance(String param1, String param2) {
        Restaurant_info_user fragment = new Restaurant_info_user();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_restaurant_info_user, container, false);
        getRestaurantData(v);
        return v;
    }

    public void getRestaurantData(View v){
        restaurant=data.getRestaurant();
        try {
            restaurant.getData();
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        TextView rest_rev_det =(TextView)v.findViewById(R.id.restaurant_rev_details);
        rest_rev_det.setText("Based on "+restaurant.getReviews().size()+" Reviews");
        TextView restname= (TextView) v.findViewById(R.id.restaurant_name);
        restname.setText(restaurant.getName());
        TextView restaddr=(TextView) v.findViewById(R.id.restaurant_address);
        restaddr.setText(restaurant.getAddress());
        TextView resttel=(TextView)v.findViewById(R.id.restaurant_tel);
        resttel.setText(restaurant.getTelephone());
        RatingBar restrating= (RatingBar) v.findViewById(R.id.restaurant_rating);
        restrating.setRating(restaurant.getRating());

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.restaurant_time_t);

        for(String weekday : getResources().getStringArray(R.array.week)){
            LayoutInflater li = LayoutInflater.from(v.getContext());
            View timetableItem = li.inflate(R.layout.timetable_view,null);
            TextView dow= (TextView) timetableItem.findViewById(R.id.dow);
            dow.setText(weekday);
            TextView dinner= (TextView) timetableItem.findViewById(R.id.time_dinner);
            dinner.setText("09.00-18.00");
            ll.addView(timetableItem);
        }


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            data=(restaurantData) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
