package com.bestdealfinance.bdfpartner.adapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.APIUtils;
import com.bestdealfinance.bdfpartner.activity.Congrats_App;
import com.bestdealfinance.bdfpartner.activity.Product_Landing;
import com.bestdealfinance.bdfpartner.activity.QuotesGroupActivity;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.FeedItemLoan;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

/**
 * Created with Love by devesh on 21/10/15.
 */
public class LoanOfferAdapter extends RecyclerView.Adapter<LoanOfferAdapter.LoanOfferViewHolder> {
    private List<FeedItemLoan> feedItemList;
    private Context mContext;
    private String loan_type;
    private String application_id;
    private int payout;
    private Bundle apply;
    private Bundle bundle;
    private FeedItemLoan selected_item;


    public LoanOfferAdapter(Context context, List<FeedItemLoan> feedItemList, String loan_type , Bundle referBundle) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.loan_type = loan_type;
        this.bundle=referBundle;
    }


    @Override
    public LoanOfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loan_offers_row,viewGroup,false);
        LoanOfferViewHolder viewHolder = new LoanOfferViewHolder(view);

        return viewHolder;
    }


    private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logs.LogD("Recieved","Braodcast");
            try {
                JSONObject jsonObject=new JSONObject();
                JSONObject lead=new JSONObject();
                JSONObject notes=new JSONObject();
                JSONObject zer=new JSONObject();
                zer.put("id","");
                zer.put("note_type","user");
                zer.put("note","");
                notes.put("0",zer);


                JSONObject lead_info=new JSONObject();
                JSONObject lead_fields=new JSONObject();
                lead_info.put("name",bundle.getString("name"));
                lead_info.put("email",bundle.getString("email"));
                lead_info.put("phone",bundle.getString("phone"));
                lead_info.put("loan_amount_needed",selected_item.getEligible_amount());
                lead_info.put("product_type_sought",bundle.getString("type"));
                lead_info.put("product_id",selected_item.getProduct_id());
                lead.put("lead_info", lead_info);
                lead.put("lead_fields",getLeadFields(loan_type,selected_item));
                lead.put("notes",notes);

                jsonObject.put("lead", lead);


                Logs.LogD("Request", jsonObject.toString());
                ApplyLoan loan=new ApplyLoan(jsonObject);
                loan.executeOnExecutor(Util.threadPool);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    @Override
    public void onBindViewHolder(final LoanOfferViewHolder customViewHolder, int i) {
        FeedItemLoan feedItem = feedItemList.get(i);
        //Download image using picasso library
//        Picasso.with(mContext).load(feedItem.getBank_logo())
//                .error(R.drawable.cc)
//                .placeholder(R.drawable.cc)
//                .into(customViewHolder.imageView);
        Glide.with(mContext).load(feedItem.getBank_logo())
                .error(R.drawable.cc)
                .placeholder(R.drawable.cc)
                .centerCrop()
                .into(customViewHolder.imageView);

        payout=200;
        //Setting text view title
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoanOfferViewHolder holder = (LoanOfferViewHolder) view.getTag();
                int position = holder.getAdapterPosition();
                Logs.LogD("Position", " +" + position);
                FeedItemLoan feedItem = feedItemList.get(position);
                switch (view.getId()) {
                    case R.id.details:
                        Bundle bundle1=new Bundle();
                        bundle1.putString("type",loan_type);
                        bundle1.putString("id",feedItem.getProduct_id());
                        bundle1.putBoolean("apply",false);
                        Logs.LogD("Bundle",bundle1.toString());
                        mContext.startActivity(new Intent(mContext, Product_Landing.class).putExtras(bundle1));
                        break;
                        //Launch New Activity
                    case R.id.check_eligible:
                        //Launch New Activity
                        selected_item=feedItem;
                        Intent myIntent =  new Intent(mContext,QuotesGroupActivity.class);
                        Bundle bc=bundle;;
//                        bc.putString("name",bundle.getString("name"));
//                        bc.putString("phone",bundle.getString("semail"));
//                        bc.putString("type",loan_type);
//                        bc.putString("tname",bundle.getString(loan_type));
                        bc.putString("product_id",selected_item.getProduct_id());
                        bc.putString("product_name",selected_item.getTitle_bank());
                        bc.putString("bank_logo",selected_item.getBank_logo());
                        bc.putString("roi",selected_item.getRoi());
                        bc.putString("finbank",selected_item.getFinbank());
                        bc.putString("amount",bundle.getString("amount"));
                        bc.putString("lead_id",bundle.getString("lead_id"));
                        bc.putString("tenure",bundle.getString("tenure"));
                        bc.putString("emi",feedItem.getEmi());

                        bc.putBoolean("fill", false);
                        myIntent.putExtras(bc);
                        Logs.LogD("Bundle", bc.toString());
                        mContext.startActivity(myIntent);
                        Activity activity= (Activity) mContext;
                        activity.finish();
                        return;
                }
            }
        };
        try {
            Float amount= Float.valueOf(bundle.getString("amount","0"));
            float ir= Float.valueOf(feedItem.getRoi());
            int tenure= Integer.valueOf(bundle.getString("tenure", "1"));
            String emi=Util.parseRs(String.valueOf(calPMT(Math.round( amount), ir, tenure)));
                    customViewHolder.emi.setText(emi);
            feedItem.setEmi(emi);
        }
        catch (Exception e){
            Logs.LogD("Exception",e.getLocalizedMessage());
        }
        customViewHolder.title.setText(Html.fromHtml(feedItem.getTitle_bank()));
