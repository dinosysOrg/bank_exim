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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

//import org.apache.log4j.Logger;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rsa.securidlib.Otp;
import com.rsa.securidlib.android.AndroidSecurIDLib;
import com.rsa.securidlib.android.TokenImportDataParser;
import com.rsa.securidlib.exceptions.DatabaseException;
import com.rsa.securidlib.exceptions.DatabaseFullException;
import com.rsa.securidlib.exceptions.DeviceIDInaccessibleException;
import com.rsa.securidlib.exceptions.DuplicateTokenException;
import com.rsa.securidlib.exceptions.ExpiredTokenException;
import com.rsa.securidlib.exceptions.InvalidDeviceBindingException;
import com.rsa.securidlib.exceptions.SecurIDLibException;
import com.rsa.securidlib.exceptions.TokenImportFailureException;
import com.rsa.securidlib.exceptions.TokenNotFoundException;
import com.rsa.securidlib.tokenstorage.TokenMetadata;
import com.rsa.eximbankapp.R;

/**
 * This class provides helper methods that interact with the RSA SecurID SDK.
 * Make sure that your mobile client application has the following privileges set:
 * android.permission.READ_PHONE_STATE
 * android.permission.ACCESS_WIFI_STATE
 * 
 * If you use CT-KIP to import tokens, your mobile client application also needs the following privileges:
 * android.permission.INTERNET
 * android.permission.ACCESS_NETWORK_STATE
 * 
 * See the Javadoc for RSA SecurID SDK for Android for more information on required Android permissions.
 */
public class SecurIDLibHelper {
	

    //private final Logger log = Logger.getLogger(ImportTokenActivity.class);

    private static SecurIDLibHelper m_instance;
    private AndroidSecurIDLib m_lib;    
    private Context m_context;
            
    private DateFormat m_usersDateFormat;
    
    private SecurIDLibHelper(Context context) throws SecurIDLibException
    {
        //initialize AndroidSecurIDLib
        m_lib = new AndroidSecurIDLib(context);    
        m_context = context;
        
        TimeZone tz = TimeZone.getTimeZone("GMT-0");
        m_usersDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        m_usersDateFormat.setTimeZone(tz);
    }
    
    static public SecurIDLibHelper getInstance(Context context) throws SecurIDLibException
    {
        if(m_instance == null) {
            m_instance = new SecurIDLibHelper(context);
        }
        
        return m_instance;        
    }
    
    /**
     * Get a list of token serial numbers
     * @return array of token serial numbers
     */
    public ArrayList<String> getTokenSNList()
    {
        ArrayList<String> tokenSNList = new ArrayList<String>();        
        try {
            //get an enumeration of token metadata
            Enumeration<TokenMetadata> tokenList = m_lib.getTokenList();                    
            if(tokenList != null)
            {
                //enumerate all of the token serial numbers
                while(tokenList.hasMoreElements())
                {
                    TokenMetadata tm = tokenList.nextElement();
                    tokenSNList.add(tm.getSerialNumber());
                }
            }        
        }
        catch(SecurIDLibException ex) {
            ex.printStackTrace();
            displayError(ex);
        }
        return tokenSNList;
    }
    
    /**
     * Get the metadata (attributes) of the token specified by token serial number
     * @param tokenSerialNumber serial number of the token
     * @return token metadata
     */
    public TokenMetadata getTokenMetadata(String tokenSerialNumber)
    {        
        TokenMetadata tm = null;
        try {
            Enumeration<TokenMetadata> tokenList = m_lib.getTokenList();
            if(tokenList != null) {            
            	TokenMetadata tmTemp = null;
                while(tokenList.hasMoreElements()) {
                	tmTemp = tokenList.nextElement();
                    if(tmTemp.getSerialNumber().equals(tokenSerialNumber)) {
                    	tm = tmTemp;
                        break;
                    }
                }
                if(tm != null) {
                    //get token serial number
                    String tokenSN = tm.getSerialNumber();
                    //retrieve token expiration date and format it
                    long expirationDateMillis = tm.getExpirationDate();
                    Date date = new Date(expirationDateMillis);
                    String strExpirationDate = m_usersDateFormat.format(date);
                    //get interval in seconds between the current one-time password (OTP) and the next OTP
                    //30 for 30-second token or 60 for 60-second token
                    int tokenInterval = tm.getInterval();
                    //get length of OTP
                    //6 for 6-digit token or 8 for 8-digit token
                    int tokenLength = tm.getLength();
                    //get token PIN type: PINPad, PINless, or Fob
                    //for PINPad token, mobile client application should prompt user for the PIN
					//to get passcode (PIN combined with OTP)
                    int tokenPINType = tm.getType();
                    //get token nickname
                    String nickname = tm.getNickname();
                }
            }
        }
        catch(SecurIDLibException ex) {
            ex.printStackTrace();
            displayError(ex);
        }
        return tm;
    }
    
