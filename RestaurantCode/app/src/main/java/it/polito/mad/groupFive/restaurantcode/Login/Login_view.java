package it.polito.mad.groupFive.restaurantcode.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login_view.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login_view#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login_view extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout incorrect_log;
    private SharedPreferences sharedPreferences;
    private EditText username;
    private EditText password;
    private Customer user;
    private RestaurantOwner owner;
    private boolean own;
    private OnFragmentInteractionListener mListener;

    public Login_view() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login_view.
     */
    // TODO: Rename and change types and number of parameters
    public static Login_view newInstance(String param1, String param2) {
        Login_view fragment = new Login_view();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_login_view, container, false);
        sharedPreferences=getContext().getSharedPreferences(getString(R.string.user_pref),getContext().MODE_PRIVATE);
        Button create_new= (Button) v.findViewById(R.id.register);
        create_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register=new Intent(getContext(),CreateLogin.class);
                startActivityForResult(register,5);
            }
        });
        username=(EditText)v.findViewById(R.id.username);
        password=(EditText)v.findViewById(R.id.password);
        if(sharedPreferences.getBoolean("owner",false)){
            try {

                owner=new RestaurantOwner(getContext(),sharedPreferences.getInt("uid",-1));
                own=true;
            } catch (RestaurantOwnerException e) {
                e.printStackTrace();
            }
        }else {
            try {
                user=new Customer(getContext(),sharedPreferences.getInt("uid",-1));
                own=false;
            } catch (CustomerException e) {
                e.printStackTrace();
            }
        }
        incorrect_log= (LinearLayout) v.findViewById(R.id.ll_incorrect);
        Button login=(Button)v.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

    if(!own){
        if ((sharedPreferences.getInt("uid",-1)!=-1)&&username.getText().toString().equals(user.getUserName())&&password.getText().toString().equals(user.getPassword())){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("logged",true);
            editor.commit();
            mListener.onFragmentInteraction();
            getFragmentManager().popBackStack();
        }
        else{
            incorrect_log.removeAllViewsInLayout();
            View view=inflater.inflate(R.layout.incorrect_login,null);
            incorrect_log.addView(view);}
    }
                if(own){
                    if((sharedPreferences.getInt("uid",-1)!=-1)&&username.getText().toString().equals(owner.getUserName())&&password.getText().toString().equals(owner.getPassword())){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean("logged",true);
                        editor.commit();
                        Log.v("pws",password.getText().toString());
                        mListener.onFragmentInteraction();
                        getFragmentManager().popBackStack();
                    }else{
                        incorrect_log.removeAllViewsInLayout();
                        View view=inflater.inflate(R.layout.incorrect_login,null);
                        incorrect_log.addView(view);}

                }



            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        update();
    }

    public void update(){
        sharedPreferences=getContext().getSharedPreferences(getString(R.string.user_pref),getContext().MODE_PRIVATE);
        if(sharedPreferences.getBoolean("owner",false)){
            try {
                Log.v("owner", "tr");
                owner=new RestaurantOwner(getContext(),sharedPreferences.getInt("uid",-1));
                own=true;
            } catch (RestaurantOwnerException e) {
                e.printStackTrace();
            }
        }else {
            try {
                Log.v("owner", "fl");
                user=new Customer(getContext(),sharedPreferences.getInt("uid",-1));
                own=false;
            } catch (CustomerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
