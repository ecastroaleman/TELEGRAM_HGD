package com.ericsson.hgd;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.util.concurrent.Promise;


/**
 * Hello world!
 *
 */
public class App 
{
   
    
	  private static final String JIRA_URL = ApplicationProperties.INSTANCE.getJiraUrl();
	    private static final String JIRA_ADMIN_USERNAME = ApplicationProperties.INSTANCE.getJiraAdminUsername();
	    private static final String JIRA_ADMIN_PASSWORD = ApplicationProperties.INSTANCE.getJiraAdminPassword();
	    static int totRegistros=0;
	    public static final Logger lg = Logger.getLogger(App.class);
	    
	public static void main(String[] args) throws Exception {
	
		
		lg.info("Creando Conexión a Jira...");
		JiraRestClient connJira = Utils.getclienteJira(JIRA_URL,JIRA_ADMIN_USERNAME,JIRA_ADMIN_PASSWORD);
		lg.info("Obteniendo Tickets...");	
	
		Utils.asignarSP(connJira, 21, "LNFDES-4")	;	 
		 
		
		 
		
		
			
	/*	Iterable<Transition> transitions = null;
		 Issue ticket = connJira.getIssueClient().getIssue("LNFDESAATLAS-6115").get();
		
		 transitions = connJira.getIssueClient().getTransitions(ticket.getTransitionsUri()).claim();
		 
		 Iterator<Transition> it = transitions.iterator();
		 
		  while(it.hasNext()) {
                Transition element = it.next();
                lg.info(element.getId()+" - "+element.getName());
                
                
		  }*/
	/*	  Promise<SearchResult> searchJqlPromise = connJira.getSearchClient().searchJql("project = LNFDES and issuekey = LNFDES-4");
		
		  for (Issue issue : searchJqlPromise.claim().getIssues()) {
	
			  transitions = connJira.getIssueClient().getTransitions(issue.getTransitionsUri()).claim();
			  
			 Iterator<Transition> it = transitions.iterator();
			 
			  while(it.hasNext()) {
	                 Transition element = it.next();
	                 lg.info(element.getName());
	                 lg.info(element.getId());
	                 
	                 connJira
	                 .getIssueClient()
	                 .transition(
	                     issue.getTransitionsUri(), new TransitionInput(21))
	                 .claim();
	                 
			  }
			
		  }*/
		
	
		
		  
		  
		  
		  
      
        
        
        }

     
        
}
