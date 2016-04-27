package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Delete_dish.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Delete_dish#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Delete_dish extends Fragment {

    ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options;
    Option sett;
    shareDish dish;
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

    public Delete_dish() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Delete_dish.
     */
    // TODO: Rename and change types and number of parameters
    public static Delete_dish newInstance(String param1, String param2) {
        Delete_dish fragment = new Delete_dish();
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
        View v=inflater.inflate(R.layout.fragment_delete_dish, container, false);
        showlist(v);
        Button complete= (Button) v.findViewById(R.id.dd_compl);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return v;

    }

    public void showlist(View v){
        sett=dish.getOption();
        options=sett.getMenu().getOptions();
        DeleteCourse dc=new DeleteCourse(options,getContext());
        ListView list= (ListView) v.findViewById(R.id.ll_del);
        list.setAdapter(dc);

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public class DeleteCourse extends BaseAdapter{
        ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options;
        Context context;
        public DeleteCourse(ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options, Context context){
            this.options=options;
            this.context=context;
        }

        @Override
        public int getCount() {
            return options.get(sett.getOpt_number()).getCourses().size();
        }

        @Override
        public Object getItem(int position) {
            return options.get(sett.getOpt_number()).getCourses().get(position);
        }

        @Override
        public long getItemId(int position) {
            return options.get(sett.getOpt_number()).getCourses().get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView=LayoutInflater.from(context).inflate(R.layout.delete_item_list, null);
            TextView name= (TextView) convertView.findViewById(R.id.obj_li);
            name.setText(options.get(sett.getOpt_number()).getCourses().get(position).getName());

            ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete_but);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    options.get(sett.getOpt_number()).getCourses().remove(position);
                    showlist(v.getRootView());
                }
            });


            return convertView;
        }
    }
}
