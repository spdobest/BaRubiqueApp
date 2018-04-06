package com.bestdealfinance.bdfpartner.FragmentNew;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.AdapterPreview;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.base.BaseFragment;
import com.bestdealfinance.bdfpartner.base.SnackbarCallback;
import com.bestdealfinance.bdfpartner.model.ModelPreview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by siba.prasad on 20-12-2016.
 */

public class PreviewFragment extends BaseFragment implements View.OnClickListener, SnackbarCallback {
    public static final String TAG = "PreviewFragment";
    // other variables
    private static String leadId;
    ///////////WIDGET DECLARATION////////////////
    //root layout
    View rootView;
    Toolbar toolbar;
    private AppCompatTextView textViewDiscardToolbar;
    private AppCompatTextView textViewProductTypePreview;
    private LinearLayout relativeLayoutRootPreview;
    private AppCompatTextView textViewEditPreview;
    private AppCompatButton buttonContinuePreview;
    private ArrayList<ModelPreview> listPreview = new ArrayList<>();
    private ArrayList<ModelPreview> listPreviewMain = new ArrayList<>();
    private AdapterPreview adapterPreview;
    private LinearLayoutManager linearLayoutManager;

    // arraylist for different group for filter
    private RecyclerView recyclerViewPreview;
    private ArrayList<ModelPreview> listPreviewApplication = new ArrayList<>();
    private String[] arrayApplicationBaseId = {"388", "384", "84", "76", "72", "60", "56", "52", "44", "40", "297", "217", "206", "561", "387", "383", "75", "71", "79", "55", "51", "43", "39", "223", "643", "456", "386", "382", "78", "74", "70", "62", "54", "50", "46", "323", "42", "38", "295", "222", "188", "648", "642", "389", "385", "77", "73", "69", "57", "53", "49", "45", "322", "41"};
    private List<String> listApplication;
    private ArrayList<ModelPreview> listPreviewLoan = new ArrayList<>();
    private String[] arrayLoanBaseId = {"381", "162", "161", "164", "160", "163", "159"};
    private List<String> listLoan;
    private ArrayList<ModelPreview> listPreviewContact = new ArrayList<>();
    private String[] arrayContactBaseId = {"6", "5"};
    private List<String> listContact;
    private ArrayList<ModelPreview> listPreviewPersonal = new ArrayList<>();
    private String[] arrayPersonalBaseId = {"157", "153", "148", "645", "501", "35", "31", "27", "22", "18", "14", "10", "2", "156", "151", "147", "644", "500", "34", "30", "25", "21", "17", "13", "9", "1", "155", "150", "146", "503", "33", "29", "24", "303", "20", "299", "16", "12", "8", "4", "154", "149", "502", "36", "32", "28", "23", "19", "298", "15", "11", "7", "3"};
    private List<String> listPersonal;
    private ArrayList<ModelPreview> listPreviewFinancial = new ArrayList<>();
    private String[] arrayFinancialBaseId = {"144", "92", "88", "80", "305", "190", "143", "95", "91", "87", "83", "59", "304", "296", "189", "142", "94", "90", "86", "82", "307", "145", "93", "85", "81", "306", "221"};
    private List<String> listFinancial;
    private ArrayList<ModelPreview> listPreviewProperty = new ArrayList<>();
    private String[] arrayPropertyBaseId = {"199", "128", "124", "309", "225", "212", "134", "127", "123", "308", "230", "216", "211", "210", "133", "126", "122", "227", "214", "129", "125", "121", "266", "226", "213"};
    private List<String> listProperty;

    private ArrayList<ModelPreview> listPreviewVehicle = new ArrayList<>();
    private String[] arrayVehicleBaseId = {"207", "203", "140", "136", "202", "139", "205", "201", "138", "208", "204", "200", "141", "137", ""};
    private List<String> listVehicle;

    private ArrayList<ModelPreview> listPreviewBusiness = new ArrayList<>();
    private String[] arrayBusinessBaseId = {"68", "64", "48", "67", "63", "47", "66", "58", "61", "65"};
    private List<String> listBusiness;
    private ArrayList<ModelPreview> listPreviewBanking = new ArrayList<>();
    private String[] arrayBankingBaseId = {"253", "249", "252", "251", "250"};
    private List<String> listBanking;
    private RequestQueue queue;
    private Bundle productBundle;
    // single instance method to create object
    private boolean isFromLeadActivity = false;

