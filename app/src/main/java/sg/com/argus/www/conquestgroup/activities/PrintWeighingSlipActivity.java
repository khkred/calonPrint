package sg.com.argus.www.conquestgroup.activities;

import static java.util.Calendar.getInstance;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sg.com.argus.www.conquestgroup.BuildConfig;
import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.utils.BluetoothUtil;
import sg.com.argus.www.conquestgroup.utils.ESCUtil;
import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;


public class PrintWeighingSlipActivity extends AppCompatActivity {
    private String SName, Com, tName, cName, lRate, noOfBag, bagType, TotalWeight, bagTypeId, loginid, password, userid, orgid, lotId, QuintalWeight, NetWeight, BagsWeight, actualBags, netAmt;
    private TextView sellerNAme, commodity, TraderName, CaName, bidPrice, PbagType, PnumBag, lotid, actual_no_of_bag, gross_weight, net_weight, net_amt, bag_weight;
    private Button printBtn, saveBtn,closebtn;
    LinearLayout ll, llh;
    private TextView bagTxt, weightTxt;
    int j = 0, k = 0;
    private double NetWeightValue, BagsWeightValue, ActualNoofBags;
    String oprId;
    int permissionCheck, permissionCheckWrite;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 99;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread thread;
    private byte[] readBuffer;
    private int readBufferPos;
    private boolean stopWorker;
    private Spinner deviceSpinner;
    private ArrayAdapter<String> arrayAdapter;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    List<BluetoothDevice> devices = new ArrayList<>();
    private String userActualName;
    private ArrayList<Double> bagWeightList;



    //From Sunmi
    private String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "Windows-1252", "CP866", "CP852", "CP858", "CP874",  "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_weighing_slip);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        final Activity activity = this;
        ll = (LinearLayout) findViewById(R.id.linearLayout);

        sellerNAme = (TextView) findViewById(R.id.sellName);
        commodity = (TextView) findViewById(R.id.commod);
        TraderName = (TextView) findViewById(R.id.tradName);
        PbagType = (TextView) findViewById(R.id.PbagType);
        PnumBag = (TextView) findViewById(R.id.PnumBag);
        lotid = (TextView) findViewById(R.id.lotid);
        bidPrice = (TextView) findViewById(R.id.bidPrice);
        CaName = (TextView) findViewById(R.id.Name);
        actual_no_of_bag = (TextView) findViewById(R.id.actual_no_of_bag);
        gross_weight = (TextView) findViewById(R.id.gross_weight);
        bag_weight = (TextView) findViewById(R.id.bag_weight);
        net_weight = (TextView) findViewById(R.id.net_weight);
        net_amt = (TextView) findViewById(R.id.net_amt);
        saveBtn = (Button) findViewById(R.id.savebtn);
        printBtn = (Button) findViewById(R.id.printbtn);
        //closebtn = (Button) findViewById(R.id.closebtn);
        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("--select--");
        deviceSpinner.setAdapter(arrayAdapter);

        if (bluetoothDevice != null)
            openBluetooth();

        final Intent intent = getIntent();
        loginid = intent.getStringExtra("u_name");
        password = intent.getStringExtra("u_pass");
        orgid = intent.getStringExtra("u_orgid");
        userid = intent.getStringExtra("u_id");
        oprId = intent.getStringExtra("opr_id");
        lotId = getIntent().getStringExtra("lotId");
        bagTypeId = intent.getStringExtra("bagTypeId");
        bagType = getIntent().getStringExtra("BagTypeDesc");
        noOfBag = getIntent().getStringExtra("NoOfbag");
        TotalWeight = getIntent().getStringExtra("TotalWeight");
        QuintalWeight = getIntent().getStringExtra("QuintalWeight");
        NetWeight = getIntent().getStringExtra("NetWeight");
        actualBags = getIntent().getStringExtra("actualBags");
        BagsWeight = getIntent().getStringExtra("BagsWeight");
        userActualName = intent.getStringExtra("username");

