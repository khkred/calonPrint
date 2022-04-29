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
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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


public class BluetoothActivity extends AppCompatActivity implements Bluetooth.CommunicationCallback {


    static StringBuilder stringbuilder;
    Spinner bluetooth_devices;
     List<BluetoothDevice> mDeviceList;
    private final String TAG = BluetoothActivity.class.getSimpleName();
    private TextView NumofBags, onlyBagWeight;
    LinearLayout blueDisable;
    LinearLayout blueEnable;
    private static int i = 0, numbag = 0, ActualNoOfBags = 1;
    private static TextView bagTxt, weightTxt, TotalWeightInQuintal;
//    LinearLayout bagsLinearLayout, llh;
    Button addBagBtn, submit, delete_btn;
    ConnectionDetector cd;
    private String userActualName;
    Boolean isInternetPresent = false;
    ArrayList<String> bagNetWeight = new ArrayList<String>();
    ArrayList<String> bagGrossWeight = new ArrayList<String>();

    RecyclerView bagRecyclerView;

    TextView liveFeedTV;
    public String liveFeedString = "";

    //=========Single Bag Layout Variables================
    LinearLayout singleBagLayout;
    ArrayList<Bag> bagArrayList = new ArrayList<>();
    BagViewAdapter bagViewAdapter;


    private static String startWeight = "0.0", FetchWeight = "", QuintalWeight = "", BagTypeDesc, ConvertedWeight = "", bagConvertedWeight = "";
    private static String outputWeight = "", netWeightProduct, calBagsWeight;
    private static double WeightbeforeDeletebag, TotalWeight, newbag;
    private double GotBags;
    private String loginid, password, orgid, actualBags, userid, bagTypeId, lotId, caName, lotRate, SellerName, Commodity, traderName, feeCategoryId, newBagTypeValue;

    String oprId, Sequence = "1";
    ScrollView scrollView;
    public Bluetooth f30b;
    public boolean registered = false;

    /**
     * Check if Bluetooth is enabled through broadcast receiver
     */
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                if (intExtra == 10) {
                    if (BluetoothActivity.this.registered) {
                        BluetoothActivity.this.unregisterReceiver(BluetoothActivity.this.mReceiver);
                    }
                    //TODO implement bluetooth state 10 dialog
                } else if (intExtra == 13) {
                    if (BluetoothActivity.this.registered) {
                        BluetoothActivity.this.unregisterReceiver(BluetoothActivity.this.mReceiver);
                        BluetoothActivity.this.registered = false;
                    }
                    //TODO implement bluetooth state 13 dialog
                }
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
        stringbuilder = new StringBuilder();
        submit = (Button) findViewById(R.id.submit);
        delete_btn = (Button) findViewById(R.id.removebag);

//        bagsLinearLayout = findViewById(R.id.bags_list_linear_layout);
        scrollView = findViewById(R.id.scrollView);

        liveFeedTV = findViewById(R.id.live_weight_textView);
        //Single Bag Layout
        bagRecyclerView = findViewById(R.id.recycler_view);
        blueDisable = (LinearLayout) findViewById(R.id.blueDisable);
        bluetooth_devices = (Spinner) findViewById(R.id.bluetoothDevices);
        onlyBagWeight = (TextView) findViewById(R.id.onlyBagWeight);
        NumofBags = (TextView) findViewById(R.id.numBag);
        TotalWeightInQuintal = (TextView) findViewById(R.id.totalWeight);
        addBagBtn = (Button) findViewById(R.id.add_bag_btn);
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
        newBagTypeValue = intent.getStringExtra("newBagTypeValue");
        feeCategoryId = intent.getStringExtra("feeCategoryId");
        BagTypeDesc = intent.getStringExtra("BagTypeDesc");

        try {
            GotBags = Double.parseDouble(actualBags);
            ActualNoOfBags = Integer.parseInt(roundOffTo0DecPlaces(GotBags));
        } catch (NumberFormatException nfe) {
            Toast.makeText(BluetoothActivity.this, "Could not parse", Toast.LENGTH_SHORT).show();

        }
        /**
         * Bag Recycler View code from Here
         */

        bagViewAdapter = new BagViewAdapter(getApplicationContext(),bagArrayList);

        bagRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        bagRecyclerView.setLayoutManager(linearLayoutManager);
        bagRecyclerView.setAdapter(bagViewAdapter);


        /**
         * Bluetooth code from here
         */
        f30b = new Bluetooth(this);
        f30b.enableBluetooth();
        f30b.setCommunicationCallback(this);
        addDevicesToList();

        Display("Connecting...");
        this.f30b.connectToDevice(f30b.getPairedDevices().get(bluetooth_devices.getSelectedItemPosition()));
        Log.d("Harish",bluetooth_devices.getSelectedItemPosition()+" pos");
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

