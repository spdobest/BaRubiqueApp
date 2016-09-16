package com.bestdealfinance.bdfpartner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bumptech.glide.Glide;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_know_more;
    ImageView img_banner;
    Button txt_login;
    Button txt_register;
    private LinearLayout footer;
    private TextView mainMsg;
    private TextView loginMsg;
    private TextView or;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();

        setOnClickListener();

        footer.setTranslationY(1000.0F);
        footer.animate().translationY(0.0F).setDuration(400).start();
        final String[] language_code = {"en","bn","gu","hi","kn","mr","te","ta"};
        final String[] languages = {"English","Bengali","Gujrati","Hindi","Kannada","Marathi","Telugu","Tamil"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Language:-");
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Locale locale = new Locale(language_code[i]);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, null);
                dialogInterface.dismiss();

                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();



    }



    private void setOnClickListener() {
        btn_know_more.setOnClickListener(this);
        txt_login.setOnClickListener(this);
        txt_register.setOnClickListener(this);
    }

    private void initialization() {
        btn_know_more = (Button) findViewById(R.id.btn_know_more);
        txt_login = (Button) findViewById(R.id.txt_login);
        txt_register = (Button) findViewById(R.id.txt_register);
        img_banner = (ImageView) findViewById(R.id.img_banner);
        footer = (LinearLayout) findViewById(R.id.footer);

        mainMsg=(TextView)findViewById(R.id.main_msg);
        loginMsg=(TextView)findViewById(R.id.login_question);
        or=(TextView)findViewById(R.id.or);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btn_know_more:
                startActivity(new Intent(this, KnowMore.class));
                break;
            case R.id.txt_register:
                startActivity(new Intent(this, RegisterActivity.class));

        }
    }
}
