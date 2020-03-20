package com.jaats.agrovehicledriver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import com.jaats.agrovehicledriver.R;
import com.jaats.agrovehicledriver.app.App;
import com.jaats.agrovehicledriver.config.Config;
import com.jaats.agrovehicledriver.listeners.DocumentStatusListener;
import com.jaats.agrovehicledriver.model.DocumentBean;
import com.jaats.agrovehicledriver.model.DocumentStatusBean;
import com.jaats.agrovehicledriver.net.DataManager;
import com.jaats.agrovehicledriver.util.AppConstants;

public class DriverDocumentsActivity extends BaseAppCompatNoDrawerActivity {

    private static final String TAG = "DriverDocA";

    private static final int REQUEST_DRIVER_LICENCE = 0;

    private static final int REQUEST_DRIVER_JOINING = 8;
    private static final int REQUEST_DRIVER_PASSBOOK = 7;
    private static final int REQUEST_POLICE_CLEARANCE_CERTIFICATE = 1;
    private static final int REQUEST_FITNESS_CERTIFICATE = 2;
    private static final int REQUEST_VEHICLE_REGISTRATION = 3;
    private static final int REQUEST_VEHICLE_PERMIT = 4;
    private static final int REQUEST_COMMERCIAL_INSURANCE = 5;
    private static final int REQUEST_TAX_RECEIPT = 6;

    private ImageView ivDriverLicenceNext;
    private ImageView ivDriverLicenceSaved;

    private ImageView ivDriverPassbookNext;
    private ImageView ivDriverPassbookSaved;


    private ImageView ivDriverJoiningNext;
    private ImageView ivDriverJoiningSaved;


    private ImageView ivPoliceClearanceCertificateNext;
    private ImageView ivPoliceClearanceCertificateSaved;
    private ImageView ivFitnessCertificateNext;
    private ImageView ivFitnessCertificateSaved;
    private ImageView ivVehicleRegistrationNext;
    private ImageView ivVehicleRegistrationSaved;
    private ImageView ivVehiclePermitNext;
    private ImageView ivVehiclePermitSaved;
    private ImageView ivCommercialInsuranceNext;
    private ImageView ivCommercialInsuranceSaved;
    private ImageView ivTaxReceiptNext;
    private ImageView ivTaxReceiptSaved;
    private View.OnClickListener snackBarRefreshOnClickListener;
    private boolean isDriverLicenceUploaded;

    private boolean isDriverPassbookUploaded;
    NestedScrollView nestedScrollView;


    private boolean isDriverJoiningUploaded;
    private boolean isPoliceClearanceCertificateUploaded;
    private boolean isFitnessCertificateUploaded;
    private boolean isVehicleRegistrationUploaded;
    private boolean isVehiclePermitUploaded;
    private boolean isCommercialInsuranceUploaded;
    private boolean isTaxReceiptUploaded;
    private DocumentStatusBean documentStatusBean;

    String driverid;

