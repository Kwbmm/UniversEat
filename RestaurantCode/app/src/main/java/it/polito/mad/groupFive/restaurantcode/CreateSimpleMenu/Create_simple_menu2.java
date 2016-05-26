package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.holders.DishViewHolder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Create_simple_menu2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Create_simple_menu2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Create_simple_menu2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public interface shareData{
        MenuData getdata();
    }
    private shareData sData;
    private it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu;
    private Restaurant rest;
    private MenuData data;
    private DishAdapter adp;
    private RecyclerView recyclerView;
    private EditText price;
    private CheckBox fee;
    private CheckBox drink;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Create_simple_menu2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Create_simple_menu2.
     */
    // TODO: Rename and change types and number of parameters
    public static Create_simple_menu2 newInstance(String param1, String param2) {
        Create_simple_menu2 fragment = new Create_simple_menu2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private void fetchData(){
        if (sData.getdata().isEdit()){
            price.setText(String.valueOf(sData.getdata().getMenu().getPrice()));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_add,menu);
        menu.findItem(R.id.add_ab).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                        Course newDish;
                Add_simple_dish add_dish=new Add_simple_dish();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_holder,add_dish).commit();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        data=sData.getdata();
        rest=sData.getdata().getRest();
        menu=sData.getdata().getMenu();
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_create_simple_menu2, container, false);
        setUpData(v);
        fetchData();

        return v;
    }

    public void setUpData(View v){
        adp= new DishAdapter(data.getMenu().getCourses());
        recyclerView=(RecyclerView)v.findViewById(R.id.my_recycler_view);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        price= (EditText) v.findViewById(R.id.fcm2_price);
        drink= (CheckBox) v.findViewById(R.id.cbch_2_1);
        fee=(CheckBox)v.findViewById(R.id.cbch_2_2);
        Button end =(Button)v.findViewById(R.id.fin);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


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

    private class DishAdapter extends RecyclerView.Adapter<DishViewHolder>{
        ArrayList<Course> courses;

        public DishAdapter(ArrayList<Course> courses){
            this.courses=courses;
        }

        @Override
        public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getContext()).inflate(R.layout.menu_dish_frag,null);

            return  new DishViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DishViewHolder holder, int position) {
            int visible;
            Course course= courses.get(position);
            holder.dish_name.setText(course.getName());
            Log.v("Course",course.getName());
            holder.delete.setOnClickListener(new onClickRemove(position,courses));

            if(course.isGlutenFree()){
                visible=View.VISIBLE;
            }
            else{
                visible=View.INVISIBLE;
            }
            holder.glunten_free.setVisibility(visible);

            if(course.isSpicy()){
                visible=View.VISIBLE;
            }
            else{
                visible=View.INVISIBLE;
            }
            holder.hot.setVisibility(visible);

            if(course.isVegan()){
                visible=View.VISIBLE;
            }
            else{
                visible=View.INVISIBLE;
            }
            holder.vegan.setVisibility(visible);

            if(course.isVegetarian()){
                visible=View.VISIBLE;
            }
            else{
                visible=View.INVISIBLE;
            }
            holder.vegetarian.setVisibility(visible);





        }
        public class onClickRemove implements View.OnClickListener{
            int position;
            ArrayList<Course> alc;
            public onClickRemove(int position,ArrayList<Course> courses){
                this.position=position;
                this.alc=courses;
            }
            @Override
            public void onClick(View v) {
                alc.remove(position);
                adp.notifyItemRemoved(position);
                adp.notifyDataSetChanged();

            }
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }
    }
}
