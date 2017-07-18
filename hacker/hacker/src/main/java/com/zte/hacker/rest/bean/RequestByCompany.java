package com.zte.hacker.rest.bean;

import com.zte.hacker.common.bean.EhsObject;

public class RequestByCompany extends EhsObject{
	
	private String projectName;
	
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