    public static PreviewFragment newInstance(Bundle productBundle) {
        PreviewFragment fragment = new PreviewFragment();
        fragment.setArguments(productBundle);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        Helper.hideKeyboard(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize volley request queue
        queue = Volley.newRequestQueue(getActivity());
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        // here initialize the preview group wise
        listPreviewPersonal.add(new ModelPreview("PERSONAL INFORMATION", "", "", "", 1));
        listPreviewLoan.add(new ModelPreview("LOAN DETAILS", "", "", "", 1));
        listPreviewContact.add(new ModelPreview("CONTACT INFORMATION", "", "", "", 1));
        listPreviewFinancial.add(new ModelPreview("FINANCIAL DETAILS", "", "", "", 1));
        listPreviewProperty.add(new ModelPreview("PROPERTY DETAILS", "", "", "", 1));
        listPreviewVehicle.add(new ModelPreview("VEHICLE DETAILS", "", "", "", 1));
        listPreviewBusiness.add(new ModelPreview("BUSINESS DETAILS", "", "", "", 1));
        listPreviewBanking.add(new ModelPreview("BANKING DETAILS", "", "", "", 1));
        listPreviewApplication.add(new ModelPreview("APPLICANT INFORMATION", "", "", "", 1));

        listBanking = Arrays.asList(arrayBankingBaseId);
        listBusiness = Arrays.asList(arrayBusinessBaseId);
        listContact = Arrays.asList(arrayContactBaseId);
        listFinancial = Arrays.asList(arrayFinancialBaseId);
        listLoan = Arrays.asList(arrayLoanBaseId);
        listPersonal = Arrays.asList(arrayPersonalBaseId);
        listProperty = Arrays.asList(arrayPropertyBaseId);
        listVehicle = Arrays.asList(arrayVehicleBaseId);
        listApplication = Arrays.asList(arrayApplicationBaseId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_preview, container, false);

        productBundle = getArguments();

        if (productBundle.containsKey(Constant.BundleKey.IS_FROM_LEAD_ACTIVITY)) {
            isFromLeadActivity = productBundle.getBoolean(Constant.BundleKey.IS_FROM_LEAD_ACTIVITY);
        }
        initViews(rootView);
        // check internet connection and hit the api to get lead details
        if (Helper.isNetworkAvailable(getActivity()))
            fetchLeadDetails();
        else
            Helper.showSnackBar(this, relativeLayoutRootPreview, getResources().getString(R.string.no_connection), "Retry");

        return rootView;
    }

    // mmethod to initialize the widgets
    @Override
    public void initViews(View rootView) {
        recyclerViewPreview = (RecyclerView) rootView.findViewById(R.id.recyclerViewPreview);
        relativeLayoutRootPreview = (LinearLayout) rootView.findViewById(R.id.relativeLayoutRootPreview);
        buttonContinuePreview = (AppCompatButton) rootView.findViewById(R.id.buttonContinuePreview);
        textViewEditPreview = (AppCompatTextView) rootView.findViewById(R.id.textViewEditPreview);
        textViewDiscardToolbar = (AppCompatTextView) rootView.findViewById(R.id.textViewDiscardToolbar);

        textViewProductTypePreview = (AppCompatTextView) rootView.findViewById(R.id.textViewProductTypePreview);


        textViewDiscardToolbar.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) rootView.findViewById(R.id.new_toolbar);
        textViewDiscardToolbar.setOnClickListener(this);


        if (isFromLeadActivity) {
            buttonContinuePreview.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
            textViewEditPreview.setVisibility(View.GONE);
            ToolbarHelper.initializeToolbar(getActivity(), toolbar, "Lead Preview", false, false, true);
        } else {
            buttonContinuePreview.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            textViewEditPreview.setVisibility(View.VISIBLE);
            ToolbarHelper.initializeToolbar(getActivity(), toolbar, "Lead Preview", false, false, true);
        }
        // call the set on click listener
        setClickListener();
    }

    @Override
    public void setClickListener() {
        textViewEditPreview.setOnClickListener(this);
        buttonContinuePreview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewEditPreview:

                    getActivity().onBackPressed();

                break;
            case R.id.buttonContinuePreview:
                showOfferListFragment();
                break;
            case R.id.textViewDiscardToolbar:
                Helper.showAlertDialog(getActivity(), "", "Your previous progress is saved. Do you really want to discard ?", "DISCARD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                }, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    // get the lead details from server
    private void fetchLeadDetails() {
        showProgressDialog("");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lead_id", productBundle.getString(Constant.BundleKey.LEAD_ID));
            jsonObject.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            Helper.showLog(URL.FETCH_ALL_LEAD_FIELDS, jsonObject.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.FETCH_ALL_LEAD_FIELDS, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response != null && response.has("msg")) {
                            if (response.getString("msg").equalsIgnoreCase("Success")) {
                                JSONObject jsonObjectBody = response.getJSONObject("body");
                                JSONArray jsonArrayDetails = jsonObjectBody.getJSONArray("detail");


                                for (int i = 0; i < jsonArrayDetails.length(); i++) {
                                    JSONObject jsonObjectInner = jsonArrayDetails.getJSONObject(i);
                                    listPreview.add(new ModelPreview(jsonObjectInner.getString("field_name"), jsonObjectInner.getString("field_value"), jsonObjectInner.getString("field_meta_id"), jsonObjectInner.getString("base_id"), 2));
                                }
                                if(listPreview.size()>0) {
                                    recyclerViewPreview.setLayoutManager(linearLayoutManager);
                                    adapterPreview = new AdapterPreview(getActivity(), listPreview);
                                    recyclerViewPreview.setAdapter(adapterPreview);
                                }


                            }
                        }


                        JSONArray fieldArray = response.getJSONObject("body").getJSONArray("detail");

                        for (int i = 0; i < fieldArray.length(); i++) {
                            if (fieldArray.getJSONObject(i).getString("base_id").equals("25")) {

                                //    leadCity = fieldArray.getJSONObject(i).getString("field_value");
                            }
                        }
                        hideProgresssDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgresssDialog();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgresssDialog();
                    Helper.showSnackbarMessage(relativeLayoutRootPreview, "Server Error", Snackbar.LENGTH_LONG);
                }
            });
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSnackbarActionClick() {
        if (Helper.isNetworkAvailable(getActivity()))
            fetchLeadDetails();
        else
            Helper.showSnackBar(this, relativeLayoutRootPreview, getResources().getString(R.string.no_connection), "Retry");
    }

    private void showOfferListFragment() {
        OfferListFragment aNew = new OfferListFragment();
        aNew.setArguments(productBundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, aNew, "OfferListFragment").commit();
    }
}