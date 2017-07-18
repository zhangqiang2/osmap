package com.zte.hacker.fetchchangeinfo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10069681 on 2017/6/30.
 */
public class VersionInfo {
    private String prjName;
    private List<ReleaseInfo> releaseInfos = new ArrayList<ReleaseInfo>();

    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

    public List<ReleaseInfo> getReleaseInfos() {
        return releaseInfos;
    }

    public void setReleaseInfos(List<ReleaseInfo> releaseInfos) {
        this.releaseInfos = releaseInfos;
    }
}
