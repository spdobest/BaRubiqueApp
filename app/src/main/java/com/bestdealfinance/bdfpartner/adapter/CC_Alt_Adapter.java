package com.bestdealfinance.bdfpartner.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.CustomAutoSuggest;
import com.bestdealfinance.bdfpartner.UI.CustomDateView;
import com.bestdealfinance.bdfpartner.UI.CustomDropdownModelView;
import com.bestdealfinance.bdfpartner.UI.CustomDropdownView;
import com.bestdealfinance.bdfpartner.UI.CustomDurationView;
import com.bestdealfinance.bdfpartner.UI.CustomTextView;
import com.bestdealfinance.bdfpartner.activity.AlternateOffersActivity;
import com.bestdealfinance.bdfpartner.activity.ApplicationCongrats;
import com.bestdealfinance.bdfpartner.activity.Product_Landing;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.DataFieldModel;
import com.bestdealfinance.bdfpartner.model.FeedItemCC;
import com.bestdealfinance.bdfpartner.model.Node;
import com.bestdealfinance.bdfpartner.model.Tree;
import com.bumptech.glide.Glide;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikas on 16/5/16.
 */
public class CC_Alt_Adapter extends RecyclerView.Adapter<CC_Alt_Adapter.CCOfferViewHolder> {
    private List<FeedItemCC> feedItemList;
    private Context mContext;
    private String seleceted_id="";
    private Bundle bundle;
    private FeedItemCC selected_item;
    List<Node> allVisible = new ArrayList<>();
    private JSONArray payload;
    private JSONObject payload_values;
    private String current_product;
    LinearLayout head;
    Tree root;
    String payout;
    static int position;
    List<Integer> invalid=new ArrayList<>();
    private String current_lead;

