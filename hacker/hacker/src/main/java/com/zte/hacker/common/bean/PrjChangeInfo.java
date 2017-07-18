package com.zte.hacker.common.bean;

public class PrjChangeInfo extends EhsObject{
	private String prjname;
	private String versionName;
	private String issuename;
	private String committime;
	private String contributor;
	private String committer;
	private String mail;
	private String addcodelines;
	private String deletecodelines;
	private String modifyfiles;	
	private String modifyfilelists;
	private String updatetime;
	
	private String company;
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPrjname() {
		return prjname;
	}
	public void setPrjname(String prjname) {
		this.prjname = prjname;
	}
	public String getIssuename() {
		return issuename;
	}
	public void setIssuename(String issuename) {
		this.issuename = issuename;
	}
	public String getCommittime() {
		return committime;
	}
	public void setCommittime(String committime) {
		this.committime = committime;
	}
	public String getContributor() {
		return contributor;
	}
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
	public String getCommitter() {
		return committer;
	}
	public void setCommitter(String committer) {
		this.committer = committer;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAddcodelines() {
		return addcodelines;
	}
	public void setAddcodelines(String addcodelines) {
		this.addcodelines = addcodelines;
	}
	public String getDeletecodelines() {
		return deletecodelines;
	}
	public void setDeletecodelines(String deletecodelines) {
		this.deletecodelines = deletecodelines;
	}
	public String getModifyfiles() {
		return modifyfiles;
	}
	public void setModifyfiles(String modifyfiles) {
		this.modifyfiles = modifyfiles;
	}
	public String getModifyfilelists() {
		return modifyfilelists;
	}
	public void setModifyfilelists(String modifyfilelists) {
		this.modifyfilelists = modifyfilelists;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	
}
