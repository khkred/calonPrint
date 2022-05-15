package sg.com.argus.www.conquestgroup.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.adapters.ConnectionDetector;

/**
 * Created by root on 10/31/18.
 */
public class GateEntryActivity extends AppCompatActivity {

    private Button next;
    private final static String TAG = WelcomeUserActivity.class.getSimpleName();

    private boolean mConnected = false;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ImageView searchbtn;
    private AutoCompleteTextView searchLotDetails;
    private String loginid, password, orgid, userid, bagTypeId, lotId, caName, lotRate, traderName, actualBags;
    private SharedPreferences savedata;
    private TextView sellerName, commodity, lotPrice, tradderName, welcomeuser, logout, onlyBagWeight;
    private Spinner bagType;
    private double bagtypecal, newbagTypeValue;
    private String bagtypeRel;
    String oprId;
    ArrayList<String> stringLotArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateentry);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

    }
}
