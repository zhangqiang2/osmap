package com.zte.hacker.rest.bean;

import com.zte.hacker.common.bean.EhsObject;

public class RequestByContributor extends EhsObject{
	
	private String projectName;
	private String contributor;

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
