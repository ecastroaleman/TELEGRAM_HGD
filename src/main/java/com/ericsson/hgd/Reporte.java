package com.ericsson.hgd;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jettison.json.JSONException;
import org.joda.time.DateTime;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;



public class Reporte {
	   private static final String JIRA_URL = ApplicationProperties.INSTANCE.getJiraUrl();
	    private static final String JIRA_ADMIN_USERNAME = ApplicationProperties.INSTANCE.getJiraAdminUsername();
	    private static final String JIRA_ADMIN_PASSWORD = ApplicationProperties.INSTANCE.getJiraAdminPassword();
	    private static final String ENCODING = "UTF-8";
	    static int totRegistros=0;
	    public static final Logger lg = Logger.getLogger(Reporte.class);
	    
	    
	  
	    
		public static void iteraTicketsExcel( ArrayList<Tickets> datos, String sprint) throws IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, JSONException {  
		    String archCSV = "C:\\digicel2\\HGD_TELEGRAM_ES\\reporte\\";
			DateTime fechahora = new DateTime();
			String nombrerep = "reporte_"+fechahora.toString("ddMMyyyyHHmmss")+".xls";
		
			HSSFWorkbook libro = new HSSFWorkbook();
			
			HSSFSheet hojaFrontales = null;			
			HSSFSheet hojaCdr = null;
			HSSFSheet hojaEntradas = null;
			HSSFSheet hojaAsignacion = null;
			
			int lineaF=0;
			int lineaC=0;
			int lineaE=0;
			int lineaA=0;
		
			HSSFCell celda0 = null ;
			HSSFCell celda1 = null ;
			HSSFCell celda2 = null ;
			HSSFCell celda5 = null ;
			HSSFCell celda7 = null ;
			HSSFCell celda12 = null ;
			
								
			for (int i=0; i<datos.size(); i++) {
				
				switch(datos.get(i).getFixVersion()){
				
				case "Defectos Frontales" :
					if (lineaF==0) {hojaFrontales = libro.createSheet("Frontales");}
					HSSFRow filaF = hojaFrontales.createRow(lineaF);
					lineaF++;
					celda0 = filaF.createCell((short)0);
					celda1 = filaF.createCell((short)1);
					celda2 = filaF.createCell((short)2);
					celda5 = filaF.createCell((short)5);
					celda7 = filaF.createCell((short)7);
					celda12 = filaF.createCell((short)12);
					break;
				case "Defectos CDR" :
					if (lineaC==0) {hojaCdr = libro.createSheet("Cdr");}
					HSSFRow filaC = hojaCdr.createRow(lineaC);
					lineaC++;
					celda0 = filaC.createCell((short)0);
					celda1 = filaC.createCell((short)1);
					celda2 = filaC.createCell((short)2);
					celda5 = filaC.createCell((short)5);
					celda7 = filaC.createCell((short)7);
					celda12 = filaC.createCell((short)12);
					break;
				case "Defectos Entrada" :
					if (lineaE==0) {hojaEntradas = libro.createSheet("Entrada");}
					HSSFRow filaE = hojaEntradas.createRow(lineaE);
					lineaE++;
					celda0 = filaE.createCell((short)0);
					celda1 = filaE.createCell((short)1);
					celda2 = filaE.createCell((short)2);
					celda5 = filaE.createCell((short)5);
					celda7 = filaE.createCell((short)7);
					celda12 = filaE.createCell((short)12);
					break;
				case "Defectos Asignacion" :
					if (lineaA==0) {hojaAsignacion = libro.createSheet("Asignacion");}
					HSSFRow filaA = hojaAsignacion.createRow(lineaA);
					lineaA++;
					celda0 = filaA.createCell((short)0);
					celda1 = filaA.createCell((short)1);
					celda2 = filaA.createCell((short)2);
					celda5 = filaA.createCell((short)5);
					celda7 = filaA.createCell((short)7);
					celda12 = filaA.createCell((short)12);
					break;		
				}
			
			
			HSSFRichTextString texto0 = new HSSFRichTextString(datos.get(i).getKey());
			HSSFRichTextString texto1 = new HSSFRichTextString(sprint);
			HSSFRichTextString texto2 = new HSSFRichTextString(datos.get(i).getStatus());
			HSSFRichTextString texto5 = new HSSFRichTextString(datos.get(i).getEpicName());
			HSSFRichTextString texto7 = new HSSFRichTextString(datos.get(i).getDateCreated());
			HSSFRichTextString texto12 = new HSSFRichTextString(datos.get(i).getFixVersion());
		
			if (celda0 != null || celda2 != null || celda5 != null || celda7 != null || celda12 != null) {
			celda0.setCellValue(texto0);
			celda1.setCellValue(texto1);
			celda2.setCellValue(texto2);
			celda5.setCellValue(texto5);
			celda7.setCellValue(texto7);
			celda12.setCellValue(texto12);
			}
			
			}	
			
			try {
				   FileOutputStream elFichero = new FileOutputStream(archCSV+nombrerep);
				   libro.write(elFichero);
				   elFichero.close();
				} catch (Exception e) {
				   lg.info("Error en Archivo Excel -> "+e.getMessage());
				}
			
			
					lg.info("Sent ? -> "+SendMsg.sendFileToTelegram(archCSV+nombrerep, "400542399"));
			
			

			
			}
	    
		
		public static void iteraTickets( ArrayList<Tickets> datos) throws IOException {  
		    String archCSV = "C:\\digicel2\\HGD_TELEGRAM_ES\\reporte\\";
			DateTime fechahora = new DateTime();
			String nombrerep = "reporte_"+fechahora.toString("ddMMyyyyHHmmss")+".csv";
					
			StringBuilder sb = new StringBuilder();
			FileOutputStream fos = new FileOutputStream(archCSV+nombrerep);
			Writer out = new BufferedWriter(new OutputStreamWriter(
				       fos, StandardCharsets.ISO_8859_1));
			try {
						
			for (int i=0; i<datos.size(); i++) {
			
			lg.info(datos.get(i).getKey()+",<Sprint>,"+
			        datos.get(i).getStatus()+",<AsignadoA>,<sp>,"+
					datos.get(i).getEpicName()+",<causa>,"+
			        datos.get(i).getDateCreated()+",,,,,"+
					datos.get(i).getFixVersion());
			
		sb.append(datos.get(i).getKey()+",<Sprint>,"+
			        datos.get(i).getStatus()+",<AsignadoA>,<sp>,"+
					datos.get(i).getEpicName()+",<causa>,"+
			        datos.get(i).getDateCreated()+",,,,,"+
					datos.get(i).getFixVersion()+"\n");
			
			
			}	
			 out.write(sb.toString());
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
	           out.close();
			}
			}
	public static void main(String[] args) throws Exception {
	
		lg.info("Creando Conexión a Jira...");
		JiraRestClient connJira = Utils.getclienteJira(JIRA_URL,JIRA_ADMIN_USERNAME,JIRA_ADMIN_PASSWORD);
		lg.info("Obteniendo Tickets creados Hoy...");	
		
		String sprint = Utils.obtenerSprint(connJira, JIRA_ADMIN_USERNAME, Security.decrypt("LNFDESAATLAS",JIRA_ADMIN_PASSWORD));
		
		String filterRep =  ApplicationProperties.INSTANCE.getFilterRep();
		lg.info(sprint);
		ArrayList<Tickets> totalTickets = Utils.obtenerTickets(connJira, filterRep, true);
	
		iteraTicketsExcel(totalTickets, sprint);

/* 		Funciona para cambiar status
 * Utils.moveStatus(connJira,4,"LNFDES-3");
	
		
		*/

/* Funciona para agregar fixVersion		
		Iterable<Version> vers = iss.getFixVersions();	
		Iterator<Version> itvers = vers.iterator();
		List<Version> fixVersions = new ArrayList<>();
		while (itvers.hasNext()) {
		Version itemvers = itvers.next();
		lg.info(itemvers.getSelf());
		lg.info(itemvers.getId());
		lg.info(itemvers.getName());
		lg.info(itemvers.getDescription());
		lg.info(itemvers.getReleaseDate());
		fixVersions.add(itemvers);
		}
		
	URI fixuri = new URI("http://192.168.1.109:8080/rest/api/2/version/10001");
	Long id = (long) 10001;
	DateTime fecha = new DateTime("2020-08-31T00:00:00.000-06:00");
	Version e = new Version(fixuri ,id ,"Defectos_Entrada","",false,false,fecha);
		
		fixVersions.add(e);
		
		updateFixVersion("LNFDES-4", fixVersions,connJira);*/
	
//FUNCIONA ec setIssueLabels("Sprint_2020_11_Entradas",connJira,iss);

//FUNCIONA PARA PONER LA DESCRIPCION		
//*ec setIssueDescription(connJira,"LNFDES-1","Comentario");



// FUNCIONA PARA PONER LABELS
/*ec
List<String> listado = new ArrayList<String>();
listado.add("11-20");
setIssueLabels("LNFDES-1",listado,connJira);
*/		

/*		  Promise<SearchResult> searchJqlPromise = connJira.getSearchClient().searchJql(Inicial.obtenerJQL(connJira));
		  
		  for (Issue issue : searchJqlPromise.claim().getIssues()) {
				 
				
					 
				 //   tic.setDateCreated(issue.getCreationDate().toString("dd/MM/yyyy HH:mm"));
		            lg.info(issue.getKey());
		            lg.info(issue.getStatus().getName());
		            
		            
		            Iterable<Comment> coment = issue.getComments();
		          
		            Iterator<Comment> itcom = coment.iterator();
		            
		            lg.info(coment.toString());
		            while(itcom.hasNext()) {
		            	 Comment element = itcom.next();
		            	lg.info(element.getAuthor());
		            	
		            	
		            	
		            	
		         
		            	
		            }
		            
		            
		            
		            lg.info(issue.getFieldByName("Story points").getValue());
		            
		            
		            
		          JSONObject origen = new JSONObject(issue.getFieldByName("Origen Bug").getValue().toString());
					
		          JSONObject origen2 = (JSONObject) origen.get("child");
		          
		           
		            lg.info(origen2.get("value").toString());
		            
		            
		            JSONObject solucion = new JSONObject(issue.getFieldByName("Origen Solución").getValue().toString());
		            lg.info(solucion.get("value").toString());
		          
		           Iterable<IssueLink> links = issue.getIssueLinks();
		           Iterator<IssueLink> itlink = links.iterator();
		           
		           
		           while(itlink.hasNext()) {
		            	  IssueLink element = itlink.next();
		            	  lg.info(element.getTargetIssueKey());
		                  lg.info(element.getIssueLinkType().getDescription());
		           
		            	
		            }
		           
		           
		      
		  }*/
		            
	}
	
	 
	

}
