package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.CreateRestaurant.CreateRestaurant;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Create_simple_menu1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Create_simple_menu1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Create_simple_menu1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public interface shareData{
        MenuData getdata();
    }
    shareData sData;
    MenuData data;
    private ImageView restaurantImg;
    private EditText name;
    private EditText description;
    private Uri menuPicUri = null;
    private ImageView menuPicView = null;
    private ImageView imageView14;
    private ImageView imageView15;
    private Bitmap imageBitmap;
    private boolean boolean_name=false;
    private boolean boolean_descr=false;
    private boolean isImageSet = false;
    private TextView next;
    private ProgressBar progressBar;

    private static final String IMAGE_TYPE = "image/*";
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Create_simple_menu1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Create_simple_menu1.
     */
    // TODO: Rename and change types and number of parameters
    public static Create_simple_menu1 newInstance(String param1, String param2) {
        Create_simple_menu1 fragment = new Create_simple_menu1();
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
        View v=inflater.inflate(R.layout.fragment_create_simple_menu1, container, false);
        data=sData.getdata();
        imageView14=(ImageView)v.findViewById(R.id.imageView14);
        imageView15=(ImageView)v.findViewById(R.id.imageView15);
        name=(EditText)v.findViewById(R.id.cmed_1_1);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView14.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView14.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        description=(EditText)v.findViewById(R.id.cmed_1_2);
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView15.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView15.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar4);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        restaurantImg = (ImageView) v.findViewById(R.id.cmiw_1_1);
        TextView setPic=(TextView)v.findViewById(R.id.image_label);
        setPic.setOnClickListener(new View.OnClickListener() {
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
                }     }
        });
                next= (TextView) v.findViewById(R.id.next);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(name.getText().toString().length()>0)
                            boolean_name=true;
                        if(description.getText().toString().length()>0)
                            boolean_descr=true;
                        if(boolean_name && boolean_descr && isImageSet) {
                            data.getMenu().setName(name.getText().toString());
                            data.getMenu().setDescription(description.getText().toString());
                            data.getMenu().setImageLocal(menuPicUri.toString());

                            if (!sData.getdata().isEdit()) {
                                //data.getMenu().setImageFromDrawable(menuPicView.getDrawable());
                            } else {

                            }

                            Create_simple_menu2 csm2 = new Create_simple_menu2();
                            getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_left_in,R.anim.slide_left_out,R.anim.slide_right_in,R.anim.slide_right_out)
                                    .replace(R.id.fragment_holder, csm2).commit();
                        }
                        else
                        {
                            Toast.makeText(getContext(),getResources().getString(R.string.toast_empty_field),Toast.LENGTH_LONG)
                                    .show();

                        }
                    }

                });

        fetchData();
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void fetchData(){
        if(sData.getdata().isEdit()){
            //restaurantImg.setImageBitmap(sData.getdata().getMenu().getImageBitmap());
            next.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            description.setText(sData.getdata().getMenu().getDescription());
            name.setText(sData.getdata().getMenu().getName());
            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference imageref=storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/menus/");
            try {
                getFromNetwork(imageref,sData.getdata().getMenu().getMid(),restaurantImg);
                isImageSet=true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
                    Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.intentChooser_select_image)), SELECT_PICTURE);                }
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
                this.isImageSet=true;
            } catch(IOException ioe) { Log.e(METHOD_NAME,ioe.getMessage());}
        }
        if(resultCode == CreateRestaurant.RESULT_OK && requestCode == CAPTURE_IMAGE){
            this.menuPicView = (ImageView) getActivity().findViewById(R.id.cmiw_1_1);
            try{
                this.menuPicView.setImageBitmap(new Picture(this.menuPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap());
                this.isImageSet = true;
            } catch(IOException ioe) { Log.e(METHOD_NAME,ioe.getMessage());}
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
                menuPicUri=imgPath;
                try {
                    Bitmap b = new Picture(imgPath,getActivity().getContentResolver()).getBitmap();
                    imView.setImageBitmap(b);
                    progressBar.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Log.e("getFromNet",e.getMessage());
                }
            }
        });
    }
}
