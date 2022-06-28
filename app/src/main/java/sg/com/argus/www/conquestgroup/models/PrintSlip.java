package sg.com.argus.www.conquestgroup.models;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PrintSlip {
    @Id
    public long id;
    public String formatted_Date;
    public String lotId;
    public String cName;
    public String SName;
    public String Com;
    public String tName;
    public double ActualNoofBags;
    public String noOfBag;
    public List<String> bagWeightList;
    public String QuintalWeight;
    public double BagsWeightValue;
    public double NetWeightValue;
    public String lRate;
    public String netAmt;
    public String transactionNo;
    public String invoiceDocNo;


    @Override
    public String toString() {
        return "PrintSlip{" +
                "id=" + id +
                ", formatted_Date='" + formatted_Date + '\'' +
                ", lotId='" + lotId + '\'' +
                ", cName='" + cName + '\'' +
                ", SName='" + SName + '\'' +
                ", Com='" + Com + '\'' +
                ", tName='" + tName + '\'' +
                ", ActualNoofBags=" + ActualNoofBags +
                ", noOfBag='" + noOfBag + '\'' +
                ", bagWeightList=" + bagWeightList +
                ", QuintalWeight='" + QuintalWeight + '\'' +
                ", BagsWeightValue=" + BagsWeightValue +
                ", NetWeightValue=" + NetWeightValue +
                ", lRate='" + lRate + '\'' +
                ", netAmt='" + netAmt + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", invoiceDocNo='" + invoiceDocNo + '\'' +
                '}';
    }
}