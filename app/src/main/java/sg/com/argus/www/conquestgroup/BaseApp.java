package sg.com.argus.www.conquestgroup;

import android.app.Application;

import sg.com.argus.www.conquestgroup.utils.SunmiPrintHelper;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    /**
     * Connect print service through interface library
     */
    private void init(){
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this);
    }
}

