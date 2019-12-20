package unibo.mobileapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Set;
import java.util.UUID;

import unibo.mobileapp.utils.C;
import unibo.lib.BluetoothChannel;
import unibo.lib.ConnectionTask;
import unibo.lib.RealBluetoothChannel;
import unibo.lib.ConnectToBluetoothServerTask;
import unibo.lib.BluetoothUtils;
import unibo.lib.exceptions.BluetoothDeviceNotFound;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private BluetoothChannel btChannel;
    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                findViewById(R.id.connectBtn).setEnabled(true);
                findViewById(R.id.btnMode).setEnabled(false);
                findViewById(R.id.btnPumps).setEnabled(false);
                findViewById(R.id.rbMinimumFlow).setEnabled(false);
                findViewById(R.id.rbMedium).setEnabled(false);
                findViewById(R.id.rbMaximum).setEnabled(false);
                ((TextView) findViewById(R.id.statusLabel)).setText(String.format("Status : not connected"));
                ((TextView) findViewById(R.id.humidityLabel)).setText(String.format(""));
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.btAdapter != null && !btAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    C.bluetooth.ENABLE_BT_REQUEST);
        }
        registerReceiver(br, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        initUI();
    }

    private void initUI() {
        ((TextView) findViewById(R.id.statusLabel)).setText(String.format("Status : not connected"));

        findViewById(R.id.connectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    connectToBTServer();
                } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                    bluetoothDeviceNotFound.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean manualMode = ((Button)findViewById(R.id.btnMode)).getText().equals(C.utility.MANUAL_MODE);
                ((Button) findViewById(R.id.btnMode)).setText(manualMode ? C.utility.AUTOMATIC_MODE :
                        C.utility.MANUAL_MODE);
                findViewById(R.id.btnPumps).setEnabled(manualMode ? true : false);
                findViewById(R.id.rbMinimumFlow).setEnabled(manualMode ? true : false);
                findViewById(R.id.rbMedium).setEnabled(manualMode ? true : false);
                findViewById(R.id.rbMaximum).setEnabled(manualMode ? true : false);
                btChannel.sendMessage(manualMode ? C.message.MANUAL_MODE : C.message.AUTOMATIC_MODE);
            }
        });

        findViewById(R.id.btnPumps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = findViewById(R.id.rdbGroup);
                int radioId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(radioId);
                boolean setPumpsOff = ((Button)findViewById(R.id.btnPumps)).getText().equals(C.utility.PUMPS_ON);
                if (setPumpsOff) {
                    String flow = radioButton.getText().equals(C.utility.MINIMUM) ? C.message.MINIMUM_FLOW :
                            radioButton.getText().equals(C.utility.MEDIUM) ? C.message.MEDIUM_FLOW :
                                    C.message.MAXIMUM_FLOW;
                    btChannel.sendMessage(flow);
                } else {
                    btChannel.sendMessage(C.message.PUMPS_OFF);
                }
                ((Button) findViewById(R.id.btnPumps)).setText(setPumpsOff ? C.utility.PUMPS_OFF : C.utility.PUMPS_ON);
                findViewById(R.id.rbMinimumFlow).setEnabled(setPumpsOff ? false : true);
                findViewById(R.id.rbMedium).setEnabled(setPumpsOff ? false : true);
                findViewById(R.id.rbMaximum).setEnabled(setPumpsOff ? false : true);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        btChannel.close();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_OK){
            Log.d(C.APP_LOG_TAG, "Bluetooth enabled!");
        }

        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_CANCELED){
            Log.d(C.APP_LOG_TAG, "Bluetooth not enabled!");
        }
    }

    private void connectToBTServer() throws BluetoothDeviceNotFound {
        BluetoothDevice tmpServerDevice = null;

        Set<BluetoothDevice> pairedList = btAdapter.getBondedDevices();
        if (pairedList.size() > 0) {
            for (BluetoothDevice device : pairedList) {
                if (device.getName().equals(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME)) {
                    tmpServerDevice = device;
                }
            }
        }
        if (tmpServerDevice != null) {
            final BluetoothDevice serverDevice = tmpServerDevice;
            final UUID uuid = BluetoothUtils.generateUuidFromString(C.bluetooth.BT_SERVER_UUID);
            AsyncTask<Void, Void, Integer> execute = new ConnectToBluetoothServerTask(serverDevice, uuid, new ConnectionTask.EventListener() {
                @Override
                public void onConnectionActive(final BluetoothChannel channel) {
                    ((TextView) findViewById(R.id.statusLabel)).setText(String.format("Status : connected to server on device %s",
                            serverDevice.getName()));
                    findViewById(R.id.connectBtn).setEnabled(false);

                    btChannel = channel;
                    btChannel.registerListener(new RealBluetoothChannel.Listener() {
                        @Override
                        public void onMessageReceived(String receivedMessage) {
                            managementMessageReceived(receivedMessage);
                        }

                        @Override
                        public void onMessageSent(String sentMessage) {
                        }
                    });
                }

                @Override
                public void onConnectionCanceled() {
                    ((TextView) findViewById(R.id.statusLabel)).setText(String.format("Status : unable to connect, device %s not found!",
                            C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME));
                }
            }).execute();
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private void managementMessageReceived(String message) {
        if (isNumeric(message)) {
            ((TextView) findViewById(R.id.humidityLabel)).setText("Humidity: " + message + " %");
        } else if (message.equals(C.message.CONNECTION_ENABLE)) {
            ((Button)findViewById(R.id.btnMode)).setText(C.utility.MANUAL_MODE);
            findViewById(R.id.btnMode).setEnabled(true);
        } else if (message.equals(C.message.CONNECTION_DISABLED)) {
            findViewById(R.id.btnMode).setEnabled(false);
            findViewById(R.id.btnPumps).setEnabled(false);
            findViewById(R.id.rbMinimumFlow).setEnabled(false);
            findViewById(R.id.rbMedium).setEnabled(false);
            findViewById(R.id.rbMaximum).setEnabled(false);
            ((TextView) findViewById(R.id.humidityLabel)).setText("");
        } else if (message.equals(C.message.PUMPS_OFF)) {
            ((Button) findViewById(R.id.btnPumps)).setText(C.utility.PUMPS_OFF);
        } else if (message.equals(C.message.PUMPS_ON)) {
            ((Button) findViewById(R.id.btnPumps)).setText(C.utility.PUMPS_ON);
        }
    }
}
