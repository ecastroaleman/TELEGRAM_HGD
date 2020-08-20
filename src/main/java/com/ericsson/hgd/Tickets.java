package com.ericsson.hgd;

import java.util.HashSet;
import java.util.Set;

public class Tickets {
	private String priority;
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	private String detectionOn;
	public String getDetectionOn() {
		return detectionOn;
	}

	public void setDetectionOn(String det) {
		this.detectionOn = det;
	}
	private String key;
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getFixVersion() {
		return fixVersion;
	}

	public void setFixVersion(String fixVersion) {
		this.fixVersion = fixVersion;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String dateCreated;
	private String fixVersion;
	private String createdBy;
	private String status;

	private Tickets ticket;
	public Tickets getTicket() {
		return ticket;
	}

	public void setTicket(Tickets tk) {
		this.ticket = tk;
	}
	
	 private Set<Tickets> tkk = new HashSet<Tickets>();      

	  public Set<Tickets> getEmployees() {                       
	    return tkk;
	  }


}
