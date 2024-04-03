package com.example.tester;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    Button btSENT;
    TextView txt_power,txt_him;
    EditText EditTextmessage;
    private SMSReceiver smsReceiver;
    private CircularSeekBar circularSeekBar,circularSeekBarSOIL,circularSeekBarTEMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        circularSeekBar = findViewById(R.id.circularSeekBar);
        btSENT=findViewById(R.id.button);

        txt_power=findViewById(R.id.text_power);
        txt_him=findViewById(R.id.him_text);

        EditTextmessage=findViewById(R.id.editText);

        Switch smsSwitch = findViewById(R.id.smsSwitch);

        // Get the current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(calendar.getTime());

        // switch
        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String number ; //= phoneNumberEditText.getText().toString();
                number = "0657106771";

                if (number.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No number , please try again.", Toast.LENGTH_LONG).show();

                }else {
                    if (isChecked) {
                        sendSMS("@", number);
                    } else {
                        sendSMS("#",number);
                    }
                }
            }
        });
        btSENT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                 // sendSMS("60");
             } catch (Exception e) {
                    e.printStackTrace();
                }
                // messageTextView.setText(n+"");
            }  });

        if (checkSmsPermission()) {
            setupSmsReceiver();
        } else {
            requestSmsPermission();
        }
    } // end oncreat
    public void sendSMS(String msg,String number) {
       // String phoneNumber = messageEditText.getText().toString();
       // String test_message = "hello ,how are you ! ";
        //String message = messageEditText.getText().toString();

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Demande d'autorisation pour envoyer des SMS
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            // L'autorisation est déjà accordée, envoyez le SMS
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, null, null);

            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
        }
    }// end sendsms
    
    
    
    
    // PERMISSION
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_REQUEST_CODE);
    }

    // recieve FUNCTOION
    private void setupSmsReceiver() {
        smsReceiver = new SMSReceiver();
        IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsReceiver, intentFilter);

        // Register a content observer to listen for changes to the SMS inbox
        getContentResolver().registerContentObserver(Telephony.Sms.CONTENT_URI, true
                , new SMSContentObserver(new Handler()));
    }

    // PERMISSION
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSmsReceiver();
            } else {
                Toast.makeText(this, "Permission SMS refusée. Impossible de lire les messages.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }

    // SMS FUNCTION
    private class SMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                try {
                    displayLastReceivedMessage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class SMSContentObserver extends ContentObserver {
        public SMSContentObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            try {
                displayLastReceivedMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }



    private void displayLastReceivedMessage() throws Exception {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, null, null, null, "date DESC");

        if (cursor != null && cursor.moveToFirst()) {
            String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            String valueStr = message;//editText.getText().toString();


           // System.out.println( isInteger(valueStr));
            if (isInteger(valueStr)==false){
               // Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
            }

           // mycircle_custom(valueStr,circularSeekBar);

            cursor.close();
        } else {

            //Aucun message trouvé dans la boîte de réception.
        }
    } // end display method






 // circle creat
   private void mycircle_custom(String Str,CircularSeekBar seekBar){
       if (!Str.isEmpty()) {
           int value = Integer.parseInt(Str);
           if (value >= 0 && value <= 100) {
               float angle = (float) value * 3.6f; // Convert percentage to angle (360 degrees total)
               seekBar.setAngle(angle);
               txt_him.setText(value+"%");
           } else {
               EditTextmessage.setError("Enter a value between 0 and 100");
           }
       } else {
           EditTextmessage.setError("Enter a value");
       }
   }

// int test
    public static boolean isInteger(Object value) {
        return value instanceof Integer;
    }

}