    ArrayList<String> list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_documents);
        Documents();




        initViews();

        getSupportActionBar().setTitle(R.string.label_documents);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void Documents() {



        try {
            driverid=getIntent().getExtras().getString("keyy");
            list.add(driverid);
            Toast.makeText(this, driverid+"", Toast.LENGTH_SHORT).show();

        }catch (Exception e)
        {
/*
            Snackbar.make(nestedScrollView,"Please Enter Email Address",Snackbar.LENGTH_SHORT)
                    .show();
*/

            Snackbar.make(coordinatorLayout, R.string.message_please_upload_tax_receiptt, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DRIVER_LICENCE && resultCode == RESULT_OK) {
            isDriverLicenceUploaded = true;
            ivDriverLicenceNext.setVisibility(View.GONE);
            ivDriverLicenceSaved.setVisibility(View.VISIBLE);
        }



        if (requestCode == REQUEST_DRIVER_PASSBOOK && resultCode == RESULT_OK) {
            isDriverPassbookUploaded = true;
            ivDriverPassbookNext.setVisibility(View.GONE);
            ivDriverPassbookSaved.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_DRIVER_JOINING && resultCode == RESULT_OK) {
            isDriverJoiningUploaded = true;
            ivDriverJoiningNext.setVisibility(View.GONE);
            ivDriverJoiningSaved.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_POLICE_CLEARANCE_CERTIFICATE && resultCode == RESULT_OK) {
            isPoliceClearanceCertificateUploaded = true;
            ivPoliceClearanceCertificateNext.setVisibility(View.GONE);
            ivPoliceClearanceCertificateSaved.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_FITNESS_CERTIFICATE && resultCode == RESULT_OK) {
            isFitnessCertificateUploaded = true;
            ivFitnessCertificateNext.setVisibility(View.GONE);
            ivFitnessCertificateSaved.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_VEHICLE_REGISTRATION && resultCode == RESULT_OK) {
            isVehicleRegistrationUploaded = true;
            ivVehicleRegistrationNext.setVisibility(View.GONE);
            ivVehicleRegistrationSaved.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_VEHICLE_PERMIT && resultCode == RESULT_OK) {
            isVehiclePermitUploaded = true;
            ivVehiclePermitNext.setVisibility(View.GONE);
            ivVehiclePermitSaved.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_COMMERCIAL_INSURANCE && resultCode == RESULT_OK) {
            isCommercialInsuranceUploaded = true;
            ivCommercialInsuranceNext.setVisibility(View.GONE);
            ivCommercialInsuranceSaved.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_TAX_RECEIPT && resultCode == RESULT_OK) {
            isTaxReceiptUploaded = true;
            ivTaxReceiptNext.setVisibility(View.GONE);
            ivTaxReceiptSaved.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (documentStatusBean == null) {
            setProgressScreenVisibility(true, true);
            getData(false);
        } else {
            getData(true);
        }
    }

    private void getData(boolean isSwipeRefreshing) {
        swipeView.setRefreshing(isSwipeRefreshing);
        if (App.isNetworkAvailable()) {
            fetchDocumentStatus();
        } else {
            Snackbar.make(coordinatorLayout, AppConstants.NO_NETWORK_AVAILABLE, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.btn_retry, snackBarRefreshOnClickListener).show();
        }
    }

    private void initViews() {

        snackBarRefreshOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                mVibrator.vibrate(25);
                setProgressScreenVisibility(true, true);
                getData(false);
            }
        };

        ivDriverLicenceNext = (ImageView) findViewById(R.id.iv_driver_docuements_driver_license_next);
        ivDriverLicenceSaved = (ImageView) findViewById(R.id.iv_driver_docuements_driver_license_saved);

        ivDriverPassbookNext = (ImageView) findViewById(R.id.iv_driver_docuements_Bank_Passbook_next);
        ivDriverPassbookSaved = (ImageView) findViewById(R.id.iv_driver_docuements_Bank_Passbook_Save);

        ivDriverJoiningNext = (ImageView) findViewById(R.id.iv_driver_docuements_Joining_Form_next);
        ivDriverJoiningSaved = (ImageView) findViewById(R.id.iv_driver_docuements_Joining_FormSave);


        ivPoliceClearanceCertificateNext = (ImageView) findViewById(R.id.iv_driver_docuements_police_clearance_certificate_next);
        ivPoliceClearanceCertificateSaved = (ImageView) findViewById(R.id.iv_driver_docuements_police_clearance_certificate_saved);

        ivFitnessCertificateNext = (ImageView) findViewById(R.id.iv_driver_docuements_fitness_certificate_next);
        ivFitnessCertificateSaved = (ImageView) findViewById(R.id.iv_driver_docuements_fitness_certificate_saved);

        ivVehicleRegistrationNext = (ImageView) findViewById(R.id.iv_driver_docuements_vehicle_registration_next);
        ivVehicleRegistrationSaved = (ImageView) findViewById(R.id.iv_driver_docuements_vehicle_registration_saved);

        ivVehiclePermitNext = (ImageView) findViewById(R.id.iv_driver_docuements_vehicle_permit_next);
        ivVehiclePermitSaved = (ImageView) findViewById(R.id.iv_driver_docuements_vehicle_permit_saved);

        ivCommercialInsuranceNext = (ImageView) findViewById(R.id.iv_driver_docuements_commercial_insurance_next);
        ivCommercialInsuranceSaved = (ImageView) findViewById(R.id.iv_driver_docuements_commercial_insurance_saved);


    }

    private void fetchDocumentStatus() {


        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put("auth_token", Config.getInstance().getAuthToken());

        DataManager.fetchDocumentStatus(urlParams, new DocumentStatusListener() {
            @Override
            public void onLoadCompleted(DocumentStatusBean documentStatusBeanWS) {
                documentStatusBean = documentStatusBeanWS;
                populateDocumentStatus();
            }

            @Override
            public void onLoadFailed(String error) {
                swipeView.setRefreshing(false);
                setProgressScreenVisibility(true, false);
                Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.btn_retry, snackBarRefreshOnClickListener).show();

                if (App.getInstance().isDemo()) {
                    setProgressScreenVisibility(false, false);
                }
            }
        });

    }

    private void populateDocumentStatus() {


        for (DocumentBean bean : documentStatusBean.getDocuments()) {
            Log.i(TAG, "populateDocumentStatus: DocumentBean  : " + new Gson().toJson(bean));

            if (bean.getType() == AppConstants.DOCUMENT_TYPE_DRIVER_LICENCE) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isDriverLicenceUploaded = true;
                    ivDriverLicenceNext.setVisibility(View.GONE);
                    ivDriverLicenceSaved.setVisibility(View.VISIBLE);
                } else {
                    isDriverLicenceUploaded = false;
                    ivDriverLicenceNext.setVisibility(View.VISIBLE);
                    ivDriverLicenceSaved.setVisibility(View.GONE);
                }
            } else if (bean.getType() == AppConstants.DOCUMENT_TYPE_POLICE_CLEARANCE_CERTIFICATE) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isPoliceClearanceCertificateUploaded = true;
                    ivPoliceClearanceCertificateNext.setVisibility(View.GONE);
                    ivPoliceClearanceCertificateSaved.setVisibility(View.VISIBLE);
                } else {
                    isPoliceClearanceCertificateUploaded = false;
                    ivPoliceClearanceCertificateNext.setVisibility(View.VISIBLE);
                    ivPoliceClearanceCertificateSaved.setVisibility(View.GONE);
                }
            }
            else if (bean.getType() == AppConstants.DOCUMENT_TYPE_POLICE_CLEARANCE_CERTIFICATE) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isPoliceClearanceCertificateUploaded = true;
                    ivPoliceClearanceCertificateNext.setVisibility(View.GONE);
                    ivPoliceClearanceCertificateSaved.setVisibility(View.VISIBLE);
                } else {
                    isPoliceClearanceCertificateUploaded = false;
                    ivPoliceClearanceCertificateNext.setVisibility(View.VISIBLE);
                    ivPoliceClearanceCertificateSaved.setVisibility(View.GONE);
                }
        }else if (bean.getType() == AppConstants.DOCUMENT_TYPE_FITNESS_CERTIFICATE) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isFitnessCertificateUploaded = true;
                    ivFitnessCertificateNext.setVisibility(View.GONE);
                    ivFitnessCertificateSaved.setVisibility(View.VISIBLE);
                } else {
                    isFitnessCertificateUploaded = false;
                    ivFitnessCertificateNext.setVisibility(View.VISIBLE);
                    ivFitnessCertificateSaved.setVisibility(View.GONE);
                }
            } else if (bean.getType() == AppConstants.DOCUMENT_TYPE_VEHICLE_REGISTRATION) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isVehicleRegistrationUploaded = true;
                    ivVehicleRegistrationNext.setVisibility(View.GONE);
                    ivVehicleRegistrationSaved.setVisibility(View.VISIBLE);
                } else {
                    isVehicleRegistrationUploaded = false;
                    ivVehicleRegistrationNext.setVisibility(View.VISIBLE);
                    ivVehicleRegistrationSaved.setVisibility(View.GONE);
                }
            } else if (bean.getType() == AppConstants.DOCUMENT_TYPE_VEHICLE_PERMIT) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isVehiclePermitUploaded = true;
                    ivVehiclePermitNext.setVisibility(View.GONE);
                    ivVehiclePermitSaved.setVisibility(View.VISIBLE);
                } else {
                    isVehiclePermitUploaded = false;
                    ivVehiclePermitNext.setVisibility(View.VISIBLE);
                    ivVehiclePermitSaved.setVisibility(View.GONE);
                }
            } else if (bean.getType() == AppConstants.DOCUMENT_TYPE_COMMERCIAL_INSURANCE) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isCommercialInsuranceUploaded = true;
                    ivCommercialInsuranceNext.setVisibility(View.GONE);
                    ivCommercialInsuranceSaved.setVisibility(View.VISIBLE);
                } else {
                    isCommercialInsuranceUploaded = false;
                    ivCommercialInsuranceNext.setVisibility(View.VISIBLE);
                    ivCommercialInsuranceSaved.setVisibility(View.GONE);
                }
            } else if (bean.getType() == AppConstants.DOCUMENT_TYPE_TAX_RECEIPT) {
                if (bean.isUploaded() || (bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_PENDING_APPROVAL
                        && bean.getDocumentStatus() == AppConstants.DOCUMENT_STATUS_APPROVED)) {
                    isTaxReceiptUploaded = true;
                    ivTaxReceiptNext.setVisibility(View.GONE);
                    ivTaxReceiptSaved.setVisibility(View.VISIBLE);
                } else {
                    isTaxReceiptUploaded = false;
                    ivTaxReceiptNext.setVisibility(View.VISIBLE);
                    ivTaxReceiptSaved.setVisibility(View.GONE);
                }
            }
        }

        swipeView.setRefreshing(false);
        setProgressScreenVisibility(false, false);
    }

    public void onDriverDocumentsDriverLicenseClick(View view) {

        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
in.putExtra("type", AppConstants.DOCUMENT_TYPE_DRIVER_LICENCE);
in.putExtra("key",""+1);
startActivity(in);
    }



    public void onDriverDocumentsDriverBankPassbookClick(View view) {

        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

        Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
        in.putExtra("type", AppConstants.DOCUMENT_TYPE_DRIVER_PASSBOOK);
        in.putExtra("key",""+2);
        startActivity(in);
/*
        startActivityForResult(new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class)
                .putExtra("type", AppConstants.DOCUMENT_TYPE_DRIVER_PASSBOOK), REQUEST_DRIVER_PASSBOOK);*/
    }



    public void onDriverDocumentsDriverJoiningFormClick(View view) {

        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

        Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
        in.putExtra("type", AppConstants.DOCUMENT_TYPE_DRIVER_JOINING);
        in.putExtra("key",""+3);
        startActivity(in);
/*
        startActivityForResult(new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class)
                .putExtra("type", AppConstants.DOCUMENT_TYPE_DRIVER_JOINING), REQUEST_DRIVER_JOINING);*/
    }



    public void onDriverDocumentsWorkPermitClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);


        Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
        in.putExtra("type", AppConstants.DOCUMENT_TYPE_POLICE_CLEARANCE_CERTIFICATE);
        in.putExtra("key",""+4);
        startActivity(in);
