package sg.com.argus.www.conquestgroup.adapters;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import sg.com.argus.www.conquestgroup.activities.BluetoothActivity;

public class BTConnection {
    static BluetoothAdapter mBluetoothAdapter;
    static BluetoothSocket mmSocket;
    static BluetoothDevice mmDevice;
    static OutputStream mmOutputStream;
    static InputStream mmInputStream;


    public BTConnection() {
    }

    @TargetApi(5)
    public static void openBT(String address) throws IOException {
        BTConnection.findBT(address);
        try {

            UUID uuid2 = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            BTConnection.mmSocket = BTConnection.mmDevice.createRfcommSocketToServiceRecord(uuid2);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            // beginListenForData();
            getData();
        } catch (NullPointerException var3) {
            ;
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    @TargetApi(19)
    public static void findBT(String address) {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }

            Set var6 = mBluetoothAdapter.getBondedDevices();
            BluetoothDevice device1;
            if (var6.size() > 0) {
                Iterator var4 = var6.iterator();

                while (var4.hasNext()) {
                    device1 = (BluetoothDevice) var4.next();
                    if (device1.getAddress().equals(address)) {
                        mmDevice = device1;
                        break;
                    }
                }
            }

            device1 = null;
            mmDevice = mBluetoothAdapter.getRemoteDevice(address);
            mmDevice.createBond();
        } catch (NullPointerException var5) {
            var5.printStackTrace();
        } catch (Exception var61) {
            var61.printStackTrace();
        }

    }

    public static void closeBT() throws IOException {
        try {
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (NullPointerException var2) {
            var2.printStackTrace();

        } catch (Exception var3) {
            var3.printStackTrace();

        }

    }


    public static void getData() {
        try {
            synchronized (mmInputStream) {
                mmInputStream.wait(1500);
            }
            String data = "";
            while (mmInputStream.available() > 0) {
                final byte[] packetBytes = new byte[mmInputStream.available()];
                mmInputStream.read(packetBytes);
                data = data + ";" + new String(packetBytes);
                if(!data.isEmpty())
                    break;
            }
            if (data == "" && data == null) {
                return;
            }
            BluetoothActivity.receivedData(data);
            closeBT();
        } catch (Exception e3) {
            try {
                closeBT();
                e3.printStackTrace();
            } catch (Exception e) {
            }
        }


    }
}
