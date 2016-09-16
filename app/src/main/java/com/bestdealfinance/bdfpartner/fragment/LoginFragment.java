package com.bestdealfinance.bdfpartner.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
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



public class LoginFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Bundle bundle;
    TextView forgot_password;
    TextView txt_login_register;
    private EditText txt_login_email, txt_login_password;
    Button btn_login_signin, btn_register;
    LinearLayout waiting_layout;
    ImageView progressBar, img_back_login;//, img_login_header;
    private Context mContext;
    private AnimationDrawable animation;
    String str_name, str_password, str_utoken, ref_val;
    private OnFragmentInteractionListener mListener;
    public LoginFragment() {
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
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        bundle=getArguments();
        initialization(view);


        btn_login_signin.setOnClickListener(this);
        img_back_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        return view; // This need to be return.
    }
    private void initialization(View view) {
        waiting_layout = (LinearLayout)view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView)view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        img_back_login = (ImageView)view.findViewById(R.id.back_arrow);
//        img_login_header = (ImageView)findViewById(R.id.img_login_header);
//        Glide.with(LoginActivity.this).load(R.drawable.login_now).into(img_login_header);
        txt_login_email = (EditText)view.findViewById(R.id.txt_login_email);
        txt_login_email.setFocusable(false);
        txt_login_email.setClickable(false);
        txt_login_password = (EditText)view.findViewById(R.id.txt_login_password);
        txt_login_password.setTransformationMethod(new PasswordTransformationMethod());
        btn_login_signin = (Button)view.findViewById(R.id.btn_login_signin);
        txt_login_register = (TextView)view.findViewById(R.id.txt_login_register);
        txt_login_email.setText(bundle.getString("semail"));
        btn_register= (Button) view.findViewById(R.id.btn_login_register);
        forgot_password= (TextView) view.findViewById(R.id.btn_forgot_password);
        forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgot_password.setOnClickListener(this);
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
            case R.id.btn_login_signin:
                if(txt_login_email.getText().toString().trim().isEmpty()){
                    txt_login_email.setError("Please enter valid Email-ID.");
                    txt_login_email.requestFocus();
                    } else {
                    str_name = txt_login_email.getText().toString().trim();
                    if(Patterns.EMAIL_ADDRESS.matcher(txt_login_email.getText().toString().trim()).matches()) {
                        if(txt_login_password.getText().toString().trim().isEmpty()){
                            txt_login_password.setError("Please enter authenticate password.");
                            txt_login_password.requestFocus();
                        } else {
                            str_password = txt_login_password.getText().toString().trim();
                            new HttpAsyncTask().execute();
                        }
                    } else {
                        txt_login_email.setError("Please enter valid Email-ID");
                        txt_login_email.requestFocus();
                    }
                }
                break;
            case R.id.btn_login_register:
                Logs.LogD("Register","requested");
                Fragment fragment = new RegisterFragment();
                ((LoginRegSinglePage) getActivity()).replaceFragment_NOHISTORY(fragment);
                break;
            case R.id.btn_forgot_password:
                Logs.LogD("Register","requested");
                Fragment fragment1 = new ForgotFragment();
                ((LoginRegSinglePage) getActivity()).replaceFragment_NOHISTORY(fragment1);
                break;
            case R.id.back_arrow:
                getActivity().finish();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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

                HttpPost httpPost = new HttpPost(Util.LOGIN);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", str_name);
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
                Header[] headers = httpResponse.getAllHeaders();
                for (Header header : headers) {
//                    Logs.LogD("Headers : ", header.getName() + " ,Value : " + header.getValue());
                    if (header.getName().equals("utoken")){
                        str_utoken=header.getValue();
                        Logs.LogD("utoken",str_utoken);
                    }
                }
//                Header[] cookieHeader = httpResponse.getHeaders("Set-Cookie");
//                if (cookieHeader.length > 0) {
//                    String[] str_header = cookieHeader[0].getValue().split(";");
//                    String[] str_token = str_header[0].split("=");
//                    str_utoken = str_token[1];
//                }

//                System.out.println("!!!!!!!!!!!!!!!!Disha utoken= " + httpResponse.getHeaders("utoken"));

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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Logs.LogD("Login", result.toString());
            waiting_layout.setVisibility(View.GONE);
            animation.stop();

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {
//                        if(ref_val.equals("referral")){
//                            ReferralActivity.callSubmitFunctionn();
//                        } else {
                        JSONObject data = output1.getJSONObject("body");
                        Util.o_id = data.getString("id");
                        Util.email = data.getString("email");
                        Util.mobile = data.getString("mobile_number");
                        SharedPreferences.Editor editor=getActivity().getSharedPreferences(Util.MY_PREFERENCES, 0).edit();
                        editor.putString(Util.username, Util.email);
                        editor.putString(Util.utoken, str_utoken);
                        editor.putString(Util.phone,Util.mobile);
                        editor.putString(Util.userid, data.getString("id"));
                        editor.apply();

                        //Send Signal TO activity.
                        mListener.onFragmentInteraction(true);

                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(boolean m);
    }
}
