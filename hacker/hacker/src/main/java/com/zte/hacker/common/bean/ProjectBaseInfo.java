package com.zte.hacker.common.bean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 10180976
 *
 */
public class ProjectBaseInfo extends EhsObject {

    private String projectName;
    private String orgsourceAddr;
    private String prjUrl;
    private String licenseName;
    private String licenseInternalUrl;
    private String downloadUrl;
    private String communityName;
    private String communityUrl;
    private String foundationName;
    private String updateTime;
    private String vitality;
    private int commitNum3Months;
    private String updateuser;
    

    private List<PrjChangeInfo> projectMembers = new ArrayList<PrjChangeInfo>();

    private List<Version> versions = new ArrayList<Version>();

    public ProjectBaseInfo() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOrgsourceAddr() {
        return orgsourceAddr;
    }

    public void setOrgsourceAddr(String orgsourceAddr) {
        this.orgsourceAddr = orgsourceAddr;
    }

    public String getPrjUrl() {
        return prjUrl;
    }

    public void setPrjUrl(String prjUrl) {
        this.prjUrl = prjUrl;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseInternalUrl() {
        return licenseInternalUrl;
    }

    public void setLicenseInternalUrl(String licenseInternalUrl) {
        this.licenseInternalUrl = licenseInternalUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityUrl() {
        return communityUrl;
    }

    public void setCommunityUrl(String communityUurl) {
        this.communityUrl = communityUurl;
    }

    public String getFoundationName() {
        return foundationName;
    }

    public void setFoundationName(String foundationName) {
        this.foundationName = foundationName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<PrjChangeInfo> getProjectChangeInfo() {
        return projectMembers;
    }

    public void setProjectChangeInfo(List<PrjChangeInfo> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    public String getVitality() {
        return vitality;
    }

    public void setVitality(String vitality) {
        this.vitality = vitality;
    }

	public int getCommitNum3Months() {
		return commitNum3Months;
	}

	public void setCommitNum3Months(int commitNum3Months) {
		this.commitNum3Months = commitNum3Months;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

}