//        customViewHolder.details.setText(feedItem.getDetails());
        try {
            Double pfees=Double.valueOf(feedItem.getPf());
            double roundOff1 = Math.round(pfees*100)/100;
            customViewHolder.p_fees.setText(""+roundOff1);
        }
        catch (Exception e){
            customViewHolder.p_fees.setText(feedItem.getPf());
        }

//        customViewHolder.p_fees.setText(feedItem.getPf());
        customViewHolder.tenure.setText(bundle.getString("tenure", "0"));
        Double roi=Double.valueOf(feedItem.getRoi()) * 100 ;
        double roundOff = Math.round(roi*100)/100;
        customViewHolder.roi.setText(roundOff+"%");
        customViewHolder.amount.setText(Util.parseRs(bundle.getString("amount", "0")));
//        customViewHolder.emi.setText(Util.parseRs(feedItem.getEmi()));
        customViewHolder.check_eligible.setOnClickListener(clickListener);
        customViewHolder.its.setOnClickListener(clickListener);
        customViewHolder.details.setOnClickListener(clickListener);
        customViewHolder.check_eligible.setTag(customViewHolder);
        customViewHolder.details.setTag(customViewHolder);
        customViewHolder.its.setTag(customViewHolder);

    }
    private int calPMT(int amount, Float ir, int tenure){
        double v = (1+(ir/12)); double t = (-(tenure/12)*12);
        double result=(amount*(ir/12))/(1-Math.pow(v,t));
        return (int)result;
    }
    private void sendAnalytics_value(int amount) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
        Tracker tracker = analytics.newTracker("UA-63900664-1");
        // Send hits to tracker id UA-XXXX-Y
        // All subsequent hits will be send with screen name = "main screen"
        Logs.LogD("Refer Now","Not Valid Loan Value");
        Logs.LogD("Product",loan_type);
        tracker.setScreenName("Refer Screen");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(loan_type)
                .setAction("click")
                .setLabel("Applied")
                .setValue(amount)
                .build());
    }


    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class LoanOfferViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView title;
        protected Button check_eligible;
        protected CardView its;
        protected TextView details;
        protected TextView amount;
        protected TextView roi;
        protected TextView p_fees;
        protected TextView emi;
        protected TextView ltv;
        protected TextView tenure;
        protected TextView payout;
        protected ProgressBar bar;


        public LoanOfferViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.title = (TextView) view.findViewById(R.id.title);
