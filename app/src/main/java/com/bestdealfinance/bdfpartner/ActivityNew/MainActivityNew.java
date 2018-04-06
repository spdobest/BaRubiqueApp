package com.bestdealfinance.bdfpartner.ActivityNew;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.HomeFragmentPagerAdapter;
import com.bestdealfinance.bdfpartner.FragmentNew.DashboardFragmentNew;
import com.bestdealfinance.bdfpartner.FragmentNew.EarningsFragmentNew;
import com.bestdealfinance.bdfpartner.FragmentNew.LoggedInDashboardFragment;
import com.bestdealfinance.bdfpartner.FragmentNew.MoreFragmentNew;
import com.bestdealfinance.bdfpartner.FragmentNew.PayoutFragmentNew;
import com.bestdealfinance.bdfpartner.FragmentNew.ReferFragmentNew;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.ChatHead;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivityNew extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivityNew";
    private static final int REQUEST_CODE = 0;
    View bottomLayoutDashboard, bottomLayoutNewLead, bottomLayoutPayout, linearLayoutNavTabEarnings, bottomLayoutMore;

    int bottomIconIds[] = {R.id.dashboard_bottom_icon, R.id.new_lead_bottom_icon, R.id.payout_bottom_icon, R.id.meeting_bottom_icon, R.id.more_bottom_icon};
    int bottomTextViewsIds[] = {R.id.dashboard_bottom_text, R.id.new_lead_bottom_text, R.id.payout_bottom_text, R.id.meeting_bottom_text, R.id.more_bottom_text};
    JSONObject stepObject = null;
    List<Fragment> listFragments;
    ViewPager viewpagerHomeTabbedFragment;
    HomeFragmentPagerAdapter homeFragmentPagerAdapter;
    List<Integer> listTabIcon;
    private RequestQueue queue;
    private DatabaseReference mDatabase;
    private DB snappyDB;
    private FirebaseAuth mAuth;
    private JSONArray allStepsJsonArray, maxPayoutInEachProductTypeJsonArray;
    private JSONArray allPayoutJsonArray;
    private ReferFragmentNew referFragmentNew;
    private PayoutFragmentNew payoutFragmentNew;
    private int selectedPosition = 0;
    private RelativeLayout activity_home_new;
    boolean isTabSet = false;
    Snackbar snackbar;
    private LinearLayout linearLayoutBottomNavigation;

    Uri deeplinkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        queue = Volley.newRequestQueue(this);


        Intent intent = getIntent();
        String action = intent.getAction();
        deeplinkUrl = intent.getData();

        Log.i(TAG, "onCreate: " + action + "\n" + deeplinkUrl);

        if (deeplinkUrl != null && !TextUtils.isEmpty(deeplinkUrl.toString()) && deeplinkUrl.toString().contains("rubique.com/r")) {
            Intent IntentReminder = new Intent(MainActivityNew.this, ReminderActivity.class);
            startActivity(IntentReminder);
            finish();
        }

        activity_home_new = (RelativeLayout) findViewById(R.id.activity_home_new);
        linearLayoutBottomNavigation = (LinearLayout) findViewById(R.id.linearLayoutBottomNavigation);

        loginToFirebase();
        //checkDrawOverlayPermission();

