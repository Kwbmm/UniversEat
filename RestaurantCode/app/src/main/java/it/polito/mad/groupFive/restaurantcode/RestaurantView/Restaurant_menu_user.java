package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad.groupFive.restaurantcode.Menu_details;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Restaurant_menu_user.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Restaurant_menu_user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Restaurant_menu_user extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public interface restaurantData{
        public Restaurant getRestaurant();
    }
    restaurantData restData;
    ArrayList<Menu> menusshared;
    Restaurant rest;
    MenuAdapter adp;

    public Restaurant_menu_user() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Restaurant_menu_user.
     */
    // TODO: Rename and change types and number of parameters
    public static Restaurant_menu_user newInstance(String param1, String param2) {
        Restaurant_menu_user fragment = new Restaurant_menu_user();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public void readdata(View v) {


        rest=restData.getRestaurant();
        menusshared = rest.getMenus();
        adp= new MenuAdapter(menusshared);
        RecyclerView recyclerView=(RecyclerView)v.findViewById(R.id.my_recycler_view);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(v.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


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
        View v=inflater.inflate(R.layout.fragment_restaurant_menu_user, container, false);
        readdata(v);
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
            restData=(restaurantData)context;
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
        void onFragmentInteraction(Uri uri);
    }


    public static class MenuViewHoder extends RecyclerView.ViewHolder {
        protected TextView menu_name;
        protected TextView menu_desctiprion;
        protected TextView menu_price;
        protected CardView card;

        public MenuViewHoder(View itemView) {
            super(itemView);
            this.menu_name =(TextView)itemView.findViewById(R.id.menu_name);
            this.menu_desctiprion=(TextView)itemView.findViewById(R.id.menu_description);
            this.menu_price=(TextView)itemView.findViewById(R.id.menu_price);
            this.card= (CardView) itemView.findViewById(R.id.menu_card);
        }
    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private ArrayList<Menu> menus;

        public MenuAdapter(ArrayList<Menu> menus){
            this.menus=menus;
            sort();

        }
        public void sort(){
            Collections.sort(this.menus, new Comparator<Menu>() {
                @Override
                public int compare(Menu lhs, Menu rhs) {

                    return rhs.getType()-lhs.getType();
                }});
        }
        public void update(){
            menus=menusshared;
            sort();

        }

        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,null);
            return new MenuViewHolder(menu_view);
        }

        @Override
        public void onBindViewHolder(MenuViewHolder holder, int position) {
            Menu menu =menus.get(position);
            holder.menu_description.setText(menu.getDescription());
            holder.menu_name.setText(menu.getName());
            holder.menu_price.setText(menu.getPrice()+"€");
            if(!menu.isGlutenFree())
                holder.glutenfree_icon.setVisibility(View.GONE);
            if(!menu.isVegetarian())
                holder.vegetarian_icon.setVisibility(View.GONE);
            if(!menu.isVegan())
                holder.vegan_icon.setVisibility(View.GONE);
            if(!menu.isSpicy())
                holder.spicy_icon.setVisibility(View.GONE);
            try {
                holder.menu_image.setImageBitmap(menu.getImageBitmap());
            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            }
            holder.card.setOnClickListener(new OnCardClick(menu));



        }

        @Override
        public int getItemCount() {
            return menus.size();
        }

        public void remove(int position){
            menus.remove(position);
            rest.setMenus(menus);
            try {
                rest.saveData();
            } catch (RestaurantException e) {
                e.printStackTrace();
            }
            menusshared=menus;
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, menus.size());
        }

        public class onEditclick implements View.OnClickListener{
            private int position;
            public onEditclick(int position){
                this.position=position;
            }

            @Override
            public void onClick(View v) {


            }
        }

        public class onPositionClickDialog implements DialogInterface.OnClickListener{
            private int position;

            public onPositionClickDialog(int position){
                this.position=position;
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:{
                        //TODO:edit intent
                        break;
                    }
                    case 1:{
                        //TODO:delete item
                        remove(position);
                        break;
                    }
                    default:{
                        dialog.cancel();
                    }
                }

            }
        }

        private class OnCardClick implements View.OnClickListener {
            Menu menu;
            public OnCardClick(Menu menu) {
                this.menu=menu;

            }

            @Override
            public void onClick(View v) {
                Intent menu_info=new Intent(getContext(), Menu_details.class);
                menu_info.putExtra("rid",menu.getRid());
                menu_info.putExtra("mid",menu.getMid());
                startActivity(menu_info);
            }
        }
    }
}
