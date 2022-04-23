package sg.com.argus.www.conquestgroup.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.ConnectionDetector;
import sg.com.argus.www.conquestgroup.adapters.Stateadapter;
import sg.com.argus.www.conquestgroup.models.AppController;
import sg.com.argus.www.conquestgroup.models.Client;
import sg.com.argus.www.conquestgroup.models.HttpsTrustManager;
import sg.com.argus.www.conquestgroup.utils.DialogScreens;

public class LoginActivity extends AppCompatActivity {
    private Button login, Cancel;
    private EditText loginid, userpassword;
    private String username, password;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    //String apmcId = "49";
    String apmcId = "72";

    private Spinner spin_states;
    private ArrayList<Client> hotelCnstsesList;
    String GetLogid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cd = new ConnectionDetector(this);

        loginid = (EditText) findViewById(R.id.loginId);
        userpassword = (EditText) findViewById(R.id.passwordLogin);
        saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        login = (Button) findViewById(R.id.LogIn);
        Cancel = (Button) findViewById(R.id.Cancel);
        spin_states = (Spinner) findViewById(R.id.spin_states);
        RelativeLayout rl_spin = (RelativeLayout) findViewById(R.id.rl_spin);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        loginPreferences = getSharedPreferences("weighing_scale", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            loginid.setText(loginPreferences.getString("username", ""));
            userpassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }
        GetLogid = getIntent().getStringExtra("screen");
        Log.e("GetLogid","GetLogid"+GetLogid);

        if(GetLogid.equalsIgnoreCase("1")){

            rl_spin.setVisibility(View.GONE);
        }else if(GetLogid.equalsIgnoreCase("2")){
             GetStates("https://train.enam.gov.in/NamWebSrv/rest/MastersUpdate/getStates");
            spin_states.setVisibility(View.VISIBLE);

        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetPresent = cd.isConnectingToInternet();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginid.getWindowToken(), 0);

                username = loginid.getText().toString();
                password = userpassword.getText().toString();
               // save username and password in sharedpreferences
                if (isInternetPresent) {
                    if (saveLoginCheckBox.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("username", username);
                        loginPrefsEditor.putString("password", password);
                        loginPrefsEditor.commit();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }
                    new LoginUser().execute();
                } else {
                    showInternetAlert();
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogScreens.showExitAlert(LoginActivity.this);

            }
        });


    }


    @Override
    public void onBackPressed() {

        DialogScreens.showExitAlert(LoginActivity.this);

    }

    void showInternetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You don't have Internet Connection.")
                .setCancelable(false)
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        }).setNegativeButton("Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        startActivity(getIntent());
                    }
                });

        // Creating dialog box
        AlertDialog alert = builder.create();
        // Setting the title manually
        alert.setTitle("Internet Connection");
        alert.show();

    }

   // To call LoginUser Api
    private class LoginUser extends AsyncTask<String, Void, String> {
        String text = "";
        String uname = "", pass = "";
        ProgressDialog p = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {

            p.setMessage("Please Wait...");
            p.setCancelable(false);
            p.show();

            uname = loginid.getText().toString();
            pass = userpassword.getText().toString();

        }

        protected String doInBackground(String... params) {

            try {
                HttpsTrustManager.allowAllSSL();
                String urlParameters = "loginId=" + uname + "&password=" + pass + "&apmcId=" + apmcId;
                Log.e("params",""+urlParameters);
                byte[] postData = new byte[0];

                    postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                //URL url = new URL("https://enam.gov.in/NamWebSrv/rest/verifyUser");
                URL url = new URL("http://www.train.enam.gov.in/NamWebSrv/rest/verifyUser");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                connection.getOutputStream().write(postData);
                int respo = connection.getResponseCode();
                String res = connection.getResponseMessage();

                Log.e("ressss","ressss==>"+res);
                BufferedReader br;
                if (respo != 200) {

                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

                }

                String line = "";
                while ((line = br.readLine()) != null) {
                    text += line;
                    //  return sb.toString();
                }
                connection.disconnect();
            } catch (Exception exc) {
                exc.getMessage();

            }
            return text;
        }

        protected void onPostExecute(String result) {
            Log.e("result","result"+result);
            p.dismiss();
            Client client = new Client();
            try {
                JSONObject object = new JSONObject(result);
                String StatusMsg = object.getString("statusMsg");
                if (StatusMsg.equals("S")) {
                    JSONArray Jarray = object.getJSONArray("listLogin");
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject json = Jarray.getJSONObject(i);
                        client.setOrgId(json.getString("orgId"));
                        client.setUserId(json.getString("userId"));
                        client.setLoginType(json.getString("loginType"));
                        client.setUsername(json.getString("userName"));
                    }

                    if(GetLogid.equalsIgnoreCase("1")){
                        Intent intent = new Intent(LoginActivity.this, WelcomeUserActivity.class);
                        intent.putExtra("opr_id", apmcId.toString());
                        intent.putExtra("u_name", loginid.getText().toString());
                        intent.putExtra("username", client.getUsername());
                        intent.putExtra("u_pass", userpassword.getText().toString());
                        intent.putExtra("u_orgid", client.getOrgId());
                        intent.putExtra("u_id", client.getUserId());
                        startActivity(intent);
                        finish();
                    }else if(GetLogid.equalsIgnoreCase("2")){
                        Intent intent = new Intent(LoginActivity.this, GateEntryActivity.class);
                        intent.putExtra("opr_id", apmcId.toString());
                        intent.putExtra("u_name", loginid.getText().toString());
                        intent.putExtra("username", client.getUsername());
                        intent.putExtra("u_pass", userpassword.getText().toString());
                        intent.putExtra("u_orgid", client.getOrgId());
                        intent.putExtra("u_id", client.getUserId());
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Authentication Failled", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                p.dismiss();
                e.printStackTrace();

            }

        }
    }

    private void GetStates(String url) {
        hotelCnstsesList = new ArrayList<Client>();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject arg0) {
                        // TODO Auto-generated method stub
                        try {
                            JSONArray array = arg0.getJSONArray("listStates");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject job = array.getJSONObject(i);
                                    String statusMsg = job.getString("statusMsg");
                                    String stateId = job.getString("stateId");
                                    String stateDesc = job.getString("stateDesc");
                                    String stateDescEn = job.getString("stateDescEn");
                                    String stateCode = job.getString("stateCode");
                                    String gmStateId = job.getString("gmStateId");
                                    String listStates = job.getString("listStates");
                                    Client offersCnsts = new Client();
                                    offersCnsts.setStatusMsg(statusMsg);
                                    offersCnsts.setStateId(stateId);
                                    offersCnsts.setStateDesc(stateDesc);
                                    offersCnsts.setStateDescEn(stateDescEn);
                                    offersCnsts.setStateCode(stateCode);
                                    offersCnsts.setGmStateId(gmStateId);
                                    offersCnsts.setListStates(listStates);
                                    hotelCnstsesList.add(offersCnsts);

                                }
                                spin_states.setAdapter(new Stateadapter(LoginActivity.this, hotelCnstsesList));
                                //selectcardtype.setSelection(position);

                            }
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonRequest);
    }


}