    /**
     * Get OTP for the token specified by the token serial number
     * @param tokenSerialNumber serial number of the token
     * @param pin PIN assigned to the token
     * @return OTP information for the token
     */
    public Otp getTokenCode(String tokenSerialNumber, String pin)
    {
        Otp otp = null;
        try {
            
        	if(pin == null) { pin = ""; }  //if there is no PIN, use empty string
            otp = m_lib.getOtp(tokenSerialNumber, pin.getBytes());
        	
        }
        catch(SecurIDLibException ex) {
						
            ex.printStackTrace();
			displayError(ex);

        }
        return otp;
    }
    
    /**
     * Import a token from a CTF string
     * @param ctf token string in CTF URI format
     * @param ctfPassword token file password 
     * @return token serial number of the imported token
     */
    String importTokenFromCTF(String ctf, String ctfPassword, String[] error)
    {
        String tokenSerialNumber = null;
        try {
        	if(ctfPassword == null) { ctfPassword = ""; } //if there is no token file password, use empty string
        	
        	String ctfData = getCtfData(ctf);        	
        			
            tokenSerialNumber = m_lib.importTokenFromCtf(ctfData, ctfPassword.getBytes());
            
            Log.e("Password",ctfPassword);
        }
    	catch (URISyntaxException e) {
            e.printStackTrace();
            displayError(e, true, error);
    	}
        catch(SecurIDLibException ex) {
            ex.printStackTrace();
            displayError(ex, true, error);
            
            Log.e("Password","SecurIDLibException Thrown");
        }
        
        return tokenSerialNumber;
    }

	/**
	 * Gets the ctf string if given string is a valid URI format.
	 *
	 * @param ctfUriString the ctf string with valid URI format.
	 * @return the ctf data string
	 * @throws URISyntaxException the uRI syntax exception
	 */
	private String getCtfData(String ctfUriString) throws URISyntaxException {
		if (m_lib.isValidCtfFormat(ctfUriString)) {
			// Parse the url to get the real ctf string
			URI tokenImportUri = new URI(ctfUriString);
			String queryString = tokenImportUri.getRawQuery();
        	String ctfString = TokenImportDataParser.getCtfDataFromQuery(queryString);
			return ctfString;
		}
		// return the original string if the given string is not a valid URI format
		return ctfUriString;
	}
    
    /**
     * Import a token from CT-KIP.
     *
     * @param url URL of the CT-KIP server
     * @param activationCode one-time activation code required for CT-KIP provisioning
     * @param allowUntrustedCert allow or prohibit untrusted CT-KIP server certificate
     * @param error the error to return the message if ocurred
     * @return token serial number of the imported token
     */
    String importTokenFromCTKIP(String url, String activationCode, boolean allowUntrustedCert, String[] error)
    {
    	String tokenSerialNumber = null;
        
        try {
            tokenSerialNumber = m_lib.importTokenFromCtkip(url, activationCode, allowUntrustedCert);
        }
        catch(SecurIDLibException ex) {

            //log.error(ex.getMessage(), ex);
            ex.printStackTrace();
            displayError(ex, true, error);
        }
        catch (Exception ue){
        	// Display unexpected exception
            //log.error(ue.getMessage(), ue);
            ue.printStackTrace();
            displayError(ue, true, error);
        }
        
        return tokenSerialNumber;
    }
    
    /**
     * Import a token from a token file. The file is also called an SDTID file due
	 * to the .sdtid file extension.
     * @param filePath path to the token file 
     * @param password token file password
     * @return token serial number of the imported token
     */
    String importTokenFromFile(String filePath, String password, String[] error)
    {
        String tokenSerialNumber = null;
        
        try {
            
        	tokenSerialNumber = m_lib.importTokenFromFile(filePath, password.getBytes());
                    	
        }
        catch(SecurIDLibException ex) {
            ex.printStackTrace();
            displayError(ex, true, error);
        }
        
        return tokenSerialNumber;
    }
    
    
    String importTokenFromData(String filePath, String password)
    {
        String tokenData = null;
        
        RandomAccessFile f;
        
        try {            
        	f = new RandomAccessFile(filePath, "r");
			byte[] b = new byte[(int)f.length()];
	    	f.read(b);
	    	
	    	tokenData =  m_lib.importTokenFromData(b, password.getBytes());
        }
        catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        catch(SecurIDLibException ex) {
            ex.printStackTrace();
            displayError(ex);
        }
        
        return tokenData;
    }
    
