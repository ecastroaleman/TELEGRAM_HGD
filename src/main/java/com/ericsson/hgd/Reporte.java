package com.ericsson.hgd;

import java.util.ArrayList;
import java.util.Iterator;


import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;

public class Reporte {
	   private static final String JIRA_URL = ApplicationProperties.INSTANCE.getJiraUrl();
	    private static final String JIRA_ADMIN_USERNAME = ApplicationProperties.INSTANCE.getJiraAdminUsername();
	    private static final String JIRA_ADMIN_PASSWORD = ApplicationProperties.INSTANCE.getJiraAdminPassword();
	    private static final String ENCODING = "UTF-8";
	    static int totRegistros=0;
	    public static final Logger lg = Logger.getLogger(Reporte.class);
	public static void main(String[] args) throws Exception {
	
		lg.info("Creando Conexión a Jira...");
		JiraRestClient connJira = Inicial.getclienteJira();
		lg.info("Obteniendo Tickets...");	
		
		
	 		
		  Promise<SearchResult> searchJqlPromise = connJira.getSearchClient().searchJql(Inicial.obtenerJQL(connJira));
		  
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
		           
		           
		      
		  }
		            
	}

}
