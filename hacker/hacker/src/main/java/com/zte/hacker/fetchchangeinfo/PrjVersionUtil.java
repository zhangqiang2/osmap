package com.zte.hacker.fetchchangeinfo;

import com.zte.hacker.fetchchangeinfo.bean.ReleaseInfo;
import com.zte.hacker.fetchchangeinfo.bean.VersionInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class PrjVersionUtil
{
    private Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress("proxysz.zte.com.cn", 80));

    public VersionInfo getPrjVersionInfo(String prjName, String Url){
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setPrjName(prjName);
        List<ReleaseInfo> releaseInfos = new ArrayList<ReleaseInfo>();
        JSONArray prjOriginInfo = getPrjOriginInfo(Url);
        int size = prjOriginInfo.size();
        int itemOrder = 0;
        for (int i = 0; i < size; i++) {
            try{
                JSONObject jsonObj = prjOriginInfo.getJSONObject(i);
                String releaseName = jsonObj.getString("name");
                if(!releaseName.contains("-rc")){
                    JSONObject commitJsonObj = jsonObj.getJSONObject("commit");
                    String sha = commitJsonObj.getString("sha");
                    String commitDate = getCommitDate(prjName, sha);
                    ReleaseInfo releaseInfo = new ReleaseInfo();
                    releaseInfo.setVersionName(releaseName);
                    releaseInfo.setReleasetime(commitDate);
                    if(itemOrder>0){
                        releaseInfos.get(itemOrder-1).setReleasebegintime(commitDate);
                    }
                    releaseInfos.add(releaseInfo);
                    itemOrder++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        versionInfo.setReleaseInfos(releaseInfos);
        return versionInfo;
    }

    public JSONArray getPrjOriginInfo(String Url){
        String path = Url;
        HttpURLConnection httpConn = null;
        BufferedReader in = null;
        JSONArray prjJsonInfo = null;
        try {
            URL url = new URL(path);
            StringBuffer content = new StringBuffer();
            String tempStr = "";
            httpConn = (HttpURLConnection)url.openConnection(proxy);
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.connect();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            while((tempStr=in.readLine())!=null){
                content.append(tempStr);
            }
            prjJsonInfo = JSONArray.fromObject(content.toString());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prjJsonInfo;
    }


    public String getCommitDate(String prjName, String sha){
        String date = "";
        String path = "https://api.github.com/repos/"+prjName+"/commits/"+sha;
        HttpURLConnection httpConn = null;
        BufferedReader in = null;
        JSONObject commitJsonInfo = null;
        try {
            URL url = new URL(path);
            StringBuffer content = new StringBuffer();
            String tempStr = "";
            httpConn = (HttpURLConnection)url.openConnection(proxy);
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.connect();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            while((tempStr=in.readLine())!=null){
                content.append(tempStr);
            }
            commitJsonInfo = JSONObject.fromObject(content.toString());
            JSONObject commitJsonObj = commitJsonInfo.getJSONObject("commit");
            JSONObject committerJsonObj = commitJsonObj.getJSONObject("committer");
            date = committerJsonObj.getString("date");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
