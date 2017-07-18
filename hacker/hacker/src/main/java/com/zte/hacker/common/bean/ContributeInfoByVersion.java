package com.zte.hacker.common.bean;

import java.util.List;

public class ContributeInfoByVersion {
	
	private String projectName;
	
	private String versionName;
	
	private List<VersionPersonContribute> versionPersonContribute;
	
	private List<VersionCompanyContribute> versionCompanyContribute;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public List<VersionPersonContribute> getVersionPersonContribute() {
		return versionPersonContribute;
	}

	public void setVersionPersonContribute(List<VersionPersonContribute> versionPersonContribute) {
		this.versionPersonContribute = versionPersonContribute;
	}

	public List<VersionCompanyContribute> getVersionCompanyContribute() {
		return versionCompanyContribute;
	}

	public void setVersionCompanyContribute(List<VersionCompanyContribute> versionCompanyContribute) {
		this.versionCompanyContribute = versionCompanyContribute;
	}
	
}
