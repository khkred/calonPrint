package sg.com.argus.www.conquestgroup.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.utils.Constants;
import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;

public class SummaryActivity extends AppCompatActivity {

    Button pickDateBtn, getSummaryBtn;
    private int day, month, year;

    TextView dateView, loginIdEditText, passwordEditText;

    String loginId, password;

    SunmiPrintHelper sunmiPrintHelper;

    String dateString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_summary);

        sunmiPrintHelper = SunmiPrintHelper.getInstance();

        pickDateBtn = findViewById(R.id.pick_date_btn);
        getSummaryBtn = findViewById(R.id.submit_summary_btn);

        dateView = findViewById(R.id.date_text_view);

        loginIdEditText = findViewById(R.id.login_id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        getSummaryBtn.setOnClickListener(view -> {
            loginId = loginIdEditText.getText().toString();
            password = passwordEditText.getText().toString();
            new GetSummary().execute();
        });

    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {

        dateString = new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year).toString();

        dateView.setText(dateString);
        pickDateBtn.setVisibility(View.GONE);
    }

    private class GetSummary extends AsyncTask<String, Void, String> {

        String text = "";
        ProgressDialog p = new ProgressDialog(SummaryActivity.this);

        @Override
        protected void onPreExecute() {

            p.setMessage("Please Wait...");
            p.setCancelable(false);
            p.show();

        }

        @Override
        protected String doInBackground(String... strings) {


            try {
//                String urlParameters = "{\n" +
//                        "\"orgId\": \"" + "1" + "\",\n" +
//                        "\"oprId\": \"" + "72" + "\",\n" +
//                        "\"loginId\": \"" + loginId + "\",\n" +
//                        "\"password\": \"" + password + "\",\n" +
//                        "\"wbTrnDate\": \"" + dateString + "\",\n" +
//                        "}";

                String urlParameters = "orgId=" + "1" + "&oprId=" + "72" + "&loginId=" + loginId + "&password=" + password + "&webTrnDate=" + dateString;

                Log.e("HarishData", "Data" + urlParameters);

                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                //URL url = new URL("");
                URL url = new URL(Constants.SUMMARY_PRINT_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                connection.getOutputStream().write(postData);
                int respo = connection.getResponseCode();
                String res = connection.getResponseMessage();

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
                String excep = exc.getMessage();
                Log.e("TAG", "excep" + excep);
                Log.d("HarishData", "Error in Async Task " + excep);
            }
            return text;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                p.dismiss();
                JSONObject object = new JSONObject(result);
                summaryPrint(object);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void summaryPrint(JSONObject object) {

        try {

            int totalBags = 0;
            double netWeightInQt = 0;
            StringBuilder headingStringBuilder = new StringBuilder();

            String StatusMsg = object.getString("statusMsg");
            String userName = object.getString("userName");
            String userType = object.getString("userType");
            JSONArray lotArrays = object.getJSONArray("lotInfo");


            StringBuilder bodyString = new StringBuilder();

            //AMC SURYAPET
            headingStringBuilder.append("        AMC SURYAPET").append("\n\n\n");
            headingStringBuilder.append("   Weighment Summary");
            printSlip(headingStringBuilder.toString(), Constants.HEADING_SIZE, Constants.BOLD_ON);

            bodyString.append("Business Day :  ").append(dateString).append("\n");
            bodyString.append("Machine No : ").append(72).append("\n");
            bodyString.append("-----------------------------").append("\n");
            bodyString.append("LotNo     TotalBags  TotalWeight").append("\n");
            bodyString.append("-----------------------------").append("\n");
            bodyString.append(userName).append("\n");

            for (int i = 0; i < lotArrays.length(); i++) {

                JSONObject lot = lotArrays.getJSONObject(i);
                bodyString.append(lot.getString("lotId") + "   " + lot.getString("noOfBags") + "   " + lot.getString("netWeightQtl"));

                int singleLotBags = Integer.parseInt(lot.getString("noOfBags"));
                totalBags += singleLotBags;

                StringBuilder printString = new StringBuilder();
                double lotWeightInQt = Double.parseDouble(lot.getString("netWeightQtl"));
                netWeightInQt += lotWeightInQt;
            }
            printSlip(bodyString.toString(), Constants.DEFAULT_PRINT_SIZE, Constants.BOLD_OFF);
        } catch (JSONException e) {
            e.printStackTrace();

            String jsonError = "Json Error";

            printSlip(jsonError, Constants.HEADING_SIZE, Constants.BOLD_ON);

        }


    }


    public void printSlip(String content, float size, boolean isBold) {

        sunmiPrintHelper.printText(content, size, false, false, null);
        sunmiPrintHelper.feedPaper();

    }


}
