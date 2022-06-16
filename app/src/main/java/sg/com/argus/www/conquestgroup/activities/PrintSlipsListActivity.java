package sg.com.argus.www.conquestgroup.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import sg.com.argus.www.conquestgroup.interfaces.ItemClickListener;
import sg.com.argus.www.conquestgroup.models.ObjectBox;
import sg.com.argus.www.conquestgroup.models.PrintSlip;

public class PrintSlipsListActivity extends AppCompatActivity{

    Box<PrintSlip> box;
    int k = 0;

    List<PrintSlip> printSlips;

    private static Printer2 a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_slips_list);

        a = Printer2.getInstance(PrintSlipsListActivity.this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        box = ObjectBox.get().boxFor(PrintSlip.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        printSlips = box.getAll();

        PrintSlipAdapter printSlipAdapter = new PrintSlipAdapter(printSlips, new PrintSlipAdapter.ItemClickListener() {
            @Override
            public void onItemClick(PrintSlip printSlip) {
                Toast.makeText(PrintSlipsListActivity.this, "Printing the "+printSlip.toString(), Toast.LENGTH_SHORT).show();

                try {
                    printData(printSlip);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PrintSlipsListActivity.this, "Error printing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(printSlipAdapter);

    }

    void defaultPrint(String content){
        a.appendTextEntity2(new TextEntity(content , FontLattice.TWENTY_FOUR, true, Align.LEFT, true));
    }

    void defaultPrint() {
        a.appendTextEntity2(new TextEntity("" ,FontLattice.TWENTY_FOUR, true, Align.LEFT, true));
    }

    void defaultPrint(String content, Align align, boolean lineBreak){
        a.appendTextEntity2(new TextEntity(content ,FontLattice.TWENTY_FOUR, true, align, lineBreak));
    }

    private void printData(PrintSlip printSlip) throws IOException {
        k =0;
        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formatted_Date = df.format(c.getTime());

            defaultPrint("Date:",Align.LEFT,false);
            defaultPrint(formatted_Date,Align.CENTER,false);
            defaultPrint();

            defaultPrint("Lot Id:",Align.LEFT,false);
            defaultPrint(printSlip.lotId,Align.CENTER,false);
            defaultPrint();

            defaultPrint("CA Name:",Align.LEFT,false);
            defaultPrint(printSlip.cName,Align.CENTER,false);
            defaultPrint();

            defaultPrint("FARMER NAME:",Align.LEFT,false);

            defaultPrint(printSlip.SName,Align.RIGHT,false);
            defaultPrint();

            defaultPrint("COMMODITY: "+printSlip.Com);
            defaultPrint("TRADER: "+printSlip.tName);

            defaultPrint("ACTUAL NO OF BAGS: "+roundOffTo0DecPlaces(printSlip.ActualNoofBags));

            //printString.append("TOTAL NO OF BAGS   :").append(noOfBag).append("\n");
            defaultPrint("TOTAL NO OF BAGS: "+printSlip.noOfBag);

            //printString.append("-----------------------------").append("\n");
            defaultPrint("-----------------------------");

            //     printString.append("SERIAL NO" + "        QUANTITY(Kg)").append("\n");
            defaultPrint("SERIAL NO",Align.LEFT,false);
            defaultPrint("QUANTITY(Kg)",Align.RIGHT,false);
            defaultPrint();

            for (int i = 0; i < printSlip.bagWeightList.size(); i++) {
                k++;
                defaultPrint(String.valueOf(k),Align.LEFT,false);
                defaultPrint(String.valueOf(printSlip.bagWeightList.get(i)),Align.RIGHT,false);
                defaultPrint();
            }
            a.startPrint();

            //  printString.append("-----------------------------").append("\n");
            defaultPrint("-----------------------------");

            // printString.append("Gross Wt (Qt):     ").append(QuintalWeight).append("\n");
            defaultPrint("Gross Wt (Qt):",Align.LEFT,false);
            defaultPrint(String.valueOf(printSlip.QuintalWeight),Align.RIGHT,false);
            defaultPrint();

            // printString.append("Bag Wt (Qt):     ").append(roundOffTo3DecPlaces(BagsWeightValue / 100)).append("\n");
            defaultPrint("Bag Wt (Qt):",Align.LEFT,false);
            defaultPrint(String.valueOf(roundOffTo3DecPlaces(printSlip.BagsWeightValue / 100)),Align.RIGHT,false);
            defaultPrint();

            //     printString.append("-----------------------------").append("\n");
            defaultPrint("-----------------------------");

            // printString.append("Net Wt (Qt):     ").append(roundOffTo5DecPlaces(NetWeightValue / 100)).append("\n");
            defaultPrint("Net Wt (Qt):",Align.LEFT,false);
            defaultPrint(String.valueOf(roundOffTo5DecPlaces(printSlip.NetWeightValue / 100)),Align.RIGHT,false);
            defaultPrint();

            //   printString.append("Lot Amt(Rs):     ").append(lRate).append("\n");
            defaultPrint("Lot Amt(Rs):",Align.LEFT,false);
            defaultPrint(printSlip.lRate,Align.RIGHT,false);
            defaultPrint();

            //  printString.append("Net Amt(Rs):     ").append(netAmt).append("\n");
            defaultPrint("Net Amt(Rs):",Align.LEFT,false);
            defaultPrint(printSlip.netAmt,Align.RIGHT,false);
            defaultPrint();

            //  printString.append("Transaction No:").append("\n\t").append(transactionNo).append("\n");
            defaultPrint("Transaction No:");
            defaultPrint(" "+printSlip.transactionNo);
            defaultPrint();

            // printString.append("Invoice No:     ").append(invoiceDocNo).append("\n");
            defaultPrint("Invoice No:",Align.LEFT,false);
            defaultPrint(printSlip.invoiceDocNo,Align.RIGHT,false);
            defaultPrint();

            //  printString.append("-----------------------------").append("\n");
            defaultPrint("-----------------------------");
            a.startPrint();
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

        } catch (Exception excep) {
            Toast.makeText(PrintSlipsListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
