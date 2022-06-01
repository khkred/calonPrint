package sg.com.argus.www.conquestgroup.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import io.objectbox.Box;
import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.PrintSlipAdapter;
import sg.com.argus.www.conquestgroup.interfaces.ItemClickListener;
import sg.com.argus.www.conquestgroup.models.ObjectBox;
import sg.com.argus.www.conquestgroup.models.PrintSlip;
import sg.com.argus.www.conquestgroup.utils.Constants;
import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;

public class PrintSlipsList extends AppCompatActivity implements ItemClickListener {

    Box<PrintSlip> box;
    int k = 0;

    SunmiPrintHelper sunmiPrintHelper;

    List<PrintSlip> printSlips;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_slips_list);

        sunmiPrintHelper = SunmiPrintHelper.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        box = ObjectBox.get().boxFor(PrintSlip.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

         printSlips = box.getAll();

        PrintSlipAdapter printSlipAdapter = new PrintSlipAdapter(printSlips);

        recyclerView.setAdapter(printSlipAdapter);

    }

    @Override
    public void onClick(View view, int position) {
        final PrintSlip printSlip = printSlips.get(position);
        try {
            printData(printSlip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printSlip(String content, float size, boolean isBold) {

        sunmiPrintHelper.printText(content, size, false, false, null);
        sunmiPrintHelper.feedPaper();

    }


    // print data from printer
    private void printData(PrintSlip printSlip) throws IOException {

        try {

            StringBuilder printString = new StringBuilder();

            printString.append("Date    :").append(printSlip.formatted_Date).append("\n");

//            WelcomeUserActivity.ngxPrinter.printText("Date    :" + formatted_Date);
            printString.append("Lot Id  :").append(printSlip.lotId).append("\n");
            printString.append("CA Name :").append(printSlip.cName).append("\n");
            printString.append("FARMER NAME     :").append("\n");
            printString.append(printSlip.SName).append("\n");

            printString.append("COMMODITY:").append(printSlip.Com).append("\n");

            printString.append("TRADER   :").append(printSlip.tName).append("\n");
            printString.append("ACTUAL NO OF BAGS  :").append(roundOffTo0DecPlaces(printSlip.ActualNoofBags)).append("\n");
            printString.append("TOTAL NO OF BAGS   :").append(printSlip.noOfBag).append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("SERIAL NO" + "        QUANTITY(Kg)").append("\n");


            for (int i = 0; i < printSlip.bagWeightList.size(); i++) {
                k++;
                printString.append("    ").append(k).append("              ").append(printSlip.bagWeightList.get(i)).append("\n");
            }


            printString.append("-----------------------------").append("\n");
            printString.append("Gross Wt (Qt):     ").append(printSlip.QuintalWeight).append("\n");
            printString.append("Bag Wt (Qt)  :     ").append(roundOffTo3DecPlaces(printSlip.BagsWeightValue / 100)).append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("Net Wt (Qt)  :     ").append(roundOffTo5DecPlaces(printSlip.NetWeightValue / 100)).append("\n");
            printString.append("Lot Amt (Rs) :     ").append(printSlip.lRate).append("\n");
            printString.append("Net Amt (Rs) :     ").append(printSlip.netAmt).append("\n");
            printString.append("Transaction No:").append("\n\t").append(printSlip.transactionNo).append("\n");
            printString.append("Invoice No:     ").append(printSlip.invoiceDocNo).append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("\n\n");
            printString.append("Sign of Farmer").append("\n");
            printString.append("\n\n");
            printString.append("Sign of Dadwal").append("\n");
            printString.append("-----------------------------").append("\n");
            printString.append("\n\n");

            printSlip(printString.toString(), Constants.DEFAULT_PRINT_SIZE, Constants.BOLD_OFF);


        } catch (Exception excep) {
            Toast.makeText(PrintSlipsList.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

}