package it.polito.mad.groupFive.restaurantcode.CreateMenu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.libs.RealPathUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Create_menu_frag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Create_menu_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Create_menu_frag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Codes for intents
    private static final String IMAGE_TYPE = "image/*";
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri menuPicUri = null;

    private OnFragmentInteractionListener mListener;

    private ImageView menuPicView = null;

    private Menu menu= null;
    private Restaurant restaurant= null;
    private EditText txtname;
    private EditText txtdesc;
    private int type=1;
    private int value;
    private String spin ;
    private NumberPicker numberPicker;

    private View v=null;

    public Create_menu_frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Create_menu_frag.
     */
    // TODO: Rename and change types and number of parameters
    public static Create_menu_frag newInstance(String param1, String param2) {
        Create_menu_frag fragment = new Create_menu_frag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+"onCreate";
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        spin=getString(R.string.create_menu_frag_fixed);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreateView";
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_create_menu, container, false);
        txtname = (EditText) v.findViewById(R.id.cmed_1_1);
        txtdesc = (EditText) v.findViewById(R.id.cmed_1_2);
        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.spinner_menu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        numberPicker = (NumberPicker) v.findViewById(R.id.numberPicker);
        numberPicker.setEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                spin = parent.getItemAtPosition(position).toString();
                String s = getString(R.string.create_menu_frag_multiple_choice);
                if(spin.equals(s)) {
                    numberPicker.setEnabled(true);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(6);
                    numberPicker.setWrapSelectorWheel(true);
                    type=2;

                }
                else {
                    numberPicker.setEnabled(false);
                    type=1;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            { }
        });

        ImageView restaurantImg = (ImageView) v.findViewById(R.id.cmiw_1_1);
        restaurantImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    /*
                    pickImage is called here only after the first run.
                    On the first run, the call to isStoragePermissionGranted will return false.
                    However if the permission, inside onRequestPermissionsResult, is granted
                    the call to pickImage() is done over there even if isStoragePermissionGranted
                    will return false.
                     */
                    pickImage();
                }
            }
        });

        Button btnNext = (Button) v.findViewById(R.id.next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(METHOD_NAME,"Press Next: OK");

                Activity a = getActivity();
                if(a instanceof OnFragmentInteractionListener) {
                    setMenuData();
/*
                    FrameLayout mlay= (FrameLayout) v.findViewById(R.id.frame);
                    mlay.inflate(v.getContext(), R.layout.activity_create_menu, mlay);
                    Create_menu_frag2 fragment= new Create_menu_frag2();
                    getFragmentManager().beginTransaction().replace(R.id.acm_1,fragment).addToBackStack(null).commit();
*/
                    OnFragmentInteractionListener obs = (OnFragmentInteractionListener) a;
                    obs.onChangeFrag(menu);
                }


                //TODO passare type, immagine, recuperare restaurant id, creare oggetto menu, lanciare nuova activity
            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChangeFrag(Menu menu);
    }

    private void pickImage(){
        final CharSequence[] choices = {getResources().getString(R.string.pick_gallery),getResources().getString(R.string.take_picture)};
        final String METHOD_NAME = this.getClass().getName()+" - pickImage";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.alertBox_photo_title));
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choices[which].equals(getResources().getString(R.string.take_picture)) &&
                        getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    //Take photo
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                        File photo = null;
                        try {
                            photo = createImageFile();
                        } catch (IOException ioe) {
                            Log.e(METHOD_NAME, ioe.getMessage());
                        }
                        if (photo != null) {
                            menuPicUri = Uri.fromFile(photo);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, menuPicUri);
                            startActivityForResult(cameraIntent, CAPTURE_IMAGE);
                        }
                    }
                } else if (choices[which].equals(getResources().getString(R.string.pick_gallery))) {
                    //Choose from gallery
                    Intent intent = new Intent();
                    intent.setType(IMAGE_TYPE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.intentChooser_select_image)), SELECT_PICTURE);
                }
            }
        });
        builder.show();
    }

    public boolean isStoragePermissionGranted() {
        final String METHOD_NAME = this.getClass().getName()+" - isStoragePermissionGranted";
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(METHOD_NAME,"Permission granted");
                return true;
            } else {

                Log.v(METHOD_NAME,"Permission revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(METHOD_NAME,"Permission granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        final String METHOD_NAME = this.getClass().getName()+" - onRequestPermissionResult";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(METHOD_NAME,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            pickImage();
        }
        else
            Log.e(METHOD_NAME,"Permission: "+permissions[0]+" was "+grantResults[0]);
    }

    private File createImageFile() throws IOException {
        final String METHOD_NAME = this.getClass().getName()+" - createImageFile";
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = getResources().getString(R.string.app_name) + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String METHOD_NAME = this.getClass().getName()+" - onActivityResult";
        super.onActivityResult(requestCode, resultCode, data);
        int imageWidth = 600;
        int imageHeight = 600;
        if(resultCode == getActivity().RESULT_OK && requestCode == SELECT_PICTURE){
            this.menuPicView = (ImageView) getActivity().findViewById(R.id.cmiw_1_1);
            this.menuPicUri = data.getData();
            try{
                this.menuPicView.setImageBitmap(new Picture(this.menuPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap());
            } catch(IOException ioe) { Log.e(METHOD_NAME,ioe.getMessage());}
        }
        if(resultCode == getActivity().RESULT_OK && requestCode == CAPTURE_IMAGE){
            this.menuPicView = (ImageView) getActivity().findViewById(R.id.cmiw_1_1);
            try{
                this.menuPicView.setImageBitmap(new Picture(this.menuPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap());
            } catch(IOException ioe){Log.e(METHOD_NAME,ioe.getMessage());}
        }
    }

    private void setMenuData() {
        final String METHOD_NAME = this.getClass().getName()+" - setMenuData";
        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), Create_menu.MODE_PRIVATE);
        int rid = sp.getInt("rid",-1);
        int uid = sp.getInt("uid",-1);

        try {
            restaurant = new Restaurant(getContext(),rid);
            restaurant.getData();
            menu=new Menu(restaurant);
            menu.setName(txtname.getText().toString());
            menu.setDescription(txtdesc.getText().toString());
            if(numberPicker.isEnabled()){
            value =numberPicker.getValue();}
            else { value=1;
            }
            menu.setChoiceAmount(value);
            if (!menu.setType(type))
                throw new MenuException("Error number of type");


            ImageView menuImg = (ImageView) v.findViewById(R.id.cmiw_1_1);
            // menu.setImage64FromDrawable(menuImg.getDrawable());
        } catch (RestaurantException e) {
            e.printStackTrace();
        }  catch (MenuException e) {
            e.printStackTrace();
        }

    }
}
