package sg.com.argus.www.conquestgroup.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.ConnectionDetector;
import sg.com.argus.www.conquestgroup.models.TableItem;
import sg.com.argus.www.conquestgroup.utils.Constants;
import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;

public class SummaryActivity extends AppCompatActivity {

    Button pickDateBtn;
    private int day, month, year;

    TextView dateView, loginIdEditText;
    String loginId;

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

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


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
        String oprId = "72";

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
                summaryPrint(jsonObject);
                pickDateBtn.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HarishData", " " + e);
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
            String dataAvailable = object.getString("dataAvailable");

            JSONArray lotArrays = object.getJSONArray("lotInfo");


            StringBuilder bodyString = new StringBuilder();

            //AMC SURYAPET
            headingStringBuilder.append("        CONQUEST GROUP").append("\n\n");
            headingStringBuilder.append("      Weighment Summary").append("\n");
            sunmiPrintHelper.printText(headingStringBuilder.toString(), Constants.HEADING_SIZE, Constants.BOLD_ON, false, null);
            sunmiPrintHelper.print1Line();
            sunmiPrintHelper.print1Line();
            bodyString.append("Business Day :  ").append(dateString).append("\n");
            /**
             * TODO: This is the APM ID, It is supposed to change for every device
             */
            bodyString.append("Machine No : ").append(Constants.APMC_OPR_ID).append("\n");

            if (dataAvailable.equals("true")) {
                bodyString.append("-----------------------------").append("\n");
                bodyString.append("LotNo     TotalBags  TotalWeight").append("\n");
                bodyString.append("-----------------------------").append("\n");
                bodyString.append(userName).append("\n\n");
                sunmiPrintHelper.printText(bodyString.toString(), Constants.DEFAULT_PRINT_SIZE, Constants.BOLD_OFF, false, null);

                LinkedList<TableItem> tableItems = new LinkedList<>();
                //Clearing the Body String
                bodyString.setLength(0);
                for (int i = 0; i < lotArrays.length(); i++) {
                    TableItem ti = new TableItem();
                    JSONObject lot = lotArrays.getJSONObject(i);
                    String[] lots = new String[]{lot.getString("lotId"), lot.getString("noOfBags"), lot.getString("netWeightQtl")};
                    ti.setText(lots);
                    ti.setWidth(new int[]{13, 3, 6});
                    ti.setAlign(new int[]{0, 0, 2});
                    tableItems.add(ti);
                    int singleLotBags = Integer.parseInt(lot.getString("noOfBags"));
                    totalBags += singleLotBags;
                    double lotWeightInQt = Double.parseDouble(lot.getString("netWeightQtl"));
                    netWeightInQt += lotWeightInQt;
                }
                printAllTables(tableItems);
                sunmiPrintHelper.print1Line();

                LinkedList<TableItem> tableItems1 = new LinkedList<>();
                TableItem lotTableItem = new TableItem(new String[]{"TOTAL LOTS :", String.valueOf(lotArrays.length())});
                lotTableItem.setWidth(new int[]{11, 4});
                tableItems1.add(lotTableItem);

                TableItem bagsTableItem = new TableItem(new String[]{"TOTAL BAGS :", String.valueOf(totalBags)});
                bagsTableItem.setWidth(new int[]{11, 4});
                tableItems1.add(bagsTableItem);

                TableItem kgsTableItem = new TableItem(new String[]{"TOTAL NET(Kgs) :", String.valueOf(netWeightInQt * 100)});
                kgsTableItem.setWidth(new int[]{16, 9});
                tableItems1.add(kgsTableItem);

                TableItem qtTableItem = new TableItem(new String[]{"TOTAL Net(QT) :", String.valueOf(netWeightInQt)});
                qtTableItem.setWidth(new int[]{15, 7});
                tableItems1.add(qtTableItem);


                TableItem printedOnTableItem = new TableItem(new String[]{"Printed On", completeTime});
                printedOnTableItem.setWidth(new int[]{10, 19});
                printedOnTableItem.setAlign(new int[]{0, 0});
                tableItems1.add(printedOnTableItem);

                printAllTables(tableItems1);
                sunmiPrintHelper.print1Line();
            } else {
                bodyString.append(userName).append("\n\n");
                bodyString.append("-----------------------------").append("\n");
                bodyString.append("No Lots added on "+dateString).append("\n");
                bodyString.append("-----------------------------").append("\n\n");

            }
            bodyString.append("Sign of Dadwal").append("\n");

            printSlip(bodyString.toString(), Constants.DEFAULT_PRINT_SIZE, Constants.BOLD_OFF);
            sunmiPrintHelper.print1Line();

        } catch (JSONException e) {
            e.printStackTrace();

            String jsonError = "Json Error";

            printSlip(jsonError, Constants.HEADING_SIZE, Constants.BOLD_ON);

        }
    }

    void printAllTables(LinkedList<TableItem> dataItems) {

        if (dataItems.isEmpty()) {
            Toast.makeText(this, "No Tables to Print", Toast.LENGTH_SHORT).show();
        } else {
            for (TableItem item : dataItems) {
                sunmiPrintHelper.printTable(item.getText(), item.getWidth(), item.getAlign());
            }
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

    public void printSlip(String content, float size, boolean isBold) {

        sunmiPrintHelper.printText(content, size, false, false, null);
        sunmiPrintHelper.feedPaper();

    }


}
