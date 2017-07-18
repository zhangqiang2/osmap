package com.zte.hacker.rest.bean;

import com.zte.hacker.common.bean.EhsObject;

public class RequestByVersion extends EhsObject{
	private String projectName;
	private String versionname;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getVersionname() {
		return versionname;
	}

	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "projectName: " + projectName + ", versionname: " + versionname;
	}
}
