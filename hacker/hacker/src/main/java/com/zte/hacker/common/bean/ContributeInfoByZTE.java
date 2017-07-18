package com.zte.hacker.common.bean;

import java.util.List;

public class ContributeInfoByZTE {
	
	private String projectName;
	
	private List<VersionPersonContribute> personContribute;
	

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public List<VersionPersonContribute> getPersonContribute() {
		return personContribute;
	}

	public void setVersionPersonContribute(List<VersionPersonContribute> personContribute) {
		this.personContribute = personContribute;
	}
	
}
