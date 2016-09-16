
package com.bestdealfinance.bdfpartner.adapter;
import android.app.Activity;
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
import com.bestdealfinance.bdfpartner.model.FeedItemCC;
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

public class CC_offers_adapter extends RecyclerView.Adapter<CC_offers_adapter.CCOfferViewHolder> {
    private List<FeedItemCC> feedItemList;
    private Context mContext;
    private Bundle bundle;
    private String seleceted_id="";


    public CC_offers_adapter(Context context, List<FeedItemCC> feedItemList,Bundle referBundle) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.bundle=referBundle;
    }

    @Override
    public CCOfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_offers_row,viewGroup,false);
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
                        FeedItemCC selected_item=feedItem;
                        seleceted_id=feedItem.getCc_id();
                        //TODO Create the JSON
                        Intent myIntent =  new Intent(mContext,QuotesGroupActivity.class);
                        Bundle bc=bundle;
//                        bc.putString("name",bundle.getString("name"));
//                        bc.putString("phone",bundle.getString("semail"));
//                        bc.putString("type","11");
//                        bc.putString("tname",bundle.getString("11"));
                        bc.putString("product_id",selected_item.getProduct_id());
                        bc.putString("product_name",selected_item.getTitle());
                        bc.putString("bank_logo",selected_item.getThumbnail());
                        bc.putString("fees",selected_item.getFees());
                        bc.putString("finbank",selected_item.getFinbank());
                        bc.putString("lead_id", bundle.getString("lead_id"));
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
        customViewHolder.title.setText(Html.fromHtml(feedItem.getTitle()));
       // customViewHolder.details.setText(Html.fromHtml(feedItem.getFeatures(), null, new MyTagHandler()));
        customViewHolder.fees.setText("Rs. " +Util.parseRs(feedItem.getFees()));
//        customViewHolder.roi.setText(feedItem.getRoi());
        customViewHolder.category.setText(feedItem.getCategory());



        customViewHolder.check_eligible.setOnClickListener(clickListener);
        customViewHolder.its.setOnClickListener(clickListener);
        customViewHolder.details.setOnClickListener(clickListener);
        customViewHolder.check_eligible.setTag(customViewHolder);
        customViewHolder.details.setTag(customViewHolder);
        customViewHolder.its.setTag(customViewHolder);
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
        protected ProgressBar bar;


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
//            this.roi=(TextView) view.findViewById(R.id.interest);

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
                String url=Util.ROOT_URL_V2+"customer/submitCustomerAppWithFinbankDetails";
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
                Logs.LogD("Refer Now","Not Valid Loan Value");
                Logs.LogD("Product","11");
                tracker.setScreenName("Refer Screen");
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("11")
                        .setAction("click")
                        .setLabel("Applied")
                        .setValue(Long.valueOf("1000"))
                        .build());

        }

        protected void onPostExecute(String result) {
            Logs.LogD("Result", result);
            sendAnalytics_value();
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
                        APIUtils.SendSMS smsba= new APIUtils.SendSMS("3",app_id,name,mContext, bundle.getString("semail"),"Credit Card");
                        smsba.executeOnExecutor(Util.threadPool);
                        APIUtils.SendSMS smsuser= new APIUtils.SendSMS("5",app_id,name,mContext, bundle.getString("phone"),"Credit Card");
                        smsuser.executeOnExecutor(Util.threadPool);
                    }
                    catch (Exception e){
                        //DO Nothing Failed to Send the SMS
                    }

                    Intent intent= new Intent(mContext, Congrats_App.class);
                    Bundle app=new Bundle();
                    app.putString("type","11");
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


        }
    }


}
