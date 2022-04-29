package sg.com.argus.www.conquestgroup.adapters;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bluetooth {
    /* access modifiers changed from: private */
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private Activity activity;
    /* access modifiers changed from: private */
    public BluetoothAdapter bluetoothAdapter;
    /* access modifiers changed from: private */
    public CommunicationCallback communicationCallback = null;
    /* access modifiers changed from: private */
    public boolean connected = false;
    /* access modifiers changed from: private */
    public BluetoothDevice device;
    /* access modifiers changed from: private */
    public BluetoothDevice devicePair;
    /* access modifiers changed from: private */
    public DiscoveryCallback discoveryCallback = null;
    /* access modifiers changed from: private */
    public BufferedReader input;
    /* access modifiers changed from: private */
    public final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                int intExtra2 = intent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", Integer.MIN_VALUE);
                if (intExtra == 12 && intExtra2 == 11) {
                    context.unregisterReceiver(Bluetooth.this.mPairReceiver);
                    if (Bluetooth.this.discoveryCallback != null) {
                        Bluetooth.this.discoveryCallback.onPair(Bluetooth.this.devicePair);
                    }
                } else if (intExtra == 10 && intExtra2 == 12) {
                    context.unregisterReceiver(Bluetooth.this.mPairReceiver);
                    if (Bluetooth.this.discoveryCallback != null) {
                        Bluetooth.this.discoveryCallback.onUnpair(Bluetooth.this.devicePair);
                    }
                }
            }
        }
    };

    public BroadcastReceiver mReceiverScan = new BroadcastReceiver() {
        /* JADX WARNING: Removed duplicated region for block: B:17:0x003b  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0055  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0070  */
        /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r4, android.content.Intent r5) {
            throw new UnsupportedOperationException("Method not decompiled: p004me.aflak.bluetooth.Bluetooth.C02421.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    /* access modifiers changed from: private */
    public OutputStream out;
    /* access modifiers changed from: private */
    public BluetoothSocket socket;

    public interface CommunicationCallback {
        void onConnect(BluetoothDevice bluetoothDevice);

        void onConnectError(BluetoothDevice bluetoothDevice, String str);

        void onDisconnect(BluetoothDevice bluetoothDevice, String str);

        void onError(String str);

        void onMessage(String str);
    }

    public interface DiscoveryCallback {
        void onDevice(BluetoothDevice bluetoothDevice);

        void onError(String str);

        void onFinish();

        void onPair(BluetoothDevice bluetoothDevice);

        void onUnpair(BluetoothDevice bluetoothDevice);
    }

    public Bluetooth(Activity activity2) {
        this.activity = activity2;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void enableBluetooth() {
        if (this.bluetoothAdapter != null && !this.bluetoothAdapter.isEnabled()) {
            this.bluetoothAdapter.enable();
        }
    }

    public void disableBluetooth() {
        if (this.bluetoothAdapter != null && this.bluetoothAdapter.isEnabled()) {
            this.bluetoothAdapter.disable();
        }
    }

    public void connectToAddress(String str) {
        new ConnectThread(this.bluetoothAdapter.getRemoteDevice(str)).start();
    }

    public void connectToName(String str) {
        for (BluetoothDevice next : this.bluetoothAdapter.getBondedDevices()) {
            if (next.getName().equals(str)) {
                connectToAddress(next.getAddress());
                return;
            }
        }
    }

    public void connectToDevice(BluetoothDevice bluetoothDevice) {
        new ConnectThread(bluetoothDevice).start();
    }

    public void disconnect() {
        try {
            this.socket.close();
        } catch (IOException e) {
            if (this.communicationCallback != null) {
                this.communicationCallback.onError(e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void send(String str) {
        try {
            this.out.write(str.getBytes());
        } catch (IOException e) {
            this.connected = false;
            if (this.communicationCallback != null) {
                this.communicationCallback.onDisconnect(this.device, e.getMessage());
            }
        }
    }

    private class ReceiveThread extends Thread implements Runnable {
        private ReceiveThread() {
        }

        public void run() {
            while (true) {
                try {
                    String readLine = Bluetooth.this.input.readLine();
                    if (readLine == null) {
                        return;
                    }
                    if (Bluetooth.this.communicationCallback != null) {
                        Bluetooth.this.communicationCallback.onMessage(readLine);
                    }
                } catch (IOException e) {
                    boolean unused = Bluetooth.this.connected = false;
                    if (Bluetooth.this.communicationCallback != null) {
                        Bluetooth.this.communicationCallback.onDisconnect(Bluetooth.this.device, e.getMessage());
                        return;
                    }
                    return;
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        public ConnectThread(BluetoothDevice bluetoothDevice) {
            BluetoothDevice unused = Bluetooth.this.device = bluetoothDevice;
            try {
                BluetoothSocket unused2 = Bluetooth.this.socket = bluetoothDevice.createRfcommSocketToServiceRecord(Bluetooth.MY_UUID);
            } catch (IOException e) {
                if (Bluetooth.this.communicationCallback != null) {
                    Bluetooth.this.communicationCallback.onError(e.getMessage());
                }
            }
        }

        public void run() {
            Bluetooth.this.bluetoothAdapter.cancelDiscovery();
            try {
                Bluetooth.this.socket.connect();
                OutputStream unused = Bluetooth.this.out = Bluetooth.this.socket.getOutputStream();
                BufferedReader unused2 = Bluetooth.this.input = new BufferedReader(new InputStreamReader(Bluetooth.this.socket.getInputStream()));
                boolean unused3 = Bluetooth.this.connected = true;
                new ReceiveThread().start();
                if (Bluetooth.this.communicationCallback != null) {
                    Bluetooth.this.communicationCallback.onConnect(Bluetooth.this.device);
                }
            } catch (IOException e) {
                if (Bluetooth.this.communicationCallback != null) {
                    Bluetooth.this.communicationCallback.onConnectError(Bluetooth.this.device, e.getMessage());
                }
                try {
                    Bluetooth.this.socket.close();
                } catch (IOException e2) {
                    if (Bluetooth.this.communicationCallback != null) {
                        Bluetooth.this.communicationCallback.onError(e2.getMessage());
                    }
                }
            }
        }
    }

    public List<BluetoothDevice> getPairedDevices() {
        ArrayList arrayList = new ArrayList();
        for (BluetoothDevice add : this.bluetoothAdapter.getBondedDevices()) {
            arrayList.add(add);
        }
        return arrayList;
    }

    public BluetoothSocket getSocket() {
        return this.socket;
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    public void scanDevices() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.FOUND");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.activity.registerReceiver(this.mReceiverScan, intentFilter);
        this.bluetoothAdapter.startDiscovery();
    }

    public void pair(BluetoothDevice bluetoothDevice) {
        this.activity.registerReceiver(this.mPairReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
        this.devicePair = bluetoothDevice;
        try {
            bluetoothDevice.getClass().getMethod("createBond", (Class[]) null).invoke(bluetoothDevice, (Object[]) null);
        } catch (Exception e) {
            if (this.discoveryCallback != null) {
                this.discoveryCallback.onError(e.getMessage());
            }
        }
    }

    public void unpair(BluetoothDevice bluetoothDevice) {
        this.devicePair = bluetoothDevice;
        try {
            bluetoothDevice.getClass().getMethod("removeBond", (Class[]) null).invoke(bluetoothDevice, (Object[]) null);
        } catch (Exception e) {
            if (this.discoveryCallback != null) {
                this.discoveryCallback.onError(e.getMessage());
            }
        }
    }

    public void setCommunicationCallback(CommunicationCallback communicationCallback2) {
        this.communicationCallback = communicationCallback2;
    }

    public void removeCommunicationCallback() {
        this.communicationCallback = null;
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback2) {
        this.discoveryCallback = discoveryCallback2;
    }

    public void removeDiscoveryCallback() {
        this.discoveryCallback = null;
    }
}
