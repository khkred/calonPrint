package sg.com.argus.www.conquestgroup.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

import io.objectbox.Box;
import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.interfaces.ItemClickListener;
import sg.com.argus.www.conquestgroup.models.PrintSlip;

public class PrintSlipsListActivity extends AppCompatActivity implements ItemClickListener {

    Box<PrintSlip> box;
    int k = 0;

    List<PrintSlip> printSlips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_slips_list);

    }

    @Override
    public void onClick(View view, int position) {

    }
}
