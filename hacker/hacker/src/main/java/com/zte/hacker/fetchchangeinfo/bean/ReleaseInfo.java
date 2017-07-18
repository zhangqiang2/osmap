package com.zte.hacker.fetchchangeinfo.bean;

/**
 * Created by 10069681 on 2017/6/30.
 */
public class ReleaseInfo {
    private String versionName;
    private String releasetime;
    private String releasebegintime;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime;
    }

    public String getReleasebegintime() {
        return releasebegintime;
    }

    public void setReleasebegintime(String releasebegintime) {
        this.releasebegintime = releasebegintime;
    }
}
