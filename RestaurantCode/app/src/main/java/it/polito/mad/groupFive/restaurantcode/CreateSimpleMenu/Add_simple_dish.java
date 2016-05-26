package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Add_simple_dish.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Add_simple_dish#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add_simple_dish extends Fragment{


    public interface shareData{
        MenuData getdata();
    }
    private shareData sData;
    private EditText name;
    private MenuData data;
    private CheckBox vegan;
    private CheckBox vegetarian;
    private CheckBox glutenFree;
    private CheckBox hot;
    private TextView price;
    private Course newDish;
    private View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Add_simple_dish() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add_simple_dish.
     */
    // TODO: Rename and change types and number of parameters
    public static Add_simple_dish newInstance(String param1, String param2) {
        Add_simple_dish fragment = new Add_simple_dish();
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
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        showTags(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_add_simple_dish, container, false);
        data=sData.getdata();

        newDish=sData.getdata().getNewDish();
        name= (EditText)v.findViewById(R.id.nd_et_1);
        vegan=(CheckBox)v.findViewById(R.id.nd_ck_1);
        vegetarian=(CheckBox)v.findViewById(R.id.nd_ck_2);
        hot=(CheckBox)v.findViewById(R.id.nd_ck_3);
        glutenFree=(CheckBox)v.findViewById(R.id.nd_ck_4);

        ImageButton add_tags= (ImageButton) v.findViewById(R.id.add_tags_but);
        add_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Simple_menu_add_tags tags_frag=new Simple_menu_add_tags();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_holder,tags_frag).commit();

            }
        });
        //showTags(v);
        Button add= (Button)v.findViewById(R.id.ad_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    newDish.setName(name.getText().toString());
                    newDish.setVegan(vegan.isChecked());
                    newDish.setGlutenFree(glutenFree.isChecked());
                    newDish.setVegetarian(vegetarian.isChecked());
                    newDish.setSpicy(hot.isChecked());
                    ArrayList<Course> alc=data.getMenu().getCourses();
                    alc.add(newDish);
                    data.getMenu().setCourses(alc);
//                    data.getRest().saveData();
                    Log.v("back","back");
                    getFragmentManager().popBackStack();

            }
        });
        Spinner cuisine = (Spinner) v.findViewById(R.id.nd_type);
        cuisine.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.cuisine)));
        Spinner type = (Spinner) v.findViewById(R.id.nd_type_2);
        type.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.type)));
        view=v;
        return v;
    }

    public void showTags(View v){

        ScrollView scrollView= (ScrollView) v.findViewById(R.id.tag_list);
        LinearLayout verlay=(LinearLayout) v.findViewById(R.id.tag_list_vert);
        LinearLayout horlay=new LinearLayout(getContext());
        horlay.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horlay.setLayoutParams(LLParams);
        verlay.addView(horlay);

            ArrayList<String> res;

            res=sData.getdata().getNewDish().getTags();
            Log.v("count",res.size()+"");
            int count=0,lng=0;
            for (String s:res){
                View child=LayoutInflater.from(getContext()).inflate(R.layout.tag_card_frag,null);
                TextView tag_name=(TextView)child.findViewById(R.id.tag_name);
                //lng+=res[count].length();
                Log.v("count",count+"");
                tag_name.setText(s);
                if(lng+s.length()>20){
                    horlay=new LinearLayout(getContext());
                    horlay.setOrientation(LinearLayout.HORIZONTAL);
                    horlay.setLayoutParams(LLParams);
                    verlay.addView(horlay);
                    lng=0;
                }
                lng+=s.length();
                horlay.addView(child);
                count++;
            }}



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
            sData=(shareData)context;
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
