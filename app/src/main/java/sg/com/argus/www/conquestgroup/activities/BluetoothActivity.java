package sg.com.argus.www.conquestgroup.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.BagViewAdapter;
import sg.com.argus.www.conquestgroup.adapters.Bluetooth;
import sg.com.argus.www.conquestgroup.adapters.ConnectionDetector;
import sg.com.argus.www.conquestgroup.models.Bag;
import sg.com.argus.www.conquestgroup.utils.Constants;


public class BluetoothActivity extends AppCompatActivity implements Bluetooth.CommunicationCallback {


    Spinner bluetooth_devices;
    List<BluetoothDevice> mDeviceList;
    private TextView onlyBagWeight;
    private static TextView NumofBags;
    LinearLayout blueDisable;
    private static TextView TotalWeightInQuintal;
    //    LinearLayout bagsLinearLayout, llh;
    Button  submit, addMultipleBagsBtn;
    ConnectionDetector cd;
    private String userActualName;
    Boolean isInternetPresent = false;
    private static String BagTypeDesc;

    RecyclerView bagRecyclerView;

    TextView liveFeedTV;
    EditText manualBagsToAdd;
    public String liveFeedString = "";

    //=========Single Bag Layout Variables================
    ArrayList<Bag> bagArrayList = new ArrayList<>();
    BagViewAdapter bagViewAdapter;
    static ArrayList<Double> bagWeightList = new ArrayList<>();
    double emptyBagWeight;
    String emptyBagWtKg;

    private String loginid, password, orgid, actualBags, userid, bagTypeId, lotId, caName, lotRate, SellerName, Commodity, traderName, feeCategoryId;

    String oprId, Sequence = "1";
    ScrollView scrollView;
    public Bluetooth blueToothWeightDevice;
    public boolean registered = false;

    /**
     * Check if Bluetooth is enabled through broadcast receiver
     */
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);

                //10 means bluetooth is turned off
                if (intExtra == 10) {
                    if (BluetoothActivity.this.registered) {
                        BluetoothActivity.this.unregisterReceiver(BluetoothActivity.this.mReceiver);
                    }
                    //TODO implement bluetooth state 10 dialog


                }

                else if (intExtra == 13) {
                    if (BluetoothActivity.this.registered) {
                        BluetoothActivity.this.unregisterReceiver(BluetoothActivity.this.mReceiver);
                        BluetoothActivity.this.registered = false;
                    }
                    //TODO implement bluetooth state 13 dialog
                }

                //TODO: Implement Bluetooth no device paired TOAST
                //TODO: ON ACTIVITY DESTROYED, data is getting removed.
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        final Activity activity = this;
        cd = new ConnectionDetector(this);
        submit = findViewById(R.id.submit);

