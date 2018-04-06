package com.bestdealfinance.bdfpartner.ActivityNew;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.RfcListAdapter;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectRfcActivityNew extends AppCompatActivity {

    private RequestQueue queue;
    private GoogleMap mMap;
    private Location location;
    private LocationManager locationManager;
    private Button btnShowList;
    private LinearLayout showListLayout;
    private ImageButton btnCloseList;
    private RecyclerView rfcList;
    private RfcListAdapter rfcAdapter;
    private Bundle productBundle;
    private JSONArray rfcCenter;
    private Toolbar toolbar;
    private AppCompatImageView referIcon;
    private AppCompatImageView appFillIcon;
    private AppCompatImageView docPickupIcon;
    private TextView referAmount;
    private TextView appFillAmount;
    private TextView docPickupAmount;
    private DB snappyDB;
    private JSONArray allPayoutJsonArray;
    private JSONArray allStepsJsonArray;
    private JSONArray maxPayoutInEachProductTypeJsonArray;
    private String leadCity;
    private AppCompatImageView disburseIcon;
    private TextView disAmount;

    private static final int PERMISSIONS_REQUEST_LOCATION = 3;

    boolean isFromLeadList = false;

    @Override
    public void onBackPressed() {
        if(isFromLeadList){
            Intent intentALlLead = new Intent(SelectRfcActivityNew.this,AllLeadsActivityNew.class);
            startActivity(intentALlLead);
            finish();
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_rfc_new);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "Select your RFC ", false, true, true);

        queue = Volley.newRequestQueue(this);
        productBundle = getIntent().getBundleExtra("productBundle");

        if(productBundle.containsKey("fromLead")){
            isFromLeadList = productBundle.getBoolean("fromLead");
        }

        // Testing
        //productBundle.putString("product_type_sought", "39");
        //productBundle.putString("amount", "1000000");
        //

        initialize();

        fetchLeadDetails();
        setBubbleLayout();

        requestLocationPermission();

    }

    private void fetchLeadDetails() {

        JSONObject object = new JSONObject();
        try {
            object.put("lead_id", productBundle.get("lead_id"));
            object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, SelectRfcActivityNew.this));

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.FETCH_ALL_LEAD_FIELDS, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONArray fieldArray = response.getJSONObject("body").getJSONArray("detail");

                        for (int i = 0; i < fieldArray.length(); i++) {
                            if (fieldArray.getJSONObject(i).getString("base_id").equals("25")) {

                                leadCity = fieldArray.getJSONObject(i).getString("field_value");
                            }
                        }
                        fetchNearByRfc();

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

    private void initialize() {

        btnShowList = (Button) findViewById(R.id.rfc_list_button);
        showListLayout = (LinearLayout) findViewById(R.id.show_list_layout);
        btnCloseList = (ImageButton) findViewById(R.id.close_button);
        rfcList = (RecyclerView) findViewById(R.id.rfc_list);
        rfcAdapter = new RfcListAdapter(this, productBundle);
        rfcList.setAdapter(rfcAdapter);
        rfcList.setLayoutManager(new LinearLayoutManager(this));

        showListLayout.setTranslationY(2500F);
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListLayout.animate().translationY(0F);
            }
        });

        btnCloseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListLayout.animate().translationY(2500F);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you need application form ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFormCollected();
            }
        });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();


    }

    private void updateFormCollected() {
        try {
            final JSONObject object = new JSONObject();
            object.put("lead_id", productBundle.get("lead_id"));
            object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, SelectRfcActivityNew.this));

            Helper.showLog(URL.UPDATE_FORM_COLLECTED,object.toString());

            JsonObjectRequest request = new JsonObjectRequest(URL.UPDATE_FORM_COLLECTED, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(SelectRfcActivityNew.this, "Lead status updated to form collected", Toast.LENGTH_LONG).show();
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

    private void fetchNearByRfc() {
        try {
            final JSONObject object = new JSONObject();
            object.put("city", leadCity);
            object.put("product_type_id", productBundle.getString("product_type_sought"));
            object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, SelectRfcActivityNew.this));
            Helper.showLog(URL.FETCH_NEARBY_RFC,object.toString());
            JsonObjectRequest request = new JsonObjectRequest(URL.FETCH_NEARBY_RFC, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject body = response.optJSONObject("body").optJSONObject("detail");

                    JSONArray jsonArray = body.optJSONArray("part_org");

                    if (jsonArray == null || jsonArray.length() == 0) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectRfcActivityNew.this);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("No Rfc in your area");
                        alertDialog.setMessage("Please call regoinal manager for help");
                        alertDialog.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(SelectRfcActivityNew.this,DocumentListActivity.class);
                                productBundle.putString("rfc_id","1"); // HARD CODED
                                intent.putExtra("productBundle",productBundle);
                                startActivity(intent);
                                finish();

                            }
                        });
                        alertDialog.show();

                    } else {
                        rfcCenter = jsonArray;
                        rfcAdapter.updateProduct(rfcCenter);

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        //builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                        for (int i = 0; i < rfcCenter.length(); i++) {

                            JSONObject object1 = rfcCenter.optJSONObject(i);
                            builder.include(new LatLng(object1.optDouble("lat", 0), object1.optDouble("lng", 0)));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(object1.optDouble("lat", 0), object1.optDouble("lng", 0))).title(object1.optString("name")).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon_2)));
                        }

                        LatLngBounds bounds = builder.build();
                        int padding = 150; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
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

    private void initializeMap() {


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Add a marker in Sydney, Australia, and move the camera.
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        showListLayout.animate().translationY(0F);
                    }
                });


            }
        });

    }


    private void setBubbleLayout() {
        referIcon = (AppCompatImageView) findViewById(R.id.refer_bubbble);
        appFillIcon = (AppCompatImageView) findViewById(R.id.app_fill_bubble);
        docPickupIcon = (AppCompatImageView) findViewById(R.id.doc_pickup_bubble);
        disburseIcon = (AppCompatImageView) findViewById(R.id.dis_bubble);

        referAmount = (TextView) findViewById(R.id.refer_bubbble_amount);
        appFillAmount = (TextView) findViewById(R.id.app_fill_bubble_amount);
        docPickupAmount = (TextView) findViewById(R.id.doc_pickup_bubble_amount);
        disAmount = (TextView) findViewById(R.id.dis_bubble_amount);

        try {
            snappyDB = DBFactory.open(this);
            allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
            allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
            maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();

            snappyDB.close();
            double payout = 0;
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

           /* referAmount.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));
            appFillAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step2") / 10000));
            docPickupAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step3") / 10000));
            disAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step4") / 10000));*/

            appFillIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Green300)));
            referIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Green300)));

            docPickupIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Orange300)));
            docPickupAmount.setTextColor(getResources().getColor(R.color.Orange300));
            referAmount.setTextColor(ContextCompat.getColor(SelectRfcActivityNew.this,R.color.Grey400));
            appFillAmount.setTextColor(ContextCompat.getColor(SelectRfcActivityNew.this,R.color.Grey400));

            referAmount.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));
            appFillAmount.setText("Rs " + Math.round((loan * payout * stepObject.getDouble("step2") / 10000)));
            docPickupAmount.setText("+Rs " +  Math.round((loan * payout * stepObject.getDouble("step2") / 10000) + (loan * payout * stepObject.getDouble("step1") / 10000)+ (loan * payout * stepObject.getDouble("step3") / 10000)));
            disAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step4") / 10000));


        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initializeMap();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectRfcActivityNew.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Permission Required");
                    alertDialog.setMessage("We need those permission. We will not store your personal data on our servers.");
                    alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestLocationPermission();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }
    private void requestLocationPermission() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSIONS_REQUEST_LOCATION);
        else
            initializeMap();
    }
}
