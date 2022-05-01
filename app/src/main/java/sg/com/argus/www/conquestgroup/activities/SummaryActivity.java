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
import sg.com.argus.www.conquestgroup.adapters.ConnectionDetector;
import sg.com.argus.www.conquestgroup.utils.Constants;
import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;

public class SummaryActivity extends AppCompatActivity {

    Button pickDateBtn ;
    private int day, month, year;

    TextView dateView, loginIdEditText, passwordEditText;
    String loginId, password;

    SunmiPrintHelper sunmiPrintHelper;
    String dateString = "";

    ConnectionDetector connectionDetector;
    Boolean isInternetPresent = false;
    Button getSummaryBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_summary);

        connectionDetector = new ConnectionDetector(this);

        sunmiPrintHelper = SunmiPrintHelper.getInstance();
        sunmiPrintHelper.initSunmiPrinterService(getApplicationContext());

        pickDateBtn = findViewById(R.id.pick_date_btn);

        dateView = findViewById(R.id.date_text_view);

        loginIdEditText = findViewById(R.id.login_id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


         getSummaryBtn = findViewById(R.id.submit_summary_btn);
        getSummaryBtn.setOnClickListener(view -> {
            
            loginId = loginIdEditText.getText().toString();
            password = passwordEditText.getText().toString();
            isInternetPresent = connectionDetector.isConnectingToInternet();
            
            if (loginId.isEmpty())
            {
                Toast.makeText(this, "Login Id is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dateString.isEmpty()){
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            }

            if (isInternetPresent) {
                new GetSummary().execute();
            }
            else {
                Toast.makeText(this, "No Internet Present", Toast.LENGTH_SHORT).show();
            }

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

        dateString = new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year).toString();

        dateView.setText(dateString);
        pickDateBtn.setVisibility(View.GONE);
    }
    
    private class GetSummary extends AsyncTask<String, Void, String> {

        ProgressDialog p = new ProgressDialog(SummaryActivity.this);
        String orgId = "1";
        String oprId = "72";

        @Override
        protected void onPreExecute() {
            p.setMessage("Please Wait...");
            p.setCancelable(false);
            p.show();

            if (dateString.isEmpty()) {
                Toast.makeText(SummaryActivity.this, "Empty Date String", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(SummaryActivity.this, dateString, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected   String doInBackground(String... strings) {
            String text = "";


            String urlParameters = "orgId=" + orgId + "&oprId=" + oprId + "&loginId=" + loginId + "&password=" + password + "&wbTrnDate=" +dateString+"";

            try {
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
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
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return text;
        }

        @Override
        protected void onPostExecute(String result) {
            p.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    summaryPrint(jsonObject);
                    dateView.setVisibility(View.GONE);
                    getSummaryBtn.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("HarishData"," "+e.toString());
                }


        }
    }


    private void summaryPrint(JSONObject object) {

        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String completeTime = df.format(calendar.getTime());

            int totalBags = 0;
            double netWeightInQt = 0;
            StringBuilder headingStringBuilder = new StringBuilder();

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
                bodyString.append(lot.getString("lotId") + "   " + lot.getString("noOfBags") + "    " + lot.getString("netWeightQtl")).append("\n");

                int singleLotBags = Integer.parseInt(lot.getString("noOfBags"));
                totalBags += singleLotBags;
                double lotWeightInQt = Double.parseDouble(lot.getString("netWeightQtl"));
                netWeightInQt += lotWeightInQt;
            }

            bodyString.append("\n").append("TOTAL LOTS :            ").append(lotArrays.length()).append("\n");
            bodyString.append("TOTAL BAGS :             ").append(totalBags).append("\n");
            bodyString.append("TOTAL NET(Kgs) :        ").append(netWeightInQt*100).append("\n");
            bodyString.append("TOTAL Net(QT) :       ").append(netWeightInQt).append("\n");
            bodyString.append("Printed On   ").append(completeTime).append("\n");
            bodyString.append("\n\n");
            bodyString.append("Sign of Dadwal").append("\n");

            printSlip(bodyString.toString(), Constants.DEFAULT_PRINT_SIZE, Constants.BOLD_OFF);
        } catch (JSONException e) {
            e.printStackTrace();

            String jsonError = "Json Error";

            printSlip(jsonError, Constants.HEADING_SIZE, Constants.BOLD_ON);

        }


    }

    public void sampleTable()
    {
        String[] txtArray = new String[]{"72202204252","14","8.6100"};
        int[] widths = new int[] {2};
        int[] aligns  = new int[] {1};

        sunmiPrintHelper.printTable(txtArray,widths,aligns);
    }


    public void printSlip(String content, float size, boolean isBold) {


        sunmiPrintHelper.printText(content, size, false, false, null);
        sunmiPrintHelper.feedPaper();

    }


}