//            this.ltv = (TextView) view.findViewById(R.id.ltv);
            this.tenure = (TextView) view.findViewById(R.id.tenure);
            this.check_eligible = (Button) view.findViewById(R.id.check_eligible);

            this.its = (CardView) view.findViewById(R.id.offer);
            this.details = (TextView) view.findViewById(R.id.details);
            this.emi = (TextView) view.findViewById(R.id.emi);
            this.amount = (TextView) view.findViewById(R.id.amount_eligible);
            this.roi = (TextView) view.findViewById(R.id.interest);
            this.p_fees = (TextView) view.findViewById(R.id.p_fees);
            this.bar= (ProgressBar) view.findViewById(R.id.progressBar);
//            this.payout = (TextView) view.findViewById(R.id.payout);
        }
    }
    private class ApplyLoan extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;
        public ApplyLoan(JSONObject jsonObject) {
            this.jsonObject=jsonObject;
        }
        @Override
        protected void onPreExecute() {


        }


        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                String url=Util.APPLICATION_SUBMIT;
                HttpPost httpPost = new HttpPost(url);

                String json = "";
                json = new String(jsonObject.toString().getBytes("ISO-8859-1"), "UTF-8");

                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

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
        private void sendAnalytics_value() {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
            Tracker tracker = analytics.newTracker("UA-63900664-1");
            // Send hits to tracker id UA-XXXX-Y
            // All subsequent hits will be send with screen name = "main screen"
            if (!loan_type.equals("11")){
                Logs.LogD("Loan Amount",apply.getString("eligible_loan_amount"));
                Logs.LogD("Product", loan_type);
                tracker.setScreenName("Refer Screen");
                try {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory(loan_type)
                            .setAction("click")
                            .setLabel("Applied")
                            .setValue(Long.valueOf(apply.getString("eligible_loan_amount")))
                            .build());
                }
                catch (NullPointerException ne){
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory(loan_type)
                            .setAction("click")
                            .setLabel("Applied")
                            .setValue(Long.valueOf("1000"))
                            .build());
                }

            }
            else {
                Logs.LogD("Refer Now","Not Valid Loan Value");
                Logs.LogD("Product",loan_type);
                tracker.setScreenName("Refer Screen");
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(loan_type)
                        .setAction("click")
                        .setLabel("Applied")
                        .setValue(Long.valueOf("1000"))
                        .build());
            }
        }
        protected void onPostExecute(String result) {
            Logs.LogD("Result", result);
            try {
                JSONObject rsu=new JSONObject(result);
                String status_code =rsu.optString("status_code", "");
                if (status_code.equals("2000")){
                    JSONObject body= rsu.getJSONObject("body");
                    JSONObject info=body.getJSONObject("lead_info");
                    String app_id=info.optString("id", "");
                    String name=info.optString("name","");
                    String tname=bundle.getString("tname");
                    //Launch Congrats.
                    try {
                        APIUtils.SendSMS smsuser= new APIUtils.SendSMS("5",app_id,name,mContext, bundle.getString("phone"),tname);
                        smsuser.executeOnExecutor(Util.threadPool);
                        APIUtils.SendSMS smsba= new APIUtils.SendSMS("3",app_id,name,mContext, bundle.getString("semail"),tname);
                        smsba.executeOnExecutor(Util.threadPool);
                    }
                    catch (Exception e){
                        //DO Nothing Failed to Send the SMS
                    }
                    Intent intent= new Intent(mContext, Congrats_App.class);
                    Bundle app=new Bundle();
                    app.putString("type",loan_type);
                    app.putString("id",app_id);
                    app.putString("name",name);
                    app.putString("tname",tname);
                    intent.putExtras(app);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //sendAnalytics_value();


        }
    }
    private JSONObject getLeadFields(String type,FeedItemLoan feedItem)

    {
        JSONObject lead_fields=new JSONObject();
        JSONObject loan_amount=new JSONObject();
        JSONObject roi=new JSONObject();
        JSONObject emi=new JSONObject();
        JSONObject tenure=new JSONObject();
        JSONObject pf=new JSONObject();
        JSONObject ltv=new JSONObject();
        try {
            switch (type){
                case "25":
                    loan_amount.put("field_meta_id","2");
                    loan_amount.put("field_value",feedItem.getEligible_amount());
                    roi.put("field_meta_id","4");
                    roi.put("field_value",feedItem.getInterest_rate());
                    emi.put("field_meta_id","7");
                    emi.put("field_value", feedItem.getEmi());
                    tenure.put("field_meta_id","6");
                    tenure.put("field_value", feedItem.getTenure());
                    pf.put("field_meta_id","5");
                    pf.put("field_value", feedItem.getPf());
                    lead_fields
                            .put("0",loan_amount)
                            .put("1",roi)
                            .put("2",emi)
                            .put("3",tenure)
                            .put("4", pf);
                    break;
                case "26":
                    loan_amount.put("field_meta_id","114");
                    loan_amount.put("field_value",feedItem.getEligible_amount());
                    roi.put("field_meta_id","115");
                    roi.put("field_value",feedItem.getInterest_rate());
                    emi.put("field_meta_id","118");
                    emi.put("field_value", feedItem.getEmi());
                    tenure.put("field_meta_id","117");
                    tenure.put("field_value", feedItem.getTenure());
                    pf.put("field_meta_id","116");
                    pf.put("field_value", feedItem.getPf());
                    ltv.put("field_meta_id","217");
                    ltv.put("field_value", feedItem.getLtv());
                    lead_fields
                            .put("0",loan_amount)
                            .put("1",roi)
                            .put("2",emi)
                            .put("3",tenure)
                            .put("4", pf)
                            .put("5",ltv);
                    break;
                case "28":
                    loan_amount.put("field_meta_id","339");
                    loan_amount.put("field_value",feedItem.getEligible_amount());
                    roi.put("field_meta_id","340");
                    roi.put("field_value",feedItem.getInterest_rate());
                    emi.put("field_meta_id","343");
                    emi.put("field_value", feedItem.getEmi());
                    tenure.put("field_meta_id","342");
                    tenure.put("field_value", feedItem.getTenure());
                    pf.put("field_meta_id","341");
                    pf.put("field_value", feedItem.getPf());
                    ltv.put("field_meta_id","458");
                    ltv.put("field_value", feedItem.getLtv());
                    lead_fields
                            .put("0",loan_amount)
                            .put("1",roi)
                            .put("2",emi)
                            .put("3",tenure)
                            .put("4", pf)
                            .put("5",ltv);
                    break;
                case "22":
                    loan_amount.put("field_meta_id","466");
                    loan_amount.put("field_value",feedItem.getEligible_amount());
                    roi.put("field_meta_id","467");
                    roi.put("field_value",feedItem.getInterest_rate());
                    emi.put("field_meta_id","470");
                    emi.put("field_value", feedItem.getEmi());
                    tenure.put("field_meta_id","469");
                    tenure.put("field_value", feedItem.getTenure());
                    pf.put("field_meta_id","468");
                    pf.put("field_value", feedItem.getPf());
                    ltv.put("field_meta_id","572");
                    ltv.put("field_value", feedItem.getLtv());
                    lead_fields
                            .put("0",loan_amount)
                            .put("1",roi)
                            .put("2",emi)
                            .put("3",tenure)
                            .put("4", pf)
                            .put("5",ltv);
                    break;
                case "39":
                    loan_amount.put("field_meta_id","226");
                    loan_amount.put("field_value",feedItem.getEligible_amount());
                    roi.put("field_meta_id","227");
                    roi.put("field_value",feedItem.getInterest_rate());
                    emi.put("field_meta_id","230");
                    emi.put("field_value", feedItem.getEmi());
                    tenure.put("field_meta_id","229");
                    tenure.put("field_value", feedItem.getTenure());
                    pf.put("field_meta_id","228");
                    pf.put("field_value", feedItem.getPf());
                    lead_fields
                            .put("0",loan_amount)
                            .put("1",roi)
                            .put("2",emi)
                            .put("3",tenure)
                            .put("4", pf);
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lead_fields;

    }

}
