package com.ericsson.hgd;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;

import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
public class GetJql {

	public static void main(String[] args) {
		
		
		SearchRequestManager srm = ComponentAccessor.getComponentOfType(SearchRequestManager.class);
		List<SearchRequest> filter = srm.findByNameIgnoreCase("Bug_Para_Alertar");
		
		 ((SearchRequest) filter).getQuery().getQueryString();

	}

}