        /**
         * Get Serialisable
         */
        Bundle bundle = intent.getBundleExtra("bundle");
        bagWeightList = (ArrayList<Double>)bundle.getSerializable("bagWeightList");



        NetWeightValue = Double.parseDouble(NetWeight);
        BagsWeightValue = Double.parseDouble(BagsWeight);
        ActualNoofBags = Double.parseDouble(actualBags);

        AddBag();
        findBluetooth();
        SName = intent.getStringExtra("farmerName");
        Com = intent.getStringExtra("commodityName");
        cName = intent.getStringExtra("caName");
        lRate = intent.getStringExtra("lotRate");
        tName = intent.getStringExtra("traderName");

        if (lRate.contains("null")) {
            lRate = "0";
        }

        CalNetAmout();
        lotid.setText("" + lotId);
        CaName.setText("" + cName);
        sellerNAme.setText("" + SName);
        commodity.setText("" + Com);
        TraderName.setText("" + tName);
        PbagType.setText("" + bagType);
        PnumBag.setText("" + noOfBag);
        actual_no_of_bag.setText("" + roundOffTo0DecPlaces(ActualNoofBags));

        gross_weight.setText("" + QuintalWeight);
        bag_weight.setText("" + roundOffTo3DecPlaces(BagsWeightValue / 100));

        net_weight.setText("" + roundOffTo3DecPlaces(NetWeightValue / 100));
        bidPrice.setText("" + lRate);
        net_amt.setText("" + netAmt);