/*

        startActivityForResult(new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class)
                        .putExtra("type", AppConstants.DOCUMENT_TYPE_POLICE_CLEARANCE_CERTIFICATE),
                REQUEST_POLICE_CLEARANCE_CERTIFICATE);
*/
    }

    public void onDriverDocumentsDrivingLicenceBackClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);
        Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
        in.putExtra("type", AppConstants.DOCUMENT_TYPE_POLICE_CLEARANCE_CERTIFICATE);
        in.putExtra("key",""+8);
        startActivity(in);

       /* startActivityForResult(new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class)
                        .putExtra("type", AppConstants.DOCUMENT_TYPE_FITNESS_CERTIFICATE),
                REQUEST_FITNESS_CERTIFICATE);*/
    }

    public void onDriverDocumentsVehicleRegistrationCertificateClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);
        Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
        in.putExtra("type", AppConstants.DOCUMENT_TYPE_VEHICLE_REGISTRATION);
        in.putExtra("key",""+5);
        startActivity(in);
/*
        startActivityForResult(new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class)
                        .putExtra("type", AppConstants.DOCUMENT_TYPE_VEHICLE_REGISTRATION),
                REQUEST_VEHICLE_REGISTRATION);*/
    }

    public void onDriverDocumentsInsuranceCertificateClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

        Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
        in.putExtra("type", AppConstants.DOCUMENT_TYPE_VEHICLE_PERMIT);
        in.putExtra("key",""+6);
        startActivity(in);
