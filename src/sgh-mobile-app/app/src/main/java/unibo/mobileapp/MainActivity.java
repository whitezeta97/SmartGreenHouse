package unibo.mobileapp;

import android.Manifest;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
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
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    private ListView listItems;
    private ArrayList<String> stringArrayList = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private BluetoothChannel btChannel;
    //private Set<BluetoothDevice> nbDevices;
    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                stringArrayList.add("START DISCOVERY");
                arrayAdapter.notifyDataSetChanged();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                stringArrayList.add("FINISH DISCOVERY");
                arrayAdapter.notifyDataSetChanged();
            } else {
                stringArrayList.add(action);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.btAdapter != null && !btAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), C.bluetooth.ENABLE_BT_REQUEST);
        }
        // Register the BroadcastReceiver
        IntentFilter filterBT = new IntentFilter();
        filterBT.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filterBT.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filterBT.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(br, filterBT);
        //registerReceiver(br, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        listItems = (ListView) findViewById(R.id.lstLista);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
        listItems.setAdapter((arrayAdapter));
        initUI();
    }

    private void initUI() {
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

        /*findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = ((EditText)findViewById(R.id.editText)).getText().toString();
                btChannel.sendMessage(message);
                ((EditText)findViewById(R.id.editText)).setText("");

            }
        });*/

        /*findViewById(R.id.btnSearchDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.startDiscovery();
                stringArrayList.add("DISCOVERY");
                arrayAdapter.notifyDataSetChanged();
                ((TextView) findViewById(R.id.chatLabel)).setText("DISCOVERY");
            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
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
        //Search if device is already paired
        //btAdapter.startDiscovery();
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
       /* stringArrayList.add("ADD DEVICE");
        arrayAdapter.notifyDataSetChanged();
        stringArrayList.add(Integer.toString(nbDevices.size()));
        arrayAdapter.notifyDataSetChanged();
       */
        Set<BluetoothDevice> pairedList = btAdapter.getBondedDevices();
        stringArrayList.add("PAIRED");
        arrayAdapter.notifyDataSetChanged();
        stringArrayList.add(Integer.toString(pairedList.size()));
        arrayAdapter.notifyDataSetChanged();
        if (pairedList.size() > 0) {
            for (BluetoothDevice device : pairedList) {
                stringArrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
                if (device.getName().equals(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME)) {
                    tmpServerDevice = device;
                    stringArrayList.add("DISPOSITIVO TROVATO");
                    arrayAdapter.notifyDataSetChanged();
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
                            stringArrayList.add("received Message: " + receivedMessage);
                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onMessageSent(String sentMessage) {
                            stringArrayList.add("sent Message: " + sentMessage);
                            arrayAdapter.notifyDataSetChanged();
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
}

