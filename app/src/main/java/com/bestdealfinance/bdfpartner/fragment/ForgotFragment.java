package com.bestdealfinance.bdfpartner.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.LoginRegSinglePage;
import com.bestdealfinance.bdfpartner.application.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class ForgotFragment extends Fragment implements View.OnClickListener {
    EditText txt_forget_email, et_forget_email, et_email_otp, et_new_password;
    Button btn_forget_submit, btn_set_submit;
    TextView txt_password_bottom_text;
    LinearLayout waiting_layout, ll_forget_password, ll_set_password;
    ImageView progressBar, back_arrow;
    String str_forget_password, str_email_otp, str_new_password;
    private AnimationDrawable animation;

    public ForgotFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=null ; //= inflater.inflate(R.layout.fragment_forgot, container, false);
        initialization(view);

        btn_forget_submit.setOnClickListener(this);
        btn_set_submit.setOnClickListener(this);
        txt_password_bottom_text.setOnClickListener(this);
        back_arrow.setOnClickListener(this);



        return view;
    }
    private void initialization(View view) {
//        waiting_layout = (LinearLayout)view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView)view.findViewById(R.id.login_progress);
        back_arrow= (ImageView) view.findViewById(R.id.back_arrow);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        txt_forget_email = (EditText)view.findViewById(R.id.txt_forget_email);
        btn_forget_submit = (Button)view.findViewById(R.id.btn_forget_submit);
        txt_password_bottom_text = (TextView)view.findViewById(R.id.txt_password_bottom_text);
        ll_forget_password = (LinearLayout)view.findViewById(R.id.ll_forget_password);
        ll_set_password = (LinearLayout)view.findViewById(R.id.ll_set_password);
        et_forget_email = (EditText)view.findViewById(R.id.et_forget_email);
        et_email_otp = (EditText)view.findViewById(R.id.et_email_otp);
        et_new_password = (EditText)view.findViewById(R.id.et_new_password);
        btn_set_submit = (Button)view.findViewById(R.id.btn_set_submit);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_forget_submit:
                if (txt_forget_email.getText().toString().trim().isEmpty()){
                    txt_forget_email.setError("Please enter valid register email-id.");
                    txt_forget_email.requestFocus();
                } else {
                    if(Patterns.EMAIL_ADDRESS.matcher(txt_forget_email.getText().toString().trim()).matches()) {
                        str_forget_password = txt_forget_email.getText().toString().trim();
                        new HttpAsyncTask().execute();
                    } else {
                        txt_forget_email.setError("Please enter valid Email-ID");
                        txt_forget_email.requestFocus();
                    }
                }
                break;

            case R.id.btn_set_submit:
                if(!et_forget_email.getText().toString().isEmpty()){
                    if(et_email_otp.getText().toString().trim().isEmpty()){
                        et_email_otp.setError("Please enter otp which is sent to Email");
                        et_email_otp.requestFocus();
                    } else {
                        if(et_new_password.getText().toString().trim().isEmpty()){
                            et_new_password.setError("Please enter new password");
                            et_new_password.requestFocus();
                        } else {
                            str_email_otp = et_email_otp.getText().toString().trim();
                            str_new_password = et_new_password.getText().toString().trim();
                            new ResetPasswordAPI().execute();
                        }
                    }
                }
                break;
            case R.id.back_arrow:
                getActivity().finish();
                break;
        }
    }
    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            animation.start();
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.FORGOT_PASSWORD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", str_forget_password);

                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else {
                    result = "Did not work!";
                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            animation.stop();

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {
                        ll_forget_password.setVisibility(View.GONE);
                        ll_set_password.setVisibility(View.VISIBLE);
                        et_forget_email.setText(str_forget_password);
                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Message");
                            builder.setMessage(output1.getString("body"));
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    }
                }
            } catch (JSONException e) {

            }

        }
    }

    private class ResetPasswordAPI extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            animation.start();
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.RESET_PASSWORD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", str_forget_password);
                jsonObject.accumulate("otp", str_email_otp);
                jsonObject.accumulate("password", str_new_password);

                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else {
                    result = "Did not work!";
                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            animation.stop();
            String status, msg;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Message");
                        builder.setMessage("Password successfully updated.");
//                          builder.setIcon(R.drawable.net_conn);
                        builder.setNeutralButton("Login Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Fragment fragment = new LoginFragment();
                                ((LoginRegSinglePage) getActivity()).replaceFragment_NOHISTORY(fragment);
                            }
                        });
                        builder.show();
                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Message");
                            builder.setMessage(output1.getString("body"));
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    }
                }
            } catch (JSONException e) {

            }

        }
    }
}
