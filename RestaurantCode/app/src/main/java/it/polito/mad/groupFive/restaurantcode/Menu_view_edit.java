package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu.Create_simple_menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;


/**
 * Created by MacBookRetina on 12/04/16.
 */
public class Menu_view_edit extends NavigationDrawer {
    private ArrayList<Menu> menus;
    private MenuAdapter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private String rid;
    private FrameLayout mlay;
    private View load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_myMenus);
        mlay = (FrameLayout) findViewById(R.id.frame);

        menus = new ArrayList<>();
        readdata();


    }
    @Override
    protected void onPause(){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        adp.update();


    }

    public void readdata() {
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
        String uid;
        uid = sharedPreferences.getString("uid", null);
        rid = sharedPreferences.getString("rid", null);
        try {
            FirebaseDatabase db;
            db = FirebaseDatabase.getInstance();
            DatabaseReference myref = db.getReference("menu");

            //Myref.child("Owner").addValueEventListener(new UserDataListener(this));
            myref.orderByChild("rid").equalTo(rid).addChildEventListener(new MenuList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mlay.inflate(this, R.layout.menulist, mlay);
        adp = new MenuAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setAdapter(adp);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Create_simple_menu.class);
                intent.putExtra("mid", "-1");
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    public class MenuEditViewHolder extends RecyclerView.ViewHolder {
        protected TextView menu_name;
        protected TextView menu_desctiprion;
        protected ImageButton edit;
        protected CardView card;
        protected ImageView menu_image;
        protected ImageView spicy_icon;
        protected ImageView glutenfree_icon;
        protected ImageView vegetarian_icon;
        protected ImageView vegan_icon;


        public MenuEditViewHolder(View itemView) {
            super(itemView);
            this.menu_name = (TextView) itemView.findViewById(R.id.menu_name);
            this.menu_desctiprion = (TextView) itemView.findViewById(R.id.menu_description);
            this.edit = (ImageButton) itemView.findViewById(R.id.menu_edit);
            this.card = (CardView) itemView.findViewById(R.id.menu_card);
            this.menu_image = (ImageView) itemView.findViewById(R.id.menu_image);
            this.glutenfree_icon = (ImageView) itemView.findViewById(R.id.menu_icon2);
            this.vegetarian_icon = (ImageView) itemView.findViewById(R.id.menu_icon3);
            this.vegan_icon = (ImageView) itemView.findViewById(R.id.menu_icon4);
            this.spicy_icon = (ImageView) itemView.findViewById(R.id.menu_icon1);
        }
    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuEditViewHolder> {

        public MenuAdapter() {


        }

        public void sort() {
            Collections.sort(menus, new Comparator<Menu>() {
                @Override
                public int compare(Menu lhs, Menu rhs) {

                    return rhs.getType() - lhs.getType();
                }
            });
        }

        public void update() {
            //sort();
            adp.notifyDataSetChanged();
        }

        @Override
        public MenuEditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view_edit, null);
            return new MenuEditViewHolder(menu_view);
        }

        @Override
        public void onBindViewHolder(MenuEditViewHolder holder, int position) {
            Menu menu = menus.get(position);
            holder.card.setOnClickListener(new onEditclick(menu));
            String s = menu.getDescription();
            holder.menu_desctiprion.setText(s);
            holder.menu_name.setText(menu.getName());
            if (menu.getName().length() > 18)
                holder.menu_name.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            if (!menu.isSpicy()) holder.spicy_icon.setColorFilter(Color.GRAY);
            if (!menu.isVegan()) holder.vegan_icon.setColorFilter(Color.GRAY);
            if (!menu.isVegetarian()) holder.vegetarian_icon.setColorFilter(Color.GRAY);
            if (!menu.isGlutenfree()) holder.glutenfree_icon.setColorFilter(Color.GRAY);
            try {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageref = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/menus/");
                getFromNetwork(imageref, menu.getMid(), holder.menu_image);
                // holder.menu_image.setImageBitmap(menu.getImageBitmap());
            } catch (NullPointerException e) {
                //Log.e("immagine non caricata", " ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Log.v("image",menu.getImageByteArray().toString());
        }

        @Override
        public int getItemCount() {
            return menus.size();
        }

        public void remove(Menu position) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            String removeMid = position.getMid();
            //Log.v("remove", removeMid);

            DatabaseReference ref = db.getReference("menu");
            ref.child(removeMid).removeValue();
            int pos = menus.indexOf(position);
            menus.remove(position);


            notifyItemRemoved(pos);


        }

        public class onEditclick implements View.OnClickListener {
            private Menu position;

            public onEditclick(Menu position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Menu_view_edit.this);
                final CharSequence[] items = {getString(R.string.Menu_view_edit_Viewmenu), getString(R.string.Menu_view_edit_Editmenu), getString(R.string.Menu_view_edit_Delete)};
                dialog.setItems(items, new onPositionClickDialog(position));
                dialog.show();

            }
        }

        public class onPositionClickDialog implements DialogInterface.OnClickListener {
            private Menu position;

            public onPositionClickDialog(Menu position) {
                this.position = position;
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:{
                        Bundle bundle = new Bundle();
                        bundle.putString("rid", position.getRid());
                        bundle.putString("mid", position.getMid());
                        New_Menu_details menu_details = new New_Menu_details();
                        menu_details.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
                                .addToBackStack(null).add(R.id.frame, menu_details).commit();
                        break;
                    }
                    case 1: {
                        //TODO:edit intent
                        Intent edit_menu = new Intent(getBaseContext(), Create_simple_menu.class);
                        edit_menu.putExtra("mid", position.getMid());
                        startActivityForResult(edit_menu, 3);
                        break;
                    }
                    case 2: {
                        //TODO:delete item
                        final AlertDialog.Builder confirmDialog=new AlertDialog.Builder(Menu_view_edit.this);
                        final CharSequence[] items = { getString(R.string.menu_view_edit_yes), getString(R.string.menu_view_edit_no) };
                        confirmDialog.setTitle(getString(R.string.menu_view_edit_delete));
                        confirmDialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    remove(position);
                                }
                            }
                        });
                        confirmDialog.show();
                        break;
                    }
                    default: {
                        dialog.cancel();
                    }
                }

            }
        }
    }

    public class MenuList implements ChildEventListener {

        public MenuList() {

        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            load = LayoutInflater.from(getBaseContext()).inflate(R.layout.loading_bar, null);
            mlay.addView(load);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar_loading);
            pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            Menu menu = new Menu();
            menu.setRid((String) dataSnapshot.child("rid").getValue());
            menu.setName((String) dataSnapshot.child("name").getValue());
            menu.setMid(dataSnapshot.child("mid").getValue().toString());
            menu.setBeverage((Boolean) dataSnapshot.child("beverage").getValue());
            menu.setDescription((String) dataSnapshot.child("description").getValue());
            menu.setPrice(Float.parseFloat(dataSnapshot.child("price").getValue().toString()));
            menu.setServiceFee((Boolean) dataSnapshot.child("serviceFee").getValue());
            menu.setType(Integer.parseInt(dataSnapshot.child("type").getValue().toString()));
            menu.setImageLocal((String) dataSnapshot.child("imageLocalPath").getValue());
            menu.setSpicy((Boolean) dataSnapshot.child("spicy").getValue());
            menu.setVegetarian((Boolean) dataSnapshot.child("vegetarian").getValue());
            menu.setVegan((Boolean) dataSnapshot.child("vegan").getValue());
            menu.setGlutenfree((Boolean) dataSnapshot.child("glutenfree").getValue());
            menus.add(menu);

            adp.notifyDataSetChanged();
            mlay.removeView(load);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String mid = dataSnapshot.child("mid").getValue().toString();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference reference = db.getReference("course");
            reference.orderByChild("mid").equalTo(mid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String cid = dataSnapshot.child("cid").getValue().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("course");
                    ref.child(cid).removeValue();
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

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        final File dir = cw.getDir("images", Context.MODE_PRIVATE);
        File filePath = new File(dir, id);
        storageRoot.child(id).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File img = new File(dir, id);
                Uri imgPath = Uri.fromFile(img);
                try {
                    Bitmap b = new Picture(imgPath, getContentResolver()).getBitmap();
                    imView.setImageBitmap(b);
                } catch (IOException e) {
                    //Log.e("getFromNet", e.getMessage());
                } catch (NullPointerException e) {
                    //Log.v("Image Not Loaded", this.getClass().getName());
                }
            }
        });
    }
}

