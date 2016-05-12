package it.polito.mad.groupFive.restaurantcode.Login;

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
import android.widget.EditText;
import android.widget.Toast;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;

/**
 * Created by Giovanni on 22/04/16.
 */
public class Createlog_frag2 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Codes for intents
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;

    private View v=null;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Customer user=null;
    private RestaurantOwner user_r=null;

    private EditText txtpassword;
    private EditText txtrepeat;
    private boolean owner;
    private String pw1;
    private String pw2;


    public Createlog_frag2(){
        //
    }
    public static Createlog_frag2 newInstance(String param1, String param2) {
        Createlog_frag2 fragment = new Createlog_frag2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+"onCreate";
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChangeFrag1(Customer u, RestaurantOwner u_r);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName() + " - onCreateView";
        owner =this.getArguments().getBoolean("owner");
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_createlog_2, container, false);
        txtpassword = (EditText) v.findViewById(R.id.editText_Password);
        txtrepeat = (EditText) v.findViewById(R.id.editText_Password_repeat);

        Button btnNext = (Button) v.findViewById(R.id.Button_End);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(METHOD_NAME,"Press Next: OK");

                Activity a = getActivity();
                if(a instanceof OnFragmentInteractionListener) {
                    pw1 = txtpassword.getText().toString();
                    pw2 = txtrepeat.getText().toString();
                    if(pw1.equals(pw2)){
                        setUserData();
                        OnFragmentInteractionListener obs = (OnFragmentInteractionListener) a;
                        obs.onChangeFrag1(user,user_r);
                    }
                    else{
                        Toast.makeText(getContext(),getResources().getString(R.string.toastloginFail),Toast.LENGTH_LONG)
                                .show();
                    }

                }}});

        return v;

    }

    public void setUserData(){
        final String METHOD_NAME = this.getClass().getName()+" - setUserData";
        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateLogin.MODE_PRIVATE);
        int uid = sp.getInt("uid",-1);

        try {
            if(owner){
                user_r = new RestaurantOwner(getActivity(),uid);
                pw1 = txtpassword.getText().toString();
                user_r.setPassword(pw1);
                user_r.saveData();
            }
            else {
                user = new Customer(getActivity(), uid);
                user.setPassword(pw1);
                user.saveData();
            }

        } catch (RestaurantOwnerException e) {
            e.printStackTrace();
        } catch (CustomerException e) {
            e.printStackTrace();
        }


    }

}
