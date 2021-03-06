package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad.groupFive.restaurantcode.New_Menu_details;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
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
        public String getMid();
        public ArrayList<Menu> getMenus();
    }
    restaurantData restData;
    ArrayList<Menu> menusshared;
    Restaurant rest;
    MenuAdapter adp;
    private String mid;

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
        adp= new MenuAdapter(menusshared,mid);
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
        mid=restData.getMid();
        menusshared=new ArrayList<>();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference ref2=db.getReference("menu");
        ref2.orderByChild("rid").equalTo(restData.getRestaurant().getRid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                final Menu  menu = new Menu();
                //Log.v("rid",(String)dataSnapshot.child("name").getValue());
                menu.setRid((String)dataSnapshot.child("rid").getValue());
                menu.setName((String) dataSnapshot.child("name").getValue());
                //Log.v("name",menu.getName());
                menu.setMid(dataSnapshot.child("mid").getValue().toString());
                menu.setBeverage((Boolean) dataSnapshot.child("beverage").getValue());
                menu.setDescription((String) dataSnapshot.child("description").getValue());
                menu.setPrice(Float.parseFloat(dataSnapshot.child("price").getValue().toString()));
                menu.setServiceFee((Boolean) dataSnapshot.child("serviceFee").getValue());
                menu.setType(Integer.parseInt(dataSnapshot.child("type").getValue().toString()));
                menu.setSpicy((Boolean) dataSnapshot.child("spicy").getValue());
                menu.setVegetarian((Boolean) dataSnapshot.child("vegetarian").getValue());
                menu.setVegan((Boolean) dataSnapshot.child("vegan").getValue());
                menu.setGlutenfree((Boolean) dataSnapshot.child("glutenfree").getValue());
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/menus/");
                ContextWrapper cw = new ContextWrapper(getContext());
                File dir = cw.getDir("images", Context.MODE_PRIVATE);
                final File filePath = new File(dir,menu.getMid());
                storageRoot.child(menu.getMid()).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        menu.setImageLocal(filePath.getAbsolutePath());
                        menusshared.add(menu);
                        adp.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.e("onFailure",e.getMessage());
                    }
                });
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
        private String mid;

        public MenuAdapter(ArrayList<Menu> menus,String mid){
            this.mid=mid;
            this.menus=menus;
            sort();

        }
        public void sort(){
            Collections.sort(this.menus, new Comparator<Menu>() {
                @Override
                public int compare(Menu lhs, Menu rhs) {
                    if(lhs.getMid().toString().equals(mid)){
                        return -3;
                    }
                    if (rhs.getMid().toString().equals(mid)){
                        return +3;
                    }

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
            if(menu.getMid().toString().equals(mid)){
                holder.card.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            else{
                holder.card.setCardBackgroundColor(getResources().getColor(R.color.cardview_light_background));
            }
            String s=menu.getDescription();
            holder.menu_description.setText(s);
            holder.menu_name.setText(menu.getName());
            holder.menu_price.setText(String.format("%.2f", menu.getPrice())+"€");
            if(!menu.isSpicy()) holder.spicy_icon.setColorFilter(Color.GRAY);
            if(!menu.isVegan()) holder.vegan_icon.setColorFilter(Color.GRAY);
            if(!menu.isVegetarian()) holder.vegetarian_icon.setColorFilter(Color.GRAY);
            if(!menu.isGlutenfree()) holder.glutenfree_icon.setColorFilter(Color.GRAY);
            File img = new File(menu.getImageLocalPath());
            try {
                holder.menu_image.setImageBitmap(new Picture(Uri.fromFile(img),getActivity().getContentResolver(),300,300).getBitmap());
            } catch (IOException e) {
                //Log.e(METHOD_NAME,e.getMessage());
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
             //save
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
                Bundle bundle = new Bundle();
                bundle.putString("rid",menu.getRid());
                bundle.putString("mid",menu.getMid());
                New_Menu_details menu_details = new New_Menu_details();
                menu_details.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_up,R.anim.slide_down,R.anim.slide_up,R.anim.slide_down)
                        .addToBackStack(null).add(R.id.frame,menu_details).commit();
            }
        }
    }

    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        final File dir = cw.getDir("images", Context.MODE_PRIVATE);
        File filePath = new File(dir,id);
        storageRoot.child(id).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File img = new File(dir, id);
                Uri imgPath = Uri.fromFile(img);
                try {
                    Bitmap b = new Picture(imgPath,getActivity().getContentResolver()).getBitmap();
                    imView.setImageBitmap(b);
                } catch (IOException e) {
                    //Log.e("getFromNet",e.getMessage());
                }
                catch (NullPointerException e){

                }
            }
        });
    }
}
