package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Add_dish.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Add_dish#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add_dish extends Fragment {
    private Option sett;
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options;
    private String namedish;
    private View parent;
    private shareDish dish;
    public interface shareDish{
        public Option getOption();
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Add_dish() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add_dish.
     */
    // TODO: Rename and change types and number of parameters
    public static Add_dish newInstance(String param1, String param2) {
        Add_dish fragment = new Add_dish();
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

        sett=dish.getOption();
        options=sett.getMenu().getOptions();
        View view=inflater.inflate(R.layout.fragment_add_dish, container, false);
        parent=view;
        Spinner type=(Spinner) view.findViewById(R.id.nd_type);
        ArrayList<String> strings=new ArrayList<>();
        strings.add(getString(R.string.add_dish_starter));
        strings.add(getString(R.string.add_dish_main_dish));
        ArrayAdapter<String> tp =new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_dropdown_item,strings);
        type.setAdapter(tp);

        Spinner type2=(Spinner) view.findViewById(R.id.nd_type_2);
        ArrayList<String> strings_2=new ArrayList<>();
        strings_2.add(getString(R.string.add_dish_italian));
        strings_2.add(getString(R.string.add_dish_chinese));
        strings_2.add(getString(R.string.add_dish_indian));
        ArrayAdapter<String> tp2 =new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_dropdown_item,strings_2);
        type2.setAdapter(tp2);






        Button add = (Button) view.findViewById(R.id.ad_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=sett.getOpt_number();
                EditText newdish = (EditText) parent.findViewById(R.id.nd_et_1);
                namedish=  newdish.getText().toString();
                if (namedish.isEmpty()==false){
                    try {
                        //TODO other info
                        Course course =new Course(dish.getOption().getRestaurant());
                        ArrayList<Course> courses=options.get(position).getCourses();
                        course.setName(namedish);
                        courses.add(course);

                    } catch (CourseException e) {
                        e.printStackTrace();
                    }


                    Log.v("dish",namedish);
                }
                Log.v("else",namedish);
                getFragmentManager().popBackStack();
            }
        });
        return view;


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
            dish=(shareDish) context;
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
        void onFragmentInteraction(Uri uri);
    }
}