        Date TodayDate = getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy        HH:mm:ss a");
        final String formattedDate = df.format(TodayDate);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createandDisplayPdf(formattedDate);
            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createandDisplayPdf(formattedDate);
                try {
                    sendData();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
      /*  closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    closeBluetooth();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });*/
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionCheckWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
        } else if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
        }
    }



    public void findBluetooth() {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "No Bluetooth Adapter Available!", Toast.LENGTH_SHORT).show();
            }
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
            } else {

                showScannedList();
            }


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Bluetooth Connection Failed!" + ex, Toast.LENGTH_SHORT).show();
        }
    }

    public void printSampleText(String content) {

        float size = 24;
        if (!BluetoothUtil.isBlueToothPrinter) {
            SunmiPrintHelper.getInstance().printText(content, size, true, false, null);
            SunmiPrintHelper.getInstance().feedPaper();
        } else {
            printByBluTooth(content);
        }
    }

    private void printByBluTooth(String content) {
        try {
            if (true) {
                BluetoothUtil.sendData(ESCUtil.boldOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.boldOff());
            }

            if (true) {
                BluetoothUtil.sendData(ESCUtil.underlineWithOneDotWidthOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.underlineOff());
            }
            int record =17;

            if (record < 17) {
                BluetoothUtil.sendData(ESCUtil.singleByte());
                BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(record)));
            } else {
                BluetoothUtil.sendData(ESCUtil.singleByteOff());
                BluetoothUtil.sendData(ESCUtil.setCodeSystem(codeParse(record)));
            }

            BluetoothUtil.sendData(content.getBytes(mStrings[record]));
            BluetoothUtil.sendData(ESCUtil.nextLine(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private byte codeParse(int value) {
        byte res = 0x00;
        switch (value) {
            case 0:
                res = 0x00;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                res = (byte) (value + 1);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                res = (byte) (value + 8);
                break;
            case 12:
                res = 21;
                break;
            case 13:
                res = 33;
                break;
            case 14:
                res = 34;
                break;
            case 15:
                res = 36;
                break;
            case 16:
                res = 37;
                break;
            case 17:
            case 18:
            case 19:
                res = (byte) (value - 17);
                break;
            case 20:
                res = (byte) 0xff;
                break;
            default:
                break;
        }
        return (byte) res;
    }

    public void openBluetooth() {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            //bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
          /*  if (bluetoothSocket == null && (outputStream == null || inputStream == null)) {
                Toast.makeText(getApplicationContext(), "Device is offline", Toast.LENGTH_SHORT).show();
                return;
            } else {*/
                listenFromData();
           // }
        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), "Open Bluetooth" + ex, Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private void listenFromData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPos = 0;
            readBuffer = new byte[1024];

                synchronized (inputStream) {
                    inputStream.wait(200);
                }
                String data = "";
                while (inputStream.available() > 0) {
                    final byte[] packetBytes = new byte[inputStream.available()];
                    inputStream.read(packetBytes);
                }
                if (data == "" && data == null) {
                    return;
                }
          /*  thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = inputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPos];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPos = 0;

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Data" + data, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPos++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            thread.start();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void closeBluetooth() throws IOException {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //logo
    public final String fileName = "drawable/Conquest1.png";
    // print data from printer
    private void sendData() throws IOException {
        try {

            /*Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/DroidSansMono.ttf");

            Bitmap bmp = getBitmapFromAssets(fileName);
            WelcomeUserActivity.ngxPrinter.addImage(bmp);
            bmp.recycle();*/
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formatted_Date = df.format(c.getTime());

            StringBuilder printString = new StringBuilder();

            printString.append("Date    :").append(formatted_Date).append("\n");

//            WelcomeUserActivity.ngxPrinter.printText("Date    :" + formatted_Date);
         printString.append("Lot Id  :").append(lotId).append("\n");


            printString.append("CA Name :").append(cName).append("\n");
            printString.append("FARMER NAME     :").append("\n");
            printString.append( SName).append("\n");
            printString.append("COMMODITY:").append(Com).append("\n");
            printString.append("TRADER   :").append(tName).append("\n");
            printString.append("ACTUAL NO OF BAGS  :").append(roundOffTo0DecPlaces(ActualNoofBags)).append("\n");
            printString.append("TOTAL NO OF BAGS   :").append(noOfBag).append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("SERIAL NO" + "        QUANTITY(Kg)").append("\n");
            for (int i = 0; i < bagWeightList.size(); i++) {
                k++;
                printString.append("    ").append(k).append("              ").append(bagWeightList.get(i)).append("\n");
            }

            printString.append("-----------------------------").append("\n");
            printString.append("Gross Wt (Qt):     ").append(QuintalWeight).append("\n");
            printString.append("Bag Wt (Qt)  :     ").append(roundOffTo3DecPlaces(BagsWeightValue / 100)).append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("Net Wt (Qt)  :     ").append(roundOffTo5DecPlaces(NetWeightValue / 100)).append("\n");
           printString.append("Lot Amt (Rs) :     ").append(lRate).append("\n");
            printString.append("Net Amt (Rs) :     ").append(netAmt).append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("\n\n");
            printString.append("Sign of Farmer").append("\n");
           printString.append("\n\n");
            printString.append("Sign of Dadwal").append("\n");
            printString.append("-----------------------------").append("\n");
             printString.append("\n\n");

            printSampleText(printString.toString());


            /*print(translator.toMiniLeft("Date       :" + formatted_Date));
            print(translator.toMiniLeft("Lot Id     :" + lotId));
            print(translator.toMiniLeft("CA Name :" + cName));
            print(translator.toMiniLeft("FARMER NAME:" + SName));
            print(translator.toMiniLeft("COMMODITY  :" + Com));
            print(translator.toMiniLeft("TRADER     :" + tName));
            print(translator.toMiniLeft("ACTUAL NO OF BAGS:" + roundOffTo0DecPlaces(ActualNoofBags)));
            print(translator.toMiniLeft("TOTAL NO OF BAGS :" + noOfBag));
            print(translator.toMiniLeft("------------------------------"));

            print(translator.toMiniLeft("SERIAL NO" + "        QUANTITY(Kg)"));
            for (int i = 0; i < dr.length; i++) {
                k++;
                print(translator.toMiniLeft("    " + k + "              " + dr[i]));
            }
            print(translator.toMiniLeft("-------------------------------"));
            print(translator.toMiniLeft("Gross Wt (Qt):     " + QuintalWeight));
            print(translator.toMiniLeft("Bag Wt (Qt)  :     " + roundOffTo3DecPlaces(BagsWeightValue / 100)));
            print(translator.toMiniLeft("-------------------------------"));
            print(translator.toMiniLeft("Net Wt (Qt)  :     " + roundOffTo3DecPlaces(NetWeightValue / 100)));
            print(translator.toMiniLeft("Lot Amt (Rs) :     " + lRate));
            print(translator.toMiniLeft("Net Amt (Rs) :     " + netAmt));
            print(translator.toMiniLeft("-------------------------------"));
            print(translator.toMiniLeft("\n"));
            print(translator.toMiniLeft("Sign of Farmer"));
            print(translator.toMiniLeft("\n"));
            print(translator.toMiniLeft("Sign of Dadwal"));
            print(translator.toMiniLeft("-------------------------------"));
            print(translator.toMiniLeft("\n"));*/
        } catch (Exception excep) {
            Toast.makeText(PrintWeighingSlipActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void showScannedList() {
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        if (deviceSet.size() > 0) {
            for (BluetoothDevice device : deviceSet) {
                arrayAdapter.add(device.getName());
            }
            devices.addAll(deviceSet);
            deviceSpinner.setAdapter(arrayAdapter);
            deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position > 0)
                        bluetoothDevice = (BluetoothDevice) devices.get(position - 1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }
    }

    private void print(byte[] cmd) {
        try {


           /* openBluetooth();
            outputStream.write(cmd);
            outputStream.flush();
            closeBluetooth();*/
        } catch (Exception e) {
            try {
               /* closeBluetooth();*/
                e.printStackTrace();
            }catch (Exception ex){

            }
        }
    }

    private void AddBag() {

        for (int i = 0; i < bagWeightList.size(); i++) {
            j++;
            llh = new LinearLayout(PrintWeighingSlipActivity.this);
            llh.setOrientation(LinearLayout.HORIZONTAL);
            bagTxt = new TextView(getApplicationContext());
            LinearLayout.LayoutParams textviewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textviewLayoutParams.setMargins(60, 10, 0, 5);
            bagTxt.setLayoutParams(textviewLayoutParams);
            bagTxt.setWidth(200);
            bagTxt.setHeight(50);
            bagTxt.setText(" " + j);

            bagTxt.setTextColor(Color.parseColor("#000000"));
            llh.addView(bagTxt);
            weightTxt = new TextView(getApplicationContext());
            LinearLayout.LayoutParams textview1LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textview1LayoutParams.setMargins(85, 10, 0, 5);
            weightTxt.setLayoutParams(textview1LayoutParams);
            weightTxt.setTextColor(Color.parseColor("#000000"));
            weightTxt.setWidth(400);
            weightTxt.setHeight(50);
            weightTxt.setText(" " + bagWeightList.get(i) + " Kg");

            llh.addView(weightTxt);
            ll.addView(llh);
        }
    }

    String roundOffTo2DecPlaces(double val) {
        return String.format("%.2f", val);
    }

    String roundOffTo3DecPlaces(double val) {
        return String.format("%.3f", val);
    }

    String roundOffTo5DecPlaces(double val) {
        return String.format("%.5f", val);
    }

    String roundOffTo0DecPlaces(double val) {
        return String.format("%.0f", val);
    }

    public void createandDisplayPdf(String date) {

        //Creating a global Doc Variable
        // Document doc = new Document(PageSize.A4, 150, 5, 25, 5);
        Document doc = new Document(PageSize.A4, 150, 5, 25, 5);


        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WeighingScale";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "WeighingSlip.pdf");


            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();
            //add paragraph to document
            doc.add(new Paragraph("Date                               : " + date));
            doc.add(new Paragraph("Lot Id                          : " + lotId, boldFont));
            doc.add(new Paragraph("COMM AGENT NAME   : " + cName));
            doc.add(new Paragraph("FARMER NAME             : " + SName));
            doc.add(new Paragraph("COMMODITY                 : " + Com));
            doc.add(new Paragraph("TRADER                         : " + tName));
            doc.add(new Paragraph("ACTUAL NO OF BAGS  : " + roundOffTo0DecPlaces(ActualNoofBags)));
            doc.add(new Paragraph("TOTAL NO OF BAGS     : " + noOfBag));
            doc.add(new Paragraph("--------------------------------------------------------------------"));
            doc.add(new Paragraph("SERIAL NO   " + "              QUANTITY(Kg)", boldFont));
            for (int i = 0; i < bagWeightList.size(); i++) {
                k++;
                doc.add(new Paragraph("     " + k + "                                         " + bagWeightList.get(i), boldFont));
            }
            doc.add(new Paragraph("--------------------------------------------------------------------"));
            doc.add(new Paragraph("GROSS(QT)  :                       " + QuintalWeight, boldFont));
            doc.add(new Paragraph("BAG(QT)  :                            " + roundOffTo3DecPlaces(BagsWeightValue / 100), boldFont));
            doc.add(new Paragraph("--------------------------------------------------------------------"));
            doc.add(new Paragraph("Net (QT) :                               " + roundOffTo3DecPlaces(NetWeightValue / 100), boldFont));
            doc.add(new Paragraph("Lot Amt (RS) :                       " + lRate, boldFont));
            doc.add(new Paragraph("Net Amt (RS) :                       " + netAmt, boldFont));
            doc.add(new Paragraph("--------------------------------------------------------------------"));
            doc.add(new Paragraph("Sign of Farmer"));
            doc.add(new Paragraph("                                                                     "));
            doc.add(new Paragraph("Sign of Dadwal"));
        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            k = 0;
            doc.close();
        }
        try {
            Toast.makeText(PrintWeighingSlipActivity.this, "Weighing Slip PDF Saved Successfully", Toast.LENGTH_SHORT).show();
            //viewPdf("WeighingSlip.pdf", "WeighingScale");
        } catch (Exception ex) {
            Log.e("PDFViewer", "ioException:" + ex);
        }

    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);

        Uri path = FileProvider.getUriForFile(PrintWeighingSlipActivity.this, BuildConfig.APPLICATION_ID + ".provider", pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(PrintWeighingSlipActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    public void CalNetAmout() {
        double Netamount = Double.parseDouble(lRate) * Double.parseDouble(roundOffTo2DecPlaces(NetWeightValue / 100));
        netAmt = roundOffTo2DecPlaces(Netamount);
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PrintWeighingSlipActivity.this, BluetoothActivity.class);
        intent.putExtra("u_name", loginid.toString());
        intent.putExtra("u_pass", password.toString());
        intent.putExtra("u_orgid", orgid.toString());
        intent.putExtra("u_id", userid.toString());
        intent.putExtra("opr_id", oprId.toString());
        intent.putExtra("commodityName", commodity.getText().toString());
        intent.putExtra("lotId", lotId.toString());
        intent.putExtra("farmerName", sellerNAme.getText().toString());
        intent.putExtra("caName", CaName.getText().toString());
        intent.putExtra("lotRate", bidPrice.getText().toString());
        intent.putExtra("traderName", TraderName.getText().toString());
        intent.putExtra("BagTypeDesc", PbagType.getText().toString());
        intent.putExtra("NoOfbag", PnumBag.getText().toString());
        intent.putExtra("actualBags", actualBags.toString());
        intent.putExtra("bagTypeId", bagTypeId.toString());
        intent.putExtra("TotalWeight", TotalWeight.toString());
        intent.putExtra("username",userActualName);
        startActivity(intent);
        finish();
    }
}
