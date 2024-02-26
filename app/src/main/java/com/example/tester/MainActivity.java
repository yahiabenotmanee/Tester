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
    TextView messageTextView,txt_power;
    EditText messageEditText;
    private SMSReceiver smsReceiver;
    private CircularSeekBar circularSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circularSeekBar = findViewById(R.id.circularSeekBar);
        btSENT=findViewById(R.id.button);
        txt_power=findViewById(R.id.text_power);
        messageEditText=findViewById(R.id.editText);
        messageTextView=findViewById(R.id.numberTextView);
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
                        sendSMS("R", number);
                    } else {
                        sendSMS("K",number);
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
            if (msg=="R"){
                txt_power.setText("ON");

                //txtStatus.setTextColor(getColor(R.color.p7));
            }
            if (msg=="K"){
                txt_power.setText("OFF");
                //txtStatus.setVisibility(View.VISIBLE);
                //txtStatus.setTextColor(getColor(R.color.text_color_description));
            }
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
            messageTextView.setText(message);


            String valueStr = message;//editText.getText().toString();
            if (!valueStr.isEmpty()) {
                int value = Integer.parseInt(valueStr);
                if (value >= 0 && value <= 100) {
                    float angle = (float) value * 3.6f; // Convert percentage to angle (360 degrees total)
                    circularSeekBar.setAngle(angle);
                   // valueTextView.setText("Value: " + value);
                    messageTextView.setText(value+"%");
                } else {
                    messageEditText.setError("Enter a value between 0 and 100");
                }
            } else {
                messageEditText.setError("Enter a value");
            }



            cursor.close();
        } else {
            messageTextView.setText("Aucun message trouvé dans la boîte de réception.");
        }
    }
}
