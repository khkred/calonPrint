package sg.com.argus.www.conquestgroup.activities;

import static java.util.Calendar.getInstance;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import io.objectbox.Box;
import sg.com.argus.www.conquestgroup.BuildConfig;
import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.models.ObjectBox;
import sg.com.argus.www.conquestgroup.models.PrintSlip;
import sg.com.argus.www.conquestgroup.utils.BluetoothUtil;
import sg.com.argus.www.conquestgroup.utils.Constants;
import sg.com.argus.www.conquestgroup.utils.ESCUtil;
import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;


public class PrintWeighingSlipActivity extends AppCompatActivity {
    private String SName, Com, tName, cName, lRate, noOfBag, bagType, TotalWeight, bagTypeId, loginid, password, userid, orgid, lotId, QuintalWeight, NetWeight, BagsWeight, actualBags, netAmt;
    private TextView sellerNAme, commodity, TraderName, CaName, bidPrice, PbagType, PnumBag, lotid, actual_no_of_bag, gross_weight, net_weight, net_amt, bag_weight;
    private Button printBtn;
    LinearLayout ll, llh;
    private TextView bagTxt, weightTxt;
    int j = 0, k = 0;
    private double NetWeightValue, BagsWeightValue, ActualNoofBags;
    String oprId;
    int permissionCheck, permissionCheckWrite;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 99;
    private String userActualName;
    private ArrayList<Double> bagWeightList;

    private String transactionNo, invoiceDocNo;

    private TextView transactionNoTv, invoiceNoTv;

    SunmiPrintHelper sunmiPrintHelper;
    Box<PrintSlip> box;

    //From Sunmi
    private String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "Windows-1252", "CP866", "CP852", "CP858", "CP874", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_weighing_slip);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        final Activity activity = this;

        box = ObjectBox.get().boxFor(PrintSlip.class);
        ll = (LinearLayout) findViewById(R.id.linearLayout);

        sunmiPrintHelper = SunmiPrintHelper.getInstance();

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
        printBtn = (Button) findViewById(R.id.printbtn);
        transactionNoTv = findViewById(R.id.transaction_no_text_view);
        invoiceNoTv = findViewById(R.id.invoice_no_text_view);


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

        transactionNo = intent.getStringExtra("transactionNo");
        invoiceDocNo = intent.getStringExtra("invoiceDocNo");
        /**
         * Get Serialisable
         */
        Bundle bundle = intent.getBundleExtra("bundle");
        bagWeightList = (ArrayList<Double>) bundle.getSerializable("bagWeightList");


        NetWeightValue = Double.parseDouble(NetWeight);
        BagsWeightValue = Double.parseDouble(BagsWeight);
        ActualNoofBags = Double.parseDouble(actualBags);

        AddBag();
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
        transactionNoTv.setText(transactionNo);
        invoiceNoTv.setText(invoiceDocNo);

        Date TodayDate = getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy        HH:mm:ss a");
        final String formattedDate = df.format(TodayDate);


        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendData();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionCheckWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
        } else if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
        }
    }


    public void printSlip(String content, float size, boolean isBold) {

        sunmiPrintHelper.printText(content, size, false, false, null);
        sunmiPrintHelper.feedPaper();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_print_slip,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.past_prints) {
            Intent intent = new Intent(PrintWeighingSlipActivity.this,PrintSlipsList.class);
            startActivity(intent);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
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
            addDataToBox(formatted_Date);

            StringBuilder printString = new StringBuilder();

            printString.append("Date    :").append(formatted_Date).append("\n");

//            WelcomeUserActivity.ngxPrinter.printText("Date    :" + formatted_Date);
            printString.append("Lot Id  :").append(lotId).append("\n");
            printString.append("CA Name :").append(cName).append("\n");
            printString.append("FARMER NAME     :").append("\n");
            printString.append(SName).append("\n");

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
            printString.append("Transaction No:").append("\n\t").append(transactionNo).append("\n");
            printString.append("Invoice No:     ").append(invoiceDocNo).append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("\n\n");
            printString.append("Sign of Farmer").append("\n");
            printString.append("\n\n");
            printString.append("Sign of Dadwal").append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("\n\n");

            printSlip(printString.toString(), Constants.DEFAULT_PRINT_SIZE, Constants.BOLD_OFF);


        } catch (Exception excep) {
            Toast.makeText(PrintWeighingSlipActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDataToBox(String formatted_date) {
        PrintSlip pSlip = new PrintSlip();
        pSlip.formatted_Date = formatted_date;
        pSlip.lotId = lotId;
        pSlip.cName = cName;
        pSlip.SName = SName;
        pSlip.Com = Com;
        pSlip.tName = tName;
        pSlip.ActualNoofBags = ActualNoofBags;
        pSlip.noOfBag = noOfBag;

        List<String> bagWtList = new ArrayList<String>();

        for (double bagWeight : bagWeightList) {
            bagWtList.add(String.valueOf(bagWeight));
        }

        pSlip.bagWeightList = bagWtList;
        pSlip.QuintalWeight = QuintalWeight;
        pSlip.BagsWeightValue = BagsWeightValue;
        pSlip.NetWeightValue = NetWeightValue;
        pSlip.lRate = lRate;
        pSlip.netAmt = netAmt;
        pSlip.transactionNo = transactionNo;
        pSlip.invoiceDocNo = invoiceDocNo;

        box.put(pSlip);
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
        Intent intent = new Intent(PrintWeighingSlipActivity.this, WelcomeUserActivity.class);
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
        intent.putExtra("username", userActualName);
        startActivity(intent);
        finish();
    }
}
