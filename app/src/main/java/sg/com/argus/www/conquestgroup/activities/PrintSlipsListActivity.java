package sg.com.argus.www.conquestgroup.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mswipetech.wisepad.sdk.Print;
import com.socsi.smartposapi.printer.Align;
import com.socsi.smartposapi.printer.FontLattice;
import com.socsi.smartposapi.printer.Printer2;
import com.socsi.smartposapi.printer.TextEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.objectbox.Box;
import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.PrintSlipAdapter;
import sg.com.argus.www.conquestgroup.models.ObjectBox;
import sg.com.argus.www.conquestgroup.models.PrintSlip;

public class PrintSlipsListActivity extends AppCompatActivity implements PrintSlipAdapter.PrintSlipClickListener {

    Box<PrintSlip> box;
    int k = 0;

    List<PrintSlip> printSlips;

    private static Printer2 printer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_slips_list);

        Print.init(PrintSlipsListActivity.this);

        printer = Printer2.getInstance(PrintSlipsListActivity.this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        box = ObjectBox.get().boxFor(PrintSlip.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        printSlips = box.getAll();

        PrintSlipAdapter printSlipAdapter = new PrintSlipAdapter(printSlips, this);

        recyclerView.setAdapter(printSlipAdapter);

    }

    void defaultPrint(String content) {
        printer.appendTextEntity2(new TextEntity(content, FontLattice.TWENTY_FOUR, true, Align.LEFT, true));
    }

    void defaultPrint() {
        printer.appendTextEntity2(new TextEntity("", FontLattice.TWENTY_FOUR, true, Align.LEFT, true));
    }

    void defaultPrint(String content, Align align, boolean lineBreak) {
        printer.appendTextEntity2(new TextEntity(content, FontLattice.TWENTY_FOUR, true, align, lineBreak));
    }

    private void printData(PrintSlip printSlip) throws IOException {
        k = 0;

        String formatted_date = printSlip.formatted_Date;
        String lotId = printSlip.lotId;
        String cName = printSlip.cName;
        String SName = printSlip.SName;
        String Com = printSlip.Com;
        String tName = printSlip.tName;
        String noOfBag = printSlip.noOfBag;
        double ActualNoofBags = printSlip.ActualNoofBags;
        List<String> bagWeightList = printSlip.bagWeightList;
        String QuintalWeight = printSlip.QuintalWeight;
        double BagsWeightValue = printSlip.BagsWeightValue;
        double NetWeightValue = printSlip.NetWeightValue;
        String lRate = printSlip.lRate;
        String netAmt = printSlip.netAmt;
        String transactionNo = printSlip.transactionNo;
        String invoiceDocNo = printSlip.invoiceDocNo;


        try {
            defaultPrint("Date:", Align.LEFT, false);
            defaultPrint(formatted_date, Align.CENTER, false);
            defaultPrint();

            defaultPrint("Lot Id:", Align.LEFT, false);
            defaultPrint(lotId, Align.CENTER, false);
            defaultPrint();

            defaultPrint("CA Name:", Align.LEFT, false);
            defaultPrint(cName, Align.CENTER, false);
            defaultPrint();

            defaultPrint("FARMER NAME:", Align.LEFT, false);

            defaultPrint(SName, Align.RIGHT, false);
            defaultPrint();

            defaultPrint("COMMODITY: " + Com);
            defaultPrint("TRADER: " + tName);

            defaultPrint("ACTUAL NO OF BAGS: " + roundOffTo0DecPlaces(ActualNoofBags));

            defaultPrint("TOTAL NO OF BAGS: " + noOfBag);

            defaultPrint("-----------------------------");

            defaultPrint("SERIAL NO", Align.LEFT, false);
            defaultPrint("QUANTITY(Kg)", Align.RIGHT, false);
            defaultPrint();

            for (int i = 0; i < bagWeightList.size(); i++) {
                k++;
                defaultPrint(String.valueOf(k), Align.LEFT, false);
                defaultPrint(String.valueOf(bagWeightList.get(i)), Align.RIGHT, false);
                defaultPrint();
            }
            printer.startPrint();

            defaultPrint("-----------------------------");

            defaultPrint("Gross Wt (Qt):", Align.LEFT, false);
            defaultPrint(String.valueOf(QuintalWeight), Align.RIGHT, false);
            defaultPrint();

            defaultPrint("Bag Wt (Qt):", Align.LEFT, false);
            defaultPrint(String.valueOf(roundOffTo3DecPlaces(BagsWeightValue / 100)), Align.RIGHT, false);
            defaultPrint();

            defaultPrint("-----------------------------");

            defaultPrint("Net Wt (Qt):", Align.LEFT, false);
            defaultPrint(String.valueOf(roundOffTo5DecPlaces(NetWeightValue / 100)), Align.RIGHT, false);
            defaultPrint();

            defaultPrint("Lot Amt(Rs):", Align.LEFT, false);
            defaultPrint(lRate, Align.RIGHT, false);
            defaultPrint();

            defaultPrint("Net Amt(Rs):", Align.LEFT, false);
            defaultPrint(netAmt, Align.RIGHT, false);
            defaultPrint();

            defaultPrint("Transaction No:");
            defaultPrint(" " + transactionNo);
            defaultPrint();

            // printString.append("Invoice No:     ").append(invoiceDocNo).append("\n");
            defaultPrint("Invoice No:", Align.LEFT, false);
            defaultPrint(invoiceDocNo, Align.RIGHT, false);
            defaultPrint();

            defaultPrint("-----------------------------");
            printer.startPrint();
            Print.StartPrinting();
            Print.StartPrinting();

            // printString.append("Sign of Farmer").append("\n");
            defaultPrint("Sign of Farmer");
            Print.StartPrinting();
            Print.StartPrinting();

            // printString.append("Sign of Dadwal").append("\n");
            defaultPrint("Sign of Dadwal");

            //   printString.append("-----------------------------").append("\n");
            defaultPrint("-----------------------------");
            Print.StartPrinting();
            Print.StartPrinting();
        } catch (Exception exception) {
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

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
