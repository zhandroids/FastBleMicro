package com.clj.blesample.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.blesample.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.List;
import java.util.UUID;

/**
 * @author Administrator
 * @date 2018/12/29
 */

public class TestQbitActivity extends AppCompatActivity implements View.OnClickListener, View
        .OnTouchListener {

    Button btnRun;
    Button btnLeft;
    Button btnRight;
    Button btnBack;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGatt bluetoothGatt;
    public static final String KEY_DATA = "key_data";

    private BleDevice bleDevice;

    public static final String UARTSERVICE_SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String UART_TX_CHARACTERISTIC_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String UART_RX_CHARACTERISTIC_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";

/*控制led灯的uuid集合*/
    public static String LEDSERVICE_SERVICE_UUID = "E97DD91D-251D-470A-A062-FA1922DFA9A8";
    public static String LEDMATRIXSTATE_CHARACTERISTIC_UUID = "E97D7B77251D470AA062FA1922DFA9A8";
    public static String LEDTEXT_CHARACTERISTIC_UUID = "e97d3b10-251d-470a-a062-fa1922dfa9a8";

    public static String SCROLLINGDELAY_CHARACTERISTIC_UUID = "E97D0D2D251D470AA062FA1922DFA9A8";

    private Button btnStop;

    int pianoBtnId[] = {R.id.piano_1, R.id.piano_2, R.id.piano_3, R.id.piano_4, R.id.piano_5, R
            .id.piano_6, R.id.piano_7};
    private Button btnBell;
    private Button btnSend;
    private EditText etLed;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_test_qbit);

        btnBack = (Button) findViewById(R.id.button_back);
        btnLeft = (Button) findViewById(R.id.button_left);
        btnRight = (Button) findViewById(R.id.button_right);
        btnRun = (Button) findViewById(R.id.button_run);
        btnStop = (Button) findViewById(R.id.button_stop);
        btnBell = (Button) findViewById(R.id.button_bell);
        btnSend = (Button) findViewById(R.id.button_send);
        etLed = (EditText) findViewById(R.id.et_led);

        btnBack.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnRun.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnBell.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        for (int i = 0; i < pianoBtnId.length; i++) {
            TextView textView = (TextView) findViewById(pianoBtnId[i]);
            textView.setOnTouchListener(this);
        }


         bleDevice = getIntent().getParcelableExtra(KEY_DATA);

        bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
//        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();

        BluetoothGattService gattService = bluetoothGatt.getService(UUID.fromString
                (UARTSERVICE_SERVICE_UUID));


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


        if (notifyCharacteristic == null) {
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
                                addText((TextView) findViewById(R.id.log_text), "notify success");
                            }
                        });
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                addText(txt, exception.toString());
                            }
                        });
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addText((TextView) findViewById(R.id.log_text), HexUtil.formatHexString
                                        (characteristic.getValue(), true));
                            }
                        });
                    }
                });

        characteristic = gattService.getCharacteristic(UUID.fromString
                (UART_RX_CHARACTERISTIC_UUID));


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_back:
                sendCarRun("02");
                break;
            case R.id.button_left:
                sendCarRun("03");
                break;
            case R.id.button_right:
                sendCarRun("04");
                break;
            case R.id.button_run:
                sendCarRun("01");
//                sendDiCmd(true);
                break;

            case R.id.button_stop:
                sendCarRun("00");
                break;

            case R.id.button_bell:
                sendDiCmd(true);
                break;
            case R.id.button_send:
                sendText(etLed.getText().toString());

                break;
            default:
                break;

        }

    }

    private byte[] WriteBytes = new byte[20];

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void SendBLEData(String paramString) {

        StringBuilder stringBuilder = new StringBuilder("CMD|01|");
        stringBuilder.append(paramString);

        stringBuilder.append("|$");
        this.WriteBytes = stringBuilder.toString().getBytes();
//        this.characteristic.setValue(arrayOfByte[0], 17, 0);
        this.characteristic.setValue(WriteBytes);

        bluetoothGatt.writeCharacteristic(characteristic);
    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void sendText(String paramString) {

//        StringBuilder stringBuilder = new StringBuilder("CMD|01|");
//        stringBuilder.append(paramString);
//
//        stringBuilder.append("|$");
        this.WriteBytes = paramString.getBytes();
//        this.characteristic.setValue(arrayOfByte[0], 17, 0);
//        this.characteristic.setValue(WriteBytes);


        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();

        for (BluetoothGattService gattService : serviceList) {

            Log.e("asker","服务"+gattService.getUuid().toString());

        }

        BluetoothGattService ledService = bluetoothGatt.getService(UUID.fromString(LEDSERVICE_SERVICE_UUID));
       List<BluetoothGattCharacteristic> characteristicList =  ledService.getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : characteristicList) {

            Log.e("asker","特征"+gattCharacteristic.getUuid());

        }


        BluetoothGattCharacteristic ledCharacteristic = ledService.getCharacteristic(UUID.fromString(LEDTEXT_CHARACTERISTIC_UUID));
//
        ledCharacteristic.setValue(WriteBytes);

        bluetoothGatt.writeCharacteristic(ledCharacteristic);

        BleManager.getInstance().write(bleDevice, ledCharacteristic.getService().getUuid().toString(), ledCharacteristic.getUuid().toString(), WriteBytes, new BleWriteCallback() {



            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {

                Log.e("asker","发送成功"+justWrite);

            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("asker","发送失败"+exception.toString());
            }
        });
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                for (int i = 0; i < pianoBtnId.length; i++) {
                    if (v.getId()==pianoBtnId[i]){
                        sendRgb("01");
//                        sendDiCmd(true);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

            default:
                break;


        }


        return true;
    }


    private void sendDiCmd(boolean paramBoolean){

        StringBuilder stringBuilder = new StringBuilder("CMD|09|");

        if (paramBoolean){
            stringBuilder.append("01");

        }else {
            stringBuilder.append("00");
        }

        stringBuilder.append("|$");


        byte[] arrayOfByte = new byte[20];
        arrayOfByte[0] = 0;
        WriteBytes = stringBuilder.toString().getBytes();
//        this.characteristic.setValue(arrayOfByte[0], 17, 0);
        this.characteristic.setValue(WriteBytes);

        bluetoothGatt.writeCharacteristic(characteristic);

    }

    private void sendCarRun(String  param){

        String stringBuilder = "CMD|01|" + param +
                "|$";

        //        this.WriteBytes = stringBuilder.toString().getBytes();
//        this.characteristic.setValue(arrayOfByte[0], 17, 0);
        this.characteristic.setValue(stringBuilder.getBytes());

        bluetoothGatt.writeCharacteristic(characteristic);



    }

    private void sendRgb(String  param){

        String stringBuilder = "CMD|08|" + param +
                "|$";

                this.WriteBytes = stringBuilder.toString().getBytes();
//        this.characteristic.setValue(arrayOfByte[0], 17, 0);
        this.characteristic.setValue(stringBuilder.getBytes());

        bluetoothGatt.writeCharacteristic(characteristic);
        if (stringBuilder!=null){
            return;
        }

        BleManager.getInstance().write(bleDevice
                ,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                stringBuilder.getBytes(),
                new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(final int current, final int
                            total, final byte[] justWrite) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addText((TextView)findViewById(R.id.log_text), "write success, current: " +
                                        current
                                        + " total: " + total
                                        + " justWrite: " + HexUtil
                                        .formatHexString(justWrite, true));
                            }
                        });
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addText((TextView)findViewById(R.id.log_text), exception.toString());
                            }
                        });
                    }
                });



    }

    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }





}
