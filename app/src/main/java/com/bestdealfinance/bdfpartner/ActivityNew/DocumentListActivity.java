package com.bestdealfinance.bdfpartner.ActivityNew;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bumptech.glide.Glide;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class DocumentListActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "DocumentListActivity";
    private RequestQueue queue;
    private Bundle productBundle;
    private Toolbar toolbar;
    private LinearLayout coverLayout;
    private TextView tvApplicantName;
    private ImageView productIcon;
    private TextView tvProduct;
    private TextView tvLoanAmount;
    private TextView tvLeadId;
    private RelativeLayout loanLayout;
    private AppCompatImageView referIcon;
    private AppCompatImageView appFillIcon;
    private AppCompatImageView docPickupIcon;
    private TextView referAmount;
    private TextView appFillAmount;
    private TextView docPickupAmount,disAmount;
    private DB snappyDB;
    private JSONArray allPayoutJsonArray;
    private JSONArray allStepsJsonArray;
    private JSONArray maxPayoutInEachProductTypeJsonArray;
    private Button btnMeeting;
    private EditText etMeeting;

    String lat,lang;

    boolean isMeetingDateSelected = false;
    private String meetingDateSelected;

    // by prasad
    String address;
    private AppCompatTextView textViewRubiqueAddress;
    private AppCompatTextView textViewProductTypePreview;
    private AppCompatTextView textViewNameProductList;
    private AppCompatTextView textViewAddressProductList;
    private AppCompatTextView textViewPhoneProductList;
    private AppCompatTextView textViewTrackIdProductList;

    private RecyclerView recyclerViewDocList;
    private View viewDocumentsListLine;
    private AppCompatTextView textViewDocCheckList;

    private AppCompatTextView textViewScheduleMeetingDocList;
    private AppCompatTextView textViewGetDirection;
    private AppCompatTextView textViewSelectDate;
    private Button buttonFinishDocList;

    private String addressCustomer;

    boolean isFromLeadList = false;

    @Override
    public void onBackPressed() {
        if(isFromLeadList){
            Intent intentALlLead = new Intent(DocumentListActivity.this,AllLeadsActivityNew.class);
            startActivity(intentALlLead);
            finish();
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "Documents List", false, true, true);

        queue = Volley.newRequestQueue(this);
        productBundle = getIntent().getBundleExtra("productBundle");
        if(productBundle.containsKey("fromLead")){
            isFromLeadList = productBundle.getBoolean("fromLead");
        }
        if(productBundle.containsKey("address"))
            address = productBundle.getString("address");

        if(productBundle.containsKey("lat") && productBundle.containsKey("lang")) {
            lang = productBundle.getString("lang");
            lat = productBundle.getString("lat");
        }

        initialize();
        updateRFC();
        fetchDocuments();
        setLeadData();
        setBubbleLayout();


        textViewRubiqueAddress.setText(address);
        textViewTrackIdProductList.setText(productBundle.getString("lead_id"));



        fetchLeadDetails();
        if(productBundle!=null && productBundle.containsKey("product_name")) {
            Log.i(TAG, "onCreate: "+productBundle.getString("product_name"));
            textViewProductTypePreview.setVisibility(View.VISIBLE);
            textViewProductTypePreview.setText(productBundle.getString(productBundle.getString("product_name")));
        }
    }

    private void updateRFC() {

        JSONObject object = new JSONObject();
        try {
            object.put("lead_id",productBundle.getString("lead_id"));
            object.put("partner_org_id",productBundle.getString("rfc_id"));
            Helper.showLog(URL.UPDATE_LEAD_RFC,object.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.UPDATE_LEAD_RFC, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    scheduleMeeting();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void scheduleMeeting() {

        btnMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Helper.validateEditText(DocumentListActivity.this,etMeeting,"text",true))
                {
                    btnMeeting.setText("Scheduling...");
                    JSONObject object = new JSONObject();
                    try {
                        object.put(Constant.UTOKEN,Helper.getStringSharedPreference(Constant.UTOKEN,DocumentListActivity.this));
                        object.put("location","Customer Address");
                        object.put("lead_id",productBundle.getString("lead_id"));
                        object.put("start_datatime",etMeeting.getText().toString());
                        object.put("end_datatime",etMeeting.getText().toString());
                        Helper.showLog(URL.SCHEDULE_MEETING,object.toString());
                        JsonObjectRequest request = new JsonObjectRequest(URL.SCHEDULE_MEETING, object, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Meeting",response.toString());
                                btnMeeting.setText("Scheduled");
                                btnMeeting.setEnabled(false);
                                etMeeting.setEnabled(false);
                                Toast.makeText(DocumentListActivity.this,"Meeting Scheduled",Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        queue.add(request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void fetchDocuments() {
        try {
            JSONObject object = new JSONObject();
            object.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, this));
            object.put("product_type_id", productBundle.getString("product_type_sought"));
            Helper.showLog(URL.FETCH_DOCUMENT_LIST,object.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL.FETCH_DOCUMENT_LIST, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.i(TAG, "onResponse: fetchDocuments "+response);
                    try {
                        createDocumentLayout(response.getJSONArray("body"));
                        viewDocumentsListLine.setVisibility(View.VISIBLE);
                        recyclerViewDocList.setVisibility(View.VISIBLE);
                        textViewDocCheckList.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void createDocumentLayout(JSONArray body) {
        String parent_group="";
        try {
            for (int i = 0; i < body.length(); i++) {


                JSONObject docGroup = body.getJSONObject(i);


                View child = getLayoutInflater().inflate(R.layout.component_linear_layout, null);
                LinearLayout linearLayout = (LinearLayout) child.findViewById(R.id.linearLayout);

                if(docGroup.has("parent_group")) {
                    if (!parent_group.equalsIgnoreCase(docGroup.optString("parent_group", ""))) {
                        parent_group = docGroup.optString("parent_group", "");
                        View mainHeading = getLayoutInflater().inflate(R.layout.component_textview, null);
                        TextView headingParent = (TextView) mainHeading.findViewById(R.id.textView);

                        headingParent.setText(parent_group);
                        headingParent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        headingParent.setTypeface(Typeface.DEFAULT_BOLD);
                        headingParent.setTextColor(getResources().getColor(R.color.Grey800));
                        headingParent.setGravity(Gravity.LEFT);
                        linearLayout.addView(headingParent);
                    }
                }

                View child2 = getLayoutInflater().inflate(R.layout.component_textview, null);
                TextView heading = (TextView) child2.findViewById(R.id.textView);
                heading.setText(docGroup.optString("group_name", ""));
                heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                heading.setTextColor(getResources().getColor(R.color.Grey800));
                heading.setGravity(Gravity.LEFT);
                linearLayout.addView(heading);

                JSONArray array = docGroup.getJSONArray("documents");

                for (int j = 0; j < array.length(); j++) {
                    View child3 = getLayoutInflater().inflate(R.layout.component_textview, null);
                    TextView text = (TextView) child3.findViewById(R.id.textView);
                    text.setText(array.getJSONObject(j).optString("name", ""));
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    text.setTextColor(getResources().getColor(R.color.Grey600));

                    text.setGravity(Gravity.LEFT);
                    linearLayout.addView(text);
                }

                coverLayout.addView(linearLayout);

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setLeadData() {

        tvApplicantName.setText(productBundle.getString("name"));
        tvLeadId.setText(productBundle.getString("lead_id"));
        if (productBundle.getString("product_type_sought").equals("11")) {
            loanLayout.setVisibility(View.GONE);
        } else {
            loanLayout.setVisibility(View.VISIBLE);
            tvLoanAmount.setText("Rs " + productBundle.getString("amount"));
        }


        switch (productBundle.getString("product_type_sought")) {
            case "11":
                Glide.with(this).load(R.drawable.ic_product_credit_card).into(productIcon);
                break;
            case "22":
                Glide.with(this).load(R.drawable.ic_product_car_loan).into(productIcon);
                break;
            case "23":
                Glide.with(this).load(R.drawable.ic_product_two_wheeler_loan).into(productIcon);
                break;
            case "24":
                Glide.with(this).load(R.drawable.ic_product_credit_card).into(productIcon);
                break;
            case "25":
                Glide.with(this).load(R.drawable.ic_product_personal_loan).into(productIcon);
                break;
            case "26":
                Glide.with(this).load(R.drawable.ic_product_home_loan).into(productIcon);
                break;
            case "28":
                Glide.with(this).load(R.drawable.ic_product_loan_against_property).into(productIcon);
                break;
            case "29":
                Glide.with(this).load(R.drawable.ic_product_car_loan).into(productIcon);
                break;
            case "51":
                Glide.with(this).load(R.drawable.ic_product_life_insurance).into(productIcon);
                break;
            case "52":
                Glide.with(this).load(R.drawable.ic_product_general_insurance).into(productIcon);
                break;
            case "53":
                Glide.with(this).load(R.drawable.ic_product_health_insurance).into(productIcon);
                break;
            default:
                Glide.with(this).load(R.drawable.ic_product_business_loan).into(productIcon);
                break;
        }

        tvProduct.setText(productBundle.getString("product_type_name"));


    }

    private void setBubbleLayout() {
        referIcon = (AppCompatImageView) findViewById(R.id.refer_bubbble);
        appFillIcon = (AppCompatImageView) findViewById(R.id.app_fill_bubble);
        docPickupIcon = (AppCompatImageView) findViewById(R.id.doc_pickup_bubble);
        referAmount = (TextView) findViewById(R.id.refer_bubbble_amount);
        appFillAmount = (TextView) findViewById(R.id.app_fill_bubble_amount);
        docPickupAmount = (TextView) findViewById(R.id.doc_pickup_bubble_amount);
        disAmount = (TextView) findViewById(R.id.dis_bubble_amount);

        appFillIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Grey400)));
        docPickupIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Orange300)));
        docPickupAmount.setTextColor(getResources().getColor(R.color.Orange300));
        appFillAmount.setTextColor(getResources().getColor(R.color.Grey400));

        try {
            snappyDB = DBFactory.open(this);
            allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
            allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
            maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();

            snappyDB.close();
            double payout = 0 ;
            JSONObject stepObject = null;

            for (int i = 0; i < maxPayoutInEachProductTypeJsonArray.length(); i++) {
                if (maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                    payout = maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getDouble("payout");
                }
            }

            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                    stepObject = allStepsJsonArray.getJSONObject(i);
                }
            }
            double loan = 0;
            if (productBundle.getString("product_type_sought").equals("11")) {
                loan = 100000;
            } else {
                loan = Double.parseDouble(productBundle.getString("amount"));
            }

            referAmount.setTextColor(ContextCompat.getColor(this, R.color.Grey400));

            appFillIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Green300)));
            referIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Green300)));

            referAmount.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));
            appFillAmount.setText("Rs " + Math.round((loan * payout * stepObject.getDouble("step2") / 10000)));
            docPickupAmount.setText("Rs " + Math.round((loan * payout * stepObject.getDouble("step2") / 10000) + (loan * payout * stepObject.getDouble("step1") / 10000)+ (loan * payout * stepObject.getDouble("step3") / 10000)));
            disAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step4") / 10000));

           /* referAmount.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));
            appFillAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step2") / 10000));
            docPickupAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step3") / 10000));
*/

        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }

    }


    private void initialize() {
        coverLayout = (LinearLayout) findViewById(R.id.cover_layout);
        tvApplicantName = (TextView) findViewById(R.id.applicant_name);
        productIcon = (ImageView) findViewById(R.id.product_icon);
        tvProduct = (TextView) findViewById(R.id.product);
        tvLoanAmount = (TextView) findViewById(R.id.loan_amount);
        tvLeadId = (TextView) findViewById(R.id.lead_id);
        loanLayout = (RelativeLayout) findViewById(R.id.loan_amount_layout);
        etMeeting = (EditText) findViewById(R.id.meeting_time);
        btnMeeting = (Button) findViewById(R.id.btn_meeting);
        buttonFinishDocList = (Button) findViewById(R.id.buttonFinishDocList);

        buttonFinishDocList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFromLeadList){
                    Intent intentALlLead = new Intent(DocumentListActivity.this,AllLeadsActivityNew.class);
                    startActivity(intentALlLead);
                    finish();
                }
                else
                    finish();
            }
        });

        //=======================
        textViewRubiqueAddress = (AppCompatTextView) findViewById(R.id.textViewRubiqueAddress);
        textViewProductTypePreview  = (AppCompatTextView) findViewById(R.id.textViewProductTypePreview);
        textViewNameProductList = (AppCompatTextView) findViewById(R.id.textViewNameProductList);
        textViewAddressProductList = (AppCompatTextView) findViewById(R.id.textViewAddressProductList);
        textViewPhoneProductList = (AppCompatTextView) findViewById(R.id.textViewPhoneProductList);
        textViewTrackIdProductList = (AppCompatTextView) findViewById(R.id.textViewTrackIdProductList);

        recyclerViewDocList = (RecyclerView) findViewById(R.id.recyclerViewDocList);
        viewDocumentsListLine = findViewById(R.id.viewDocumentsListLine);
        textViewDocCheckList = (AppCompatTextView) findViewById(R.id.textViewDocCheckList);
        /*viewDocumentsListLine.setVisibility(View.GONE);
        recyclerViewDocList.setVisibility(View.GONE);
        textViewDocCheckList.setVisibility(View.GONE);*/


        textViewScheduleMeetingDocList = (AppCompatTextView) findViewById(R.id.textViewScheduleMeetingDocList);
        textViewGetDirection = (AppCompatTextView) findViewById(R.id.textViewGetDirection);
        textViewScheduleMeetingDocList.setOnClickListener(this);
        textViewSelectDate = (AppCompatTextView) findViewById(R.id.textViewSelectDate);
        textViewSelectDate.setOnClickListener(this);
        textViewGetDirection.setOnClickListener(this);
    }

    private JSONArray getMaxInPayoutArray() {
        JSONArray resultJsonArray = new JSONArray();

        if (allStepsJsonArray != null && allStepsJsonArray.length() > 0) {
            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                try {
                    String productTypeId = allStepsJsonArray.getJSONObject(i).getString("product_type");
                    JSONObject newJsonObject = new JSONObject();
                    newJsonObject.put("product_type", productTypeId);
                    newJsonObject.put("payout", 0);
                    /* "payout": "0.5000",
                       "product_type": "11",*/
                    resultJsonArray.put(i, newJsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        if (allPayoutJsonArray != null && allPayoutJsonArray.length() > 0) {
            for (int i = 0; i < allPayoutJsonArray.length(); i++) {
                try {
                    String productTypeId = allPayoutJsonArray.getJSONObject(i).getString("product_type");
                    String payout = allPayoutJsonArray.getJSONObject(i).getString("payout");
                    int position = 0;
                    for (int j = 0; j < resultJsonArray.length(); j++) {
                        if (productTypeId.equals(resultJsonArray.getJSONObject(j).getString("product_type"))) {
                            position = j;
                            break;
                        }
                    }
                    float previousValue = Float.parseFloat(resultJsonArray.getJSONObject(position).getString("payout"));
                    if (previousValue < Float.parseFloat(payout)) {
                        resultJsonArray.getJSONObject(position).put("payout", payout);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultJsonArray;
    }

    private void fetchLeadDetails() {

        JSONObject object = new JSONObject();
        try {
            object.put("lead_id", productBundle.get("lead_id"));
            object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, DocumentListActivity.this));

            Helper.showLog(URL.FETCH_ALL_LEAD_FIELDS,object.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.FETCH_ALL_LEAD_FIELDS, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: getLead Details in ");
                    try {
                        JSONArray fieldArray = response.getJSONObject("body").getJSONArray("detail");
                        for (int i = 0; i < fieldArray.length(); i++) {
                            JSONObject jsonObject  = fieldArray.getJSONObject(i);
                            if (jsonObject.getString("base_id").equals("2"))
                                textViewNameProductList.setText(jsonObject.getString("field_value"));



                            if (jsonObject.getString("base_id").equals("20"))
                                addressCustomer = jsonObject.getString("field_value");

                            if (jsonObject.getString("base_id").equals("21"))
                                addressCustomer = addressCustomer+" , "+jsonObject.getString("field_value");

                            if (jsonObject.getString("base_id").equals("25"))
                                addressCustomer = addressCustomer+" , "+jsonObject.getString("field_value");

                            if (jsonObject.getString("base_id").equals("5"))
                                textViewPhoneProductList.setText(jsonObject.getString("field_value"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    textViewAddressProductList.setText(addressCustomer);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textViewSelectDate :
                if(textViewScheduleMeetingDocList.getText().toString().equalsIgnoreCase("Scheduled")){
                    Toast.makeText(DocumentListActivity.this, "Meeting Already Scheduled", Toast.LENGTH_SHORT).show();
                }
                else
                    selectDate();
                break;
            case R.id.textViewScheduleMeetingDocList :

                if(textViewScheduleMeetingDocList.getText().toString().equalsIgnoreCase("Scheduled")){
                    Toast.makeText(DocumentListActivity.this, "Meeting Already Scheduled", Toast.LENGTH_SHORT).show();
                }
                else if(textViewScheduleMeetingDocList.getText().toString().trim().equalsIgnoreCase("Select Date and Time"))
                    Toast.makeText(DocumentListActivity.this, "Please select Date and Time", Toast.LENGTH_SHORT).show();
                else
                    scheduleMeetingApicall();

                break;
            case R.id.textViewGetDirection :
                Intent intent = null;
                try {
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + lat + "," +lang));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                break;
        }
    }
    private void selectDate(){
        final Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, final int year, final int monthOfYear, final int dayOfMonth) {

                        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                                                                             @Override
                                                                                             public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute, int second) {
                                                                                                 isMeetingDateSelected = true;
                                                                                                 meetingDateSelected = ""+year+"-"+ (monthOfYear + 1) +"-" + dayOfMonth + " "+hourOfDay+":00:00";
                                                                                                 textViewSelectDate.setText(meetingDateSelected);
                                                                                             }
                                                                                         },
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE), true);
                        timePickerDialog.setTitle("Select Time");
                        timePickerDialog.enableMinutes(false);
                        timePickerDialog.enableSeconds(false);
                        timePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                        timePickerDialog.show(getSupportFragmentManager(),"Select Time");

                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setTitle("Select Date");
        datePickerDialog.setMinDate(now);
        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePickerDialog.show(getSupportFragmentManager(), "Select Date");
    }
    private void scheduleMeetingApicall() {
        if (!isMeetingDateSelected) {
            Toast.makeText(DocumentListActivity.this, "Please Select Date", Toast.LENGTH_SHORT).show();
        } else {
            textViewScheduleMeetingDocList.setText("Scheduling...");
            JSONObject object = new JSONObject();
            try {
                object.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, DocumentListActivity.this));
                object.put("location", "Customer Address");
                object.put("lead_id", productBundle.getString("lead_id"));
                object.put("start_datatime", meetingDateSelected);
                object.put("end_datatime", meetingDateSelected);

                Helper.showLog(URL.SCHEDULE_MEETING,object.toString());

                JsonObjectRequest request = new JsonObjectRequest(URL.SCHEDULE_MEETING, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Meeting", response.toString());
                        if(response.optInt("status_code") == 2000) {
                            textViewScheduleMeetingDocList.setText("Scheduled");
                            textViewScheduleMeetingDocList.setEnabled(false);
                            Toast.makeText(DocumentListActivity.this, "Meeting Scheduled", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(request);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