/*
        startActivityForResult(new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class)
                        .putExtra("type", AppConstants.DOCUMENT_TYPE_VEHICLE_PERMIT),
                REQUEST_VEHICLE_PERMIT);*/
    }

    public void onDriverDocumentsFitnessCertificateClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

        Intent in=new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class);
        in.putExtra("type", AppConstants.DOCUMENT_TYPE_COMMERCIAL_INSURANCE);
        in.putExtra("key",""+7);
        startActivity(in);
/*
        startActivityForResult(new Intent(DriverDocumentsActivity.this, DocumentUploadActivity.class)
                        .putExtra("type", AppConstants.DOCUMENT_TYPE_COMMERCIAL_INSURANCE),
                REQUEST_COMMERCIAL_INSURANCE);*/
    }


    public void onDriverDocumentsSubmitClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    //    Toast.makeText(this, getString(R.string.nkpl)+"", Toast.LENGTH_SHORT).show();




   //     coolect();

        //mVibrator.vibrate(25);

        /*Snackbar.make(coordinatorLayout, R.string.message_upload_all_the_required_document, Snackbar.LENGTH_SHORT)
                .setAction(R.string.btn_refresh, snackBarRefreshOnClickListener).show();


        startActivity(new Intent(DriverDocumentsActivity.this, ProfilePhotoUploadActivity.class));
        finish();

        */
