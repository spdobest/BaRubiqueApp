package com.bestdealfinance.bdfpartner.AdapterNew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.ActivityNew.AllLeadsActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.DocumentListActivity;
import com.bestdealfinance.bdfpartner.ActivityNew.LeadDetailActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.LeadFlowActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.SelectRfcActivityNew;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AllLeadsRowAdapter extends RecyclerView.Adapter<AllLeadsRowAdapter.ViewHolder> {
    private static final String TAG = "AllLeadsRowAdapter";
    JSONObject jsonObjectItem;
    private Context context;
    private LayoutInflater inflater;
    private List<JSONObject> leadList;
    private JSONArray productListJsonArray;
    private RequestQueue jsonObjectRequest;

    public AllLeadsRowAdapter(Context context, List<JSONObject> leadList, JSONArray productListJsonArray) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.leadList = leadList;
        this.productListJsonArray = productListJsonArray;
        jsonObjectRequest = Volley.newRequestQueue(context);
    }

    @Override
    public AllLeadsRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_all_lead, parent, false);
        return new AllLeadsRowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AllLeadsRowAdapter.ViewHolder holder, final int position) {

        jsonObjectItem = leadList.get(position);


        Log.i(TAG, "onBindViewHolder: " + jsonObjectItem.toString());


        final String leadId = leadList.get(position).optString("id");
        final String stage = leadList.get(position).optString("stage");
        final int partner_org_id = leadList.get(position).optInt("partner_org_id", 0);
        holder.leadId = leadId;

        String name = leadList.get(position).optString("first_name");
        String middleName = leadList.get(position).optString("middle_name");
        String lastName = leadList.get(position).optString("last_name");

        if (middleName != null && !middleName.equals("null")) {
            name += " " + middleName;
        }
        if (lastName != null && !lastName.equals("null")) {
            name += " " + lastName;
        }

        holder.leadName.setText(name);
        holder.leadPhone.setText(leadList.get(position).optString("phone"));
        final int temp = position;
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + leadList.get(temp).optString("phone")));
                context.startActivity(Intent.createChooser(i, "Choose"));
            }
        });

        String productTypeID = leadList.get(position).optString("product_type_id");

        if (productTypeID.equals("11")) {
            holder.loanAmount.setText("---");
        } else {
            holder.loanAmount.setText(Helper.formatRs(leadList.get(position).optString("loan_amount_needed")));
        }


        holder.leadStatus.setText(leadList.get(position).optString("lead_state_original"));
        holder.creationDate.setText(Helper.timestampDate(leadList.get(position).optString("date_created")));
        holder.leadEarning.setText(context.getString(R.string.rupee_symbol) + Helper.formatRs(leadList.get(position).optString("projected_payout")));

        String leadBank = leadList.get(position).optString("product_name");

        if (leadBank != null && !leadBank.equals("null") && !leadBank.isEmpty()) {
            holder.loanBankLayout.setVisibility(View.VISIBLE);
            holder.loanBank.setText(leadBank);
        } else {
            holder.loanBankLayout.setVisibility(View.GONE);
        }


        if (productListJsonArray != null) {
            for (int i = 0; i < productListJsonArray.length(); i++) {
                if (productListJsonArray.optJSONObject(i).optString("id").equals(productTypeID)) {
                    holder.loanType.setText(productListJsonArray.optJSONObject(i).optString("name"));
                }
            }
        }

        if (position != 0) {
            if (Helper.timestampDate(leadList.get(position).optString("date_created")).equals(Helper.timestampDate(leadList.get(position - 1).optString("date_created")))) {
                holder.creationDate.setVisibility(View.INVISIBLE);
                holder.leadArrow.setVisibility(View.INVISIBLE);
            } else {
                holder.creationDate.setVisibility(View.VISIBLE);
                holder.leadArrow.setVisibility(View.VISIBLE);
            }
        } else {
            holder.creationDate.setVisibility(View.VISIBLE);
            holder.leadArrow.setVisibility(View.VISIBLE);
        }

        holder.seeLeadDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(context, LeadDetailActivityNew.class);
                Log.i(TAG, "onResponse : lead : " + jsonObjectItem.toString());
               /*   newIntent.putExtra("leadId",jsonObjectItem.optString("id"));
                newIntent.putExtra("stage",jsonObjectItem.optInt("stage"));
                newIntent.putExtra("partner_org_id",jsonObjectItem.optString("partner_org_id","0"));
                newIntent.putExtra("loan_amount_needed",jsonObjectItem.optString("loan_amount_needed"));
                newIntent.putExtra("date_created",jsonObjectItem.optString("date_created"));
                newIntent.putExtra("product_type_id",jsonObjectItem.optString("product_type_id"));
                newIntent.putExtra("first_name",jsonObjectItem.optString("first_name"));
                newIntent.putExtra("lead_state",jsonObjectItem.optString("lead_state"));
                context.startActivity(newIntent);*/
                Bundle bundle = new Bundle();
                newIntent.putExtra("lead_json", leadList.get(position).toString());
                context.startActivity(newIntent);
                ((AppCompatActivity) context).finish();
            }
        });

        try {
            if (leadList.get(position).optString("lead_state_original").equalsIgnoreCase("INCOMPLETE")) {
                holder.nextAction.setVisibility(View.VISIBLE);
                holder.nextAction.setText("CLICK TO COMPLETE");
                final String finalName = name;
                holder.nextAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle productBundle = new Bundle();
                        productBundle.putString("incomplete", "1");
                        productBundle.putString("lead_id", leadList.get(temp).optString("id"));
                        productBundle.putString("amount", leadList.get(temp).optString("loan_amount_needed", "0"));
                        productBundle.putString("product_type_sought", leadList.get(temp).optString("product_type_id"));
                        productBundle.putString("stage", leadList.get(temp).optString("stage"));
                        productBundle.putString("name", finalName);
                        productBundle.putBoolean("fromLead", true);

                        Intent intent = new Intent(context, LeadFlowActivityNew.class);
                        intent.putExtra("bundle", productBundle);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();

                    }
                });
            } else if ((leadList.get(position).optString("lead_state_original").equalsIgnoreCase("APPLIED") || leadList.get(position).optString("lead_state_original").equals("FRESH")) && (leadList.get(position).optString("partner_org_id", "0").equalsIgnoreCase("0") || leadList.get(position).optString("partner_org_id", "0").equalsIgnoreCase("null")) && Integer.parseInt(leadList.get(position).getString("stage")) > 2) {
                // tthis is for RFC activity
                holder.nextAction.setVisibility(View.VISIBLE);
                holder.nextAction.setText("SELECT RFC");
                final String finalName = name;
                holder.nextAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle productBundle = new Bundle();
                        productBundle.putString("incomplete", "1");
                        productBundle.putString("lead_id", leadList.get(temp).optString("id"));
                        productBundle.putString("amount", leadList.get(temp).optString("loan_amount_needed", "0"));
                        productBundle.putString("product_type_sought", leadList.get(temp).optString("product_type_id"));
                        productBundle.putString("stage", leadList.get(temp).optString("stage"));
                        productBundle.putString("name", finalName);
                        productBundle.putBoolean("fromLead", true);
                        Intent intent = new Intent(context, SelectRfcActivityNew.class);
                        intent.putExtra("productBundle", productBundle);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();

                    }
                });
            } else if (leadList.get(position).optString("lead_state_original").equalsIgnoreCase("Rejected") || leadList.get(position).optString("lead_state_original").equalsIgnoreCase("Withdrawn") || leadList.get(position).optString("lead_state_original").equalsIgnoreCase("Disbursed")) {
                holder.nextAction.setVisibility(View.VISIBLE);
                holder.nextAction.setText("COPY LEAD");
                holder.nextAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Helper.showAlertDialog(context, "", "Do you want to copy lead ?", "Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getDuplicateLeadId(leadList.get(position).optString("id"), leadList.get(position).optString("product_type_id"));
                            }
                        }, "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    }
                });
            } else if (leadList.get(position).optString("lead_state_original").equals("APPLIED") && !leadList.get(position).optString("partner_org_id").equals("null") && leadList.get(position).optInt("partner_org_id", 0) > 0 && Integer.parseInt(leadList.get(position).getString("stage")) > 2) {
                // tthis is for RFC activity
                holder.nextAction.setVisibility(View.VISIBLE);
                holder.nextAction.setText("SCHEDULE MEETING");
                final String finalName = name;
                holder.nextAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle productBundle = new Bundle();
                        productBundle.putString("incomplete", "1");
                        productBundle.putString("lead_id", leadList.get(temp).optString("id"));
                        productBundle.putString("amount", leadList.get(temp).optString("loan_amount_needed", "0"));
                        productBundle.putString("product_type_sought", leadList.get(temp).optString("product_type_id"));
                        productBundle.putString("stage", leadList.get(temp).optString("stage"));
                        productBundle.putBoolean("fromLead", true);

                        try {
                            productBundle.putString("rfc_id", leadList.get(temp).getString("partner_org_id"));
                            productBundle.putString("partner_org_id", leadList.get(temp).getString("partner_org_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        productBundle.putString("name", finalName);

                        Intent intent = new Intent(context, DocumentListActivity.class);
                        intent.putExtra("productBundle", productBundle);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();

                    }
                });
            } else if (leadList.get(position).optString("lead_state_original").equals("APPLIED") && leadList.get(position).optString("partner_org_id", "0").equals("null") && Integer.parseInt(leadList.get(position).getString("stage")) > 2) {
                // tthis is for RFC activity
                holder.nextAction.setVisibility(View.VISIBLE);
                holder.nextAction.setText("SCHEDULE MEETING");
                final String finalName = name;
                holder.nextAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle productBundle = new Bundle();
                        productBundle.putString("incomplete", "1");
                        productBundle.putString("lead_id", leadList.get(temp).optString("id"));
                        productBundle.putString("amount", leadList.get(temp).optString("loan_amount_needed", "0"));
                        productBundle.putString("product_type_sought", leadList.get(temp).optString("product_type_id"));
                        productBundle.putString("stage", leadList.get(temp).optString("stage"));
                        productBundle.putBoolean("fromLead", true);

                        try {
                            productBundle.putString("rfc_id", leadList.get(temp).getString("partner_org_id"));
                            productBundle.putString("partner_org_id", leadList.get(temp).getString("partner_org_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        productBundle.putString("name", finalName);

                        Intent intent = new Intent(context, DocumentListActivity.class);
                        intent.putExtra("productBundle", productBundle);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();

                    }
                });
            } else {
                holder.nextAction.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.getMessage();
        }


    }

    @Override
    public int getItemCount() {
        return leadList.size();
    }

    private void getDuplicateLeadId(String leadId, String product_type) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("lead_id", leadId);
            reqObject.put("product_type", product_type);
            reqObject.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, context));

            Log.i(TAG, "COPY lead : URL " + URL.DUPLICATE_LEAD + "\n params " + reqObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.DUPLICATE_LEAD, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    progressDialog.dismiss();
                    try {
                        if (response.getInt("status_code") == 200) {
                            JSONObject body = response.optJSONObject("body");
                            String leadIdDuplicate = body.getString("lead_id");

                            Toast.makeText(context, "Duplicate Lead Created", Toast.LENGTH_SHORT).show();

                            context.startActivity(new Intent(context, AllLeadsActivityNew.class));
                            ((AppCompatActivity) context).finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();

                }
            }) /*{

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), OtherActivity.this);
                return super.parseNetworkResponse(response);
            }
        }*/;

            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton callButton;
        private AppCompatTextView creationDate;
        private TextView leadArrow;
        private TextView leadName;
        private TextView loanType;
        private TextView loanAmount;
        private TextView loanBank;
        private TextView leadPhone;
        private TextView nextAction;
        private TextView leadStatus;
        private TextView leadRemark;
        private TextView leadEarning;
        private AppCompatTextView seeLeadDetail;
        private String leadId;
        private View loanBankLayout;
        private CardView mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            creationDate = (AppCompatTextView) itemView.findViewById(R.id.lead_date_of_creation);
            leadArrow = (TextView) itemView.findViewById(R.id.lead_arrow);
            leadName = (TextView) itemView.findViewById(R.id.lead_name);
            loanType = (TextView) itemView.findViewById(R.id.lead_loan_type);
            loanAmount = (TextView) itemView.findViewById(R.id.lead_loan_amount);
            loanBank = (TextView) itemView.findViewById(R.id.loan_bank);

            leadPhone = (TextView) itemView.findViewById(R.id.lead_phone);
            callButton = (ImageButton) itemView.findViewById(R.id.call_button);
            nextAction = (TextView) itemView.findViewById(R.id.lead_next_action);
            leadStatus = (TextView) itemView.findViewById(R.id.lead_status);
            leadRemark = (TextView) itemView.findViewById(R.id.lead_remark_del);
            leadEarning = (TextView) itemView.findViewById(R.id.lead_earning);

            loanBankLayout = itemView.findViewById(R.id.loan_bank_layout);
            mainLayout = (CardView) itemView.findViewById(R.id.lead_main_layout);

            seeLeadDetail = (AppCompatTextView) itemView.findViewById(R.id.txt_see_lead_detail);

        }
    }
}
