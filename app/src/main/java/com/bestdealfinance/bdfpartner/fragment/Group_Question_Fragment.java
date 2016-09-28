package com.bestdealfinance.bdfpartner.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.CustomAutoSuggest;
import com.bestdealfinance.bdfpartner.UI.CustomDateView;
import com.bestdealfinance.bdfpartner.UI.CustomDropdownModelView;
import com.bestdealfinance.bdfpartner.UI.CustomDropdownView;
import com.bestdealfinance.bdfpartner.UI.CustomDurationView;
import com.bestdealfinance.bdfpartner.UI.CustomTextView;
import com.bestdealfinance.bdfpartner.activity.QuotesGroupActivity;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.DataFieldModel;
import com.bestdealfinance.bdfpartner.model.Node;
import com.bestdealfinance.bdfpartner.model.Tree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



public class Group_Question_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnGroupInteraction mListener;

    public Group_Question_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.


     * @return A new instance of fragment Group_Question_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Group_Question_Fragment newInstance(Bundle bundle) {
        Group_Question_Fragment fragment = new Group_Question_Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private HashMap<Integer, LinearLayout> allLinearLayout = new HashMap<>();
    LinearLayout head;
    Tree root;
    TextView group_head;
    JSONArray payload;
    JSONObject payload_values;
    ArrayAdapter adapter;
    List<String> company_list;
    List<Node> allVisible = new ArrayList<>();
    String this_product,this_product_name, loan_amount_prev,this_lead, this_product_id;
    Button submit;
    Bundle intent;
    private ImageView progressBar;
    private AnimationDrawable animation;
    TextView information;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getExtras().toString();
            Bundle bundle = intent.getExtras();
            Log.d("receiver", "Got message: " + message);
            changeLayoutRuntime(bundle.getString("id"), bundle.getString("value"), bundle.getString("field",""));
        }
    };
