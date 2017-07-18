package com.zte.hacker.common.bean;

/**
 * 
 * @author 10180976
 *
 */
public class Version extends EhsObject{
	private String prjName;
	private String releaseTime;
	private String releaseBegintime;
	private String versionName;
	private String updateTime;
	public String getPrjName() {
		return prjName;
	}
	public void setPrjName(String prjName) {
		this.prjName = prjName;
	}
	public String getReleaseTime() {
		return releaseTime;
	}
	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}
	public String getReleaseBegintime() {
		return releaseBegintime;
	}
	public void setReleaseBegintime(String releaseBegintime) {
		this.releaseBegintime = releaseBegintime;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
