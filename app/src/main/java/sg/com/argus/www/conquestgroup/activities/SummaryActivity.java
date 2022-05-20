package sg.com.argus.www.conquestgroup.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mswipetech.wisepad.sdk.Print;
import com.socsi.smartposapi.printer.Align;
import com.socsi.smartposapi.printer.FontLattice;
import com.socsi.smartposapi.printer.Printer2;
import com.socsi.smartposapi.printer.TextEntity;

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

public class SummaryActivity extends AppCompatActivity {

    Button pickDateBtn;
    private int day, month, year;

    /**
     * Test Print
     */
    Button testPrintBtn;

    TextView dateView, loginIdEditText;
    String loginId;

    String dateString = "";

    ConnectionDetector connectionDetector;
    Boolean isInternetPresent = false;
    Button getSummaryBtn;

    private static Printer2 a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_summary);

        connectionDetector = new ConnectionDetector(this);

        testPrintBtn = findViewById(R.id.test_print);


        Print.init(SummaryActivity.this);

        /**
         * Setting up Printer 2
         */
        a = Printer2.getInstance(SummaryActivity.this);

        pickDateBtn = findViewById(R.id.pick_date_btn);
        dateView = findViewById(R.id.date_text_view);

        loginIdEditText = findViewById(R.id.login_id_edit_text);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        testPrintBtn.setOnClickListener(view -> {
            Print.init(SummaryActivity.this);
            Print.StartPrinting("TEST PRINT", FontLattice.FORTY_EIGHT, true, Align.CENTER, false);

        });




        getSummaryBtn = findViewById(R.id.submit_summary_btn);
        getSummaryBtn.setOnClickListener(view -> {

            loginId = loginIdEditText.getText().toString();
            isInternetPresent = connectionDetector.isConnectingToInternet();

            if (loginId.isEmpty()) {
                Toast.makeText(this, "Login Id is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dateString.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            }

            if (isInternetPresent) {
                new GetSummary().execute();
            } else {
                Toast.makeText(this, "No Internet Present", Toast.LENGTH_SHORT).show();
            }

        });

    }

    void defaultPrint() {
        a.appendTextEntity2(new TextEntity("" ,FontLattice.TWENTY_FOUR, true, Align.LEFT, true));
    }


    void defaultPrint(String content){
        a.appendTextEntity2(new TextEntity(content ,FontLattice.TWENTY_FOUR, true, Align.LEFT, true));
    }

    /**
     * Overloading
     * @param content
     * @param align
     */
    void defaultPrint(String content, Align align, boolean lineBreak){
        a.appendTextEntity2(new TextEntity(content ,FontLattice.TWENTY_FOUR, true, align, lineBreak));
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

    private final DatePickerDialog.OnDateSetListener myDateListener = new
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
        String oprId = Constants.APMC_OPR_ID;

        @Override
        protected void onPreExecute() {
            p.setMessage("Please Wait...");
            p.setCancelable(false);
            p.show();

            if (dateString.isEmpty()) {
                Toast.makeText(SummaryActivity.this, "Empty Date String", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SummaryActivity.this, dateString, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String text = "";


            String urlParameters = "orgId=" + orgId + "&oprId=" + oprId + "&loginId=" + loginId + "&wbTrnDate=" + dateString + "";

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
                summarySwipePrint2(jsonObject);
                pickDateBtn.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HarishData", " " + e);
            }


        }
    }


    private void summarySwipePrint2(JSONObject object) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String completeTime = df.format(calendar.getTime());

            int totalBags = 0;
            double netWeightInQt = 0;

            String userName = object.getString("userName");
            String userType = object.getString("userType");
            String dataAvailable = object.getString("dataAvailable");


            a.appendTextEntity2(new TextEntity(Constants.CONQUEST_GROUP,FontLattice.THIRTY_SIX,true,Align.CENTER,true));
            a.startPrint();
            Print.StartPrinting();

            a.appendTextEntity2(new TextEntity("Weighment Summary",FontLattice.THIRTY_SIX,true,Align.CENTER,true));
            a.startPrint();
            Print.StartPrinting();
            defaultPrint("Business Day :  "+dateString);

            //TODO: Change MACHINE NO. ASK ADIL
            defaultPrint("Machine No : "+Constants.APMC_OPR_ID);

            if (dataAvailable.equals("true")) {

            defaultPrint("-----------------------------");
            defaultPrint("LotNo     TotalBags  TotalWeight");
            defaultPrint("-----------------------------");
            defaultPrint(userName);
            a.startPrint();

                JSONArray lotArrays = object.getJSONArray("lotInfo");

                for (int i = 0; i < lotArrays.length(); i++) {
                    JSONObject lot = lotArrays.getJSONObject(i);
                    String[] lots = new String[]{lot.getString("lotId"), lot.getString("noOfBags"), lot.getString("netWeightQtl")};
                    defaultPrint(lots[0], Align.LEFT, false);
                    defaultPrint(lots[1], Align.CENTER, false);
                    defaultPrint(lots[2], Align.RIGHT, false);
                    defaultPrint();

                    int singleLotBags = Integer.parseInt(lot.getString("noOfBags"));
                    totalBags += singleLotBags;
                    double lotWeightInQt = Double.parseDouble(lot.getString("netWeightQtl"));
                    netWeightInQt += lotWeightInQt;
                }


                defaultPrint("TOTAL LOTS :", Align.LEFT, false);
                defaultPrint(String.valueOf(lotArrays.length()), Align.RIGHT, false);
                defaultPrint();

                defaultPrint("TOTAL BAGS :", Align.LEFT, false);
                defaultPrint(String.valueOf(totalBags), Align.RIGHT, false);
                defaultPrint();

                defaultPrint("TOTAL NET(Kgs) :", Align.LEFT, false);
                defaultPrint(String.valueOf(netWeightInQt * 100), Align.RIGHT, false);
                defaultPrint();

                defaultPrint("TOTAL Net(QT) :", Align.LEFT, false);
                defaultPrint(String.valueOf(netWeightInQt), Align.RIGHT, false);
                defaultPrint();
                a.startPrint();
            }
            else {
                defaultPrint(userName);
                defaultPrint("-----------------------------");

                a.appendTextEntity2(new TextEntity("No Lots added on "+dateString,FontLattice.TWENTY_EIGHT,true,Align.CENTER,true));
                defaultPrint("-----------------------------");

                a.startPrint();

            }


            defaultPrint("Printed On",Align.LEFT,false);
            defaultPrint(completeTime,Align.RIGHT,false);
            defaultPrint();

            Print.StartPrinting();

            defaultPrint("Sign of Dadwal");

            Print.StartPrinting();
            Print.StartPrinting();
            Print.StartPrinting();
            Print.StartPrinting();

        } catch (JSONException e) {
            e.printStackTrace();
            String jsonError = "Json Error";
            Print.StartPrinting(jsonError,FontLattice.THIRTY_SIX,true,Align.CENTER,true);
            Print.StartPrinting();
            Print.StartPrinting();

        }
    }


    void show_selection() {
        LayoutInflater layoutInflater = LayoutInflater.from(SummaryActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_exit_summary, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SummaryActivity.this);

        final Button no = promptView
                .findViewById(R.id.exit_no_btn);

        final Button yes = promptView
                .findViewById(R.id.exit_yes_btn);

        final RadioGroup group= promptView.findViewById(R.id.radioGroup);

        alertDialogBuilder.setView(promptView).setCancelable(true);

        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();


        no.setOnClickListener(v -> {
            // cancel dialog
            alert.cancel();
        });

        yes.setOnClickListener(v -> {
            int selectedId=group.getCheckedRadioButtonId();
            final RadioButton radioButton= promptView.findViewById(selectedId);
            if (radioButton.getText().equals("Weighing Scale")){
                runWelcomeUserActivity();
                alert.cancel();
            }
            if(radioButton.getText().equals("Exit App")){
                finish();
                System.exit(0);
            }
        });
    }

    void runWelcomeUserActivity(){
        Intent i = new Intent(SummaryActivity.this, LoginActivity.class);
        i.putExtra("screen","1");
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        show_selection();

    }



}
