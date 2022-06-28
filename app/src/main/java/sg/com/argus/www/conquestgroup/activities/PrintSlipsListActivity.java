package sg.com.argus.www.conquestgroup.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import io.objectbox.Box;
import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.PrintSlipAdapter;
import sg.com.argus.www.conquestgroup.models.ObjectBox;
import sg.com.argus.www.conquestgroup.models.PrintSlip;
import sg.com.argus.www.conquestgroup.utils.Constants;
import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;

public class PrintSlipsListActivity extends AppCompatActivity implements PrintSlipAdapter.PrintSlipClickListener {

    Box<PrintSlip> box;
    int k = 0;

    List<PrintSlip> printSlips;
    SunmiPrintHelper sunmiPrintHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_slips_list);

        sunmiPrintHelper = SunmiPrintHelper.getInstance();
        sunmiPrintHelper.initSunmiPrinterService(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        box = ObjectBox.get().boxFor(PrintSlip.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        printSlips = box.getAll();

        PrintSlipAdapter printSlipAdapter = new PrintSlipAdapter(printSlips, this);

        recyclerView.setAdapter(printSlipAdapter);

    }

    private void printData(PrintSlip printSlip) throws IOException {

        k = 0;

        String formatted_Date = printSlip.formatted_Date;
        String lotId = printSlip.lotId;
        String cName = printSlip.cName;
        String SName = printSlip.SName;
        String Com = printSlip.Com;
        String tName = printSlip.tName;
        String noOfBag = printSlip.noOfBag;
        double ActualNoOfBags = printSlip.ActualNoofBags;
        List<String> bagWeightList = printSlip.bagWeightList;
        String QuintalWeight = printSlip.QuintalWeight;
        double BagsWeightValue = printSlip.BagsWeightValue;
        double NetWeightValue = printSlip.NetWeightValue;
        String lRate = printSlip.lRate;
        String netAmt = printSlip.netAmt;
        String transactionNo = printSlip.transactionNo;
        String invoiceDocNo = printSlip.invoiceDocNo;
        try {



            StringBuilder printString = new StringBuilder();

            printString.append("Date    :").append(formatted_Date).append("\n");

//            WelcomeUserActivity.ngxPrinter.printText("Date    :" + formatted_Date);
            printString.append("Lot Id  :").append(lotId).append("\n");


            printString.append("CA Name :").append(cName).append("\n");
            printString.append("FARMER NAME     :").append("\n");
            printString.append( SName).append("\n");
            printString.append("COMMODITY:").append(Com).append("\n");
            printString.append("TRADER   :").append(tName).append("\n");
            printString.append("ACTUAL NO OF BAGS  :").append(roundOffTo0DecPlaces(ActualNoOfBags)).append("\n");
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

            printSunmi(printString.toString(), Constants.DEFAULT_PRINT_SIZE,Constants.BOLD_OFF);



        } catch (Exception excep) {
            Toast.makeText(PrintSlipsListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void printSunmi(String content, float size, boolean isBold) {

        sunmiPrintHelper.printText(content, size, false, false, null);
        sunmiPrintHelper.feedPaper();

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


    @Override
    public void onSlipItemClick(int position) {
        try {
            printData(printSlips.get(position));
        } catch (IOException e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        Log.d("printharish", printSlips.get(position).toString());

    }
}