                Log.e("Harish","Clicked Position"+position);
                Display("Connecting...");
                BluetoothActivity.this.f30b.removeCommunicationCallback();
                BluetoothActivity.this.f30b.disconnect();
                BluetoothActivity.this.f30b = new Bluetooth(BluetoothActivity.this);
                BluetoothActivity.this.f30b.enableBluetooth();
                BluetoothActivity.this.f30b.setCommunicationCallback(BluetoothActivity.this);
                BluetoothActivity.this.f30b.connectToDevice(BluetoothActivity.this.f30b.getPairedDevices().get(position));



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        addBagBtn.setOnClickListener(v -> {
            if (bluetooth_devices.getSelectedItem() != null) {

                double emptyBagWeight = BagWeight(onlyBagWeight.getText().toString());

                // emptyBagWeight shouldn't be more than 3 KG.
                if (emptyBagWeight <= 3) {
//                    addBagAndFetchWeight();
                    addSingleBag();

                    individualBagWeight(onlyBagWeight.getText().toString(), FetchWeight);
                } else {
                    Toast.makeText(BluetoothActivity.this, "Empty Bag weight should not be greater than 3 Kg", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BluetoothActivity.this, "Device is not connected", Toast.LENGTH_SHORT).show();
            }

        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show_exitAlert();
                deleteBag(view);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        if (QuintalWeight != null && netWeightProduct != null && numbag != 0) {
                            new BluetoothActivity.SendBag().execute();
                        } else {
                            Toast.makeText(BluetoothActivity.this, "Please Add Bag First", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        showInternetAlert();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void addDevicesToList(){

        mDeviceList = f30b.getPairedDevices();
        ArrayList<String> stringArrayList = new ArrayList<>();

        for (BluetoothDevice device: mDeviceList) {
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
                        BluetoothActivity.this.f30b.connectToDevice(bluetoothDevice);
                    }
                }, 2000);
            }
        });

    }

    @Override
    public void onDisconnect(BluetoothDevice bluetoothDevice, String str) {
        Display("Disconnected!");
        Display("Connecting again...");
        this.f30b.connectToDevice(bluetoothDevice);

    }

    @Override
    public void onError(String str) {

    }

    @Override
    public void onMessage(String str) {
        liveFeedString = str;
        Display(str);

    }

    //==========================================================================================================


