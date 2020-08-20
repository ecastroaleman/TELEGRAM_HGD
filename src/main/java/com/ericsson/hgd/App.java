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
    	
    	   String msj = "Defectos ATLAS - Defectos Asignador - ";
    	   
    	   if (msj.contains("Entrada")) {
    		   System.out.println("Entrada");
    	   }else if (msj.contains("Asignacion")) {
    		   System.out.println("Asignación");
    	   }else if (msj.contains("Frontales")) {
    		   System.out.println("Frontales");
    	   }else if (msj.contains("CDR")) {
    		   System.out.println("CDR");
    	   }else {
    		   System.out.println("Otro Equipo");
    	   }
    	   
    	  
    	
    }
}