/*

        if (coolect()==false) {



            startActivity(new Intent(DriverDocumentsActivity.this, ProfilePhotoUploadActivity.class));
            finish();




        } else {

*//*
            Snackbar.make(coordinatorLayout, R.string.message_upload_all_the_required_document, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.btn_refresh, snackBarRefreshOnClickListener).show();
*//*

        }*/
    }

     private  boolean coolect()
    {


        ArrayList<String> list = new ArrayList<String>();

        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");

        String a = null ;
        for (int i=0;i<list.size();i++)
        {
            a=    list.get(i);

            if (a=="1")

            {

                Snackbar.make(coordinatorLayout, R.string.message_please_upload_driver_licence_front, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }


            else if (list.get(i)=="2")
            {
                Snackbar.make(coordinatorLayout, R.string.label_driver_Passbook, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }

            else if (list.get(i)=="3")
            {
                Snackbar.make(coordinatorLayout, R.string.label_driver_Joining, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }


            else if (list.get(i)=="4")
            {
                Snackbar.make(coordinatorLayout, R.string.label_police_clearance_certificate, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }
            else if (list.get(i)=="5")
            {
                Snackbar.make(coordinatorLayout, R.string.label_vehicle_registration, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }
            else if (list.get(i)=="6")
            {
                Snackbar.make(coordinatorLayout, R.string.label_vehicle_permit, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }
            else if (list.get(i)=="7")
            {
                Snackbar.make(coordinatorLayout, R.string.label_commercial_insurance, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }
            /*
            else if (list.get(i)=="8")
            {
                Snackbar.make(coordinatorLayout, R.string.label_fitness_certificate, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                //   Toast.makeText(this, "please upload document", Toast.LENGTH_SHORT).show();

            }*/

        }

        return true;

    }



    private boolean collectDriverDocuments() {


        if (!isDriverLicenceUploaded) {
            Snackbar.make(coordinatorLayout, R.string.message_please_upload_driver_licence, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (!isPoliceClearanceCertificateUploaded) {
            Snackbar.make(coordinatorLayout, R.string.message_please_upload_police_clearance_certificate, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (!isFitnessCertificateUploaded) {
            Snackbar.make(coordinatorLayout, R.string.message_please_upload_fitness_certificate, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (!isVehicleRegistrationUploaded) {
            Snackbar.make(coordinatorLayout, R.string.message_please_upload_vehicle_registration, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (!isVehiclePermitUploaded) {
            Snackbar.make(coordinatorLayout, R.string.message_please_upload_vehicle_permit, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (!isCommercialInsuranceUploaded) {
            Snackbar.make(coordinatorLayout, R.string.message_please_upload_commercial_insurance, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (!isTaxReceiptUploaded) {
            Snackbar.make(coordinatorLayout, R.string.message_please_upload_tax_receipt, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }

        return true;
    }
}
