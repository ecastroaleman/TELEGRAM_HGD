package com.ericsson.hgd;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    	 //  System.out.println(ApplicationProperties.INSTANCE.getJIRA_URL());
    	  // System.out.println(ApplicationProperties.INSTANCE.getJIRA_ADMIN_USERNAME());
    	
    	   String msj = "Test Execution DRS-XXXXXX VPN-IP EN RED FUSION NUEVO ASIGNADOR";
    	   
    	   int inicio  = msj.indexOf("DRS");
    	   String msj2  = msj.substring(inicio,msj.length());
    	  System.out.println(msj.indexOf("DRS"));
    	  System.out.println(msj2);
    	   
    	  
    	
    }
}