    /**
     * Delete a token specified by the token serial number
     * @param tokenSerialNumber serial number of the token
     * @return true token deleted successfully
     * @return false failed to delete token
     */
    boolean deleteToken(String tokenSerialNumber)
    {
        boolean ret = true;
        try {
            m_lib.deleteToken(tokenSerialNumber);
        }
        catch(SecurIDLibException ex) {
            ret = false;
            ex.printStackTrace();
            displayError(ex);
        }
        
        return ret;
    }
    
    /**
     * Set or change a token nickname
     * @param tokenSerialNumber serial number of the token
     * @param tokenNickname new token nickname
     * @return true token is renamed successfully
     * @return false failed to rename token
     */
    boolean renameToken(String tokenSerialNumber, String tokenNickname)
    {
        boolean ret = true;
        try {
            m_lib.setTokenNickname(tokenSerialNumber, tokenNickname);
        }
        catch(SecurIDLibException ex) {
            ret = false;
            ex.printStackTrace();
            displayError(ex);
        }
        
        return ret;
    }
    
    /**
     * Get Device ID used for token device binding
     * @return Device ID
     */
    String getDeviceID()
    {
        String deviceID = "";
        try {
            deviceID = m_lib.getDeviceId();
        }
        catch(SecurIDLibException ex) {
            ex.printStackTrace();
            displayError(ex);
        }
        
        return deviceID;
    }
    
    /**
     * Get the SDK library information
     * @return SDK library information
     */
    String getLibaryInfo() 
    {
        return m_lib.getLibraryInfo();
    }
    
    
    
    /**
     * Display error message based on Exception
     * @param ex exception information to be displayed
     * @param displayErrorInAsyncTask if the error is shown after the complete of AsyncTask
     * @param error
     */
    private void displayError(Exception ex, boolean displayErrorInAsyncTask, String[] error)
    {
        String errorMsg = m_context.getString(R.string.general);

        if (!displayErrorInAsyncTask){

        	Toast toast = Toast.makeText(m_context, errorMsg, Toast.LENGTH_SHORT);
        	toast.show();
        }
        else
        {
        	error[0] += errorMsg;
        }
    }
    

    /**
     * 
     * Display error message based on SecurIDLibException
     * @param ex
     */
    private void displayError(SecurIDLibException ex)
    {
    	displayError(ex, false, null);
    }
    
    /**
     * Display error message based on SecurIDLibException, or return the message to caller.
     * @param ex exception information to be displayed
     * @param displayErrorInAsyncTask if the error is shown after the complete of AsyncTask
     * @param error return the message if the error occurred
     */
    private void displayError(SecurIDLibException ex, boolean displayErrorInAsyncTask, String[] error)
    {
        String errorMsg;
        if(ex instanceof DatabaseFullException) {
            errorMsg = m_context.getString(R.string.databasefullexception);
        }
        else if(ex instanceof DatabaseException) {
            errorMsg = m_context.getString(R.string.databaseexception);
        }
        else if(ex instanceof DeviceIDInaccessibleException) {
            errorMsg = m_context.getString(R.string.deviceidinaccessibleexception);
        }
        else if(ex instanceof DuplicateTokenException) {
            errorMsg = m_context.getString(R.string.duplicatetokenexception);
        }
        else if(ex instanceof ExpiredTokenException) {
            errorMsg = m_context.getString(R.string.expiredtokenexception);
        }
        else if(ex instanceof InvalidDeviceBindingException) {
            errorMsg = m_context.getString(R.string.invaliddevicebindingexception);
        }
        else if(ex instanceof TokenNotFoundException) {
            errorMsg = m_context.getString(R.string.tokennotfoundexception);
        }
        else if(ex instanceof TokenImportFailureException) {
            errorMsg = m_context.getString(R.string.tokenimportexception);
        }
        else {
            errorMsg = m_context.getString(R.string.general);
        }
        
        if (!displayErrorInAsyncTask){
        	Toast toast = Toast.makeText(m_context, errorMsg, Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
        	error[0] += errorMsg;
        }
        
    }

	 
}
