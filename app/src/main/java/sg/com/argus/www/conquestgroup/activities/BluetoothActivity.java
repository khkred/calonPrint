package sg.com.argus.www.conquestgroup.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.BTConnection;
import sg.com.argus.www.conquestgroup.adapters.P25Connector;
import sg.com.argus.www.conquestgroup.adapters.ConnectionDetector;

public class BluetoothActivity extends AppCompatActivity {


    static StringBuilder stringbuilder;
    Spinner bluetooth_devices;
    private P25Connector mConnector;
    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;
    private BluetoothAdapter mBluetoothAdapter;
    public static String BTAddress;
    boolean blueToothconnected = false;
    private final String TAG = BluetoothActivity.class.getSimpleName();
    BTConnection uConn;
    private TextView NumofBags, onlyBagWeight;
    LinearLayout blueDisable;
    LinearLayout blueEnable;
    private static int i = 0, numbag = 0, ActualNoofBags = 0;
    private static TextView bagTxt, weightTxt, TotalBaightInQuintal;
    LinearLayout ll, llh;
    boolean isPrinterConnected;
    Button connect_btn, submit, delete_btn;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ArrayList<String> bagNetWeight = new ArrayList<String>();
    ArrayList<String> bagGrossWeight = new ArrayList<String>();

    private static String startWeight = "0.0", FetchWeight = "", QuintalWeight = "", BagTypeDesc, ConvertedWeight = "", bagConvertedWeight = "";
    private static String outputWeight = "", netWeightProduct, calBagsWeight;
    private static double WeightbeforeDeletebag, TotalWeight, newbag;
    private double GotBags;
    private String loginid, password, orgid, actualBags, userid, bagTypeId, lotId, caName, lotRate, SellerName, Commodity, traderName,feeCategoryId, newBagTypeValue;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    String oprId, Sequence = "1";
    ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        final Activity activity = this;
        cd = new ConnectionDetector(this);
        stringbuilder = new StringBuilder();
        submit = (Button) findViewById(R.id.submit);
        delete_btn = (Button) findViewById(R.id.removebag);

        ll = (LinearLayout) findViewById(R.id.linearLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        blueDisable = (LinearLayout) findViewById(R.id.blueDisable);
        bluetooth_devices = (Spinner) findViewById(R.id.bluetoothDevices);
        onlyBagWeight = (TextView) findViewById(R.id.onlyBagWeight);
        NumofBags = (TextView) findViewById(R.id.numBag);
        TotalBaightInQuintal = (TextView) findViewById(R.id.totalWeight);
        connect_btn = (Button) findViewById(R.id.btn_connect);
        final Intent intent = getIntent();
        loginid = intent.getStringExtra("u_name");
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
            ActualNoofBags = Integer.parseInt(roundOffTo0DecPlaces(GotBags));
        } catch (NumberFormatException nfe) {
            Toast.makeText(BluetoothActivity.this, "Could not parse", Toast.LENGTH_SHORT).show();

        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth_devices.getSelectedItem() != null) {
                    double emptycount = BagWeight(onlyBagWeight.getText().toString());
                    if (emptycount <= 3) {
                        addBAgAndFetchWeight();
                        individualBagWeight(onlyBagWeight.getText().toString(), FetchWeight);
                    } else {
                        Toast.makeText(BluetoothActivity.this, "Empty Bag weight should not be greater than 3 Kg", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BluetoothActivity.this, "Device is not connected", Toast.LENGTH_SHORT).show();
                }

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
        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);