//...

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroy();
        Logs.LogD("Fragment", "onDestroy "+this.getTag());
    }



    @Override
    public void onResume(){
        super.onResume();
        Logs.LogD("Fragment", "onResume "+this.getTag());

    }

    @Override
    public void onStart(){
        super.onStart();
        Logs.LogD("Fragment", "Onstart");
//        clearData();
    }

    private void clearData(){
        allVisible=new ArrayList<>();
        getAllVisibleLayouts(root.getRoot());
        payload_values=new JSONObject();
        for (Node root : allVisible) {
            if (root != null && root.isVisible) {
                DataFieldModel temp = (DataFieldModel) root.view;
                temp.setData("");
            }
        }
    }

    private void initialization(View view) {
        ImageView back= (ImageView) view.findViewById(R.id.back_arrow);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        progressBar = (ImageView) view.findViewById(R.id.waiting);
        assert progressBar != null;
        progressBar.setBackgroundResource(R.drawable.waiting);
        submit= (Button) view.findViewById(R.id.btn_boarding_submit);
        animation = (AnimationDrawable) progressBar.getBackground();
        information= (TextView) view.findViewById(R.id.txt_information);

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
    private void setDataAvailable(HashMap<String, String > data){
        Log.w("Fragment", "Setting Avaialble Data");
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
                Logs.LogD("Node ID",pair.getKey().toString());
                Node temp = findNodebyID((String) pair.getKey(), root.getRoot());
                if (temp != null && pair.getKey()!=null) {
                    DataFieldModel model = (DataFieldModel) temp.view;
                    model.setData(data.get(String.valueOf(pair.getKey())));
            Logs.LogD("Setting", "For ID " + String.valueOf(pair.getKey()) + " Data frm HASH " + data.get(String.valueOf(pair.getKey())));
//            Logs.LogD("Setting", "Data for " + root.id + " " + root.uiName);
                }

        }
    }
    private void getAllVisibleLayouts(Node node) {
        if (node != null && !allVisible.contains(node)) {
            if (node.isVisible) {
                allVisible.add(node);
            }
            List<Node> temp = node.getChildren();
            for (int i = 0; i < temp.size(); i++) {
                getAllVisibleLayouts(temp.get(i));
            }
        }
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

      private boolean rupeshNode(JSONObject jsonObject){
        String fieldName=jsonObject.optString("fieldName");
        if (fieldName.equals("enq_model")){
            return true;
        }
        return false;
    }


    void changeLayoutRuntime(String id, String value, String value2) {
//        Logs.LogD("Layout","Changing Layout with Vlaue "+value+" value 2 "+value2);
        if (id.equals("24")){
                Util.associativeHash.put("1100", value2);
                Node temp1 = findNodebyID("25", root.getRoot());
                if (temp1 != null) {
                    DataFieldModel view = (DataFieldModel) temp1.view;
                    if (view != null) {
                        view.setData("");
                        temp1.isVisible = true;
                    }
                }
        }
        if (id.equals("34")){
            Util.associativeHash.put("1100", value2);
//            Node parent =findNodebyID(id, root.getRoot());
//            Node temp1 = findNodebyID("35", root.getRoot());
//            if (temp1 != null) {
//                View view = temp1.view;
//                if (view != null) {
//                    temp1.isVisible = true;
//                }
//            }
        }
        if (id.equals("137")){
            Util.associativeHash.put("1101", value2);
            Node temp1 = findNodebyID("138", root.getRoot());
            if (temp1 != null) {
                View view = temp1.view;
                if (view != null) {
                    temp1.isVisible = true;
                }
            }
        }
        if (id.equals("207")){
            Util.associativeHash.put("1106", value2);
            Node temp1 = findNodebyID("208", root.getRoot());
            if (temp1 != null) {
                View view = temp1.view;
                if (view != null) {
                    temp1.isVisible = true;
                }
            }
        }
        Node currentNode = getNodebyID(id, root.getRoot());
        if(currentNode!=null) {
            if (currentNode.id.equals(id)) {
                Logs.LogD("Layout","Found the Matching ID Node");
                Logs.LogD("Found Node", currentNode.toString());
                Logs.LogD("Child size", String.valueOf(currentNode.getChildSize()));
                for (int i = 0; i < currentNode.getChildSize(); i++) {
                    Node temp = currentNode.getChildren().get(i);
                    Logs.LogD("Child Found", temp.toString());
                    String nodeVal = temp.showCondition;
                    if (nodeVal.equals(value)) {
                        //Make Only this child Visible
                        View view = temp.view;
                        try {
                            Logs.LogD("Adding Child to view", temp.uiName);
                            head.addView(view);
                            temp.isVisible = true;
                            if (temp.id.equals("35")){
                                Logs.LogD("City","FOUND Permanent City");
                                Util.associativeHash.put("1100", value2);
                            }
                        }
                        catch (IllegalStateException e){
                            Logs.LogD("Exception","Child is Alreary Present");
                            Logs.LogD("Exception",e.getLocalizedMessage());
                            head.removeView(view);
                            head.addView(view);
                            temp.isVisible=true;
                        }
                    } else {
                        //Removed all views below it
                        removeViewRecursively(temp);
                    }
                }
            }
        }
    }

    Node findNodebyName(String name,Node node){
        if (node == null) {
            Logs.LogD("NOde","Node NAME Nll");
            return null;
        }
        if (node.fieldName.equals(name)) {
            Logs.LogD("Node", "Found Node with NAME" + node.toString());
            return node;
        }
        List<Node> temp = node.getChildren();
        Node res = null;
        for (int i = 0; res == null && i < temp.size(); i++) {
            res = findNodebyName(name, temp.get(i));
        }
        return res;
    }
    Node findNodebyID(String name,Node node){
        if (node == null) {
            Logs.LogD("NOde","Node Nll");
            return null;
        }
        if (node.id.equals(name)) {
            Logs.LogD("Node", "Found Node with id " + node.toString());
            return node;
        }
        List<Node> temp = node.getChildren();
        Node res = null;
        for (int i = 0; res == null && i < temp.size(); i++) {
            res = findNodebyID(name, temp.get(i));
        }
        return res;
    }

    void removeViewRecursively(Node nodetoRemove) {
        View view = nodetoRemove.view;
        head.removeView(view);
        Logs.LogD("Removing View", nodetoRemove.uiName);
        nodetoRemove.isVisible = false;
        if (nodetoRemove.hasChild()) {
            List<Node> childs = nodetoRemove.getChildren();
            for (Node child : childs) {
                removeViewRecursively(child);
            }
        }
    }

    void finalTouch(){
        Node temp=findNodebyName("is_salaried",root.getRoot());
        if (temp!=null) {
            View view = temp.view;
            head.removeView(view);
            temp.isVisible = false;
        }
    }

    public Node getNodebyID(String id, Node node) {

        if (node == null) {
            Logs.LogD("Node","Node is Null");
            return null;
        }
        if (node.id.equals( id)) {
            Logs.LogD("Node", "Found Node with id " + node.toString());
            return node;
        }
        List<Node> temp = node.getChildren();
        Node res = null;
        for (int i = 0; res == null && i < temp.size(); i++) {
            res = getNodebyID(id, temp.get(i));
        }
        return res;
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

    private void addSingleNode(JSONObject temp, Node parent, String show) {
        try {
            List<String> fieldOption = new ArrayList<>();
            List<String> fieldValue = new ArrayList<>();
            String filledID=null;
            String baseID=temp.optString("baseId");
            Boolean mandatory = temp.optBoolean("mandatory");
            JSONObject innerfield=null;
            innerfield=temp.getJSONObject("metaBase");
            String fielddata=innerfield.optString("fieldType");

//            if (!temp.isNull("fieldValue")){
//                String filledValue=temp.optString("fieldValue");
//                Logs.LogD("ADDHASH",filledValue);
//                QuotesGroupActivity.fieldValues.put(baseID,filledValue);
//            }
            String itemid=QuotesGroupActivity.fieldItemIds.get(baseID);
            if (itemid!=null){
                 filledID=itemid;
            }
            String uiName = innerfield.getString("fieldName");
            String fieldSize=innerfield.getString("fieldSize");
            String usemap = innerfield.getString("useMap");
            String fieldType = innerfield.getString("fieldUi");
            String uiOptionList=innerfield.getString("uiOptionList");

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

            /*if (baseID.equals("25")){
                fieldType="associative-autosuggest";
            }
            if (baseID.equals("35")){
                fieldType="associative-autosuggest";
            }*/
            if (baseID.equals("138")){
                fieldType="associative-autosuggest";
            }
            if (baseID.equals("208")){
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

                    LinearLayout tempLayout = GetLayout(fieldType, baseID, uiName, url, lookup, fieldOption, fieldValue,mandatory, usemap,uiOptionList,filledID,fieldSize);
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
                LinearLayout tempLayout = GetLayout(fieldType, baseID, uiName, url, lookup, fieldOption, fieldValue,mandatory,usemap, uiOptionList,filledID,fieldSize);
                Node tempNode = new Node(show, baseID, uiName, fieldType, mandatory, parent, tempLayout, fieldOption, fieldValue,fieldname);
                parent.addChild(tempNode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private LinearLayout GetLayout(String type, String id, String uiname, String dataURL, String lookupkey, List<String> fieldOption, List<String> fieldValues,boolean mandatory, String usemap, String uioptionlist, String filledID, String size) {
        LinearLayout result = null;
        switch (type) {
            case "date-dmy":
                result = new CustomDateView(getActivity(), getFragmentManager(), uiname,mandatory, false);
                Logs.LogD("Forms", "Created Datepicker");
                break;
            case "dob":
                result = new CustomDateView(getActivity(), getFragmentManager(), uiname,mandatory, true);
                Logs.LogD("Forms", "Created Datepicker");
                break;
            case "textbox":
                result = new CustomTextView(getActivity(), id, uiname, type, mandatory, size, null);
                break;
            case "number":
                result = new CustomTextView(getActivity(), id, uiname, type,mandatory, size, null);
                break;
            case "numberwc":
                result = new CustomTextView(getActivity(), id, uiname, type,mandatory, size, null);
                break;
            case "static-dd":
                result = new CustomDropdownView(getActivity(), id, uiname, fieldOption, fieldValues,mandatory);
                break;
            case "hard-autosuggest":
                Logs.LogD("Autosuggest", dataURL + " + " + lookupkey);
                result = new CustomAutoSuggest(getActivity(), id, uiname, uioptionlist, lookupkey,mandatory, filledID, true);
                break;
            case "soft-autosuggest":
                Logs.LogD("Autosuggest", dataURL + " + " + lookupkey);
                result = new CustomAutoSuggest(getActivity(), id, uiname, uioptionlist, lookupkey,mandatory, filledID, false);
                break;
            case "associative-autosuggest":
                Logs.LogD("associative-autosuggest", dataURL + " + " + lookupkey);
                result = new CustomDropdownModelView(getActivity(), id, uiname, dataURL, lookupkey, usemap, filledID,mandatory);
                break;
            case "durationym":
                result = new CustomDurationView(getActivity(), id, uiname, type,getFragmentManager(),mandatory);
                break;
            case "durationy":
                result = new CustomTextView(getActivity(), id, uiname, "number", mandatory, "2", null);
                break;
            case "mobile":
                result = new CustomTextView(getActivity(), id, uiname, "number", mandatory, size, "mobile");
                break;
            case "email":
                result = new CustomTextView(getActivity(), id, uiname, "textbox", mandatory, size, "email");
                break;
            case "pan":
                result = new CustomTextView(getActivity(), id, uiname, "textbox", mandatory, size, "pan");
                break;
            default:
                result = new CustomTextView(getActivity(), id, uiname, type,mandatory, size, null);
                break;
        }
        return result;
    }


    public void SingleChoiceOccupation(FragmentActivity activity, String title, final TextView txtView, final String[] arrList) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        txtView.setText(arrList[whichButton]);
                    }
                }).show();
    }

    final class DownloadJSONForm extends AsyncTask<Void, Void, String> {
        private HttpURLConnection connection;
        private URL url;

        protected void onPreExecute() {
            submit.setVisibility(View.GONE);
            //TODO Show the Waiting.
            progressBar.setVisibility(View.VISIBLE);
            animation.start();
        }

        protected String doInBackground(Void... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                String abc= Util.JSON_FORM;
                abc=abc+this_product;
                Logs.LogD("URL",abc);
                URL url = new URL(abc);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setRequestMethod("GET");
                Logs.LogD("Refer", "Sent the Request");

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
                    //Logs.LogD("TAG", sb.toString());
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
            Logs.LogD("Response", response);
            return response;
        }

        protected void onPostExecute(String result) {
            animation.stop();
            progressBar.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String code = jsonObject.optString("message");
                if (code != null && code.equals("Success")) {

                    JSONArray resource = jsonObject.optJSONArray("resource");
                    if (resource != null) {
//                        for (int i=0;i<resource.length();i++){
//                            JSONObject singleGroup=resource.getJSONObject(i);
//                            JSONArray group_fields=singleGroup.optJSONArray("groupFields");
//                            if (group_fields!=null) {
//                                makeTree(group_fields, root.getRoot());
//                                createLayout(root.getRoot());
//                                finalTouch();
//                            }
//                            else {
//                                //TODO Throw Error..
//                            }
//                        }
                        JSONObject singleGroup=resource.getJSONObject(2);
                        JSONArray group_fields=singleGroup.optJSONArray("groupFields");
                        if (group_fields!=null) {
//                            root = new Tree(1000);
//                            makeTree(group_fields, root.getRoot());
//                            createLayout(root.getRoot());
//                            finalTouch();
                        }
                        else {
//                                //TODO Throw Error..
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_group__question_, container, false);
        initialization(view);
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(Util.SOME_ACTION));
        //Broadcast Finish
        head = (LinearLayout) view.findViewById(R.id.head);
        group_head= (TextView) view.findViewById(R.id.group_name);


        intent=getArguments();
        this_product=intent.getString("type", "");
        this_product_id=intent.getString("product_id","");
        this_lead=intent.getString("lead_id","9");
        group_head.setText(Html.fromHtml("<br>"+intent.getString("group_name", "Information")+"</br>"));
//        group_head.setText(intent.getString("group_name", "Information"));
        String fields=intent.getString("template");
        Logs.LogD("Template",fields);
        try {
            JSONArray group_fields=new JSONArray(fields);
            root = new Tree("assdbadfdknzxcd");
            makeTree(group_fields, root.getRoot());
            createLayout(root.getRoot());
            finalTouch();
        }
        catch (JSONException e){
            e.printStackTrace();

        }
        if (QuotesGroupActivity.fieldValues!=null){
            Logs.LogD("HASHMAP",QuotesGroupActivity.fieldValues.toString());
            clearData();
            setDataAvailable(QuotesGroupActivity.fieldValues);
        }
        loan_amount_prev=intent.getString("amount", "");
        this_product_name=intent.getString("product_name", "");
        if (this_product.equals("11")){
            information.setText(this_product_name);
        }
        else {
            information.setText(this_product_name + ", Rs. " + Util.formatRs(loan_amount_prev));
        }


        submit = (Button) view.findViewById(R.id.btn_boarding_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allVisible=new ArrayList<>();
                getAllVisibleLayouts(root.getRoot());
                payload = new JSONArray();
                payload_values=new JSONObject();
                try {
                        if (validateData(payload,payload_values)) {
                            //Launch the Task;

                            Update_Lead lead=new Update_Lead();
                            lead.executeOnExecutor(Util.threadPool);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupInteraction) {
            mListener = (OnGroupInteraction) context;
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logs.LogD("Fragment", "onDestroyView "+this.getTag());
    }



    public interface OnGroupInteraction {
        // TODO: Update argument type and name
        void onChange(JSONObject revert);
    }

    final class Update_Lead extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            //TODO Show the Waiting.
            progressBar.setVisibility(View.VISIBLE);
            animation.start();
        }

        protected String doInBackground(String... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(Util.UPDATE_INCOMPLETE_LEAD);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                if (Util.isRegistered(getActivity()).equals("")){
                    conn.addRequestProperty("Cookie", "ltoken=" + Util.ltoken);
                    Logs.LogD("Header", "Utoken not availabale, Sending ltoken= "+Util.ltoken);
                }
                else {
                    conn.addRequestProperty("Cookie", "utoken=" + Util.isRegistered(getActivity()));
                    Logs.LogD("Header", " Sending ltoken= " + Util.isRegistered(getActivity()));
                }

                JSONObject object =new JSONObject();
                object.put("tuple_list",payload);
                object.put("lead_id", this_lead);
                object.put("product_id",this_product_id);
                object.put("product_type_sought",this_product);
                Logs.LogD("Updating Lead", object.toString());
                Logs.LogD("Refer", "Sent the Request");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(object.toString());
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
        protected void onPostExecute(String result) {
            Logs.LogD("result",result.toString());
            animation.stop();
            progressBar.setVisibility(View.GONE);
            mListener.onChange(payload_values);
            Logs.LogD("ResultUpdate",result);
            try {
                JSONObject data = new JSONObject(result);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
