package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.holders.TagsHolder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Simple_menu_add_tags.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Simple_menu_add_tags#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Simple_menu_add_tags extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public interface shareData{
        MenuData getdata();
    }

    shareData sData;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayMap<String,Boolean> tags;

    public Simple_menu_add_tags() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Simple_menu_add_tags.
     */
    // TODO: Rename and change types and number of parameters
    public static Simple_menu_add_tags newInstance(String param1, String param2) {
        Simple_menu_add_tags fragment = new Simple_menu_add_tags();
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
        View v= inflater.inflate(R.layout.fragment_simple_menu_add_tags, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.actionBar_addTags);
        setUpData(v);
        return v;
    }
    public void setUpData(View v){
        TagAdapter adp;
        tags=new ArrayMap<>();
        for(String s : convertStringArrayToArraylist(getResources().getStringArray(R.array.tags))){
            tags.put(s,false);
        }
        adp= new TagAdapter(tags);
        RecyclerView recyclerView;
        recyclerView=(RecyclerView)v.findViewById(R.id.tag_list);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        Button create=(Button)v.findViewById(R.id.add_tags);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> c_tags=new ArrayList<String>();
                for(int i=0;i<tags.size();i++){
                    if(tags.valueAt(i)) c_tags.add(tags.keyAt(i));
                }
                sData.getdata().getNewDish().setTags(c_tags);
                //Log.v("vect",c_tags.size()+"");
                getFragmentManager().popBackStack();
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

    public class TagAdapter extends RecyclerView.Adapter<TagsHolder>{
        ArrayMap<String,Boolean> tags;

        public TagAdapter(ArrayMap<String,Boolean> tags){
            this.tags=tags;

        }

        @Override
        public TagsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getContext()).inflate(R.layout.fragment_tag_add,null);
            return new TagsHolder(v);
        }

        @Override
        public void onBindViewHolder(TagsHolder holder, final int position) {
            holder.checkBox.setText(tags.keyAt(position));
            holder.checkBox.setChecked(tags.valueAt(position));
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tags.valueAt(position)){
                        tags.setValueAt(position,false);
                    }else{
                        tags.setValueAt(position,true);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return tags.size();
        }
    }
    public static ArrayList<String> convertStringArrayToArraylist(String[] strArr){
        ArrayList<String> stringList = new ArrayList<String>();
        for (String s : strArr) {
            stringList.add(s);
        }
        return stringList;
    }
}
