package com.ericsson.hgd;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;

public class Reporte {
	   private static final String JIRA_URL = ApplicationProperties.INSTANCE.getJiraUrl();
	    private static final String JIRA_ADMIN_USERNAME = ApplicationProperties.INSTANCE.getJiraAdminUsername();
	    private static final String JIRA_ADMIN_PASSWORD = ApplicationProperties.INSTANCE.getJiraAdminPassword();
	    private static final String ENCODING = "UTF-8";
	    static int totRegistros=0;
	    public static final Logger lg = Logger.getLogger(Reporte.class);
	    
	    
		public static void iteraTickets( ArrayList<Tickets> datos) {
			  
		    StringBuilder msg = new StringBuilder();
			for (int i=0; i<datos.size(); i++) {
			
			lg.info(datos.get(i).getKey()+",<Sprint>,"+
			        datos.get(i).getStatus()+",<AsignadoA>,<sp>,"+
					datos.get(i).getEpicName()+",<causa>,"+
			        datos.get(i).getDateCreated()+",,,,,"+
					datos.get(i).getFixVersion());
			}	 
			}
	    
	public static void main(String[] args) throws Exception {
	
		lg.info("Creando Conexión a Jira...");
		JiraRestClient connJira = Inicial.getclienteJira();
		lg.info("Obteniendo Tickets creados Hoy...");	
		String filterRep =  ApplicationProperties.INSTANCE.getFilterRep();
		ArrayList<Tickets> totalTickets = Utils.obtenerTickets(connJira, filterRep, true);
		
	
		iteraTickets(totalTickets);

		
		
/*Aun no funciona para asignar usuarios		
		 final Map<String, Object> valuesMap = new HashMap<>(2);
         valuesMap.put("name", "JCASTRO"); // server
         valuesMap.put("accountId", "JCASTRO"); // cloud
         IssueInputBuilder builder = new IssueInputBuilder();
         
		// Need to use "accountId" as specified here:
         //    https://developer.atlassian.com/cloud/jira/platform/deprecation-notice-user-privacy-api-migration-guide/
         //
         // See upstream fix for setAssigneeName:
         //    https://bitbucket.org/atlassian/jira-rest-java-client/pull-requests/104/change-field-name-from-name-to-id-for/diff 
         builder.setFieldInput(new FieldInput(IssueFieldId.ASSIGNEE_FIELD, new ComplexIssueInputFieldValue(valuesMap)));
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
