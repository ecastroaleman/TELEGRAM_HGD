package com.ericsson.hgd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class Utils {
	
	private Utils() {
		throw new IllegalStateException("Utility class");

	}
	  public static final Logger lg = Logger.getLogger(Utils.class);
	  private static final String JIRA_URL = ApplicationProperties.INSTANCE.getJiraUrl();
	  private static final String JIRA_ADMIN_USERNAME = ApplicationProperties.INSTANCE.getJiraAdminUsername();
	  private static final String JIRA_ADMIN_PASSWORD = ApplicationProperties.INSTANCE.getJiraAdminPassword();
	  
	public static void setIssueLabels(String plabel, JiraRestClient pconnJira, Issue pissue) {
		  Set<String> lb = pissue.getLabels();
			
			Iterator<String> lbit = lb.iterator();
			List<String> listado = new ArrayList<>();
			  while(lbit.hasNext()) {
	             listado.add(lbit.next());
			  }
			  listado.add(plabel);
			  setIssueLabels(pissue.getKey(),listado,pconnJira);
		
	}

	public static void setIssueDescription(JiraRestClient pconnJira, String ticket, String description) {
			final IssueInput issueInput = new IssueInputBuilder().setDescription(description).build();
			
	        
			try {
				long timeout = 300;
				pconnJira.getIssueClient().updateIssue(ticket, issueInput).get(timeout , TimeUnit.SECONDS);
			} catch (Exception e) {
				lg.info("jira rest client update issue error. cause: " + e.getMessage(), e);
			}
		
	}

	public static void setIssueLabels(String issueKey, List<String> labels,JiraRestClient pconnJira) {
	        final IssueInput issueInput = new IssueInputBuilder()
	        		.setFieldValue(IssueFieldId.LABELS_FIELD.id, labels)
	                .build();
	        try {
	        	long timeout = 300;
				pconnJira.getIssueClient().updateIssue(issueKey, issueInput).get(timeout , TimeUnit.SECONDS);
	        } catch (Exception e) {
	            lg.info("Jira REST client update labels error for issue "+issueKey, e);
	        }
	    }    
	
	 public static void updateFixVersion(String issueKey, List<Version> fixVersions,JiraRestClient pconnJira) {
	        final IssueInput issueInput = new IssueInputBuilder().setFixVersions(fixVersions)
	                                                             .build();
	        long timeout = 300;
	        try {
	            pconnJira.getIssueClient().updateIssue(issueKey, issueInput).get(timeout, TimeUnit.SECONDS);
	        } catch (Exception e) {
	            lg.info("Jira REST client update issue error. cause: " + e.getMessage(), e);
	        }
	    }
	
	 public static String obtenerNombreEpica(Issue ticket, JiraRestClient pconnJira) throws InterruptedException, ExecutionException {
			String resp = "DRS No Relacionado";
			String cloner = "Cloners";
			
			if (ticket.getFieldByName("Epic Link").getValue() != null ) {
				resp = pconnJira.getIssueClient().getIssue(ticket.getFieldByName("Epic Link").getValue().toString()).get().getSummary();
			}else {
				lg.info("Buscando por Links");
				Iterable<IssueLink> isslink = ticket.getIssueLinks();
				Iterator<IssueLink> itisslink = isslink.iterator();
				
				while (itisslink.hasNext()) {
					 IssueLink itemisslink = itisslink.next(); 		
					 
					 Issue liss = pconnJira.getIssueClient().getIssue(itemisslink.getTargetIssueKey()).get();
					 
					 if ((liss.getIssueType().getName().equals("Epic") || 
					      liss.getIssueType().getName().equals("Theme")) &&
					      !itemisslink.getIssueLinkType().getName().equals(cloner)
					     ) {				
						 resp = liss.getSummary();
						 break;
					 }else {
						 
						 if (liss.getSummary().contains("DRS") && !itemisslink.getIssueLinkType().getName().equals(cloner)) {				
							 resp = liss.getSummary();
							 break;
						 }
						 
						 if (liss.getIssueType().getName().equals("Certification Test") && 
						     !itemisslink.getIssueLinkType().getName().equals(cloner)) {
							 
							 Issue ctiss = pconnJira.getIssueClient().getIssue(liss.getKey()).get();
							 
							 Iterable<IssueLink> ctisslink = ctiss.getIssueLinks();
							 Iterator<IssueLink> ctitisslink = ctisslink.iterator();
							 
							 while (ctitisslink.hasNext()) {
								 IssueLink ctitemisslink = ctitisslink.next(); 		
								 
								 if (!ctitemisslink.getIssueLinkType().getName().equals(cloner) &&
										 !ctitemisslink.getIssueLinkType().getName().equals("Defect") &&
										 !ctitemisslink.getIssueLinkType().getName().equals("Tests(1)")) {
									 
									 Issue epicCT = pconnJira.getIssueClient().getIssue(ctitemisslink.getTargetIssueKey()).get();
									 
									 if (epicCT.getIssueType().getName().equals("Epic") || epicCT.getIssueType().getName().equals("Theme")) {
										
										 resp = epicCT.getSummary();
										 break;
									 }
									 
								 }
								 
							 }
							 
						 }				
						 
					 }			 										 		 
					 
				}//While
				
			}//else
			
			if (resp.contains("DRS")) {
			return resp.substring(resp.indexOf("DRS"),resp.length());
			}else {return resp;}
		}

	 public static ArrayList<Tickets> obtenerTickets(JiraRestClient pconnJira, String pfilter, boolean agregarEpica ) throws InterruptedException, ExecutionException  {
		  ArrayList<Tickets> grupoTic = new ArrayList<>();
		 		
		  Promise<SearchResult> searchJqlPromise = pconnJira.getSearchClient().searchJql(obtenerJQL(pconnJira,pfilter));
		 
		  for (Issue issue : searchJqlPromise.claim().getIssues()) {
				  Tickets tic = new Tickets();
				  String originador = "";
				  String detectionOn = "";
				  StringBuilder versiones  = new StringBuilder();
	 
				    tic.setDateCreated(issue.getCreationDate().toString("dd/MM/yyyy HH:mm"));
		            tic.setKey(issue.getKey());
		            tic.setStatus(issue.getStatus().getName());
		            tic.setPriority(issue.getPriority().getName());
		            
		            
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
		            
		            try {
						JSONObject creador = new JSONObject(issue.getFieldByName("Creator").getValue().toString());
						originador  = creador.get("displayName").toString();				
					} catch (JSONException e) {
						lg.error(e.getMessage(),e);
						originador = "";
					}
		            	
		            tic.setCreatedBy(originador);
		            tic.setDetectionOn(detectionOn);
		            tic.setFixVersion(versiones.toString());
		        
		            if (agregarEpica) {
		            	tic.setEpicName(Utils.obtenerNombreEpica(issue, pconnJira));
		            }
		            
				  grupoTic.add(tic);
			  }
			return grupoTic;
		
		}
	

public static String obtenerJQL(JiraRestClient pconnJira, String pfilter) throws InterruptedException, ExecutionException {
	Promise<Filter> fjql = pconnJira.getSearchClient().getFilter(Long.parseLong(pfilter));
	String jql = fjql.get().getJql();
	lg.info(jql);		
	return jql;
}

public static String asignarTicket(JiraRestClient pconnJira, String puserName, String pticket) {
	try {
	IssueInput issueInput = IssueInput.createWithFields(new FieldInput(IssueFieldId.ASSIGNEE_FIELD, 
			ComplexIssueInputFieldValue.with("name",puserName)));
			pconnJira.getIssueClient().updateIssue(pticket, issueInput).claim();
	return "OK";
	}catch (Exception e) {
		return "NOK";
	}
}


public static String asignarSP(JiraRestClient pconnJira, int sp, String pticket) {
	 //hgd: customfield_10012
	 //local: customfield_10006
	try {
		FieldInput fieldInput = new FieldInput("customfield_10006",sp);
		 
		final IssueInput issueInput = new IssueInputBuilder().setFieldInput(fieldInput)
	                .build();
		
		long timeout = 300;
		pconnJira.getIssueClient().updateIssue(pticket, issueInput).get(timeout , TimeUnit.SECONDS);
	return "OK";
	}catch (Exception e) {
		return "NOK";
	}
}

public static String moveStatus(JiraRestClient pconnJira, int status, String pticket) {
	
	/* 761 End Development
	 * 801 Block Issue
	 * 831 Stop Progress
	 * 5 Resolve Issue
	 * 4 Start Progress
	 */
	try {
		 Issue ticket = pconnJira.getIssueClient().getIssue(pticket).get();
		 
		  pconnJira
         .getIssueClient()
         .transition(
       		  ticket.getTransitionsUri(), new TransitionInput(status))
         .claim();

	return "OK";
	}catch (Exception e) {
		return "NOK";
	}
}

public static String obtenerSprint(JiraRestClient pconnJira, String puser, String ppass) {
	  String resp = "";
	  final String SPRINT = ApplicationProperties.INSTANCE.getSprintBoard();    
      ClientResponse response;
      String auth = new String(Base64.encode(puser + ":" + ppass));
      final String headerAuthorization = "Authorization";
      final String headerAuthorizationValue = "Basic " + auth;
      final String headerType = "application/json";
      Client client = Client.create();

      WebResource webResource = client.resource(SPRINT);
     
     response =  webResource.header(headerAuthorization, headerAuthorizationValue).type(headerType)
   		  .accept(headerType).get(ClientResponse.class);
		
     
      InputStream is = response.getEntityInputStream();
     
      InputStreamReader isReader = new InputStreamReader(is);
      BufferedReader reader = new BufferedReader(isReader);
      Reader isr = new InputStreamReader(is);
      StringBuffer sb = new StringBuffer();
      String str;
      try {
		
    	  while((str = reader.readLine())!= null){
		     sb.append(str);
		  }
		
    	  JSONObject rjson;
		try {
			rjson = new JSONObject(sb.toString());
			 JSONArray rjson2 =  (JSONArray) rjson.get("values");
             rjson = new JSONObject(rjson2.get(0).toString());
             resp = rjson.get("name").toString().substring(0,5);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}	
         
        
          isr.close();
    	  
    	  
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
     
	return resp;
}

public static JiraRestClient getclienteJira (String purl, String puser, String ppass) throws URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
	JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
	 JiraRestClient client = null;
	 
		 URI uri = new URI(purl);	  
		
		 client = factory.createWithBasicHttpAuthentication(uri, puser, Security.decrypt("LNFDESAATLAS",ppass));
	
	return client;
	
}

public static JiraRestClient getclienteJira () throws Exception {
	JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
	 JiraRestClient client = null;
	 try {
		 URI uri = new URI(JIRA_URL);	  
		
		 client = factory.createWithBasicHttpAuthentication(uri, JIRA_ADMIN_USERNAME, Security.decrypt("LNFDESAATLAS",JIRA_ADMIN_PASSWORD));
	 }catch(Exception e) {
		 throw  new Exception("No se logró obtener conexión : "+e.getMessage(), e);
	 }
	return client;
	
}
	 
}
