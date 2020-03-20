package com.jaats.agrovehicledriver.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jaats.agrovehicledriver.R;
import com.jaats.agrovehicledriver.app.App;
import com.jaats.agrovehicledriver.config.Config;
import com.jaats.agrovehicledriver.listeners.BasicListener;
import com.jaats.agrovehicledriver.model.BasicBean;
import com.jaats.agrovehicledriver.net.DataManager;
import com.jaats.agrovehicledriver.util.AppConstants;
import com.jaats.agrovehicledriver.util.FileOp;

public class DocumentUploadActivity extends BaseAppCompatNoDrawerActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICKER_SELECT = 2;
    private static final String TAG = "DocUpA";
    private int type = AppConstants.DOCUMENT_TYPE_DRIVER_LICENCE;
    private String imagePath = "";
    private String documentPath;
    private TextView txtTitle;
    private ImageView ivDocumentPreview;
    private Button btnRetake;
    private Button btnSave;
    Bitmap bitmap;
    File photoFile;
    private ViewFlipper viewFlipper;

    private static final int PICK_IMAGE_REQUEST =1 ;


    String s;
    //..........//
    private String filePath;
    private View.OnClickListener snackBarRefreshOnClickListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        s=getIntent().getExtras().getString("key");
        Toast.makeText(this, s+"", Toast.LENGTH_SHORT).show();
        if (getIntent().hasExtra("type"))
            type = getIntent().getIntExtra("type", AppConstants.DOCUMENT_TYPE_DRIVER_LICENCE);

        initViews();

        getSupportActionBar().hide();
        swipeView.setPadding(0, 0, 0, 0);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data.getData() != null) {




            Uri picUri = data.getData();
            filePath = getPath(picUri);
            if (filePath != null) {
                try {

                   // textView.setText("File Selected");
                    Log.d("filePath", String.valueOf(filePath));
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    uploadBitmap(bitmap);
                  //  imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                    DocumentUploadActivity.this,"no image selected",
                        Toast.LENGTH_LONG).show();
            }

          /*  *//*
           Uri path=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);

            } catch (IOException e) {
                e.printStackTrace();
            }*//*

//            bitmap = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);
      documentPath = imagePath;
            //    setBannerPic(tempImagePath);
            setDocumentImage(imagePath);

            Log.i(TAG, "onActivityResult: IMAGE PATH : " + imagePath);*/
        }
    }

    private void uploadBitmap(final Bitmap bitmap) {

         String ROOT_URL =  getString(R.string.nkpl)+"provider/document/upload/"+s;

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));



                            Toast.makeText(getApplicationContext(), obj.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                            Intent in=new Intent(DocumentUploadActivity.this,DriverDocumentsActivity.class);
                            in.putExtra("keyy",""+s);

                            startActivity(in);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {

            //            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("tags", tags);
//                return params;
//            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file_name", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void initViews() {

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_document_upload);

        txtTitle = (TextView) findViewById(R.id.txt_document_upload_title);
        ivDocumentPreview = (ImageView) findViewById(R.id.iv_document_upload_preview);

        btnRetake = (Button) findViewById(R.id.btn_document_upload_retake);
        btnSave = (Button) findViewById(R.id.btn_document_upload_save);

        btnRetake.setTypeface(typeface);
        btnSave.setTypeface(typeface);

        txtTitle.setText(getDocumentTitle(type));

    }

    private String getDocumentTitle(int type) {

        switch (type) {

            case AppConstants.DOCUMENT_TYPE_DRIVER_LICENCE:
                return getString(R.string.label_driver_license);


            case AppConstants.DOCUMENT_TYPE_DRIVER_PASSBOOK:

                return getString(R.string.label_driver_Passbook);

            case AppConstants.DOCUMENT_TYPE_DRIVER_JOINING:
                return getString(R.string.label_driver_Joining);


            case AppConstants.DOCUMENT_TYPE_POLICE_CLEARANCE_CERTIFICATE:
                return getString(R.string.label_police_clearance_certificate);

            case AppConstants.DOCUMENT_TYPE_FITNESS_CERTIFICATE:
                return getString(R.string.label_fitness_certificate);

            case AppConstants.DOCUMENT_TYPE_VEHICLE_REGISTRATION:
                return getString(R.string.label_vehicle_registration);

            case AppConstants.DOCUMENT_TYPE_VEHICLE_PERMIT:
                return getString(R.string.label_vehicle_permit);

            case AppConstants.DOCUMENT_TYPE_COMMERCIAL_INSURANCE:
                return getString(R.string.label_commercial_insurance);

            case AppConstants.DOCUMENT_TYPE_TAX_RECEIPT:
                return getString(R.string.label_tax_receipt);

            case AppConstants.DOCUMENT_TYPE_PASS_BOOK:
                return getString(R.string.label_pass_book);

            case AppConstants.DOCUMENT_TYPE_DRIVER_LICENCE_WITH_BADGE_NUMBER:
                return getString(R.string.label_driver_licence_with_badge_number);

            case AppConstants.DOCUMENT_TYPE_BACKGROUND_CHECK_CONSENT_FORM:
                return getString(R.string.label_background_check_consent_form);

            case AppConstants.DOCUMENT_TYPE_PAN_CARD:
                return getString(R.string.label_pan_card);

            case AppConstants.DOCUMENT_TYPE_NO_OBJECTION_CERTIFICATE:
                return getString(R.string.label_no_objection_certificate);

            default:
                return getString(R.string.label_error);
        }

    }


    private void setDocumentImage(String imagePath) {

        viewFlipper.setDisplayedChild(1);
        Glide.with(getApplicationContext())
                .load(imagePath)
                .apply(new RequestOptions()
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .centerCrop())
                .into(ivDocumentPreview);

//        ibClearDisplayPic.setVisibility(View.VISIBLE);

    }

    public void onDocumentUploadTakePhotoClick(View view) {

        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);




        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(DocumentUploadActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(DocumentUploadActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {

                ActivityCompat.requestPermissions(DocumentUploadActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
            showFileChooser();
        }


        //mVibrator.vibrate(25);
/*
        if (!checkForReadWritePermissions()) {
            getReadWritePermissions();
        } else {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
              photoFile = null;
                try {
//                    imagePath = image.getAbsolutePath();
                    photoFile = App.createImageFile(App.getFileName(type)).getAbsoluteFile();
                    imagePath = photoFile.getAbsolutePath();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                if (photoFile != null) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                getApplicationContext().getPackageName() + ".provider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }*/
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public void onDocumentUploadRetakeClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

        onDocumentUploadTakePhotoClick(view);
    }

    public void onDocumentUploadSaveClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);
//        uploadImage();
    }

    public void performDocumentUploadSave() {

        swipeView.setRefreshing(true);
        JSONObject postData = getDocumentUploadSaveJSObj();

        ArrayList<String> fileList = getFileList();

        DataManager.performDocumentUpload(postData, fileList, new BasicListener() {

            @Override
            public void onLoadCompleted(BasicBean basicBean) {
                swipeView.setRefreshing(false);

//                App.saveToken(getApplicationContext(), driverDetailsBean);

                Intent intent = new Intent();
                intent.putExtra("type", type);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLoadFailed(String error) {
                swipeView.setRefreshing(false);
                Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                 /* To Be Removed....*/
                if (App.getInstance().isDemo()) {
                    Intent intent = new Intent();
                    intent.putExtra("type", type);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private ArrayList<String> getFileList() {
        ArrayList<String> fileList = new ArrayList<>();

        if (documentPath != null && !documentPath.equals("")) {
            String tempPath = FileOp.getDocumentPhotoPath(getDocumentTitle(type));
            FileOp.writeBitmapToFile(documentPath, tempPath);
            fileList.add(tempPath);
            Log.i(TAG, "getFileList: Temp : " + tempPath);
            Log.i(TAG, "getFileList: Original " + documentPath);
        }

        return fileList;
    }

    private JSONObject getDocumentUploadSaveJSObj() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("auth_token", Config.getInstance().getAuthToken());
            postData.put("type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postData;
    }

}
