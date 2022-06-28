package sg.com.argus.www.conquestgroup.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.models.ObjectBox;

public class SplashActivity extends AppCompatActivity {

    String refreshedToken,unique_id;
    String defaultid = "6cfdc54d9a06a128";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectBox.init(this);
        setContentView(R.layout.activity_splash);

        // Getting the Android Unique ID

        unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("uniqid","uniqid===>"+unique_id);
        //aae3ec516fbf30cb
        StartApp();


    }
         /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */

    private void StartApp() {
        new Handler().postDelayed(new Runnable() {


        /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */

            @Override
            public void run() {
                try {
                    if(unique_id.equals(defaultid)){

                       show_selection();

                    }else{
                       //show_exitAlert();
                        show_selection();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    void show_selection() {
        LayoutInflater layoutInflater = LayoutInflater.from(SplashActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_selection, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SplashActivity.this);

        final Button no = (Button) promptView
                .findViewById(R.id.exit_no_btn);

        final Button yes = (Button) promptView
                .findViewById(R.id.exit_yes_btn);

        final RadioGroup group=(RadioGroup)promptView.findViewById(R.id.radioGroup);



        alertDialogBuilder.setView(promptView).setCancelable(true);

        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cancel dialog
                alert.cancel();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId=group.getCheckedRadioButtonId();
                final RadioButton radioButton=(RadioButton)promptView.findViewById(selectedId);
                if (radioButton.getText().equals("Weighing Scale")){
                    runLoginActivity();
                    alert.cancel();
                }
                if(radioButton.getText().equals("Summary Print")){
                    Intent i = new Intent(SplashActivity.this, SummaryActivity.class);
                    i.putExtra("screen","2");
                    startActivity(i);
                    finish();

                    alert.cancel();
                }

                if(radioButton.getText().equals("Old Prints")){
                    Intent i =  new Intent(SplashActivity.this,PrintSlipsListActivity.class);
                    startActivity(i);
                    finish();
                    alert.cancel();
                }

            }
        });

    }
    private void runLoginActivity(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        i.putExtra("screen","1");
        startActivity(i);
        finish();
    }





}
