package it.polito.mad.groupFive.restaurantcode.Login;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

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
    private ValueAnimator valueAnimator1;
    private ValueAnimator valueAnimator2;
    private RelativeLayout background;
    private TextView login;
    private ProgressBar progressBar;
    private TextView reset;
    private User user;
    private boolean own;
    private OnFragmentInteractionListener mListener;
    private int counter = 0;

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
        View v = inflater.inflate(R.layout.fragment_login_view, container, false);
        setAnimators();
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.user_pref), getContext().MODE_PRIVATE);
        TextView create_new = (TextView) v.findViewById(R.id.register);
        create_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getContext(), CreateLogin.class);
                startActivityForResult(register, 5);
            }
        });
        username = (EditText) v.findViewById(R.id.emailAddress);
        password = (EditText) v.findViewById(R.id.password);
        reset =(TextView)v.findViewById(R.id.reset_button);
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar5);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        background = (RelativeLayout) v.findViewById(R.id.background);
        background.postDelayed(new Runnable() {
            @Override
            public void run() {
                valueAnimator1.start();
            }
        }, 300);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        incorrect_log = (LinearLayout) v.findViewById(R.id.ll_incorrect);
        login = (TextView) v.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                incorrect_log.removeAllViewsInLayout();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String psw, usr;
                psw = username.getText().toString();
                usr = password.getText().toString();
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    auth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            final FirebaseAuth auth = FirebaseAuth.getInstance();
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                User customer = new User(firebaseUser.getUid());
                                FirebaseDatabase db;
                                db = FirebaseDatabase.getInstance();
                                DatabaseReference Myref = db.getReference("User");

                                //Myref.child("Owner").addValueEventListener(new UserDataListener(this));
                                Myref.orderByChild("uid").equalTo(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        own = (Boolean) dataSnapshot.child("manager").getValue();
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("owner", own);
                                        editor.commit();
                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                        FirebaseUser firebaseUser = auth.getCurrentUser();

                                        if (!own) {

                                            editor = sharedPreferences.edit();
                                            editor.putBoolean("logged", true);
                                            editor.putBoolean("owner", false);
                                            editor.putString("uid", firebaseUser.getUid());
                                            editor.putString("psw", password.getText().toString());
                                            editor.putString("email", username.getText().toString());
                                            editor.commit();
                                            mListener.onFragmentInteraction();
                                            getFragmentManager().popBackStack();
                                        } else {
                                            editor = sharedPreferences.edit();
                                            editor.putBoolean("logged", true);
                                            editor.putBoolean("owner", true);
                                            editor.putString("uid", firebaseUser.getUid());
                                            editor.putString("psw", password.getText().toString());
                                            editor.putString("email", username.getText().toString());
                                            editor.commit();
                                            //Log.v("pws", password.getText().toString());
                                            mListener.onFragmentInteraction();
                                            getFragmentManager().popBackStack();
                                        }


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
                                });


                            } else {
                                counter++;
                                View view = inflater.inflate(R.layout.incorrect_login, null);
                                incorrect_log.addView(view);
                                if (counter < 2) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    login.setVisibility(View.VISIBLE);
                                } else {
                                    reset.setVisibility(View.VISIBLE);
                                    reset.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            auth.sendPasswordResetEmail(username.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast toast = Toast.makeText(getContext(), R.string.check_email, Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            });
                                        }
                                    });
                                    progressBar.setVisibility(View.INVISIBLE);
                                    login.setVisibility(View.VISIBLE);
                                }

                            }

                        }
                    });


                } else {
                    Toast.makeText(getActivity().getBaseContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
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

    private void setAnimators() {
        valueAnimator1 = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.fragmentTransparent), getResources().getColor(R.color.fragmentWhite));
        valueAnimator1.setDuration(300);
        valueAnimator1.setInterpolator(new DecelerateInterpolator(2));
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        valueAnimator2 = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.fragmentWhite), getResources().getColor(R.color.fragmentTransparent));
        valueAnimator2.setDuration(100);
        //valueAnimator2.setInterpolator(new DecelerateInterpolator(2));
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
    }

    @Override
    public void onDestroyView() {
        valueAnimator2.start();
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        update();
    }

    public void update() {
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.user_pref), getContext().MODE_PRIVATE);
        if (sharedPreferences.getBoolean("owner", false)) {


            //Log.v("owner", "fl");
            user = new User(sharedPreferences.getString("uid", null));
            own = false;

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