    @Override
    protected void onPause() {
        super.onPause();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onResume() {
        super.onResume();

    }

    // parse weight greeting from bluetooth and show in textview
    public static void receivedData(String data) {

        String[] data_arry = data.split(";")[1].replace("\r\n", "\n").split("\n");
        FetchWeight = data_arry[data_arry.length - 2].replace(" ", "").replaceAll("=0*\\+", "").replaceAll("000.", "00.");

        weightTxt.setText(" " + FetchWeight + " Kg");
        QuintalWeight = SumWeight(startWeight, FetchWeight);
        TotalWeightInQuintal.setText("Total Weight: " + QuintalWeight);
        CalculateNetWeight();
        StoreBagWeight(FetchWeight);

    }

    // to get total weight
    public static String SumWeight(String str1, String str2) {
        try {
            WeightbeforeDeletebag = Double.parseDouble(str2);
            TotalWeight = (Double.parseDouble(str1) + Double.parseDouble(str2));
            startWeight = Double.toString(TotalWeight);
            ConvertedWeight = roundOffTo2DecPlaces(TotalWeight / 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConvertedWeight;
    }

    // store the individual gross weight and net weight
    public void individualBagWeight(String str1, String str2) {
        try {
            bagGrossWeight.add(str2);
            double bagTotalWeight = (Double.parseDouble(str2) - Double.parseDouble(str1));
            bagNetWeight.add(roundOffTo2DecPlaces(bagTotalWeight));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // formatstring to two decimal places
    static String roundOffTo2DecPlaces(double val) {
        return String.format("%.2f", val);
    }

    // formatstring to three decimal places
    static String roundOffTo3DecPlaces(double val) {
        return String.format("%.3f", val);
    }

    // formatstring to five decimal places
    static String roundOffTo5DecPlaces(double val) {
        return String.format("%.5f", val);
    }

    //store empty bagweight
    public double BagWeight(String bag) {
        bag = bag.replace(" ", "");
        Double bagWeight = Double.parseDouble(bag);
        return newbag = bagWeight;
    }

    String roundOffTo0DecPlaces(double val) {
        return String.format("%.0f", val);
    }

    @Override
    protected void onDestroy() {

        Log.d("HarishDestroy","Destory called");
        super.onDestroy();
        if (this.registered) {
            unregisterReceiver(this.mReceiver);
            this.registered = false;
        }
        this.f30b.removeCommunicationCallback();
        this.f30b.disconnect();

    }

    // to clear all static values
    public static void reset() {
        i = 0;
        numbag = 0;
        newbag = 0;
        ActualNoOfBags = 0;
        stringbuilder = null;
        startWeight = "0.0";
    }

    // to calculate netweight
    public static String CalculateNetWeight() {
        double bagsWeight = newbag * i;
        calBagsWeight = Double.toString(bagsWeight);
        double NetWeightofProduct = (TotalWeight - Double.parseDouble(calBagsWeight));
        netWeightProduct = roundOffTo2DecPlaces(NetWeightofProduct);
        return netWeightProduct;
    }

    public void addBagAndFetchWeight() {
        AddBag();
        numbag++;
        NumofBags.setText("No of Bags:  " + numbag);
        onlyBagWeight.setFocusable(false);
    }

    private void addSingleBag() {
        numbag++;
        String bagLabel = "Bag " + numbag;
        String fetchWeightString = " " + liveFeedString + " KG";

        bagArrayList.add(new Bag(bagLabel, fetchWeightString));
        bagViewAdapter.notifyDataSetChanged();

        NumofBags.setText("No of Bags:  " + bagRecyclerView.getAdapter().getItemCount());

        QuintalWeight = SumWeight(startWeight, liveFeedString);
        TotalWeightInQuintal.setText("Total Weight: " + QuintalWeight);
        CalculateNetWeight();
        StoreBagWeight(FetchWeight);


    }


    private void AddBag() {
        try {

//            llh = new LinearLayout(BluetoothActivity.this);
//            llh.setOrientation(LinearLayout.HORIZONTAL);
//            llh.setId(R.id.addHoriBag);
//            i++;

            bagTxt = new TextView(getApplicationContext());
            LinearLayout.LayoutParams textviewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textviewLayoutParams.setMargins(30, 10, 0, 5);

            bagTxt.setLayoutParams(textviewLayoutParams);
            bagTxt.setWidth(200);
            //   bagTxt.setHeight(50);
            bagTxt.setTextColor(Color.parseColor("#000000"));
            bagTxt.setText("Bag " + i);
//            llh.addView(bagTxt);

            weightTxt = new TextView(getApplicationContext());
            LinearLayout.LayoutParams textview1LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textview1LayoutParams.setMargins(30, 10, 0, 5);

            weightTxt.setLayoutParams(textview1LayoutParams);
            weightTxt.setTextColor(Color.parseColor("#000000"));
            weightTxt.setWidth(400);
            // weightTxt.setHeight(50);
            ShapeDrawable sd = new ShapeDrawable();
            // Specify the shape of ShapeDrawable
            sd.setShape(new RectShape());
            // Specify the border color of shape
            sd.getPaint().setColor(Color.BLACK);
            // Set the border width
            sd.getPaint().setStrokeWidth(5f);
            // Specify the style is a Stroke
            sd.getPaint().setStyle(Paint.Style.STROKE);
            sd.setPadding(5, 5, 5, 5);
            // Finally, add the drawable background to TextView
            weightTxt.setBackground(sd);
//            llh.addView(weightTxt);
//            bagsLinearLayout.addView(llh);
        } catch (Exception e) {
            e.printStackTrace();
        } }

    // delete all bag one by one
    public void deleteBag(View v) {
        try {
            Thread.sleep(500);
            View myView = findViewById(R.id.addHoriBag);
            ViewGroup parent = (ViewGroup) myView.getParent();
            int j2 = parent.getChildCount() - 1;
            if (i >= 0 && j2 >= 0 && parent != null) {
                i--;
                numbag--;
                ViewGroup vv = (ViewGroup) parent.getChildAt(j2);
                parent.removeViewAt(j2);
                j2--;
                NumofBags.setText("No of Bags:  " + numbag);
                FetchWeight = weightTxt.getText().toString();
                FetchWeight = FetchWeight.replace(" Kg", "");
                if (FetchWeight != null && FetchWeight != "") {
                    TotalWeight = TotalWeight - Double.parseDouble(FetchWeight);
                    Double newWeight = Double.parseDouble(QuintalWeight);
                    newWeight = newWeight * 100 - WeightbeforeDeletebag;
                    outputWeight = roundOffTo5DecPlaces(TotalWeight / 100);
                    QuintalWeight = outputWeight.toString();
                    TotalWeightInQuintal.setText("Total Weight: " + outputWeight);
                    startWeight = Double.toString(newWeight);
                    CalculateNetWeight();
                    stringbuilder.delete(stringbuilder.length() - 7, stringbuilder.length());
                    onlyBagWeight.setFocusable(true);
                }
                bagNetWeight.clear();
                bagGrossWeight.clear();
                Toast.makeText(BluetoothActivity.this, "Bag deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BluetoothActivity.this, "Please Add Bag First", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(BluetoothActivity.this, "Please Add Bag First", Toast.LENGTH_SHORT).show();
        }
    }

    // to send individual weight
    public String sendIndividualBagWeight() {
        String bagWeight = "", updateBagWeight = "";
        try {
            JSONObject jsonObject;
            JSONArray arr = new JSONArray();
            HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

            for (int k = 0; k < numbag; k++) {
                bagWeight = "{\n" +
                        "\"bagTypeId\": \"" + bagTypeId + "\",\n" +
                        "\"sequence\": \"" + Sequence + "\",\n" +
                        "\"netWeight\": \"" + bagNetWeight.get(k) + "\",\n" +
                        "\"grossWeight\": \"" + bagGrossWeight.get(k) + "\",\n" +
                        "\"tareWeigh\": \"" + newbag + "\"\n" +
                        "}\n";

                jsonObject = new JSONObject();
                jsonObject.put("bagTypeId", bagTypeId);
                jsonObject.put("sequence", Sequence);
                jsonObject.put("netWeight", bagNetWeight.get(k));
                jsonObject.put("grossWeight", bagGrossWeight.get(k));
                jsonObject.put("tareWeigh", String.valueOf(newbag));
                map.put("json" + k, jsonObject);
                arr.put(map.get("json" + k));

            }
            Log.e("The json string is ", "" + arr.toString());
            updateBagWeight = updateBagWeight + arr.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateBagWeight;
    }

    public static void StoreBagWeight(String saveWeight) {
        try {
            saveWeight = saveWeight.replace(" ", "");
            stringbuilder.append(saveWeight + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        "\"grossWeight\": \"" + QuintalWeight + "\",\n" +
                        "\"netWeight\": \"" + netWeightProduct + "\",\n" +
                        "\"noOfBags\": \"" + numbag + "\",\n" +
                        "\"bagTypeId\": \"" + bagTypeId + "\",\n" +
//                        "\"bags\": " + IndividualBagWeight + ",\n" +
                        "\"feeCategoryId\": \"" + feeCategoryId +
                        "\"}\n" +
                        "}";

                Log.e("Data", "Data" + urlParameters);

                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                //URL url = new URL("");
                URL url = new URL("https://train.enam.gov.in/NamWebSrv/rest/SendBagWeightAuto");
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
                Log.e("TAG", "StatusMsg" + StatusMsg);
                if (StatusMsg.equals("S")) {
                    Toast.makeText(BluetoothActivity.this, "Weight Sent Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BluetoothActivity.this, PrintWeighingSlipActivity.class);
                    intent.putExtra("u_name", loginid.toString());
                    intent.putExtra("u_pass", password.toString());
                    intent.putExtra("u_orgid", orgid.toString());
                    intent.putExtra("u_id", userid.toString());
                    intent.putExtra("opr_id", oprId);
                    intent.putExtra("bagTypeId", bagTypeId.toString());
                    intent.putExtra("BagWeight", stringbuilder.toString());
                    intent.putExtra("lotId", lotId.toString());
                    intent.putExtra("BagTypeDesc", BagTypeDesc.toString());
                    intent.putExtra("NoOfbag", Integer.toString(numbag).toString());
                    intent.putExtra("TotalWeight", roundOffTo2DecPlaces(TotalWeight));
                    intent.putExtra("QuintalWeight", QuintalWeight.toString());
                    intent.putExtra("NetWeight", netWeightProduct.toString());
                    intent.putExtra("BagsWeight", calBagsWeight.toString());
                    intent.putExtra("commodityName", Commodity.toString());
                    intent.putExtra("farmerName", SellerName.toString());
                    intent.putExtra("caName", caName.toString());
                    intent.putExtra("lotRate", lotRate.toString());
                    intent.putExtra("actualBags", actualBags.toString());
                    intent.putExtra("traderName", traderName.toString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BluetoothActivity.this, "Weight could not be sent", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                p.dismiss();
                Log.e("TAG", "e" + e.toString());
            }

        }
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BluetoothActivity.this, WelcomeUserActivity.class);
        intent.putExtra("u_name", loginid.toString());
        intent.putExtra("u_pass", password.toString());
        intent.putExtra("username", userActualName.toString());
        intent.putExtra("u_orgid", orgid.toString());
        intent.putExtra("u_id", userid.toString());
        intent.putExtra("opr_id", oprId.toString());
        startActivity(intent);
        BluetoothActivity.this.finish();

    }
}
