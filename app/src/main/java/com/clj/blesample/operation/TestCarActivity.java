package com.clj.blesample.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.clj.blesample.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.UUID;

/**
 * @author Administrator
 * @date 2018/12/29
 */

public class TestCarActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRun;
    Button btnLeft;
    Button btnRight;
    Button btnBack;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGatt bluetoothGatt;
    public static final String KEY_DATA = "key_data";

    public static final String UARTSERVICE_SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String UART_TX_CHARACTERISTIC_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String UART_RX_CHARACTERISTIC_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";
    private Button btnStop;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_test_car);

        btnBack = (Button) findViewById(R.id.button_back);
        btnLeft = (Button) findViewById(R.id.button_left);
        btnRight = (Button) findViewById(R.id.button_right);
        btnRun = (Button) findViewById(R.id.button_run);
        btnStop = (Button) findViewById(R.id.button_stop);

        btnBack.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnRun.setOnClickListener(this);
        btnStop.setOnClickListener(this);


        BleDevice bleDevice = getIntent().getParcelableExtra(KEY_DATA);

         bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
//        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();

        BluetoothGattService gattService =      bluetoothGatt.getService(UUID.fromString(UARTSERVICE_SERVICE_UUID));



        //BluetoothGattCharacteristic
        //6e400001-b5a3-f393-e0a9-e50e24dcca9e
//        for (BluetoothGattService service : serviceList) {
//            UUID serviceId = service.getUuid();
//            if (serviceId.equals(UARTSERVICE_SERVICE_UUID)) {
//                gattService = service;
//                break;
//            }
//        }

        if (gattService == null) {
            return;
        }

        BluetoothGattCharacteristic notifyCharacteristic = gattService.getCharacteristic(UUID
                .fromString(UART_TX_CHARACTERISTIC_UUID));



        if (notifyCharacteristic==null)
        {
            return;
        }

        BleManager.getInstance().notify(
                bleDevice,
                notifyCharacteristic.getService().getUuid().toString(),
                notifyCharacteristic.getUuid().toString(),
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                addText(txt, "notify success");
                            }
                        });
                    }

                    @Override
                    public void onNotifyFailure(final BleException
                                                        exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                addText(txt, exception.toString());
                            }
                        });
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                addText(txt, HexUtil.formatHexString
//                                        (characteristic.getValue(), true));
//                            }
//                        });
                    }
                });

        characteristic = gattService.getCharacteristic(UUID.fromString(UART_RX_CHARACTERISTIC_UUID));


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_back:
                SendBLEData("B#");
                break;
            case R.id.button_left:
                SendBLEData("C#");
                break;
            case R.id.button_right:
                SendBLEData("D#");
                break;
            case R.id.button_run:
                SendBLEData("A#");
                break;

            case R.id.button_stop:
                SendBLEData("0#");
                break;
            default:
                break;

        }

    }

    private byte[] WriteBytes = new byte[20];

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void SendBLEData(String paramString)
    {
        int i = this.characteristic.getProperties();
        if ((i | 0x2) > 0)
        {
//            if (this.mNotifyCharacteristic != null)
//            {
//                this.mBluetoothLeService.setCharacteristicNotification(this
// .mNotifyCharacteristic, false);
//                this.mNotifyCharacteristic = null;
//            }
            byte[] arrayOfByte = new byte[20];
            arrayOfByte[0] = 0;
            this.WriteBytes = paramString.getBytes();
            this.characteristic.setValue(arrayOfByte[0], 17, 0);
            this.characteristic.setValue(this.WriteBytes);

            bluetoothGatt.writeCharacteristic(characteristic);
        }
//        if ((i | 0x10) > 0)
//        {
//            this.mNotifyCharacteristic = this.characteristic;
//            this.mBluetoothLeService.setCharacteristicNotification(this.characteristic, true);
//        }
    }


}
