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

public class SplashActivity extends AppCompatActivity {

    String refreshedToken,unique_id;
    String defaultid = "6cfdc54d9a06a128";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                        runLoginActivity();

                    }else{
                       //show_exitAlert();
                        runLoginActivity();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    private void runLoginActivity(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        i.putExtra("screen","1");
        startActivity(i);
        finish();
    }


//
//   TODO: Implement show_exitAlert() when the back button is pressed Again after Login
//
    void show_exitAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(SplashActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_exit, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SplashActivity.this);

        final Button no = (Button) promptView
                .findViewById(R.id.b_exit_no);

        final Button yes = (Button) promptView
                .findViewById(R.id.b_exit_yes);

        final TextView txt = (TextView) promptView
                .findViewById(R.id.tv_exit_text);

        txt.setText("Hoo! Your Device Is Not Recognize To This App");

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
                //  getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                android.os.Process.killProcess(android.os.Process.myPid());
                // finish();

            }
        });

    }



}
