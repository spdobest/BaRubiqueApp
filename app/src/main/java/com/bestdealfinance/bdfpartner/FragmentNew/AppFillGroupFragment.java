package com.bestdealfinance.bdfpartner.FragmentNew;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.ActivityNew.OcrCaptureActivity;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.dialog.SpeechToTextDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppFillGroupFragment extends Fragment {

    private static final String TAG = "AppFillGroupFragment";
    private static final int GOOGLE_VISION_CAPTURE_AADHAR = 9004;
    private static final int GOOGLE_VISION_CAPTURE = 9003;

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;

    AppCompatEditText editTextOTPbottomSheet;
    HashMap<String, JSONObject> treeValueMap = new HashMap<>();
    double loan = 0;
    double payout = 0;
    SpeechToTextDialog speechToTextDialog;
    private int CUR_FIELD_INDEX = 0;
    private int ACTIVE_GROUP = 0;
    private RequestQueue queue;
    private Bundle productBundle;
    private LinearLayout layoutCover;
    private JSONArray treeTemplate;
    private int TOTAL_GROUP = 0;
    private TextView groupName;
    private Button btnContinue;
    private List<AutoCompleteTextView> etGroupList;
    private List<String> fieldUiId;
    private LinearLayout layoutIndicator;
    private ImageView[] ivIndicator;
    private JSONArray groupFields;
    private HashMap<String, Boolean> etMandatoryMap;
    private HashMap<String, String> etHashMapFieldUi;
    private HashMap<String, String> etListItemMap;
    private ImageView speechToTextMic;
    private EditText panEditText;
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
    private AppCompatImageView disburseIcon;
    private TextView disAmount;
    private View rootView;
    private HashMap<String, String> cacheValues;
    private AppCompatTextView textViewDiscardToolbar;
    private String leadId;
    private String loanAmount;
    private BottomSheetDialog bottomSheetDialogVeryfyOTP;
    private BottomSheetDialog bottomSheetDialogSendOtp;
    private String otpTransactionId;
    private EditText edittextAdharCard;
    private boolean isOtpVerified = false;

    private String product_id;

    public AppFillGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        Helper.hideKeyboard(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        queue = Volley.newRequestQueue(getActivity());

        cacheValues = new HashMap<>();

        productBundle = getArguments();
        if (productBundle != null && productBundle.containsKey("lead_id"))
            leadId = productBundle.getString("lead_id");
        if (productBundle != null && productBundle.containsKey("amount"))
            loanAmount = productBundle.getString("amount");

        if (productBundle.containsKey("product_id")) {
            product_id = productBundle.getString("product_id");
        }

        rootView = inflater.inflate(R.layout.fragment_app_fill_group, container, false);
        initialize(rootView);
        setBubbleLayout(rootView);
        getTemplateFromServer();


        return rootView;
    }

    private void initialize(View rootView) {
        groupName = (TextView) rootView.findViewById(R.id.group_name);
        layoutCover = (LinearLayout) rootView.findViewById(R.id.layout_cover);
        layoutIndicator = (LinearLayout) rootView.findViewById(R.id.group_indicator);
        btnContinue = (Button) rootView.findViewById(R.id.continue_button);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ImageButton toolbar_back_button = (ImageButton) rootView.findViewById(R.id.toolbar_back_button);

        ToolbarHelper.initializeToolbar(getActivity(), toolbar, "Application Fill", false, true, false);
        textViewDiscardToolbar = (AppCompatTextView) rootView.findViewById(R.id.textViewDiscardToolbar);
        textViewDiscardToolbar.setVisibility(View.VISIBLE);

         toolbar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        textViewDiscardToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        int[] ids = new int[]{R.id.indicator1, R.id.indicator2, R.id.indicator3, R.id.indicator4, R.id.indicator5, R.id.indicator6, R.id.indicator7, R.id.indicator8, R.id.indicator9, R.id.indicator10};
        ivIndicator = new ImageView[10];
        for (int i = 0; i < 10; i++) {
            ivIndicator[i] = (ImageView) rootView.findViewById(ids[i]);
        }

        speechToTextMic = (ImageView) rootView.findViewById(R.id.speech_to_text);
        speechToTextMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etGroupList == null || etGroupList.size() <= 0) {
                    return;
                }
                speechToTextDialog = new SpeechToTextDialog();
                speechToTextDialog.setData(etGroupList, fieldUiId);
                askAudioPerission();
            }
        });


    }


    private void getTemplateFromServer() {
        try {

            Log.i(TAG, "getTemplateFromServer: URL " + URL.GETTEMPLATEPARTIAL + "\n params ");


            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_TREE_TEMPLATE + productBundle.getString("product_type_sought"))) {
                treeTemplate = new JSONArray(snappyDB.get(Constant.DB_TREE_TEMPLATE + productBundle.getString("product_type_sought")));
                TOTAL_GROUP = treeTemplate.length();
                snappyDB.close();
                fetchLeadDetails();
                JSONObject data = new JSONObject();
                data.put("lead_id", productBundle.getString("lead_id", ""));
                data.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
                data.put("filter", "Full");
                Helper.showLog(URL.GETTEMPLATEPARTIAL, data.toString());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.GETTEMPLATEPARTIAL, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject body = response.optJSONObject("body");
                        try {
                            snappyDB = DBFactory.open(getActivity());
                            snappyDB.put(Constant.DB_TREE_TEMPLATE + productBundle.getString("product_type_sought"), body.optJSONArray("tree").toString());
                        } catch (SnappydbException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });
                queue.add(request);
            } else {
                snappyDB.close();
                JSONObject data = new JSONObject();
                //data.put("product_type_id", productBundle.getString("product_type_sought"));
                //data.put("product_id", productBundle.getString("product_id", ""));
                data.put("lead_id", productBundle.getString("lead_id", ""));
                data.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
                data.put("filter", "Full");


                Helper.showLog(URL.GETTEMPLATEPARTIAL, data.toString());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.GETTEMPLATEPARTIAL, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject body = response.optJSONObject("body");
                        try {
                            snappyDB = DBFactory.open(getActivity());
                            treeTemplate = body.optJSONArray("tree");
                            snappyDB.put(Constant.DB_TREE_TEMPLATE + productBundle.getString("product_type_sought"), treeTemplate.toString());
                            snappyDB.close();
                            TOTAL_GROUP = treeTemplate.length();
                            fetchLeadDetails();

                        } catch (SnappydbException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });
                queue.add(request);
            }


        } catch (JSONException | SnappydbException e) {
            e.printStackTrace();
        }

    }

    private void fetchLeadDetails() {

        JSONObject object = new JSONObject();
        try {


            object.put("lead_id", productBundle.get("lead_id"));
            object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            Helper.showLog(URL.FETCH_ALL_LEAD_FIELDS, object.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.FETCH_ALL_LEAD_FIELDS, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    try {
                        JSONArray fieldArray = response.getJSONObject("body").getJSONArray("detail");
                        for (int i = 0; i < fieldArray.length(); i++) {
                            treeValueMap.put(fieldArray.getJSONObject(i).getString("base_id"), fieldArray.getJSONObject(i));
                        }
                        ACTIVE_GROUP = 0;
                        createLayout();


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


    public boolean backPressed() {
        if (ACTIVE_GROUP > 0) {
            ACTIVE_GROUP--;
            createLayout();
            return false;
        } else
            return true;
    }


    private void createLayout() {

        setIndicator(ACTIVE_GROUP);
        layoutCover.removeAllViews();
        JSONObject groupObject = treeTemplate.optJSONObject(ACTIVE_GROUP);
        groupName.setText(groupObject.optString("groupName"));
        groupFields = groupObject.optJSONArray("groupFields");
        Log.i(TAG, "createLayout: groupName " + groupObject.optString("groupName") + " \n" + "groupFields");

        etGroupList = new ArrayList<>();
        fieldUiId = new ArrayList<>();
        etMandatoryMap = new HashMap<>();
        etListItemMap = new HashMap<>();
        etHashMapFieldUi = new HashMap<>();
        for (int i = 0; i < groupFields.length(); i++) {
            CUR_FIELD_INDEX = i;
            layoutCover.addView(createFieldLayout(groupFields.optJSONObject(i)));
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyboard(getActivity());
                updateIncompleteLead();
            }
        });

    }

    private void updateIncompleteLead() {

        boolean flag = true;

        for (int i = 0; i < etGroupList.size(); i++) {
            AutoCompleteTextView editText = etGroupList.get(i);
            if (etMandatoryMap.get(editText.getTag())) {
                Log.i(TAG, "updateIncompleteLead: " + etHashMapFieldUi.get(editText.getTag()));
                switch (etHashMapFieldUi.get(editText.getTag())) {
                    case "static-dd":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;
                    case "iwc":
                        editText.setText(editText.getText().toString().replaceAll(",", ""));
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;
                    case "durationym":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;
                    case "mobile":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "phone", true);
                        break;
                    case "email":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "email", true);
                        break;
                    case "alpha":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;
                    case "numeric":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;

                    case "alphanum":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;

                    case "textbox":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;

                    case "dob":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;
                    case "pan":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "pan", true);
                        break;
                    case "adhar":
                        flag = flag && Helper.validateEditText(getActivity(), editText, "adhar", true);
                        break;
                    default:
                        flag = flag && Helper.validateEditText(getActivity(), editText, "text", true);
                        break;
                }
            }
        }

        if (flag) {
            try {

                JSONArray tupleArray = new JSONArray();

                for (int i = 0; i < etGroupList.size(); i++) {
                    EditText editText = etGroupList.get(i);
                    if (editText.getVisibility() == View.VISIBLE) {
                        JSONObject tuple = new JSONObject();
                        tuple.put("base_id", editText.getTag().toString());
                        tuple.put("field_value", etHashMapFieldUi.get(editText.getTag()).equals("iwc") ? editText.getText().toString().trim().replaceAll(",", "") : editText.getText().toString().trim());
                        tuple.put("list_item_id", etListItemMap.get(editText.getTag().toString()));
                        tupleArray.put(tuple);
                        cacheValues.put(editText.getTag().toString(), etHashMapFieldUi.get(editText.getTag()).equals("iwc") ? editText.getText().toString().trim().replaceAll(",", "") : editText.getText().toString().trim());
                        if (editText.getTag().toString().equals("115")) {
                            productBundle.putString("amount", editText.getText().toString().trim().replaceAll(",", ""));
                            setBubbleLayout(rootView);
                        }

                    }

                }

                JSONObject object = new JSONObject();
                object.put("tuple_list", tupleArray);
                object.put("lead_id", productBundle.getString("lead_id"));
                object.put("product_type_sought", productBundle.getString("product_type_sought"));
                object.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
                Log.i(TAG, "updateIncompleteLead: ");
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait...");
                progressDialog.setTitle("Updating Lead");
                progressDialog.show();

                Helper.showLog(URL.UPDATE_INCOMPLETE_LEAD, object.toString());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.UPDATE_INCOMPLETE_LEAD, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Lead Updated", Toast.LENGTH_LONG).show();
                        if (ACTIVE_GROUP < TOTAL_GROUP - 1) {
                            ACTIVE_GROUP++;
                            createLayout();
                        } else {
                            ACTIVE_GROUP++;
//                            showOfferListFragment();
                            showPreviewFragment();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error Updating", Toast.LENGTH_LONG).show();


                    }
                });

                request.setShouldCache(false);
                request.setRetryPolicy(new DefaultRetryPolicy(30000, 2, 1f));
                queue.add(request);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void showPreviewFragment() {
        Log.i(TAG, "showPreviewFragment: " + productBundle.getString(Constant.BundleKey.LEAD_ID));
        /*PreviewFragment previewFragment = new PreviewFragment();
        previewFragment.setArguments(productBundle);*/
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, PreviewFragment.newInstance(productBundle), PreviewFragment.TAG).commit();
    }


    private void setIndicator(int active_group) {
        for (int i = 0; i < 10; i++) {
            ivIndicator[i].setVisibility(View.GONE);
        }

        for (int i = 0; i < TOTAL_GROUP; i++) {
            ivIndicator[i].setVisibility(View.VISIBLE);
            ivIndicator[i].setImageResource(R.drawable.ic_indicator_light);
        }
        for (int i = 0; i <= active_group; i++) {
            ivIndicator[i].setImageResource(R.drawable.ic_indicator_dark);
        }


    }

    private View createFieldLayout(final JSONObject jsonObject) {

        final String baseId = jsonObject.optString("baseId");


        final AutoCompleteTextView editText = new AutoCompleteTextView(getActivity());
        editText.setBackgroundResource(R.drawable.custom_border_grey_button);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        editText.setMaxLines(1);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.Grey500));
        int paddingPixel = 10;
        float density = getActivity().getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        editText.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        if (treeValueMap.get(baseId) != null) {

            if (baseId.equalsIgnoreCase("115")) {
                if (!TextUtils.isEmpty(treeValueMap.get(baseId).optString("field_value"))) {
                    if (treeValueMap.get(baseId).optString("field_value").contains("."))
                        editText.setText(treeValueMap.get(baseId).optString("field_value").substring(0, treeValueMap.get(baseId).optString("field_value").lastIndexOf(".") - 1));
                    else
                        editText.setText(treeValueMap.get(baseId).optString("field_value"));
                }
            } else
                editText.setText(treeValueMap.get(baseId).optString("field_value"));
        }

        if (cacheValues.get(baseId) != null) {
            editText.setText(cacheValues.get(baseId));
        }


        JSONObject metaBase = jsonObject.optJSONObject("metaBase");
        final String fieldName = metaBase.optString("fieldName");
        String fieldType = metaBase.optString("fieldType");
        String fieldUi = metaBase.optString("fieldUi");
        final String uiOptionList = metaBase.optString("uiOptionList");
        String fieldSize = metaBase.optString("fieldSize");
        String fieldVisibleIfInList = metaBase.optString("fieldVisibleIfInList");
        String fieldDependsOn = metaBase.optString("fieldDependsOn");
        String useMap = metaBase.optString("useMap");
        JSONObject listName = metaBase.optJSONObject("listName");


        if (jsonObject.optBoolean("mandatory", false))
            editText.setHint(fieldName + "*");
        else
            editText.setHint(fieldName);

        editText.setTag(baseId);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(fieldSize))});
        Log.i(TAG, "createFieldLayout: field name " + baseId + " hint " + fieldName + "  fieldUi " + fieldUi);

        switch (fieldUi) {

            case "static-dd": {
                final EditText temp1 = editText;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(fieldName);
                final JSONArray listArray = listName.optJSONArray("listMembers");
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < listArray.length(); i++) {
                    list.add(listArray.optJSONObject(i).optJSONObject("uberList").optString("itemValue"));
                }
                final JSONArray[] childFields = {null};
                builder.setItems(list.toArray(new String[list.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        temp1.setText(list.get(which));
                        etListItemMap.put(baseId, listArray.optJSONObject(which).optString("memberId"));

                        if (childFields[0] != null) {
                            for (int i = 0; i < childFields[0].length(); i++) {
                                removeFieldLayout(childFields[0].optJSONObject(i).optString("baseId"));
                            }
                        }

                        int childcount = layoutCover.getChildCount();
                        int insertAt = 0;
                        for (int i = 0; i < childcount; i++) {
                            View v = layoutCover.getChildAt(i);
                            if (v.getTag().equals(baseId))
                                insertAt = i;
                        }

                        childFields[0] = listArray.optJSONObject(which).optJSONObject("uberList").optJSONArray("childFields");
                        if (childFields[0] != null) {
                            for (int i = 0; i < childFields[0].length(); i++) {
                                layoutCover.addView(createFieldLayout(childFields[0].optJSONObject(i)), ++insertAt);
                            }
                        }


                    }
                });

                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.show();
                    }
                });

                break;
            }

            case "soft-autosuggest": {
                editText.setThreshold(3);
                editText.setMaxLines(3);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().length() > 2) {
                            try {
                                JSONObject object = new JSONObject();
                                object.put("list_id", uiOptionList);
                                object.put("keyword", s.toString());
                                Helper.showLog(URL.GET_UBER_LIST_SUGGESTION, object.toString());
                                JsonObjectRequest request = new JsonObjectRequest(URL.GET_UBER_LIST_SUGGESTION, object, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            final JSONArray jsonArray = response.getJSONArray("body");
                                            List<String> list = new ArrayList<>();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                list.add(jsonArray.getJSONObject(i).getString("itemValue"));
                                            }
                                            editText.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item_suggestion, list));
                                            editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    etListItemMap.put(baseId, jsonArray.optJSONObject(position).optString("id"));
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        return super.getHeaders();
                                    }
                                };
                                request.setRetryPolicy(new DefaultRetryPolicy(30000, 5, 1F));
                                queue.add(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                break;

            }

            case "iwc": {

                final EditText temp1 = editText;
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                temp1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (temp1.getText().toString().length() > 0 && isNumeric(temp1.getText().toString().trim())) {
                            temp1.removeTextChangedListener(this);
                            temp1.setText(Helper.formatRs(s.toString()));
                            temp1.addTextChangedListener(this);
                            temp1.setSelection(temp1.getText().length());
                        } else {

                        }

                    }
                });
                break;
            }

            case "durationym": {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_tenure);
                dialog.setCancelable(false);
                setLoanSliders(dialog, editText, fieldName);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                    }
                });
                break;
            }

            case "durationy": {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.layout_year_slider);
                dialog.setCancelable(false);
                setYearSliders(dialog, editText, fieldName);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                    }
                });
                break;
            }

            case "mobile": {
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            }

            case "email": {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(50);
                editText.setFilters(FilterArray);
                break;
            }

            case "alpha": {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
            case "numeric": {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (baseId.equalsIgnoreCase("17")) {
                    edittextAdharCard = editText;
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    edittextAdharCard.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String adharNumber = edittextAdharCard.getText().toString().trim();
                            if (adharNumber.length() == 12) {
                                if (!isOtpVerified)
                                    showBottomSheetToSendOtp(adharNumber);
                            }

                        }
                    });
                }
                break;
            }

            case "alphanum": {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }

            case "textbox": {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
            case "pan": {
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                panEditText = editText;
                editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_camera, 0);
                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int DRAWABLE_LEFT = 0;
                        final int DRAWABLE_TOP = 1;
                        final int DRAWABLE_RIGHT = 2;
                        final int DRAWABLE_BOTTOM = 3;

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                // your action here
                                askExternalStoragePerission();
                                return true;
                            }
                        }
                        return false;
                    }
                });
                editText.setAllCaps(true);
                break;
            }

            case "dob": {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setFocusable(false);
                final EditText temp1 = editText;
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar now = Calendar.getInstance();
                        now.set(1992, 0, 1);
                        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                        editText.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    }
                                },
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH));

                        datePickerDialog.setTitle("Select " + fieldName);
                        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                        datePickerDialog.setMaxDate(Calendar.getInstance());
                        datePickerDialog.show(getChildFragmentManager(), "Select Date");


                    }
                });
                break;
            }

            case "date-dmy": {
                Log.i(TAG, "createFieldLayout: REGISTRATION DATE  " + baseId + " " + fieldName);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setFocusable(false);
                final EditText temp1 = editText;
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar now = Calendar.getInstance();

                        if (baseId.equalsIgnoreCase("41")) {
                            now.set(1992, 0, 1);
                        }

                        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                        editText.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    }
                                },
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH));

                        if (baseId.equalsIgnoreCase("41") || fieldName.contains("registration")) {
                            Log.i(TAG, "onClick: here spm  ");
                            Calendar now1 = Calendar.getInstance();
                            now1.set(2016, 0, 1);
                            datePickerDialog.setMaxDate(now1);
                        }

                        datePickerDialog.setTitle("Select " + fieldName);
                        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                        datePickerDialog.show(getChildFragmentManager(), "Select Date");

                    }
                });
                break;
            }


        }
        etMandatoryMap.put(baseId, jsonObject.optBoolean("mandatory", false));
        etHashMapFieldUi.put(baseId, fieldUi);
        fieldUiId.add(fieldUi);
        etGroupList.add(editText);
        return editText;
    }

    private void removeFieldLayout(String baseId) {
        EditText editText;
        for (int i = 0; i < etGroupList.size(); i++) {
            editText = etGroupList.get(i);
            if (editText.getTag().equals(baseId)) {
                etGroupList.remove(i);
                layoutCover.removeView(editText);
                etMandatoryMap.remove(baseId);
                etHashMapFieldUi.remove(baseId);

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_VISION_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    showChangeLangDialog(text);
                    //panEditText.setText(text);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.ocr_failure), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.ocr_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GOOGLE_VISION_CAPTURE_AADHAR) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    showChangeLangDialog(text);
                    //panEditText.setText(text);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.ocr_failure), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.ocr_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showChangeLangDialog(String selectedText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout_google_vision, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.google_vision_edit_text);

        edt.setText(selectedText);

        dialogBuilder.setTitle("Your Selection");
        //dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable editable = edt.getText();
                if (editable != null) {
                    String str = editable.toString();
                    str = str.replaceAll("\\s", "");
                    panEditText.setText(str);
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    private void setLoanSliders(final Dialog rootView, final EditText editText, String heading) {
        final TextView layoutHeading = (TextView) rootView.findViewById(R.id.layout_heading);
        layoutHeading.setText(heading);
        final TextView tvYear = (TextView) rootView.findViewById(R.id.tenure_year);
        AppCompatImageButton btnYearMinus = (AppCompatImageButton) rootView.findViewById(R.id.tenure_year_minus);
        AppCompatImageButton btnYearPlus = (AppCompatImageButton) rootView.findViewById(R.id.tenure_year_plus);
        final AppCompatSeekBar seekbarYear = (AppCompatSeekBar) rootView.findViewById(R.id.tenure_year_seekbar);

        final TextView tvMonth = (TextView) rootView.findViewById(R.id.tenure_month);
        AppCompatImageButton btnMonthMinus = (AppCompatImageButton) rootView.findViewById(R.id.tenure_month_minus);
        AppCompatImageButton btnMonthPlus = (AppCompatImageButton) rootView.findViewById(R.id.tenure_month_plus);
        final AppCompatSeekBar seekbarMonth = (AppCompatSeekBar) rootView.findViewById(R.id.tenure_month_seekbar);

        Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
        Button btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);

        final int[] yearSelected = {0};
        final int[] monthSelected = {0};
        seekbarYear.setMax(Constant.MAX_LOAN_YEAR);
        seekbarYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                yearSelected[0] = progress;
                tvYear.setText("" + progress + " years");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbarMonth.setMax(11);
        seekbarMonth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                monthSelected[0] = progress;
                tvMonth.setText("" + progress + " months");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnYearPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearSelected[0] < Constant.MAX_LOAN_YEAR) {
                    seekbarYear.setProgress(seekbarYear.getProgress() + 1);

                } else {
                    Toast.makeText(getActivity(), "Max allowed years is " + Constant.MAX_LOAN_YEAR, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnYearMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearSelected[0] > 0) {
                    seekbarYear.setProgress(seekbarYear.getProgress() - 1);

                } else {
                    Toast.makeText(getActivity(), "Year can't less than 0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMonthPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthSelected[0] < 12) {
                    seekbarMonth.setProgress(seekbarMonth.getProgress() + 1);

                } else {
                    Toast.makeText(getActivity(), "Please increase year for 12 months", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMonthMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthSelected[0] > 0) {
                    seekbarMonth.setProgress(seekbarMonth.getProgress() - 1);

                } else {
                    Toast.makeText(getActivity(), "Month cant less than 0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((yearSelected[0] * 12 + monthSelected[0]) == 0) {
                    Toast.makeText(getActivity(), "Loan Tenure can't be  0", Toast.LENGTH_SHORT).show();
                } else {
                    editText.setText("" + (yearSelected[0] * 12 + monthSelected[0]));
                    rootView.dismiss();
                }


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.dismiss();
            }
        });


    }

    private void setYearSliders(final Dialog rootView, final EditText editText, String heading) {
        final TextView layoutHeading = (TextView) rootView.findViewById(R.id.layout_heading);
        layoutHeading.setText(heading);
        final TextView tvYear = (TextView) rootView.findViewById(R.id.tenure_year);
        AppCompatImageButton btnYearMinus = (AppCompatImageButton) rootView.findViewById(R.id.tenure_year_minus);
        AppCompatImageButton btnYearPlus = (AppCompatImageButton) rootView.findViewById(R.id.tenure_year_plus);
        final AppCompatSeekBar seekbarYear = (AppCompatSeekBar) rootView.findViewById(R.id.tenure_year_seekbar);
        Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
        Button btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);

        final int[] yearSelected = {0};
        seekbarYear.setMax(Constant.MAX_YEARS);
        seekbarYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                yearSelected[0] = progress;
                tvYear.setText("" + progress + " years");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnYearPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearSelected[0] < Constant.MAX_YEARS) {
                    seekbarYear.setProgress(seekbarYear.getProgress() + 1);

                } else {
                    Toast.makeText(getActivity(), "Max allowed years is " + Constant.MAX_YEARS, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnYearMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearSelected[0] > 0) {
                    seekbarYear.setProgress(seekbarYear.getProgress() - 1);

                } else {
                    Toast.makeText(getActivity(), "Year can't less than 0", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("" + yearSelected[0]);
                rootView.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.dismiss();
            }
        });


    }

    private void setBubbleLayout(View rootView) {

        referIcon = (AppCompatImageView) rootView.findViewById(R.id.refer_bubbble);
        appFillIcon = (AppCompatImageView) rootView.findViewById(R.id.app_fill_bubble);
        docPickupIcon = (AppCompatImageView) rootView.findViewById(R.id.doc_pickup_bubble);
        disburseIcon = (AppCompatImageView) rootView.findViewById(R.id.dis_bubble);
        referAmount = (TextView) rootView.findViewById(R.id.refer_bubbble_amount);
        appFillAmount = (TextView) rootView.findViewById(R.id.app_fill_bubble_amount);
        docPickupAmount = (TextView) rootView.findViewById(R.id.doc_pickup_bubble_amount);
        disAmount = (TextView) rootView.findViewById(R.id.dis_bubble_amount);

        try {
            snappyDB = DBFactory.open(getActivity());
            allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
            allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
            maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();

            snappyDB.close();

            JSONObject stepObject = null;

            boolean isProductIdExist = false;

            if (!TextUtils.isEmpty(product_id)) {
                for (int i = 0; i < allPayoutJsonArray.length(); i++) {
                    JSONObject jsonObject = allPayoutJsonArray.getJSONObject(i);
                    if (jsonObject.optString("product").equals(product_id)) {
                        isProductIdExist = true;
                        payout = jsonObject.optDouble("payout");
                        break;
                    }
                }
            }
            if (!isProductIdExist) {
                for (int i = 0; i < maxPayoutInEachProductTypeJsonArray.length(); i++) {
                    if (maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                        payout = maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getDouble("payout");
                    }
                }
            }

            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                    stepObject = allStepsJsonArray.getJSONObject(i);
                }
            }

            if (productBundle.containsKey("product_type_sought")) {
                if (productBundle.getString("product_type_sought").equals("11")) {
                    loan = 100000;
                } else {
                    loan = Double.parseDouble(productBundle.getString("amount"));
                }
            }

            referAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey400));


            Log.i(TAG, "setBubbleLayout: " + "  " + productBundle.getString("product_type_sought"));

            referAmount.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));
            appFillAmount.setText("Rs " + Math.round((loan * payout * stepObject.getDouble("step2") / 10000) + (loan * payout * stepObject.getDouble("step1") / 10000)));
            docPickupAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step3") / 10000));
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


    private void showBottomSheetToVerifyOtp() {
        AppCompatButton buttonVerifyBottomSheet;
        if (bottomSheetDialogVeryfyOTP == null) {
            bottomSheetDialogVeryfyOTP = new BottomSheetDialog(getActivity());
        }


        bottomSheetDialogVeryfyOTP.setContentView(R.layout.bottomsheet_verify_otp);
        buttonVerifyBottomSheet = (AppCompatButton) bottomSheetDialogVeryfyOTP.findViewById(R.id.buttonVerifyBottomSheet);
        editTextOTPbottomSheet = (AppCompatEditText) bottomSheetDialogVeryfyOTP.findViewById(R.id.editTextOTPbottomSheet);
        editTextOTPbottomSheet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextOTPbottomSheet.getText().toString().trim().length() == 6)
                    Helper.hideKeyboard(getActivity());
            }
        });

        buttonVerifyBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = editTextOTPbottomSheet.getText().toString().trim();
                if (Helper.isNetworkAvailable(getActivity())) {
                    if (otp.length() > 0) {
                        veriFyOtpApiCall(otp, otpTransactionId);

                    } else {
//                        editTextOTPbottomSheet.setError("OTP cant be empty");
                        Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        bottomSheetDialogVeryfyOTP.show();
    }

    private void showBottomSheetToSendOtp(final String adhaarNo) {
        AppCompatButton buttonSendOTPBottomSheet;
        final AppCompatCheckBox checkBoxConditionBottomsheet;
        if (bottomSheetDialogSendOtp == null) {
            bottomSheetDialogSendOtp = new BottomSheetDialog(getActivity());
        }

        bottomSheetDialogSendOtp.setContentView(R.layout.bottomsheet_to_send_otp);
        buttonSendOTPBottomSheet = (AppCompatButton) bottomSheetDialogSendOtp.findViewById(R.id.buttonSendOTPBottomSheet);
        checkBoxConditionBottomsheet = (AppCompatCheckBox) bottomSheetDialogSendOtp.findViewById(R.id.checkBoxConditionBottomsheet);

        buttonSendOTPBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isNetworkAvailable(getActivity())) {
                    if (checkBoxConditionBottomsheet.isChecked()) {
//
                        sendOtpApiCall(adhaarNo, checkBoxConditionBottomsheet.isChecked());
                    } else {
                        Toast.makeText(getActivity(), "Please Accept the Terms and condition", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomSheetDialogSendOtp.show();
    }

    private void veriFyOtpApiCall(String otp, String transactionId) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Verifying OTP, Please wait...");
            progressDialog.show();

            Log.i(TAG, "veriFyOtpApiCall: utoken" + Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));

            JSONObject reqObject = new JSONObject();
            reqObject.put("otp", otp);
            reqObject.put("transaction_id", transactionId);
            reqObject.put("ltoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));

            String urlToVerifyOTP = "http://testapi-newarch.rubique.com/Perfios/otpverification";

            Helper.showLog(urlToVerifyOTP, reqObject.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, /*URL.OTP_VERIFICATION*/urlToVerifyOTP, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    progressDialog.dismiss();

                    if (response.optInt("status_code") == 2000) {
                        JSONObject body = response.optJSONObject("body");
                        if (body.optString("message").equalsIgnoreCase("OTP_REQUEST_SENT")) {
                            isOtpVerified = true;
                            Toast.makeText(getActivity(), " OTP Varified", Toast.LENGTH_SHORT).show();
                            if (edittextAdharCard != null) {
                                edittextAdharCard.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_marked_circle_outline, 0);
//                               edittextAdharCard.setCompoundDrawables(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.tick), null);
                            }
                            if (bottomSheetDialogVeryfyOTP != null) {
                                bottomSheetDialogVeryfyOTP.dismiss();

                                if (editTextOTPbottomSheet != null)
                                    Helper.hideKeyboard(getActivity());
                            }
                        } else {
                            Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error in Validating OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.i(TAG, "onErrorResponse: " + error.getMessage());
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1F));
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendOtpApiCall(String adhaarNo, boolean conscent) {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Sending OTP, Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("aadhaar_no", adhaarNo);
            reqObject.put("consent", conscent);
            reqObject.put("ltoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            Log.i(TAG, "sendOtpApiCall: utoken " + Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));

            String urlToSendOTP = "http://testapi-newarch.rubique.com/Perfios/startcall";

            Helper.showLog(urlToSendOTP, reqObject.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, /*URL.VERIFY_ADHAR*/urlToSendOTP, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    // {"status_code":2000,"msg":"Success","body":{"message":"OTP_REQUEST_SENT","success":true,"transaction_id":"PIGP5AHL9JDDPZMLGXKVQ"}}
                    progressDialog.dismiss();
                    JSONObject body = response.optJSONObject("body");
                    if (body.optBoolean("success")) {
                        otpTransactionId = body.optString("transaction_id");
                        if (bottomSheetDialogSendOtp != null)
                            bottomSheetDialogSendOtp.dismiss();

                        showBottomSheetToVerifyOtp();
                    } else {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error ", Toast.LENGTH_SHORT).show();
                }
            });

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void askAudioPerission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.RECORD_AUDIO
            }, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            if (speechToTextDialog == null)
                speechToTextDialog = new SpeechToTextDialog();

            speechToTextDialog.setData(etGroupList, fieldUiId);
            speechToTextDialog.show(getFragmentManager(), "speechToTextDialog");
        }
    }

    private void askExternalStoragePerission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, true);
            //intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());
            startActivityForResult(intent, GOOGLE_VISION_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    if (speechToTextDialog == null)
                        speechToTextDialog = new SpeechToTextDialog();

                    speechToTextDialog.setData(etGroupList, fieldUiId);
                    speechToTextDialog.show(getChildFragmentManager(), "speechToTextDialog");

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Audio Permission Required");
                    alertDialog.setMessage("We need those permission. We will not store your personal data on our servers.");
                    alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            askAudioPerission();
                        }
                    });
                    alertDialog.show();
                }
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        ) {
                    Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
                    intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                    //intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());
                    startActivityForResult(intent, GOOGLE_VISION_CAPTURE);
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Audio Permission Required");
                    alertDialog.setMessage("We need those permission. We will not store your personal data on our servers.");
                    alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            askExternalStoragePerission();
                        }
                    });
                    alertDialog.show();
                }
                break;
        }
    }

    public boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
