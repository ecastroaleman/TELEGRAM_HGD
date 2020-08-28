package com.ericsson.hgd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.util.concurrent.Promise;

public class Utils {
	
	private Utils() {
		throw new IllegalStateException("Utility class");

	}
	  public static final Logger lg = Logger.getLogger(Utils.class);
	  
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
			String resp = "N/A";
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
	 
}
