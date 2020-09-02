package com.ericsson.hgd;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



public class SendMsg {
	
    public static final String APITOKEN = ApplicationProperties.INSTANCE.apiToken();
    public static final String CHATEVOLUTIVOS =ApplicationProperties.INSTANCE.chatEvolutivos();
    public static final String CHATPERSONAL = ApplicationProperties.INSTANCE.chatPersonal();
    public static final String CHATTEMP= ApplicationProperties.INSTANCE.chatTemporal();
    public static final Logger lg = Logger.getLogger(SendMsg.class);
    
    public static String sendFileToTelegram(String ptexto, String pChat) throws JSONException, InvalidKeyException,  NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, IOException {
    	String resp = "OK";
        StringBuilder envio = new StringBuilder();
        envio.append("curl -X  POST \"");
        envio.append(ApplicationProperties.INSTANCE.urlTelegram());
        envio.append(Security.decrypt("LNFDESAATLAS",APITOKEN));
        envio.append("/sendDocument\" -F chat_id=");
        envio.append("\""+pChat+"\"");
        envio.append(" -F document=\"@");
        envio.append(ptexto);
        envio.append("\" --insecure");

        lg.info(envio.toString());
      
    
			Process process = Runtime.getRuntime().exec(envio.toString());
			InputStream ins = process.getInputStream();
					
			  InputStreamReader isReader = new InputStreamReader(ins);
		      BufferedReader reader = new BufferedReader(isReader);
		      StringBuffer sb = new StringBuffer();
			  StringBuffer resp2 = sb.append(reader.readLine());
		   
			  JSONObject response = new JSONObject(resp2.toString());
						      
		     resp=response.get("ok").toString();
			 ins.close();
			 process.destroy();
		
        return resp;
    }
    
    public String sendToTelegram(String ptexto, String pChat) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    	String resp = "OK";
        StringBuilder envio3 = new StringBuilder();
        envio3.append(ApplicationProperties.INSTANCE.urlTelegram());
        envio3.append(Security.decrypt("LNFDESAATLAS",APITOKEN));
        envio3.append("/sendMessage?chat_id=");
        envio3.append(pChat);
        envio3.append("&text=");
        envio3.append(ptexto);
        
        try {
        	
            URL url = new URL(envio3.toString());
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            InputStreamReader isReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isReader);
            Reader isr = new InputStreamReader(is);
            StringBuffer sb = new StringBuffer();
            String str;
            while((str = reader.readLine())!= null){
               sb.append(str);
            }
            try {
				resp  = leerJson(sb.toString());
			} catch (Exception e) {
				lg.error(e.getMessage(),e);
			}
            isr.close();
        } catch (IOException e) {
        	lg.error(e.getMessage(),e);
        }
        
        envio3 = new StringBuilder();
        lg.info("Respuesta Envío: "+resp);
        return resp;
    }
    
    public static String leerJson(String cadena) throws JSONException  {
    	 JSONObject response;
    	 String retorno;
		
			response = new JSONObject(cadena);
			 retorno = response.get("ok").toString();
		
    	return retorno;
    }
    
      
}