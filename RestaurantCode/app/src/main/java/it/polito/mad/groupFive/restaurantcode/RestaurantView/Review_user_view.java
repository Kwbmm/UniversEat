package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.Context;
import android.content.Intent;
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
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Review;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Review_user_view.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Review_user_view#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Review_user_view extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public interface restaurantData{
        public Restaurant getRestaurant();
    }
    restaurantData restData;
    Restaurant rest;
    ReviewAdapter adp;
    ArrayList<Review> reviews;
    RecyclerView recyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Review_user_view() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Review_user_view.
     */
    // TODO: Rename and change types and number of parameters
    public static Review_user_view newInstance(String param1, String param2) {
        Review_user_view fragment = new Review_user_view();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        readdata(getView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_add,menu);
        MenuItem item=menu.findItem(R.id.add_ab);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent new_rew= new Intent(getContext(),Create_review.class);
                new_rew.putExtra("rid",rest.getRid());
                new_rew.putExtra("restName",rest.getName());
                startActivityForResult(new_rew,1);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    public void readdata(View v) {


        adp= new ReviewAdapter();
        recyclerView=(RecyclerView)v.findViewById(R.id.review_list);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(v.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        TextView r_count= (TextView)v.findViewById(R.id.review_counter);
        r_count.setText(adp.getItemCount()+" reviews");
        TextView r_name=(TextView)v.findViewById(R.id.restaurant_name);
        r_name.setText(rest.getName());
        RatingBar rate=(RatingBar)v.findViewById(R.id.rate);
        rate.setRating(rest.getRating());


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reviews=new ArrayList<>();
        rest=restData.getRestaurant();
        View v =inflater.inflate(R.layout.fragment_review_user_view, container, false);
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference reference=db.getReference("review");
        reference.orderByChild("rid").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Review nrev=dataSnapshot.getValue(Review.class);
                reviews.add(nrev);
                adp.notifyDataSetChanged();
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
            mListener = (OnFragmentInteractionListener) context;
            restData=(restaurantData) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected TextView review;
        protected RatingBar rating;
        protected TextView dat;

        public ReviewHolder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.review_title);
            review= (TextView) itemView.findViewById(R.id.review);
            rating=(RatingBar) itemView.findViewById(R.id.ratebar);
            dat=(TextView) itemView.findViewById(R.id.date);

        }
    }

    public class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder> {

        public ReviewAdapter(){


        }

        @Override
        public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View review_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review,null);
            ReviewHolder holder=new ReviewHolder(review_view);

            return holder;
        }

        @Override
        public void onBindViewHolder(ReviewHolder holder, int position) {
            Review rev=reviews.get(position);
            holder.rating.setRating(rev.getRating());
            holder.title.setText(rev.getTitle());
            holder.review.setText(rev.getReviewText());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(rev.getDate());
            String time = String.format(Locale.getDefault(),"%02d",calendar.get(Calendar.HOUR_OF_DAY))+":"+
                    String.format(Locale.getDefault(),"%02d",calendar.get(Calendar.MINUTE));
            String date = String.format(Locale.getDefault(),"%02d",calendar.get(Calendar.DAY_OF_MONTH))+"/"+
                    String.format(Locale.getDefault(),"%02d",calendar.get(Calendar.MONTH))+"/"+
                    String.format(Locale.getDefault(),"%04d",calendar.get(Calendar.YEAR));
            holder.dat.setText(time+" "+date);

        }

        @Override
        public int getItemCount() {
            return reviews.size();
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
