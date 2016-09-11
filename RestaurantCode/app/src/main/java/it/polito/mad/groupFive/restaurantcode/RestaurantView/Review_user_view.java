package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Review;


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

    private void readdata(View v) {
        adp= new ReviewAdapter();
        recyclerView=(RecyclerView)v.findViewById(R.id.review_list);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm=new LinearLayoutManager(v.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("rid",rest.getRid());
                bundle.putFloat("ratingNumber",rest.getRatingNumber());
                bundle.putFloat("ratingValue",rest.getRating()*rest.getRatingNumber());
                New_Create_review create_review = new New_Create_review();
                create_review.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_up,R.anim.slide_down,R.anim.slide_up,R.anim.slide_down)
                        .addToBackStack(null).add(R.id.frame,create_review).commit();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

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

        rest=restData.getRestaurant();
        View v =inflater.inflate(R.layout.fragment_review_user_view, container, false);
        readdata(v);

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference reference=db.getReference("review");
        reference.orderByChild("rid").equalTo(rest.getRid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Review nrev=dataSnapshot.getValue(Review.class);
                adp.addChild(nrev);
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

        private SortedList<Review> reviews;

        public ReviewAdapter(){
            this.reviews = new SortedList<Review>(Review.class, new SortedList.Callback<Review>() {
                @Override
                public int compare(Review o1, Review o2) {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    try {
                        Date d1 = df.parse(o1.getDate());
                        Date d2 = df.parse(o2.getDate());
                        if(d1.getTime() < d2.getTime())
                            return 1;
                        if(d1.getTime() > d2.getTime())
                            return -1;
                        else
                            return 0;
                    } catch (ParseException e) {
                        Log.e("Compare",e.getMessage());
                    }
                    return 0;
                }

                @Override
                public void onInserted(int position, int count) {
                    notifyItemRangeInserted(position,count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    notifyItemRangeRemoved(position,count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    notifyItemMoved(fromPosition,toPosition);
                }

                @Override
                public void onChanged(int position, int count) {
                    notifyItemRangeChanged(position,count);
                }

                @Override
                public boolean areContentsTheSame(Review oldItem, Review newItem) {
                    return oldItem.getRid().equals(newItem.getRid()) && oldItem.getDate().equals(newItem.getDate()) && oldItem.getUid().equals(newItem.getUid());
                }

                @Override
                public boolean areItemsTheSame(Review item1, Review item2) {
                    return item1.getRid().equals(item2.getRid()) && item1.getDate().equals(item2.getDate()) && item1.getUid().equals(item2.getUid());
                }
            });
        }

        public void addChild(Review r){
            this.reviews.add(r);
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
            holder.review.setText("                         "+rev.getReviewText());
            String[] date=rev.getDate().split(" ");
            holder.dat.setText(date[0]);
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
