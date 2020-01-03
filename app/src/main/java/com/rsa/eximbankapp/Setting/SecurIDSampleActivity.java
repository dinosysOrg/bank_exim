/**
 *
 * Copyright (c) 2013  EMC Corporation  All Rights Reserved. Published in the USA.
 * 
 * This software contains the intellectual property of EMC Corporation or is licensed to 
 * EMC Corporation from third parties. Use of this software and the intellectual property 
 * contained therein is expressly limited to the terms and conditions of the License 
 * Agreement under which it is provided by or on behalf of EMC.
 * 
 * This software is protected, without limitation, by copyright law and international treaties. 
 * Use of this software and the intellectual property contained therein is expressly 
 * limited to the terms and conditions of the License Agreement under which it is 
 * provided by or on behalf of EMC.
 *
 **/
package com.rsa.eximbankapp.Setting;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


import com.google.android.gms.vision.barcode.Barcode;
import com.rsa.securidlib.Otp;
import com.rsa.securidlib.exceptions.SecurIDLibException;
import com.rsa.securidlib.tokenstorage.TokenMetadata;
import com.rsa.eximbankapp.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
 
/**
 * This activity displays a list of imported tokens and displays the OTP
 * for the selected token.
 * 
 * This activity provides the following menu options:
 * Import token: import a token by selecting CTF, CT-KIP, or File
 * Delete token: delete the currently selected token
 * Rename token: set a nickname for the selected token
 * Show About: display the version of the SDK library and display the Device ID
 * for binding a token to a device.
 * 
 */
public class SecurIDSampleActivity extends Activity {
    
    private SecurIDLibHelper m_libHelper;
    private Context m_context;
    private Timer m_OTPTimer;
    private Handler m_handler;
    private TokenMetadata m_activeToken;
    private String m_pin;
    
    private TextView m_nicknameLabel;
    private Spinner m_tokenDropdown;
    private TextView m_otpLabel;
    private TextView m_secondsRemainingLabel;

    //DDH Added for TxSigning
    private TextView textViewTxSigningCode;
    private EditText editTextTxID;
    private Button buttonTxSigning;
    //DDH End

    private int m_secondsRemaining = 0;
        
    private final static long ONE_SECOND = 1000;
    
    final static int IMPORT_TOKEN_REQUEST = 1;
    final static int TXCODE_REQUEST = 110;
    final static String IMPORTED_TOKEN_SERIALNUMBER = "ImportedTokenSerialNumber";