    public CC_Alt_Adapter(Context context, List<FeedItemCC> feedItemList,Bundle referBundle) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.bundle=referBundle;
        this.current_lead=referBundle.getString("lead_id","");
        this.current_product=referBundle.getString("product_id");
    }

    @Override
    public CCOfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_alt_offer_row,viewGroup,false);
        CCOfferViewHolder viewHolder = new CCOfferViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CCOfferViewHolder customViewHolder, int i) {
        FeedItemCC feedItem = feedItemList.get(i);
        //Download image using picasso library
        Glide.with(mContext).load(feedItem.getThumbnail())
                .error(R.drawable.bank_logo)
                .placeholder(R.drawable.cc)
                .fitCenter()
                .into(customViewHolder.imageView);

        //Setting text view title
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CCOfferViewHolder holder = (CCOfferViewHolder) view.getTag();
                int position = holder.getAdapterPosition();
                Logs.LogD("Position", " +" + position);
                FeedItemCC feedItem = feedItemList.get(position);
                switch (view.getId()){
                    case R.id.details:
                        Bundle bundle1=new Bundle();
                        bundle1.putString("type","11");
                        bundle1.putString("id",feedItem.getProduct_id());
                        bundle1.putBoolean("apply",false);
                        Logs.LogD("Bundle",bundle1.toString());
                        mContext.startActivity(new Intent(mContext, Product_Landing.class).putExtras(bundle1));
                        break;
                    case R.id.check_eligible:
                        //Launch New Activity
                        selected_item=feedItem;
                        current_product=selected_item.getProduct_id();
                        head=customViewHolder.head_layout;
                        bundle.putString("product_id",selected_item.getProduct_id());
                        bundle.putString("product_name",selected_item.getTitle());
                        bundle.putString("bank_logo",selected_item.getThumbnail());
                        if (selected_item.getFullQual().equals("true")){
                            //Fully Qualifed
                            DuplicateLead duplicateLead = new DuplicateLead(customViewHolder, true, feedItem.getFinbank());
                            duplicateLead.executeOnExecutor(Util.threadPool);
                        }
                        else {
                            DuplicateLead duplicateLead = new DuplicateLead(customViewHolder,false, feedItem.getFinbank());
                            duplicateLead.executeOnExecutor(Util.threadPool);
                        }

                        break;
                    case R.id.submit_again:
                        customViewHolder.sabkabaap.setVisibility(View.GONE);
//                        customViewHolder.submit.setVisibility(View.GONE);
                        getAllVisibleLayouts(root.getRoot());
                        payload = new JSONArray();
                        payload_values=new JSONObject();
                        try {
                            if (validateData(payload,payload_values)) {
                                //Launch the Task;
                                CallStep2 step2=new CallStep2(customViewHolder.waiting, customViewHolder,feedItem.getFinbank());
                                step2.executeOnExecutor(Util.threadPool);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        customViewHolder.title.setText(Html.fromHtml(feedItem.getTitle()));
        if (invalid.contains(i)){
            customViewHolder.its.setVisibility(View.INVISIBLE);
            customViewHolder.sorry_layout.setVisibility(View.VISIBLE);
        }
        else {
            customViewHolder.its.setVisibility(View.VISIBLE);
            customViewHolder.sabkabaap.setVisibility(View.GONE);
            customViewHolder.sorry_layout.setVisibility(View.GONE);
        }
        // customViewHolder.details.setText(Html.fromHtml(feedItem.getFeatures(), null, new MyTagHandler()));
        customViewHolder.fees.setText(Util.parseRs(feedItem.getFees()));
//        customViewHolder.roi.setText(feedItem.getRoi());
        customViewHolder.category.setText(feedItem.getCategory());
        customViewHolder.check_eligible.setOnClickListener(clickListener);
        customViewHolder.its.setOnClickListener(clickListener);
        customViewHolder.submit.setOnClickListener(clickListener);
        customViewHolder.details.setOnClickListener(clickListener);
        customViewHolder.check_eligible.setTag(customViewHolder);
        customViewHolder.details.setTag(customViewHolder);
        customViewHolder.its.setTag(customViewHolder);
        customViewHolder.submit.setTag(customViewHolder);
    }



    private class DuplicateLead extends AsyncTask<String, Void, String> {
        CCOfferViewHolder wait;
        AnimationDrawable animation;
        boolean qualified;
        String finbank;
        public DuplicateLead(CCOfferViewHolder waiting, boolean qual, String fin) {
            wait=waiting;
            qualified=qual;
            finbank=fin;
        }
        @Override
        protected void onPreExecute() {
            wait.waiting.setBackgroundResource(R.drawable.waiting);
            animation = (AnimationDrawable) wait.waiting.getBackground();
            animation.start();
            wait.waiting.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                String url=Util.DUPLICATE_LEAD;
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                JSONObject parent=new JSONObject();
                parent.put("product_id",current_product);
                parent.put("base_lead_id",current_lead);
                Logs.LogD("Payload",parent.toString());
                StringEntity se = new StringEntity(parent.toString());
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.addHeader("Accept", "application/json");
                httpPost.addHeader("Content-type", "application/json");
                httpPost.addHeader("Cookie", "utoken=" + Util.isRegistered(mContext));
                Logs.LogD("Cookie",Util.isRegistered(mContext));

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        protected void onPostExecute(String result) {
            animation.stop();
            wait.waiting.setVisibility(View.GONE);

            Logs.LogD("Result", result);
            try {

                JSONObject object = new JSONObject(result);
                String code= object.optString("status_code");
                if (!code.equals("2000")){
                    Toast.makeText(mContext, "Server Error, try again later", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject body = object.optJSONObject("body");
                if (body != null) {
                    current_lead = body.optString("lead_id", current_lead);
                    bundle.putString("lead_id",current_lead);
                    String qualified=body.optString("product_qualified","false");
                    String finbank_qualifed=body.optString("in_prin_approved","false");
                    if (Util.isQqualifed(qualified,finbank,finbank_qualifed)) {
                        Logs.LogD("Lead","Duplicate Says Qualifeid lead... Skip Template generation");
                        //TODO Launch the Other Fragment
                        Intent intent = new Intent(mContext, ApplicationCongrats.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        Activity activity = (Activity) mContext;
                        activity.finish();
                        return;
                    }

                    wait.sabkabaap.setVisibility(View.VISIBLE);
                    JSONArray finalArray = new JSONArray();
                    JSONObject tree_template = body.optJSONObject("tree_template");
                    if (tree_template!=null) {
                        JSONArray tree = tree_template.optJSONArray("tree");
                        if (tree != null) {
                            for (int i = 0; i < tree.length(); i++) {
                                JSONObject singleGroup = tree.getJSONObject(i);
                                JSONArray setitem = singleGroup.getJSONArray("groupFields");
                                for (int j = 0; j < setitem.length(); j++) {
                                    finalArray.put(setitem.optJSONObject(j));
                                }
                            }

                            root = null;
                            root = new Tree("assdbadfdknzxcd");
                            makeTree(finalArray, root.getRoot());
                            createLayout(root.getRoot());
                            root.getRoot().getChildren().get(0).view.requestFocus();
                        }
                        else {
                            invalid.add(wait.getAdapterPosition());
                            wait.its.setVisibility(View.INVISIBLE);
                            wait.sabkabaap.setVisibility(View.GONE);
                            wait.sorry_layout.setVisibility(View.VISIBLE);
                        }
                    }
                    else {

                        invalid.add(wait.getAdapterPosition());
                        wait.its.setVisibility(View.INVISIBLE);
                        wait.sabkabaap.setVisibility(View.GONE);
                        wait.sorry_layout.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(mContext,"Server Error, Please try again",Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
    public class CCOfferViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView title;
        protected Button check_eligible;
        protected CardView its;
        protected TextView details;
        protected TextView category;
        protected TextView roi;
        protected TextView fees;
        protected LinearLayout head_layout;
        protected FrameLayout sorry_layout;
        protected CardView offers;
        protected ProgressBar bar;
        protected ImageView waiting;
        protected CardView sabkabaap;
        protected Button submit;

        public CCOfferViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.title = (TextView) view.findViewById(R.id.title);
            this.check_eligible=(Button)view.findViewById(R.id.check_eligible);
            this.its= (CardView) view.findViewById(R.id.offer);
            this.details= (TextView) view.findViewById(R.id.details);
            this.fees=(TextView) view.findViewById(R.id.charges);
            this.category=(TextView) view.findViewById(R.id.card_category);
            this.bar= (ProgressBar) view.findViewById(R.id.progressBar);
            this.sorry_layout= (FrameLayout) view.findViewById(R.id.sorry);
            this.submit= (Button) view.findViewById(R.id.submit_again);
            this.head_layout= (LinearLayout) view.findViewById(R.id.head);
            this.waiting= (ImageView) view.findViewById(R.id.waiting);
            this.sabkabaap= (CardView) view.findViewById(R.id.sabkabaap);
            this.waiting.bringToFront();

//            this.roi=(TextView) view.findViewById(R.id.interest);

        }
    }

    private void getAllVisibleLayouts(Node node) {
        if (node != null && !allVisible.contains(node)) {
            allVisible.add(node);
            List<Node> temp = node.getChildren();
            for (int i = 0; i < temp.size(); i++) {
                getAllVisibleLayouts(temp.get(i));
            }
        }
    }

    private boolean validateData(JSONArray payload, JSONObject payload_values) throws JSONException {
        boolean proceed = true;
        for (Node root : allVisible) {
            if (root != null &&
                    root.isVisible) {
                DataFieldModel temp = (DataFieldModel) root.view;
                if (!temp.validate()) {
                    Logs.LogD("Error", root.uiName);
                    temp.setError();
                    proceed = false;
                    break;
                } else {
                    JSONObject tuple=new JSONObject();
                    tuple.put("base_id",String.valueOf(root.id));
                    tuple.put("field_value",temp.getDisplayData());
                    tuple.put("list_item_id",temp.getdata());
                    payload.put(tuple);
//                    payload.put(String.valueOf(root.id), temp.getdata());
                    payload_values.put(String.valueOf(root.id), temp.getDisplayData());
                }
            }
        }
        return proceed;
    }

    public class CallStep2 extends AsyncTask<String, Void, String > {
        ImageView wait;
        CCOfferViewHolder viewHolder;
        AnimationDrawable animation;
        String finbank;
        public CallStep2(ImageView waiting, CCOfferViewHolder customViewHolder, String fin) {
            this.wait=waiting;
            this.viewHolder=customViewHolder;
            this.finbank=fin;
        }
        @Override
        protected void onPreExecute() {
            viewHolder.waiting.setBackgroundResource(R.drawable.waiting);
            animation = (AnimationDrawable) viewHolder.waiting.getBackground();
            animation.start();
            viewHolder.waiting.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(Util.SUBMITAPPLICATIONBA);
                Logs.LogD("URL",url.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.addRequestProperty("Cookie", "utoken=" + Util.isRegistered(mContext));
                JSONObject data=new JSONObject();
                data.put("lead_id", current_lead);
                data.put("tuple_list",payload);
                data.put("product_id", current_product);
                Logs.LogD("Payload", data.toString());
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data.toString());
                writer.flush();
                writer.close();
                os.close();
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    response = sb.toString();
                } else {
                    Logs.LogD("Exception", conn.getResponseMessage());
                }
            } catch (Exception e) {
                Logs.LogD("Task Exception" +
                        "on", e.getLocalizedMessage());
                e.printStackTrace();
                response = e.getLocalizedMessage();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Logs.LogD("Response",result);
            wait.setVisibility(View.GONE);
            animation.stop();
            try {
                JSONObject response = new JSONObject(result);
                JSONObject body = response.optJSONObject("body");
                String qualified=body.optString("product_qualified", "false");
                String finbank_qualifed=body.optString("in_prin_approved","false");
                if (Util.isQqualifed(qualified, finbank, finbank_qualifed)) {
                    //TODO Launch the Other Fragment
                    Intent intent=new Intent(mContext, ApplicationCongrats.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    Activity activity = (Activity) mContext;
                    activity.finish();
                    return;
                }
                else {
//                    viewHolder.sabkabaap.setVisibility(View.GONE);
                    invalid.add(viewHolder.getAdapterPosition());
                    viewHolder.its.setVisibility(View.INVISIBLE);
                    viewHolder.sorry_layout.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean rupeshNode(JSONObject jsonObject){
        String fieldName=jsonObject.optString("fieldName");
        if (fieldName.equals("enq_model")){
            return true;
        }
        return false;
    }
    private void makeTree(JSONArray jsonArray, Node root) {
        for (int i = 0; i < jsonArray.length(); i++) {
            //xyz is default for show
            if (rupeshNode(jsonArray.optJSONObject(i))) {
                addSingleNode(jsonArray.optJSONObject(i), root, "abc");
            }
            else {
                addSingleNode(jsonArray.optJSONObject(i), root, "xyz");
            }
        }
    }
    private void addSingleNode(JSONObject temp, Node parent, String show) {
        try {
            List<String> fieldOption = new ArrayList<>();
            List<String> fieldValue = new ArrayList<>();
            String baseID=temp.optString("baseId");
            Boolean mandatory = temp.optBoolean("mandatory");
            JSONObject innerfield=temp.getJSONObject("metaBase");
            String fielddata=innerfield.optString("fieldType");


            String fieldSize=innerfield.optString("fieldSize");
            String uiName = innerfield.optString("fieldName");
            String usemap = innerfield.optString("useMap");
            String fieldType = innerfield.optString("fieldUi");
            String uiOptionList=innerfield.optString("uiOptionList","");
            if (fieldType.equals("alpha")){
                fieldType="textbox";
            }
            if (fieldType.equals("numeric")){
                fieldType="number";
            }
            if (fieldType.equals("iwc")){
                fieldType="numberwc";
            }
//            if (fielddata.equals("i")){
//                fieldType="number";
//            }
            if (fielddata.equals("durationym")){
                fieldType="durationym";
            }

            if (baseID.equals("25")){
                fieldType="associative-autosuggest";
            }
            String fieldname=innerfield.optString("fieldName","");
            Logs.LogD("AddingNode","ID: "+baseID+" uiname "+ uiName+" Type "+fieldType);
//            Logs.LogD("Tree", uiName);
            String url = innerfield.optString("useMap");
            String lookup = innerfield.optString("defaultValue");
            JSONObject optionsObject = innerfield.optJSONObject("listName");
            if (optionsObject!=null) {
                JSONArray optionList = optionsObject.optJSONArray("listMembers");
                if (optionList != null) {
                    for (int j = 0; j < optionList.length(); j++) {
//                        Logs.LogD("Tree", "ProductOptionsFound");

                        JSONObject optionals = optionList.getJSONObject(j);
                        JSONObject uberList = optionals.getJSONObject("uberList");
                        fieldOption.add(uberList.getString("itemValue"));
                        fieldValue.add(uberList.getString("id"));
                    }

                    LinearLayout tempLayout = GetLayout(fieldType, baseID, uiName, url, lookup, fieldOption, fieldValue,mandatory, usemap,uiOptionList,fieldSize);
                    Node tempNode = new Node(show, baseID, uiName, fieldType, mandatory, parent, tempLayout, fieldOption, fieldValue, fieldname);
                    parent.addChild(tempNode);
                    for (int j = 0; j < optionList.length(); j++) {
                        JSONObject optionals = optionList.getJSONObject(j);
                        JSONObject uberList = optionals.getJSONObject("uberList");
                        //TODO FieldValue will decide to hide or show.
                        String tempShow = uberList.getString("itemValue");
                        JSONArray optionChild = uberList.optJSONArray("childFields");
                        //TODO Child Template is Present
                        if (optionChild != null) {
                            for (int k = 0; k < optionChild.length(); k++) {
                                JSONObject childObject = optionChild.getJSONObject(k);
//                            JSONObject childTemplate = childObject.getJSONObject("childTemplate");
                                Logs.LogD("Tree", "Adding a Child");
                                addSingleNode(childObject, tempNode, tempShow);
                            }
                        }
                    }
                }
            }
            else {
                Logs.LogD("Tree", "Added without Child");
                LinearLayout tempLayout = GetLayout(fieldType, baseID, uiName, url, lookup, fieldOption, fieldValue,mandatory,usemap, uiOptionList,fieldSize);
                Node tempNode = new Node(show, baseID, uiName, fieldType, mandatory, parent, tempLayout, fieldOption, fieldValue,fieldname);
                parent.addChild(tempNode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private LinearLayout GetLayout(String type, String id, String uiname, String dataURL, String lookupkey, List<String> fieldOption, List<String> fieldValues,boolean mandatory, String usemap, String uioptionlist, String size) {
        LinearLayout result = null;
        android.support.v4.app.FragmentManager fragmentManager = ((AlternateOffersActivity) mContext).getSupportFragmentManager();
        switch (type) {
            case "date-dmy":
                result = new CustomDateView(mContext, fragmentManager, uiname,mandatory,false);
                Logs.LogD("Forms", "Created Datepicker");
                break;
            case "dob":
                result = new CustomDateView(mContext,fragmentManager, uiname,mandatory, true);
                Logs.LogD("Forms", "Created Datepicker");
                break;
            case "textbox":
                result = new CustomTextView(mContext, id, uiname, type, mandatory, size, null);
                break;
            case "number":
                result = new CustomTextView(mContext, id, uiname, type, mandatory, size, null);
                break;
            case "numberwc":
                result = new CustomTextView(mContext, id, uiname, type, mandatory, size, null);
                break;
            case "static-dd":
                result = new CustomDropdownView(mContext, id, uiname, fieldOption, fieldValues, mandatory);
                break;
            case "api-lookup":
                Logs.LogD("Autosuggest", dataURL + " + " + lookupkey);
                result = new CustomAutoSuggest(mContext, id, uiname, dataURL, lookupkey, mandatory, null, false);
                break;
            case "hard-autosuggest":
                Logs.LogD("Autosuggest", dataURL + " + " + lookupkey);
                result = new CustomAutoSuggest(mContext, id, uiname, uioptionlist, lookupkey,mandatory,null, true);
                break;
            case "soft-autosuggest":
                Logs.LogD("Autosuggest", dataURL + " + " + lookupkey);
                result = new CustomAutoSuggest(mContext, id, uiname, uioptionlist, lookupkey,mandatory, null, false);
                break;
            case "associative-autosuggest":
                Logs.LogD("associative-autosuggest", dataURL + " + " + lookupkey);
                result = new CustomDropdownModelView(mContext, id, uiname, dataURL, lookupkey, usemap, null, mandatory);
                break;
            case "durationym":
                result = new CustomDurationView(mContext, id, uiname, type, fragmentManager, mandatory);
                break;
            case "durationy":
                result = new CustomDurationView(mContext, id, uiname, type, fragmentManager, mandatory);
                break;
            case "mobile":
                result = new CustomTextView(mContext, id, uiname, "number", mandatory, size, "mobile");
                break;
            case "email":
                result = new CustomTextView(mContext, id, uiname, "textbox", mandatory, size, "email");
                break;
            case "pan":
                result = new CustomTextView(mContext, id, uiname, "textbox", mandatory, size, "pan");
                break;
            default:
                result = new CustomTextView(mContext, id, uiname, type, mandatory, size,null);
                break;
        }
        return result;
    }
    private void createLayout(Node root) {
        if (root != null) {
//            Logs.LogD("Layout", root.toString());
            //TODO Show Condition here, Only Inflate the Layouts which have showCondition as default
            if (root.showCondition.equals("xyz")) {

                head.addView(root.view);

                root.isVisible = true;
            }
//            else if (root.parent!=null
//                    && root.parent.payload.equals(root.show)){
//                //TODO Add Here.
//            }
            List<Node> temp = root.getChildren();
            for (int i = 0; i < temp.size(); i++) {
                createLayout(temp.get(i));
            }
        }
    }


}
