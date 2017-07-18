package com.zte.hacker.common.bean;

/**
 * 
 * @author 10180976
 *
 */
public class ProjectMember extends EhsObject{
	
	private String contributorId;
	private String prjName;
	private String contributorName;
	private String email;
	private String organization;
	private String roles;
	private String updatetime;
	public String getContributorId() {
		return contributorId;
	}
	public void setContributorId(String contributorId) {
		this.contributorId = contributorId;
	}
	public String getPrjName() {
		return prjName;
	}
	public void setPrjName(String prjName) {
		this.prjName = prjName;
	}
	public String getContributorName() {
		return contributorName;
	}
	public void setContributorName(String contributorName) {
		this.contributorName = contributorName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	
}