//        setBottomBar();

        showPrrogress(true);
        getPayoutDetailsFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listFragments != null && listFragments.size() > 0) {
            if (Helper.getStringSharedPreference(Constant.UTOKEN, this).equals("")) {
                if (!listFragments.get(0).getClass().isInstance(DashboardFragmentNew.class)) {
                    listFragments.remove(0);
                    listFragments.add(0, new DashboardFragmentNew());
                    homeFragmentPagerAdapter.notifyDataSetChanged();
                }
            } else {
                if (!listFragments.get(0).getClass().isInstance(LoggedInDashboardFragment.class)) {
                    listFragments.remove(0);
                    listFragments.add(0, LoggedInDashboardFragment.newInstance());

//                    homeFragmentPagerAdapter.notifyDataSetChanged();
                    if (viewpagerHomeTabbedFragment.getCurrentItem() != selectedPosition) {
                        homeFragmentPagerAdapter = new HomeFragmentPagerAdapter(this, getSupportFragmentManager(), listFragments);
                        viewpagerHomeTabbedFragment.setAdapter(homeFragmentPagerAdapter);
                        viewpagerHomeTabbedFragment.setCurrentItem(selectedPosition);
                    }
                }
            }
        }
    }


    private void readContacts() {


        if (!Helper.getStringSharedPreference(Constant.UTOKEN, this).equals("")) {

            Calendar calendar = Calendar.getInstance();
            Long cur_time = calendar.getTimeInMillis();
            Long last_saved = Long.parseLong(Helper.getStringSharedPreference("contact_saved", MainActivityNew.this).equals("") ? "0" : Helper.getStringSharedPreference("contact_saved", MainActivityNew.this));

            if (last_saved + 86400000 < cur_time) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        HashMap<Long, String> phoneMap = new HashMap<Long, String>();
                        HashMap<Long, String> nameMap = new HashMap<Long, String>();
                        HashMap<Long, String> emailMap = new HashMap<Long, String>();
                        HashSet<Long> ids = new HashSet<Long>();
                        JSONArray jsonArray = new JSONArray();
                        ContentResolver resolver = getContentResolver();
                        Cursor c = resolver.query(
                                ContactsContract.Data.CONTENT_URI,
                                null,
                                ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND (" + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?)",
                                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                                ContactsContract.Data.CONTACT_ID);

                        while (c.moveToNext()) {
                            long id = c.getLong(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                            String name = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                            String data1 = c.getString(c.getColumnIndex(ContactsContract.Data.DATA1));

                            nameMap.put(id, name);
                            if (Patterns.EMAIL_ADDRESS.matcher(data1.replace(" ", "").trim()).matches()) {
                                emailMap.put(id, data1);
                            } else {
                                phoneMap.put(id, data1);
                            }
                            ids.add(id);

                        }
                        c.close();

                        mDatabase = FirebaseDatabase.getInstance().getReference(Constant.FIREBASE_DB);
                        Map<String, Object> childUpdates = new HashMap<>();
                        Iterator iterator = ids.iterator();
                        while (iterator.hasNext()) {
                            Long id = (Long) iterator.next();
                            Map<String, Object> object = new HashMap<String, Object>();
                            object.put("name", nameMap.get(id) != null ? nameMap.get(id).replace(" ", "") : "");
                            object.put("email", emailMap.get(id) != null ? emailMap.get(id).replace(" ", "") : "");
                            object.put("phone", phoneMap.get(id) != null ? phoneMap.get(id).replace(" ", "") : "");
                            object.put("associated_to", Helper.getStringSharedPreference(Constant.USERID, MainActivityNew.this));
                            childUpdates.put("/contacts/" + Helper.getStringSharedPreference(Constant.USERID, MainActivityNew.this) + "/" + Helper.md5((emailMap.get(id) != null ? emailMap.get(id).replace(" ", "") : "") + (phoneMap.get(id) != null ? phoneMap.get(id).replace(" ", "") : "") + Helper.getStringSharedPreference(Constant.USERID, MainActivityNew.this)), object);
                        }
                        mDatabase.updateChildren(childUpdates);
                        Calendar calendar = Calendar.getInstance();
                        Helper.setStringSharedPreference("contact_saved", "" + calendar.getTimeInMillis(), MainActivityNew.this);
                    }
                }).start();

            } else {
                Log.d("Contacts", "Already Updated");
            }


        }
    }

    private void loginToFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(Constant.FIREBASE_DB_USERNAME, Constant.FIREBASE_DB_PASSWORD)
                .addOnCompleteListener(MainActivityNew.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Database", "Login Success");
                            readContacts();
                        } else {
                            Log.d("Database", "Login Failed");
                        }
                    }
                });
    }

    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                Intent i = new Intent(getApplicationContext(), ChatHead.class);
                startService(i);
            }
        } else {
            Intent i = new Intent(getApplicationContext(), ChatHead.class);
            startService(i);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            /** if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Intent i = new Intent(getApplicationContext(), ChatHead.class);
                    startService(i);
                }
            }
        }
    }

    private void showOverlay() {
        //Helper.setStringSharedPreference("overlay_main","", MainActivityNew.this);
        if (Helper.getStringSharedPreference("overlay_main", this).equals("")) {
            findViewById(R.id.overlay_main).setVisibility(View.VISIBLE);
            findViewById(R.id.got_it_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.setStringSharedPreference("overlay_main", "1", MainActivityNew.this);
                    findViewById(R.id.overlay_main).setVisibility(View.GONE);
                }
            });
        } else {
            findViewById(R.id.overlay_main).setVisibility(View.GONE);
        }
    }


    private void setBottomBar() {
        bottomLayoutDashboard = findViewById(R.id.dashboard_bottom_layout);
        bottomLayoutNewLead = findViewById(R.id.new_lead_bottom_layout);
        bottomLayoutPayout = findViewById(R.id.payout_bottom_layout);
        linearLayoutNavTabEarnings = findViewById(R.id.linearLayoutNavTabEarnings);
        bottomLayoutMore = findViewById(R.id.more_bottom_layout);

        bottomLayoutDashboard.setOnClickListener(this);
        bottomLayoutNewLead.setOnClickListener(this);
        bottomLayoutPayout.setOnClickListener(this);
        linearLayoutNavTabEarnings.setOnClickListener(this);
        bottomLayoutMore.setOnClickListener(this);

        activateTab(0);
        if (Helper.getStringSharedPreference(Constant.UTOKEN, this).equals("")) {
            DashboardFragmentNew dashboardFragment = new DashboardFragmentNew();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.product_activity_main_layout, dashboardFragment, "DashboardFragmentNew")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.product_activity_main_layout, LoggedInDashboardFragment.newInstance(), LoggedInDashboardFragment.TAG)
                    .commit();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dashboard_bottom_layout:
                viewpagerHomeTabbedFragment.setCurrentItem(0);
                activateTab(0);
                break;
            case R.id.new_lead_bottom_layout:
                viewpagerHomeTabbedFragment.setCurrentItem(1);
                activateTab(1);
                break;
            case R.id.payout_bottom_layout:
                viewpagerHomeTabbedFragment.setCurrentItem(2);
                activateTab(2);
                break;
            case R.id.linearLayoutNavTabEarnings:
                viewpagerHomeTabbedFragment.setCurrentItem(3);
                activateTab(3);
                break;
            case R.id.more_bottom_layout:
                viewpagerHomeTabbedFragment.setCurrentItem(4);
                activateTab(4);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.do_youwant_to_exit));
        alertDialogBuilder.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_in_caps), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Red500));
    }

    private void getPayoutDetailsFromServer() {
        try {
            snappyDB = DBFactory.open(MainActivityNew.this);
            if (snappyDB.exists(Constant.DB_ALL_PAYOUTS_JSON_ARRAY) && snappyDB.exists(Constant.DB_ALL_STEPS_JSON_ARRAY)) {
                allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
                allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
                snappyDB.close();


                if (allPayoutJsonArray != null && allPayoutJsonArray.length() > 0 && allStepsJsonArray != null && allStepsJsonArray.length() > 0) {

                    for (int i = 0; i < allStepsJsonArray.length(); i++) {
                        String pt = allStepsJsonArray.getJSONObject(i).getString("product_type");
                        if (pt.equals("11")) {
                            stepObject = allStepsJsonArray.getJSONObject(i);
                        }
                    }
                }
            }
        } catch (SnappydbException | JSONException e) {
            e.getMessage();
        }
    }

    public void setTabbedFragment() {

        linearLayoutBottomNavigation.setVisibility(View.VISIBLE);

        if (!isTabSet) {
            isTabSet = true;
            viewpagerHomeTabbedFragment = (ViewPager) findViewById(R.id.viewpagerHomeTabbedFragment);
            bottomLayoutDashboard = findViewById(R.id.dashboard_bottom_layout);
            bottomLayoutNewLead = findViewById(R.id.new_lead_bottom_layout);
            bottomLayoutPayout = findViewById(R.id.payout_bottom_layout);
            linearLayoutNavTabEarnings = findViewById(R.id.linearLayoutNavTabEarnings);
            bottomLayoutMore = findViewById(R.id.more_bottom_layout);

            bottomLayoutDashboard.setOnClickListener(this);
            bottomLayoutNewLead.setOnClickListener(this);
            bottomLayoutPayout.setOnClickListener(this);
            linearLayoutNavTabEarnings.setOnClickListener(this);
            bottomLayoutMore.setOnClickListener(this);

            activateTab(0);

            listFragments = new ArrayList<>();
            if (Helper.getStringSharedPreference(Constant.UTOKEN, this).equals("")) {
                listFragments.add(new DashboardFragmentNew());
            } else {
                listFragments.add(LoggedInDashboardFragment.newInstance());
            }

            referFragmentNew = ReferFragmentNew.newInstance(true);
            payoutFragmentNew = PayoutFragmentNew.newInstance();

            listFragments.add(referFragmentNew);
            listFragments.add(payoutFragmentNew);
            listFragments.add(EarningsFragmentNew.newInstance());
            listFragments.add(new MoreFragmentNew());


            homeFragmentPagerAdapter = new HomeFragmentPagerAdapter(MainActivityNew.this, getSupportFragmentManager(), listFragments);
            viewpagerHomeTabbedFragment.setAdapter(homeFragmentPagerAdapter);

            viewpagerHomeTabbedFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    activateTab(position);
                }

                @Override
                public void onPageSelected(int position) {
                    activateTab(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    public void activateTab(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        for (int i = 0; i < bottomIconIds.length; i++) {
            ImageView icon = (ImageView) findViewById(bottomIconIds[i]);
            TextView text = (TextView) findViewById(bottomTextViewsIds[i]);
            if (i == selectedPosition) {
                icon.getDrawable().mutate().setColorFilter(ContextCompat.getColor(this, R.color.Blue500), PorterDuff.Mode.MULTIPLY);
                text.setTextColor(ContextCompat.getColor(this, R.color.Blue500));
            } else {
                icon.getDrawable().mutate().setColorFilter(ContextCompat.getColor(this, R.color.Grey500), PorterDuff.Mode.MULTIPLY);
                text.setTextColor(ContextCompat.getColor(this, R.color.Grey500));
            }
        }
    }

    private void showPrrogress(boolean isCallingFirstTime) {
        try {
            snappyDB = DBFactory.open(MainActivityNew.this);
            if (!snappyDB.exists(Constant.DB_ALL_PAYOUTS_JSON_ARRAY) && !snappyDB.exists(Constant.DB_ALL_STEPS_JSON_ARRAY) && !snappyDB.exists(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT)) {
                snappyDB.close();
//               Helper.showSnackbarMessage(activity_home_new,, Snackbar.LENGTH_LONG);
                if (isCallingFirstTime) {
                    snackbar = Snackbar
                            .make(activity_home_new, "Fetching Data, Please Wait...", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
            } else {
                if (snackbar != null)
                    snackbar.dismiss();
                setTabbedFragment();
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public void setTabPosition(int position) {
        selectedPosition = position;
        viewpagerHomeTabbedFragment.setCurrentItem(position);
    }
}