    public static final int PERMISSIONS_REQUEST = 112;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Toast.makeText(getApplicationContext(), "SecurId Sample App Launched", Toast.LENGTH_SHORT).show();
        //DDH 20170408 Add for requesting permission if they are not set. Prevent crashing on Nougate
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
        }
        //DDE End

        try
        {
            m_context = this;
            
            //To be able to allow access to a token in Airplane mode or with a global phone
            //For first-time use of your mobile client application, you should
            //prompt the user to enable Wi-Fi and exit Airplane mode.
            if(isAllDeviceParametersAvailable()) {
                //prompt user to disable Airplane mode (for non-Wi-Fi only device)
                //and enable Wi-Fi before continuing
            }
            
            m_libHelper = SecurIDLibHelper.getInstance(this);
            m_handler = new Handler();
            
            m_nicknameLabel = (TextView)findViewById(R.id.nicknameLabel);
            m_tokenDropdown = (Spinner)findViewById(R.id.tokenList);
            m_otpLabel = (TextView)findViewById(R.id.otpText);
            m_secondsRemainingLabel = (TextView)findViewById(R.id.secsRemainingLabel);

            //populate drop-down box with a list of tokens identified by serial number
            ArrayList<String> tokenSNList = m_libHelper.getTokenSNList();
            ArrayAdapter<String> tokenSNArrayAdapter = new ArrayAdapter<String>(m_context, android.R.layout.simple_spinner_dropdown_item, tokenSNList);
            m_tokenDropdown.setAdapter(tokenSNArrayAdapter);
            m_tokenDropdown.setOnItemSelectedListener(new TokenListItemSelectedListener());

            //DDH Added for TxSigning
            buttonTxSigning = (Button) findViewById(R.id.buttonTxSigning);
            textViewTxSigningCode = (TextView) findViewById(R.id.textViewTxSigningCode);
            editTextTxID = (EditText) findViewById(R.id.editTextTxID);
            editTextTxID.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().compareToIgnoreCase("rejected")==0 || s.length() < m_otpLabel.getText().toString().length()) {
                        buttonTxSigning.setText("QR Scan");
                    } else {
                        buttonTxSigning.setText("Sign");
                    }
                }
            });

            buttonTxSigning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Hiding keyboard layout
                        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        String otpString = m_otpLabel.getText().toString();
                        String tidString = editTextTxID.getText().toString();

                        if (tidString.compareToIgnoreCase("rejected")==0 || tidString.length() < otpString.length()) {
                            //Invalid txcode, open QR Scan to import
                            Intent intent = new Intent(m_context, QRScanActivity.class);
                            startActivityForResult(intent, TXCODE_REQUEST);
                        } else {
                            //Encryption part using simple method: ciphertext=(first8bytes(hash(key)) + cleartext) mod 16, display in hex string
                            java.security.MessageDigest msg = java.security.MessageDigest.getInstance("MD5");
                            msg.update(tidString.getBytes(), 0, tidString.length() );

                            String keyString = new BigInteger(1, msg.digest()).toString(16);
                            //Toast.makeText(SecurIDSampleActivity.this, "Debug: " + keyString, Toast.LENGTH_LONG).show();

                            byte[] clearBytes = otpString.getBytes();
                            byte[] keyBytes = keyString.getBytes();

                            char[] hexArray = "0123456789abcdef".toCharArray();
                            char[] hexChars = new char[clearBytes.length];
                            for (int i = 0; i < clearBytes.length; i++) {
                                int cb = (clearBytes[i] < 59)? clearBytes[i] - '0': clearBytes[i] + 10 - 'a';
                                int kb = (keyBytes[i] < 59)? keyBytes[i] - '0': keyBytes[i] + 10 - 'a';
                                int cipherByte = (cb + kb)%16;
                                hexChars[i] = hexArray[cipherByte];
                            }

                            textViewTxSigningCode.setText(new String(hexChars));
                            Log.i("SID Test)", hexChars.toString());
                        }
                    } catch (Exception e) {
                        Toast.makeText(SecurIDSampleActivity.this, "Activity error, please view log file for detail.", Toast.LENGTH_LONG).show();
                        Log.e("SID Test", "exception: " + e.getMessage());
                        Log.e("SID Test", "exception: " + e.toString());
                    }
                }
            });
            //DDH End
            
        }
        catch(SecurIDLibException ex)
        {
            ex.printStackTrace();
            this.finish();
        }        
    }
        
    @Override
    public void onPause()
    {
        super.onPause();
        this.stopOtpCodeTimer();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean ret = true;
        switch(item.getItemId())
        {
        case R.id.importTokenMenu:
            Intent iImportToken = new Intent(m_context, ImportTokenActivity.class);            
            startActivityForResult(iImportToken, IMPORT_TOKEN_REQUEST);
            break;
            
        case R.id.deleteTokenMenu:
            if(m_activeToken != null) {
                ret = m_libHelper.deleteToken(m_activeToken.getSerialNumber());                
                if(ret) {
                    m_activeToken = null;
                    refreshTokenList();
                }
            }
            break;
        case R.id.renameTokenMenu:
            if(m_activeToken != null) {
                //rename dialog prompt
                AlertDialog.Builder renameDialog = new AlertDialog.Builder(this);
                renameDialog.setTitle(R.string.renameDialogTitle);
                renameDialog.setMessage(R.string.renameDialogMessage);
                final EditText inputText = new EditText(this);
                renameDialog.setView(inputText);
                renameDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inNickname = inputText.getText().toString();
                        if(inNickname != null) {
                            boolean ret = m_libHelper.renameToken(m_activeToken.getSerialNumber(), inNickname);
                            if(ret) {
                                m_activeToken.setNickname(inNickname);
                                //the SDK does not check for duplicate token nicknames
                                m_nicknameLabel.setText(m_activeToken.getNickname());
                            }
                        }
                    }
                });
                renameDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                renameDialog.show();
            }
            break;
        case R.id.aboutMenuItem:                        
            AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
            //display the SDK version
            String title = String.format(getString(R.string.sdkName), m_libHelper.getLibaryInfo());
            aboutDialog.setTitle(title);

            View view = LayoutInflater.from(m_context).inflate(R.layout.about_screen, null);  
            
            //set device binding information in About dialog
            String deviceID = m_libHelper.getDeviceID();
            if(deviceID == null || deviceID.equals("")) {
                deviceID = getString(R.string.unavailable);
            }
            TextView deviceIDLabel = (TextView)view.findViewById(R.id.deviceIDLabel);
            String deviceIDString = String.format(getString(R.string.deviceID), deviceID);
            deviceIDLabel.setText(String.format(getString(R.string.deviceID), deviceID));
            
            //display the About dialog
            aboutDialog.setView(view); 
            aboutDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            aboutDialog.show();

            break;
            
        default:
            ret = super.onOptionsItemSelected(item);
            break;
        }
        
        return ret;
    }

    //DDH 20170721: Adding for resuming code timer
    @Override
    public void onResume()
    {
        super.onResume();
        this.startOtpCodeTimer();
    }

    /**
     * Check result from Import Token activity
     */
    protected void onActivityResult (int requestCode, int resultCode, Intent data) 
    {
        if(requestCode == IMPORT_TOKEN_REQUEST && data != null) {
            //get response from Import Token screen and refresh the token list and information
            String tokenSerialNumber = data.getExtras().getString(IMPORTED_TOKEN_SERIALNUMBER);
            if(tokenSerialNumber != null) {
            	m_activeToken = m_libHelper.getTokenMetadata(tokenSerialNumber);            	
                refreshTokenList();
                int i = m_tokenDropdown.getAdapter().getCount()-1;
            	m_tokenDropdown.setSelection(i);
            }
        }

        /* Huy.Do - 20170805
        *   Adding for reading and processing QR scan result for importing txcode
        */
        if (requestCode == TXCODE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("barcode");
                editTextTxID.post(new Runnable() {
                    @Override
                    public void run() {
                        String txDetail;
                        try {
                            byte[] txDecodeBytes = Base64.decode(barcode.displayValue, Base64.DEFAULT);
                            txDetail = new String (txDecodeBytes);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "QR is not recognized. Please enter transaction code manually.", Toast.LENGTH_LONG).show();
                            editTextTxID.setText("REJECTED");
                            return;
                        }

                        String[] txInfo = txDetail.split("\\|", -1);
                        String txTime = (txInfo.length > 0)?txInfo[0]:"--------";
                        String txSrcAcc = (txInfo.length > 2)?txInfo[2]:"--------";
                        String txDstAcc = (txInfo.length > 2)?txInfo[3]:"--------";
                        String txAmount = (txInfo.length > 2)?txInfo[4]:"--------";
                        String txCode = txTime.substring(txTime.length() - 8, txTime.length());

                        try{
                            long timeStamp = Long.parseLong(txTime);
                            Date time = new Date(timeStamp);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                            txTime = dateFormat.format(time);
                        } catch (Exception e) {
                            txTime = "------";
                        }

                        try {
                            double amount = Double.parseDouble(txAmount);
                            DecimalFormat currencyFormat = new DecimalFormat("###,###.## VND");
                            txAmount = currencyFormat.format(amount);

                        } catch (Exception e) {
                            txAmount = "------";
                        }
                        AlertDialog.Builder alert = new AlertDialog.Builder(m_context);
                        View view = LayoutInflater.from(m_context).inflate(R.layout.txverify_dialog, null);
                        final AlertDialog alertDialog = alert.create();
                        final TextView txTimeTextView = (TextView) view.findViewById(R.id.txTimeTextView);
                        final TextView txSrcAccTextView = (TextView) view.findViewById(R.id.txSrcAccTextView);
                        final TextView txDstAccTextView = (TextView) view.findViewById(R.id.txDstAccTextView);
                        final TextView txAmountTextView = (TextView) view.findViewById(R.id.txAmountTextView);
                        final TextView txCodeTextView = (TextView) view.findViewById(R.id.txCodeTextView);

                        txTimeTextView.setText(txTime);
                        txSrcAccTextView.setText(txSrcAcc);
                        txDstAccTextView.setText(txDstAcc);
                        txAmountTextView.setText(txAmount);
                        txCodeTextView.setText(txCode);

                        alertDialog.setTitle("Transaction detail");
                        alertDialog.setView(view);
                        alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "APPROVE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                editTextTxID.setText(txCodeTextView.getText());
                            }
                        });
                        alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "REJECT", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                editTextTxID.setText("REJECTED");
                            }
                        });

                        alertDialog.setCancelable(false);
                        alertDialog.show();

                    }
                });
            }
        }
    }
    
    /**
     * Token is selected from the token list
     */
    public class TokenListItemSelectedListener implements OnItemSelectedListener 
    {    
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
        {      
            String tokenSerialNumber = parent.getItemAtPosition(pos).toString();
            m_activeToken = m_libHelper.getTokenMetadata(tokenSerialNumber);
            if(m_activeToken.getType() == TokenMetadata.PINSTYLE_PINPAD) {
            	promptForPIN();
            }
            else {
            	m_pin = "";
            	refreshToken();
            }            
        }    
        
        public void onNothingSelected(AdapterView<?> parent) 
        {      
        }
    }
    
    /**
     * Check if device parameters (IMEI/MEID or MAC) are available
     * The following Android Permissions must be set
     * to avoid RuntimeException:
     *   android.permission.READ_PHONE_STATE
     *   android.permission.ACCESS_WIFI_STATE
     * @return
     */
    private boolean isAllDeviceParametersAvailable()
    {
        boolean deviceParamAvailable = true;
        TelephonyManager telephonyManager = (TelephonyManager) m_context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ){
            Toast.makeText(getApplicationContext(), "Application need to access to device ID for checking unique device", Toast.LENGTH_LONG).show();
            return false;
        }

        //check IMEI/MEID if the device is not a Wi-Fi only device
        String imei_meid = telephonyManager.getDeviceId();
        if(imei_meid == null) {
            deviceParamAvailable = false;
        }
            
        //check MAC address
        WifiManager wifiMan = (WifiManager) m_context.getSystemService(Context.WIFI_SERVICE); 
        WifiInfo wifiInf = wifiMan.getConnectionInfo();                 
        String mac = wifiInf.getMacAddress();
        if(mac == null) {
            deviceParamAvailable = false;
        }
        
        return deviceParamAvailable;                        
    }
    
    /**
     * Refresh the token list after a token is imported or deleted
     */
    private void refreshTokenList()
    {        
        ArrayList<String> tokenSNList = m_libHelper.getTokenSNList();
        ArrayAdapter<String> tokenSNArrayAdapter = new ArrayAdapter<String>(m_context, android.R.layout.simple_spinner_dropdown_item, tokenSNList);        
        m_tokenDropdown.setAdapter(tokenSNArrayAdapter);
        m_tokenDropdown.postInvalidate();
        
        if(tokenSNList.size() > 0) {
            if(m_activeToken == null) {
                m_activeToken = m_libHelper.getTokenMetadata(tokenSNList.get(0));
                m_tokenDropdown.setSelection(0);
            }
        }
        else {
            m_activeToken = null;
        }
        
        refreshToken();
    }
    
    /**
     * Get OTP information after a new token is selected
     */
    private void refreshToken()
    {
        stopOtpCodeTimer();
        if(m_activeToken != null) {            
            m_nicknameLabel.setText(m_activeToken.getNickname());
            m_tokenDropdown.setEnabled(true);
            updateOTP();
            startOtpCodeTimer();
        }
        else {            
            m_otpLabel.setText(R.string.defaultCode);
            m_secondsRemainingLabel.setText("");
            m_nicknameLabel.setText(R.string.noTokenInstalled);
            m_tokenDropdown.setEnabled(false);
        }            
    }
    
    /**
     * Update OTP after a specific token interval
     */
    private void updateOTP()
    {
        if(m_activeToken == null)  return;
            
        boolean error = false;
        if(m_secondsRemaining <= 0) {
            Otp otp = m_libHelper.getTokenCode(m_activeToken.getSerialNumber(), m_pin);            
            if(otp != null) {            
                m_otpLabel.setText(otp.getOtp());    
                m_secondsRemaining = otp.getTimeRemaining();
                //DDH Added for txsgning
                textViewTxSigningCode.setText("--------");
                //DDH End
            }
            else
            {
            	error = true;  
            	stopOtpCodeTimer();
            }
        }
        else {
            m_secondsRemaining--;
        }
        
        if(!error) {
            String strSecRemaining = String.format(getString(R.string.secsRemaining),m_secondsRemaining);
            m_secondsRemainingLabel.setText(strSecRemaining);
        }
        else {
            m_otpLabel.setText(R.string.defaultCode);
            m_secondsRemainingLabel.setText("");
        }        
    }
    
    /**
     * Update timer that shows how many seconds the OTP is valid
     */
    private void startOtpCodeTimer()
    {
        m_OTPTimer = new Timer();
        TimerTask mUpdateOtpTimerTask = new TimerTask() {
            public void run()
            {
                m_handler.post(mUpdateOtpTask); 
            }
        };
        m_OTPTimer.scheduleAtFixedRate(mUpdateOtpTimerTask, 0, ONE_SECOND);
    }
    
    /**
     * Stop the OTP timer
     */
    private void stopOtpCodeTimer()
    {
        if (m_OTPTimer != null)
        {
            m_OTPTimer.cancel();
            m_OTPTimer.purge();
            if (m_handler != null)
            {
                m_handler.removeCallbacks(mUpdateOtpTask);
            }
        }
        m_OTPTimer = null;
        m_secondsRemaining = 0;
    }
    
    /**
     * Runnable that updates OTP periodically
     */
    private Runnable mUpdateOtpTask = new Runnable()
    {
        public void run()
        {
            updateOTP();
        }
    };
    
    /**
     * Display PIN dialog. PIN dialog accepts an empty PIN or a PIN with 4 to 8 digits.
     */
    private void promptForPIN()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this); 
        View view = LayoutInflater.from(m_context).inflate(R.layout.pin_dialog, null); 
        final AlertDialog alertDialog = alert.create();
        final EditText input = (EditText)view.findViewById(R.id.pinText);
        input.setOnKeyListener(new View.OnKeyListener() {			
	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	            //Allows empty PIN or PIN with 4 to 8 digits.
                int textLength = input.getText().toString().length();
                boolean bEnabled = (textLength == 0 || (textLength >=4 && textLength <= 8)) ? true : false;
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(bEnabled);				
                return false;
            }
        });
        alertDialog.setTitle(R.string.enterPinDialogTitle);     	
    	alertDialog.setView(view);      	
    	alertDialog.setButton(alertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int whichButton) {   
                m_pin = input.getText().toString();    
                refreshToken();
            } 
    	});  
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    
    //@Override
	//DDH public void onBackPressed()	{		Log.w("SecurID Module", "Back Button is Disabled");	}
    //DDH 20170721: Adding for back button on exit
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

}
