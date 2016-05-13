package it.polito.mad.groupFive.restaurantcode.CreateMenu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class Create_menu_frag2 extends Fragment {

    shareDish dish;
    public interface shareDish{
        public Option getOption();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Menu menu=null;
    private Menu optMenu;
    private Restaurant restaurant=null;
    private EditText editprice;
    private boolean beverage;
    private boolean servicefee;
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options;
    private Option sett;

    private OnFragmentInteractionListener mListener;

    public Create_menu_frag2() {
        // Required empty public constructor
    }

    public static Create_menu_frag2 newInstance(String param1, String param2) {
        Create_menu_frag2 fragment = new Create_menu_frag2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName() + "onCreate";
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);




        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName() + " - onCreateView";
        // Inflate the layout for this fragment
        sett=dish.getOption();
        View v = inflater.inflate(R.layout.fragment_create_menu_2, container, false);

        editprice = (EditText) v.findViewById(R.id.fcm2_price);
        final CheckBox ckbBeverage = (CheckBox) v.findViewById(R.id.cbch_2_1);
        ckbBeverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(ckbBeverage.isChecked())
                    beverage =true;
                else beverage = false;
            }
        });


        final CheckBox ckbServicefee = (CheckBox) v.findViewById(R.id.cbch_2_2);
        ckbServicefee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(ckbServicefee.isChecked())
                    servicefee =true;
                else servicefee = false;
            }
        });



        Button btnNext = (Button) v.findViewById(R.id.fin);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(METHOD_NAME, "Press Finish: OK");
                Activity a = getActivity();
                if(a instanceof OnFragmentInteractionListener) {
                    setMenuData();
                OnFragmentInteractionListener obs = (OnFragmentInteractionListener) a;
                obs.onChangeFrag1(menu);

            }}
        });
        optMenu=dish.getOption().getMenu();
        options=optMenu.getOptions();
        MenuCourse adp=new MenuCourse(container.getContext(),options);
        ListView lwcm = (ListView) v.findViewById(R.id.lwch_2_1);
        lwcm.setAdapter(adp);
        return v;
    }
    private void setMenuData() {
        final String METHOD_NAME = this.getClass().getName()+" - setMenuData";
        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), Create_menu.MODE_PRIVATE);
        int rid = sp.getInt("rid",-1);
        int uid = sp.getInt("uid",-1);

        try {
            this.menu=sett.getMenu();
            restaurant = new Restaurant(getContext(),rid);
            restaurant.getData();
            String txtprice = editprice.getText().toString();
            if (txtprice.equals(""))
                menu.setPrice(Float.parseFloat("0.0"));
            else
                menu.setPrice(Float.parseFloat(txtprice));
            menu.setBeverage(beverage);
            menu.setServiceFee(servicefee);

        } catch (RestaurantException e) {
            e.printStackTrace();
        }

    }
    public static int randInt() {

        Random rand= new Random();
        return rand.nextInt(Integer.MAX_VALUE -1 );
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            dish=(shareDish)context;
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

    @Override
    public void onResume() {
        super.onResume();
        onCreate(new Bundle());
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChangeFrag1(Menu menu);

    }

    public class MenuCourse extends BaseAdapter {
        Context context;
        ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options;

        public MenuCourse(ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options){
            this.options=options;
        }


        public MenuCourse(Context context, ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options) {
            this.options=options;
            this.context = context;
        }

        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Object getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_choice, null);
            TextView opt_title= (TextView) convertView.findViewById(R.id.twch);
            opt_title.setText(getString(R.string.Create_menu_frag2_option)+(position+1));
            LinearLayout ll= (LinearLayout) convertView.findViewById(R.id.ll_mc);


            for (Course dish:options.get(position).getCourses()){
                LayoutInflater inflater=LayoutInflater.from(context);
                View child =inflater.inflate(R.layout.row,null);
                ll.addView(child);
                TextView textView= (TextView) child.findViewById(R.id.ll_row);
                textView.setText("- "+dish.getName());


            }

            ImageButton add_dish =(ImageButton)convertView.findViewById(R.id.iwch);
            add_dish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FrameLayout mlay= (FrameLayout) v.findViewById(R.id.frame);
                    mlay.inflate(v.getContext(), R.layout.activity_create_menu, mlay);
                    Add_dish fragment= new Add_dish();
                    sett.setOpt_number(position);
                    getFragmentManager().beginTransaction().replace(R.id.acm_1,fragment).addToBackStack(null).commit();

                }
            });
            ImageButton delete_dish =(ImageButton) convertView.findViewById(R.id.iwch_2);
            delete_dish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FrameLayout mlay= (FrameLayout) v.findViewById(R.id.frame);
                    mlay.inflate(v.getContext(), R.layout.fragment_delete_dish, mlay);
                    Delete_dish fragment= new Delete_dish();
                    sett.setOpt_number(position);
                    getFragmentManager().beginTransaction().replace(R.id.acm_1,fragment).addToBackStack(null).commit();
                }
            });
            return convertView;


        }


            }

}

