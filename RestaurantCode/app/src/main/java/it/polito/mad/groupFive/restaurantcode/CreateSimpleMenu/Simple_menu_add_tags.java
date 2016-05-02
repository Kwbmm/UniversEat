package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
        TagAdapter adp;
        adp= new TagAdapter(convertStringArrayToArraylist(getResources().getStringArray(R.array.tags)));
        RecyclerView recyclerView;
        recyclerView=(RecyclerView)v.findViewById(R.id.tag_list);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
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
        ArrayList<String> tags;

        public TagAdapter(ArrayList<String> tags){
            this.tags=tags;

        }

        @Override
        public TagsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getContext()).inflate(R.layout.fragment_tag_add,null);
            return new TagsHolder(v);
        }

        @Override
        public void onBindViewHolder(TagsHolder holder, int position) {
            holder.checkBox.setText(tags.get(position));

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
