package com.bestdealfinance.bdfpartner.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.APIUtils;
import com.bestdealfinance.bdfpartner.activity.LoginRegSinglePage;
import com.bestdealfinance.bdfpartner.application.Util;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class RegisterFragment extends Fragment implements View.OnClickListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Bundle bundle;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    EditText txt_full_name, txt_phone_number, txt_email_adrs, txt_regi_password;
    AutoCompleteTextView txt_city_name;
    TextView /*txt_referral_code,*/ txt_regi_login;
    Button btn_regi_submit;
    String str_name, str_mobile, str_email, str_password, str_city, str_utoken;
    private AnimationDrawable animation;
    LinearLayout waiting_layout;
    ImageView progressBar, img_regi_back;//, img_register_header;
    SharedPreferences sharedpreferences;
    private View view;
    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_register, container, false);
        sharedpreferences = getActivity().getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);
        bundle=getArguments();
        Logs.LogD("Email",bundle.getString("semail"));
        initialization();

//        txt_referral_code.setOnClickListener(this);
        btn_regi_submit.setOnClickListener(this);
        img_regi_back.setOnClickListener(this);
        txt_regi_login.setOnClickListener(this);
        return view;
    }



    private void initialization() {
        txt_regi_login = (TextView)view.findViewById(R.id.txt_regi_login);
        img_regi_back = (ImageView)view.findViewById(R.id.img_regi_back);
        waiting_layout = (LinearLayout)view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView)view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
//        img_register_header = (ImageView)findViewById(R.id.img_register_header);
//        Glide.with(RegisterActivity.this).load(R.drawable.login_now).into(img_register_header);
        txt_full_name = (EditText)view.findViewById(R.id.txt_full_name);
        txt_phone_number = (EditText)view.findViewById(R.id.txt_phone_number);
        txt_email_adrs = (EditText)view.findViewById(R.id.txt_email_adrs);
        txt_regi_password = (EditText)view.findViewById(R.id.txt_regi_password);
        txt_regi_password.setTransformationMethod(new PasswordTransformationMethod());
        txt_city_name = (AutoCompleteTextView)view.findViewById(R.id.txt_city_name);
//        txt_referral_code = (TextView)findViewById(R.id.txt_referral_code);
        btn_regi_submit = (Button)view.findViewById(R.id.btn_regi_submit);

        txt_full_name.setText(bundle.getString("sname"));
        txt_phone_number.setText(bundle.getString("sphone"));
        txt_email_adrs.setText(bundle.getString("semail"));
        txt_city_name.setText(bundle.getString("scity"));
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.txt_referral_code:
//                break;
            case R.id.txt_regi_login:
                Fragment fragment = new LoginFragment();
                ((LoginRegSinglePage) getActivity()).replaceFragment_NOHISTORY(fragment);

                break;
            case R.id.img_regi_back:
                getActivity().finish();
                break;
            case R.id.btn_regi_submit:
                if (txt_full_name.getText().toString().trim().isEmpty()){
                    txt_full_name.setError("Please enter full name");
                    txt_full_name.requestFocus();
                } else {
                    str_name = txt_full_name.getText().toString().trim();
                    if (txt_phone_number.getText().toString().trim().isEmpty()){
                        txt_phone_number.setError("Please enter mobile number");
                        txt_phone_number.requestFocus();
                    } else {
                        str_mobile = txt_phone_number.getText().toString().trim();
                        if(android.util.Patterns.PHONE.matcher(txt_phone_number.getText().toString().intern()).matches() && txt_phone_number.getText().toString().length()==10){
                            if (txt_email_adrs.getText().toString().trim().isEmpty()){
                                txt_email_adrs.setError("Please enter Email-ID");
                                txt_email_adrs.requestFocus();
                            } else {
                                str_email = txt_email_adrs.getText().toString().trim();
                                if(Patterns.EMAIL_ADDRESS.matcher(txt_email_adrs.getText().toString().trim()).matches()) {
                                    if (txt_regi_password.getText().toString().trim().isEmpty()) {
                                        txt_regi_password.setError("Please enter password");
                                        txt_regi_password.requestFocus();
                                    } else {
                                        str_password = txt_regi_password.getText().toString().trim();
                                        if (txt_city_name.getText().toString().trim().isEmpty()) {
                                            txt_city_name.setError("Please enter city name");
                                            txt_city_name.requestFocus();
                                        } else {
                                            str_city = txt_city_name.getText().toString().trim();
                                            new HttpAsyncTask().execute();
                                        }
                                    }
                                } else {
                                    txt_email_adrs.setError("Please enter valid Email-ID");
                                    txt_email_adrs.requestFocus();
                                }
                            }
                        } else {
                            txt_phone_number.setError("Please enter valid mobile number");
                            txt_phone_number.requestFocus();
                        }
                    }
                }
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(boolean m);
    }

    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waiting_layout.setVisibility(View.VISIBLE);
            animation.start();
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.REGISTER_NEW);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("name", str_name);
                jsonObject.accumulate("email", str_email);
                jsonObject.accumulate("mobile_number", str_mobile);
                jsonObject.accumulate("password", str_password);
                jsonObject.accumulate("city", str_city);
                jsonObject.accumulate("utoken", "");

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

                String status, msg = null;

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                    final JSONObject output1 = new JSONObject(result);
                    if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                        status = output1.getString("status_code");
                        msg = output1.getString("msg");

                        if (msg.equals("Success")) {
//                            JSONObject data = output1.getJSONObject("msg");

                            result1 = getLogin();

                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Message");
                                    try {
                                        builder.setMessage(output1.getString("body") + "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
//                                            finish();
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    } else{
                        result1 = msg;
                    }

                }
                else
                    result1 = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                Log.d("Exception", e.toString());
            }

            // 11. return result
            return result1;
        }


        private String getLogin() {
            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.ROOT_URL_V2+"index.php/Customer/login");

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", str_email);
                jsonObject.accumulate("password", str_password);
                jsonObject.accumulate("remember", 1);
                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                Header[] cookieHeader = httpResponse.getHeaders("Set-Cookie");
                if (cookieHeader.length > 0) {
                    String[] str_header = cookieHeader[0].getValue().split(";");
                    String[] str_token = str_header[0].split("=");
                    str_utoken = str_token[1];
                }
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                }
                else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Logs.LogD("Login result", result.toString());
            waiting_layout.setVisibility(View.GONE);
            animation.stop();

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {

                        JSONObject data = output1.getJSONObject("body");
                        Util.o_id = data.getString("id");
                        Util.email = data.getString("email");
                        Util.mobile = data.getString("mobile_number");
                        try {
                            APIUtils.SendSMS sms=new APIUtils.SendSMS("1","","",getActivity(),data.getString("mobile_number"),"");
                            sms.executeOnExecutor(Util.threadPool);
                        }
                        catch (Exception e){
                            //DO Nothing
                        }


                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Util.username, Util.email);
                        editor.putString(Util.utoken, str_utoken);
                        editor.commit();
                        editor.apply();
                        //NotificationUtil.registerInBackground(Util.o_id, getActivity());
                        mListener.onFragmentInteraction(true);
                        getActivity().finish();
                    }
                }
            } catch (JSONException e){

            }

        }
    }
}
