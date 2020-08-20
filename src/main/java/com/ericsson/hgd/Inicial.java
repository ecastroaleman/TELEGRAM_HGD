package com.ericsson.hgd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;


public class Inicial {
	
	    private static final String JIRA_URL = ApplicationProperties.INSTANCE.getJiraUrl();
	    private static final String JIRA_ADMIN_USERNAME = ApplicationProperties.INSTANCE.getJiraAdminUsername();
	    private static final String JIRA_ADMIN_PASSWORD = ApplicationProperties.INSTANCE.getJiraAdminPassword();
	    private static final String ENCODING = "UTF-8";
	    static int totRegistros=0;
	    public static final Logger lg = Logger.getLogger(Inicial.class);
	    
	public static void main(String[] args) throws Exception {
		try {
		
		lg.info("Creando Conexión a Jira...");
		JiraRestClient connJira = getclienteJira();
		lg.info("Obteniendo Tickets...");	
		ArrayList<Tickets> totalTickets = obtenerTickets(connJira);
		
		lg.info("Total Tickets obtenidos ... "+totalTickets.size());
			
		
		iteraTickets(totalTickets);
	
		connJira.close();
		} catch (IOException e) {
			lg.error(e.getMessage(),e);
			SendMsg smsge = new SendMsg();
			smsge.sendToTelegram(URLEncoder.encode("ERROR : "+e.getMessage(), ENCODING),ApplicationProperties.INSTANCE.chatEnvio());	
		}
		
		
		lg.info("Proceso Finalizado");
		
		
		
	}
	
	public static void iteraTickets( ArrayList<Tickets> datos) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
	  
	    StringBuilder msg = new StringBuilder();
		for (int i=0; i<datos.size(); i++) {
			 
			    msg.append("\nAtender el Ticket: "+datos.get(i).getKey());   
			    msg.append("\nCreado el      : "+datos.get(i).getDateCreated());
			    msg.append("\nCreador por   : "+datos.get(i).getCreatedBy());
			    msg.append("\nPrioridad        : "+datos.get(i).getPriority());
			    msg.append("\nStatus Actual  : "+datos.get(i).getStatus());
			    msg.append("\nDetectado en  : "+datos.get(i).getDetectionOn());
			    msg.append("\nPara el Equipo : #TEAM# ");
			    msg.append("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			    
			  String[] teams = datos.get(i).getFixVersion().split("-");
			 
			  String msg1 = "";
			  if (teams.length == 1) {
				 	 
				  msg1 = msg.toString().replaceFirst("#TEAM#", teams[0]);			
				  determinarEquipo(msg1);
				  
			  }else {
							
					for (int j=0; j < teams.length;j++) {
					  msg1 = msg.toString().replace("#TEAM#", teams[j]);
					  determinarEquipo(msg1);
					 
				    }
			  }
			   			
		   
			msg = new StringBuilder();
		}
	  
	}
	
	public static String determinarEquipo(String pmsg) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		 SendMsg smsg = new SendMsg();
		 String retorno = "";
		 
		 try {
			lg.info("Enviando Mensaje : "+pmsg);
			if (pmsg.contains("Asignacion") || pmsg.contains("Nuevo Asignador")) {						
					retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg, ENCODING),ApplicationProperties.INSTANCE.chatEnvioAsig());
			}else if (pmsg.contains("Entrada")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg, ENCODING),ApplicationProperties.INSTANCE.chatEnvioEnt());		
			}else if (pmsg.contains("Frontales")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg, ENCODING),ApplicationProperties.INSTANCE.chatEnvioFro());	
			}else if (pmsg.contains("CDR")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg, ENCODING),ApplicationProperties.INSTANCE.chatEnvioCdR());	
			}
			 
			 
		 }catch (UnsupportedEncodingException e) {
				lg.error(e.getMessage(),e);
				 SendMsg smsge = new SendMsg();
				retorno = smsge.sendToTelegram(URLEncoder.encode("ERROR : "+e.getMessage(), ENCODING),ApplicationProperties.INSTANCE.chatErrores());
			}
		 
		
		return retorno;
	}
	public static JiraRestClient getclienteJira () throws URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		 JiraRestClient client = null;
		 
			 URI uri = new URI(JIRA_URL);	  
			
			 client = factory.createWithBasicHttpAuthentication(uri, JIRA_ADMIN_USERNAME, Security.decrypt("LNFDESAATLAS",JIRA_ADMIN_PASSWORD));
		
		return client;
		
	}
	public static String obtenerJQL(JiraRestClient pconnJira) throws InterruptedException, ExecutionException {
		String filterID =  ApplicationProperties.INSTANCE.getFilterID();
		Promise<Filter> fjql = pconnJira.getSearchClient().getFilter(Long.parseLong(filterID));
		String jql = fjql.get().getJql();
		lg.info(jql);		
		return jql;
	}

	public static ArrayList<Tickets> obtenerTickets(JiraRestClient pconnJira ) throws InterruptedException, ExecutionException  {
	  ArrayList<Tickets> grupoTic = new ArrayList<Tickets>();
	 		
	  Promise<SearchResult> searchJqlPromise = pconnJira.getSearchClient().searchJql(obtenerJQL(pconnJira));
	  
	  for (Issue issue : searchJqlPromise.claim().getIssues()) {
			  Tickets tic = new Tickets();
			  String originador = "";
			  String detectionOn = "";
			  StringBuilder versiones  = new StringBuilder();
 
			    tic.setDateCreated(issue.getCreationDate().toString("dd/MM/yyyy HH:mm"));
	            tic.setKey(issue.getKey());
	            tic.setStatus(issue.getStatus().getName());
	            tic.setPriority(issue.getPriority().getName());
	            
	            try {
					JSONObject creador = new JSONObject(issue.getFieldByName("Creator").getValue().toString());
					originador  = creador.get("displayName").toString();				
				} catch (JSONException e) {
					lg.error(e.getMessage(),e);
					originador = "";
				}
	            
	            
	            if (issue.getFieldByName("Detection On").getValue() != null) {
	            try {
					JSONObject dEtection = new JSONObject(issue.getFieldByName("Detection On").getValue().toString());
					detectionOn = dEtection.get("value").toString();
				} catch (JSONException e) {
					lg.error(e.getMessage(),e);
					detectionOn = "";
				}
	            }            	
	            	
	            Iterable<Version> fv = issue.getFixVersions();
	            
	            Iterator<Version> it = fv.iterator();
	            
	            
	            while(it.hasNext()) {
	                Version element = it.next();
	             	                
	                if (!element.getName().equals("Defectos ATLAS")) {
	                	
	                if (  (versiones.toString().contains("Nuevo Asignador") && element.getName().equals("Defectos Asignacion"))
	                  ||  (versiones.toString().contains("Defectos Asignacion") && element.getName().equals("Defectos Nuevo Asignador"))
	                		) {lg.info("La Etiqueta Asignación está duplicada.");}
	                else {  
	                   
	                  if (it.hasNext()) {versiones.append(element.getName()+"-");}
	                  else {versiones.append(element.getName());}
	                }
	                
	                }
	            }
	            	
	            tic.setCreatedBy(originador);
	            tic.setDetectionOn(detectionOn);
	            tic.setFixVersion(versiones.toString());
	        
	            
			  grupoTic.add(tic);
		  }
		return grupoTic;
	
	}
}
