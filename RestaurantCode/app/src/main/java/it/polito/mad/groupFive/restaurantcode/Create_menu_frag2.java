package it.polito.mad.groupFive.restaurantcode;

import android.Manifest;
import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.libs.RealPathUtil;

public class Create_menu_frag2 extends Fragment {
    int dimension=3;
    ArrayList<Option> options;
    shareDish dish;
    public interface shareDish{
        public ArrayList<Option> getdish();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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
        options=dish.getdish();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName() + " - onCreateView";
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_menu_2, container, false);


        Button btnNext = (Button) v.findViewById(R.id.fin);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(METHOD_NAME, "Press Next: OK");


                //TODO passare type, immagine, recuperare restaurant id, creare oggetto menu, lanciare nuova activity
            }
        });


        MenuCourse adp=new MenuCourse(container.getContext(),options);
        ListView lwcm = (ListView) v.findViewById(R.id.lwch_2_1);
        lwcm.setAdapter(adp);
        return v;
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
        void onFragmentInteraction(Uri uri);

    }

    public class MenuCourse extends BaseAdapter {
        Context context;
        ArrayList<Option> options;

        public MenuCourse(ArrayList<Option> options){
            this.options=options;
        }


        public MenuCourse(Context context, ArrayList<Option> options) {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_choice, null);
            TextView opt_title= (TextView) convertView.findViewById(R.id.twch);
            opt_title.setText("Option "+(position+1));
            LinearLayout ll= (LinearLayout) convertView.findViewById(R.id.ll_mc);


            for (String str:options.get(position).getDishes()){
                LayoutInflater inflater=LayoutInflater.from(context);
                View child =inflater.inflate(R.layout.row,null);
                ll.addView(child);
                TextView textView= (TextView) child.findViewById(R.id.ll_row);
                textView.setText("- "+str);


            }

            ImageButton add_dish =(ImageButton)convertView.findViewById(R.id.iwch);
            add_dish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FrameLayout mlay= (FrameLayout) v.findViewById(R.id.frame);
                    mlay.inflate(v.getContext(), R.layout.activity_create_menu, mlay);
                    Add_dish fragment= new Add_dish();
                    getFragmentManager().beginTransaction().replace(R.id.acm_1,fragment).addToBackStack(null).commit();

                }
            });
            return convertView;


        }


            }

  /* private class Option{
    private ArrayList<String> dishes;

       public ArrayList<String> getDishes() {
           return dishes;
       }

       public Option(){
        dishes=new ArrayList<String>();
           dishes.add("torta");
           dishes.add("mela");
           dishes.add("carota");
           dishes.add("salame");
           dishes.add("zucca");
           dishes.add("pizza");

    }
   }*/
}