//        bagsLinearLayout = findViewById(R.id.bags_list_linear_layout);
        scrollView = findViewById(R.id.scrollView);

        liveFeedTV = findViewById(R.id.live_weight_textView);
        //Single Bag Layout
        bagRecyclerView = findViewById(R.id.recycler_view);
        blueDisable = findViewById(R.id.blueDisable);
        bluetooth_devices = findViewById(R.id.bluetoothDevices);
        onlyBagWeight = findViewById(R.id.onlyBagWeight);
        NumofBags = findViewById(R.id.numBag);
        manualBagsToAdd = findViewById(R.id.how_many_bags_edit_text);
        TotalWeightInQuintal = findViewById(R.id.totalWeight);
        addMultipleBagsBtn = findViewById(R.id.add_multiple_bags_btn);
        final Intent intent = getIntent();
        loginid = intent.getStringExtra("u_name");
        userActualName = intent.getStringExtra("username");
        password = intent.getStringExtra("u_pass");
        orgid = intent.getStringExtra("u_orgid");
        userid = intent.getStringExtra("u_id");
        oprId = intent.getStringExtra("opr_id");
        lotId = intent.getStringExtra("lotId");
        bagTypeId = intent.getStringExtra("bagTypeId");
        SellerName = intent.getStringExtra("farmerName");
        Commodity = intent.getStringExtra("commodityName");
        caName = intent.getStringExtra("caName");
        lotRate = intent.getStringExtra("lotRate");
        actualBags = intent.getStringExtra("actualBags");
        traderName = intent.getStringExtra("traderName");
        String newBagTypeValue = intent.getStringExtra("newBagTypeValue");
        feeCategoryId = intent.getStringExtra("feeCategoryId");
        BagTypeDesc = intent.getStringExtra("BagTypeDesc");
        emptyBagWtKg = intent.getStringExtra("emptyBagWtKg");

        emptyBagWeight = Double.parseDouble(emptyBagWtKg);

        onlyBagWeight.setText(emptyBagWtKg);

        /**
         * Bag Recycler View code from Here
         */

        bagViewAdapter = new BagViewAdapter(getApplicationContext(), bagArrayList);

        bagRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        bagRecyclerView.setLayoutManager(linearLayoutManager);
        bagRecyclerView.setAdapter(bagViewAdapter);


        /**
         * Bluetooth code from here
         */
        blueToothWeightDevice = new Bluetooth(this);
        blueToothWeightDevice.enableBluetooth();
        blueToothWeightDevice.setCommunicationCallback(this);


        addDevicesToList();

        Display("Connecting...");
        this.blueToothWeightDevice.connectToDevice(blueToothWeightDevice.getPairedDevices().get(bluetooth_devices.getSelectedItemPosition()));
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
        this.registered = true;


        /**
         * Spinner dropdown selection from here
         */

        bluetooth_devices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (BluetoothActivity.this.registered) {
                    BluetoothActivity.this.unregisterReceiver(BluetoothActivity.this.mReceiver);
                    BluetoothActivity.this.registered = false;
                }

                Display("Connecting...");
                BluetoothActivity.this.blueToothWeightDevice.removeCommunicationCallback();
                BluetoothActivity.this.blueToothWeightDevice.disconnect();
                BluetoothActivity.this.blueToothWeightDevice = new Bluetooth(BluetoothActivity.this);
                BluetoothActivity.this.blueToothWeightDevice.enableBluetooth();
                BluetoothActivity.this.blueToothWeightDevice.setCommunicationCallback(BluetoothActivity.this);
                BluetoothActivity.this.blueToothWeightDevice.connectToDevice(BluetoothActivity.this.blueToothWeightDevice.getPairedDevices().get(position));


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        addMultipleBagsBtn.setOnClickListener(v -> {
            if (bluetooth_devices.getSelectedItem() != null) {
                // emptyBagWeight shouldn't be more than 3 KG.

                String bagsToAddString = manualBagsToAdd.getText().toString();

                if(bagsToAddString.isEmpty()) {
                    Toast.makeText(this, "No Bags Added", Toast.LENGTH_SHORT).show();
                    return;
                }
                int manualBagsToAddVal = Integer.parseInt(manualBagsToAdd.getText().toString());
                String WeightOfBag = liveFeedString.replace(" ", "").replaceAll("=0*\\+", "").replaceAll("000.", "00.");
                Double weightOfBagVal = Double.parseDouble(WeightOfBag);

                if(manualBagsToAddVal == 1) {
                    addSingleBag(weightOfBagVal);
                }
                else if (manualBagsToAddVal < 2) {
                    Toast.makeText(BluetoothActivity.this, "No of bags should be more than 0", Toast.LENGTH_SHORT).show();
                } else if (emptyBagWeight >= 3) {
                    Toast.makeText(BluetoothActivity.this, "Empty Bag weight should not be greater than 3 Kg", Toast.LENGTH_SHORT).show();
                }
                else if(weightOfBagVal <= emptyBagWeight){
                    Toast.makeText(BluetoothActivity.this, "Weight of the bag should be more than empty bag", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (int i = 0; i < manualBagsToAddVal; i++) {
                        addSingleBag(weightOfBagVal);
                    }
                    manualBagsToAdd.setText("1");
                }
            } else {
                Toast.makeText(BluetoothActivity.this, "Device is not connected", Toast.LENGTH_SHORT).show();
            }
        });


        submit.setOnClickListener(v -> {
            try {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    if (getBagsCount() != 0 && getTotalBagWeight() > 0) {
                        new SendBag().execute();
                    } else {
                        Toast.makeText(BluetoothActivity.this, "Please Add Bag First", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    showInternetAlert();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /*
    Removes bag from bagList
     */
    public static void decreaseBagCount(int position, Context context) {
        //remove bag
        bagWeightList.remove(position);
        NumofBags.setText("No of Bags:  " + getBagsCount());
        TotalWeightInQuintal.setText("Total Weight: " + roundOffTo3DecPlaces(getTotalQuintalWeight()));

        Toast.makeText(context, "Bag deleted successfully", Toast.LENGTH_SHORT).show();

    }

    public void addDevicesToList() {

        mDeviceList = blueToothWeightDevice.getPairedDevices();
        ArrayList<String> stringArrayList = new ArrayList<>();

//        if (mDeviceList.size()==0) {
//          showPairedDevicesPrompt();
//        }

        for (BluetoothDevice device : mDeviceList) {
            stringArrayList.add(device.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.simple_spinner_item, stringArrayList);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        bluetooth_devices.setAdapter(adapter);

    }

    public void Display(final String str) {
        runOnUiThread(new Runnable() {
            public void run() {
                liveFeedTV.setText(str);
            }
        });
    }


    /**
     * Methods for Bluetooth.callback
     */
    @Override
    public void onConnect(BluetoothDevice bluetoothDevice) {

        Display("Connected to " + bluetoothDevice.getName() + " - " + bluetoothDevice.getAddress());

    }

    @Override
    public void onConnectError(BluetoothDevice bluetoothDevice, String str) {
        runOnUiThread(new Runnable() {
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        BluetoothActivity.this.blueToothWeightDevice.connectToDevice(bluetoothDevice);
                    }
                }, 2000);
            }
        });

    }

    @Override
    public void onDisconnect(BluetoothDevice bluetoothDevice, String str) {
        Display("Disconnected!");
        Display("Connecting again...");
        this.blueToothWeightDevice.connectToDevice(bluetoothDevice);

    }

    @Override
    public void onError(String str) {

    }

    @Override
    public void onMessage(String str) {
        liveFeedString = "=0000+0009";
        Display(str);

    }

    //==========================================================================================================



    private void showPairedDevicesPrompt() {
        LayoutInflater layoutInflater = LayoutInflater.from(BluetoothActivity.this);
        View pairedDevicesPrompt = layoutInflater.inflate(R.layout.dialog_paired_devices,null);

        final Button retryBtn = pairedDevicesPrompt.findViewById(R.id.paired_retry_btn);
        final Button settingsBtn = pairedDevicesPrompt.findViewById(R.id.paired_settings_btn);

        /**
         * Alert Dialog Builder
         */


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BluetoothActivity.this);
        alertDialogBuilder.setView(pairedDevicesPrompt).setCancelable(true);

        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        retryBtn.setOnClickListener(view -> {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        });

        settingsBtn.setOnClickListener(view -> {
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
        bagWeightList.clear();
        bagArrayList.clear();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onResume() {
        super.onResume();

    }


    public static int getBagsCount() {
        return bagWeightList.size();
    }

    public static double getTotalBagWeight() {
        double totalWeight = 0;

        for (double sw : bagWeightList) {
            totalWeight += sw;
        }
        return totalWeight;
    }

    public static double getTotalQuintalWeight() {
        return getTotalBagWeight() / 100;
    }

    public double getTotalNetWeight() {
        return getTotalBagWeight() - getTotalEmptyBagWeight();
    }

    public double getTotalEmptyBagWeight() {
        return getBagsCount() * Double.parseDouble(onlyBagWeight.getText().toString());
    }


    // formatstring to three decimal places
    static String roundOffTo3DecPlaces(double val) {
        return String.format("%.3f", val);
    }

    // formatstring to five decimal places
    static String roundOffTo5DecPlaces(double val) {
        return String.format("%.5f", val);
    }


    String roundOffTo0DecPlaces(double val) {
        return String.format("%.0f", val);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (this.registered) {
            unregisterReceiver(this.mReceiver);
            this.registered = false;
        }
        this.blueToothWeightDevice.removeCommunicationCallback();
        this.blueToothWeightDevice.disconnect();

    }

    private void addSingleBag(Double weightOfBagVal) {
        String bagLabel = "Bag " + (getBagsCount() + 1);
        String fetchWeightString = " " + weightOfBagVal + " KG";
        bagArrayList.add(new Bag(bagLabel, fetchWeightString));
        bagViewAdapter.notifyDataSetChanged();
        bagWeightList.add(weightOfBagVal);
        TotalWeightInQuintal.setText("Total Weight: " + roundOffTo3DecPlaces(getTotalQuintalWeight()));
        NumofBags.setText("No of Bags:  " + getBagsCount());
    }


    // to send individual weight
    public String sendIndividualBagWeight() {
        String bagWeight = "", updateBagWeight = "";
        try {
            JSONObject jsonObject;
            JSONArray arr = new JSONArray();
            HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

            for (int k = 0; k < getBagsCount(); k++) {
                bagWeight = "{\n" +
                        "\"bagTypeId\": \"" + bagTypeId + "\",\n" +
                        "\"sequence\": \"" + Sequence + "\",\n" +
                        "\"netWeight\": \"" + (bagWeightList.get(k) - emptyBagWeight) + "\",\n" +
                        "\"grossWeight\": \"" + bagWeightList.get(k) + "\",\n" +
                        "\"tareWeigh\": \"" + emptyBagWeight + "\"\n" +
                        "}\n";

                jsonObject = new JSONObject();
                jsonObject.put("bagTypeId", bagTypeId);
                jsonObject.put("sequence", Sequence);
                jsonObject.put("netWeight", bagWeightList.get(k) - emptyBagWeight);
                jsonObject.put("grossWeight", bagWeightList.get(k));
                jsonObject.put("tareWeigh", String.valueOf(emptyBagWeight));
                map.put("json" + k, jsonObject);
                arr.put(map.get("json" + k));

            }
            Log.e("The json string is ", "" + arr);
            updateBagWeight = updateBagWeight + arr;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateBagWeight;
    }


    // to call send weight api
    private class SendBag extends AsyncTask<String, Void, String> {
        String text = "";
        String IndividualBagWeight = "";
        ProgressDialog p = new ProgressDialog(BluetoothActivity.this);

        @Override
        protected void onPreExecute() {

            p.setMessage("Please Wait...");
            p.setCancelable(false);
            p.show();

        }

        protected String doInBackground(String... params) {

            try {
//                IndividualBagWeight = sendIndividualBagWeight();
                String urlParameters = "SendBagWeight={\n" +
                        "\"SendBagWeight\": {\n" +
                        "\"orgId\": \"" + orgid + "\",\n" +
                        "\"oprId\": \"" + oprId + "\",\n" +
                        "\"loginId\": \"" + loginid + "\",\n" +
                        "\"password\": \"" + password + "\",\n" +
                        "\"lotId\": \"" + lotId + "\",\n" +
                        "\"grossWeight\": \"" + roundOffTo3DecPlaces(getTotalQuintalWeight()) + "\",\n" +
                        "\"netWeight\": \"" + getTotalNetWeight() + "\",\n" +
                        "\"noOfBags\": \"" + getBagsCount() + "\",\n" +
                        "\"bagTypeId\": \"" + bagTypeId + "\",\n" +
                        "\"feeCategoryId\": \"" + feeCategoryId +
                        "\"}\n" +
                        "}";

                Log.e("Data", "Data" + urlParameters);

                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                //URL url = new URL("");
                URL url = new URL(Constants.SEND_BAG_WEIGHT_URL);
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
            }
            return text;
        }

        protected void onPostExecute(String result) {
            try {
                p.dismiss();
                JSONObject object = new JSONObject(result);
                String StatusMsg = object.getString("statusMsg");
                String transactionNo = object.getString("transactionNo");
                String invoiceDocNo = object.getString("invoiceDocNo");
                Log.e("TAG", "StatusMsg" + StatusMsg);
                if (StatusMsg.equals("S")) {
                    Toast.makeText(BluetoothActivity.this, "Weight Sent Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BluetoothActivity.this, PrintWeighingSlipActivity.class);
                    intent.putExtra("u_name", loginid);
                    intent.putExtra("u_pass", password);
                    intent.putExtra("u_orgid", orgid);
                    intent.putExtra("u_id", userid);
                    intent.putExtra("opr_id", oprId);
                    intent.putExtra("bagTypeId", bagTypeId);
                    intent.putExtra("lotId", lotId);
                    intent.putExtra("BagTypeDesc", BagTypeDesc);
                    intent.putExtra("NoOfbag", String.valueOf(getBagsCount()));
                    intent.putExtra("TotalWeight", roundOffTo3DecPlaces(getTotalBagWeight()));
                    intent.putExtra("QuintalWeight", roundOffTo3DecPlaces(getTotalQuintalWeight()));
                    intent.putExtra("NetWeight", roundOffTo3DecPlaces(getTotalNetWeight()));
                    intent.putExtra("BagsWeight", String.valueOf(getTotalEmptyBagWeight()));
                    intent.putExtra("commodityName", Commodity);
                    intent.putExtra("farmerName", SellerName);
                    intent.putExtra("caName", caName);
                    intent.putExtra("lotRate", lotRate);
                    intent.putExtra("actualBags", actualBags);
                    intent.putExtra("traderName", traderName);
                    intent.putExtra("username", userActualName);
                    intent.putExtra("transactionNo",transactionNo);
                    intent.putExtra("invoiceDocNo",invoiceDocNo);

                    /**
                     * Serialisable Extra
                     */
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bagWeightList", bagWeightList);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BluetoothActivity.this, "Weight could not be sent", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                p.dismiss();
                Log.e("TAG", "e" + e);
            }

        }
    }

    void showInternetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You don't have Internet Connection.")
                .setCancelable(false)
                .setPositiveButton("Cancel",
                        (dialog, id) -> finish()).setNegativeButton("Retry",
                (dialog, id) -> {
                    finish();
                    startActivity(getIntent());
                });

        // Creating dialog box
        AlertDialog alert = builder.create();
        // Setting the title manually
        alert.setTitle("Internet Connection");
        alert.show();

    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BluetoothActivity.this, WelcomeUserActivity.class);
        intent.putExtra("u_name", loginid);
        intent.putExtra("u_pass", password);
        intent.putExtra("username", userActualName);
        intent.putExtra("u_orgid", orgid);
        intent.putExtra("u_id", userid);
        intent.putExtra("opr_id", oprId);
        startActivity(intent);
        BluetoothActivity.this.finish();

    }
}