                    updateDeviceList();
                }
            }

            mProgressDlg = new ProgressDialog(this);

            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg = new ProgressDialog(this);

            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {

                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();
                    showConnected();
                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                }
            });
        }
        uConn = new BTConnection();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);


    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Paired");

                    connect();
                }
            }
        }
    };

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size = data.size();
        list = new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, getArray(mDeviceList));

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        bluetooth_devices.setAdapter(adapter);
        bluetooth_devices.setSelection(0);
    }

    private void showDisabled() {
        showToast("Bluetooth disabled");
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");
    }

    private void showConnected() {
        showToast("Connected");
        isPrinterConnected = true;
        bluetooth_devices.setEnabled(false);
    }

    private void showDisonnected() {
        showToast("Disconnected");
        connect_btn.setVisibility(View.VISIBLE);
        bluetooth_devices.setEnabled(true);
    }

    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");

        connect_btn.setEnabled(false);
        bluetooth_devices.setEnabled(false);
    }

    public void createBond(BluetoothDevice device) throws Exception {
        BTAddress = device.toString();
        BTAddress.trim();
        try {
            uConn.openBT(BTAddress);
            Log.e("coonected", "bt connected2");
            Log.e("coonected", BTAddress);
        } catch (IOException e) {

            e.printStackTrace();
        }
        try {
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(bluetooth_devices.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                uConn.openBT(device.getAddress());
                registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");

                return;
            }
        }

        try {
            if (!blueToothconnected) {
                Log.e("coonected", "bt connected2");
                uConn.openBT(device.getAddress());
                Log.e("blueToothconnected", String.valueOf(blueToothconnected));
                Thread.sleep(500);
                blueToothconnected = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        

    }

    void showStatus(TextView theTextView, String theLabel, boolean theValue) {
        String msg = theLabel + ": " + (theValue ? "enabled" : "disabled") + "\n";
        theTextView.append(msg);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onResume() {
        super.onResume();

    }

   // parse weight greeting from bluetooth and show in textview
    public static void receivedData(String data) {

        String[] data_arry = data.split(";")[1].replace("\r\n", "\n").split("\n");
        FetchWeight = data_arry[data_arry.length - 2].replace(" ","").replaceAll("=0*\\+","").replaceAll("000.","00.");

        weightTxt.setText(" " + FetchWeight + " Kg");
        QuintalWeight = SumWeight(startWeight, FetchWeight);
        TotalBaightInQuintal.setText("Total Weight: " + QuintalWeight);
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
        super.onDestroy();
        try {
          //  if(mReceiver!=null)
               // unregisterReceiver(mReceiver);
            BluetoothActivity.reset();
            BTConnection.closeBT();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
   // to clear all static values
    public static void reset() {
        i = 0;
        numbag = 0;
        newbag = 0;
        ActualNoofBags = 0;
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

    public void addBAgAndFetchWeight() {
        AddBag();
        numbag++;
        connect();
        NumofBags.setText("No of Bags:  " + numbag);
        onlyBagWeight.setFocusable(false);
    }
    // to add empty textview for bag1 and bag2 and so on
    private void AddBag() {
       try {
           llh = new LinearLayout(BluetoothActivity.this);
           llh.setOrientation(LinearLayout.HORIZONTAL);
           llh.setId(R.id.addHoriBag);
           i++;

           bagTxt = new TextView(getApplicationContext());
           LinearLayout.LayoutParams textviewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           textviewLayoutParams.setMargins(30, 10, 0, 5);

           bagTxt.setLayoutParams(textviewLayoutParams);
           bagTxt.setWidth(200);
        //   bagTxt.setHeight(50);
           bagTxt.setTextColor(Color.parseColor("#000000"));
           bagTxt.setText("Bag " + i);
           llh.addView(bagTxt);

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
           sd.setPadding(5,5,5,5);
           // Finally, add the drawable background to TextView
           weightTxt.setBackground(sd);
           llh.addView(weightTxt);
           ll.addView(llh);
       }catch (Exception e){
           e.printStackTrace();
       }


    }
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
                    TotalBaightInQuintal.setText("Total Weight: " + outputWeight);
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
                jsonObject.put("bagTypeId",bagTypeId);
                jsonObject.put("sequence",Sequence);
                jsonObject.put("netWeight",bagNetWeight.get(k));
                jsonObject.put("grossWeight",bagGrossWeight.get(k));
                jsonObject.put("tareWeigh",String.valueOf(newbag));
                map.put("json" + k, jsonObject);
                arr.put(map.get("json" + k));

            }
            Log.e("The json string is ","" + arr.toString());
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
                IndividualBagWeight = sendIndividualBagWeight();
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
                        "\"bags\": " + IndividualBagWeight +
                        ",\n"+
                        "\"feeCategoryId\": \"" +feeCategoryId +
                        "\"}\n" +
                        "}";

                Log.e("Data","Data"+urlParameters);

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
                Log.e("TAG","excep"+excep);
            }
            return text;
        }

        protected void onPostExecute(String result) {
            try {
                p.dismiss();
                JSONObject object = new JSONObject(result);
                String StatusMsg = object.getString("statusMsg");
                Log.e("TAG","StatusMsg"+StatusMsg);
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
                Log.e("TAG","e"+e.toString());
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
        intent.putExtra("u_orgid", orgid.toString());
        intent.putExtra("u_id", userid.toString());
        intent.putExtra("opr_id", oprId.toString());
        startActivity(intent);
        finish();
    }
}
