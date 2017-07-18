/**
 * <p><owner>彭建华</owner> </p>
 * <p><createdate>2015-05-25</createdate></p>
 * <p>文件名称: ExecuteJobTask.java </p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 *
 * @version 1.0
 * @author 彭建华
 * Created by 彭建华 on 2015-05-13.
 */
package com.zte.hacker.fetchchangeinfo;

import com.zte.hacker.common.bean.PrjChangeInfo;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.common.bean.Version;
import com.zte.hacker.dao.AddSourceDao;
import com.zte.hacker.fetchchangeinfo.bean.PrjNameInfo;
import com.zte.hacker.fetchchangeinfo.bean.VersionInfo;
import com.zte.hacker.rest.service.OpenSourceService;
import com.zte.hacker.utils.Utils;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class ExecuteJobTask extends Task {
    private static final Logger LOG = Logger.getLogger(ExecuteJobTask.class);

    private OpenSourceService openSourceService;

    private AddSourceDao dao;
    
    private JobConServiceInterface jobConService;

    public ExecuteJobTask(JobConServiceInterface jobConService,OpenSourceService openSourceService,AddSourceDao addSourceDao) {
    	this.jobConService = jobConService;
    	this.openSourceService = openSourceService;
    	this.dao= addSourceDao;
    }

    private boolean running = false;
    @Override
    public void execute(TaskExecutionContext taskExecutionContext) throws RuntimeException {
        LOG.error("===ExecuteJobTask begin to execute============");
        if (running)
            return;
        running = true;
        List<ProjectBaseInfo> dbPrjBaseInfos = openSourceService.getProjectBaseInfo("%");
//        List<ProjectBaseInfo> dbPrjBaseInfos = DbImpl.getProjectBaseInfo();
        try {
            for (ProjectBaseInfo dbPrjBaseInfo : dbPrjBaseInfos) {
                //test
                String prjName = dbPrjBaseInfo.getProjectName().replace(" ", "");
//                if (!prjName.equals("opnfv"))
//                    continue;
                if (dbPrjBaseInfo.getOrgsourceAddr() == null )
                	continue;
                if (dbPrjBaseInfo.getOrgsourceAddr().indexOf("http://") == 0)
                	continue;
                dao.deleteCommitsAndVersion(dbPrjBaseInfo.getProjectName());

                gitCommitsInfos(dbPrjBaseInfo);
            }
        }
        catch(Exception e){

        }
        finally {
            running = false;
        }
    }

    private boolean gitCommitsInfos(ProjectBaseInfo dbPrjBaseInfo) {
    	PrjNameInfo objPrjName = new PrjNameInfo();
//    	String prjName = dbPrjBaseInfo.getProjectName().replace(" ", "");
    	objPrjName.setPrjName(dbPrjBaseInfo.getProjectName());
    	objPrjName.setPrjShortName(dbPrjBaseInfo.getProjectName().replace(" ", ""));
        String[] codelocations = dbPrjBaseInfo.getOrgsourceAddr().split(",");
        for (String codelocation : codelocations) {
        	String [] codeUrl = codelocation.split(" ");
        	if (codeUrl == null || codeUrl.length < 1)
        		continue;
            gitClonePrj(objPrjName, codeUrl[0]); //Test
            getPrjGithubRecords(objPrjName, codeUrl[0]);
            analyseGithubRecordsToDb(objPrjName, codeUrl[0]);
        }

        return true;
    }

    private String getVersionRestUrl(String codeUrl){
    	String versionAddr = codeUrl.replaceFirst("//github.com", "//api.github.com/repos");
    	String endStr = versionAddr.substring(versionAddr.length() - 4, versionAddr.length());
    	if (endStr.equals(".git"))
    		versionAddr = versionAddr.substring(0, versionAddr.length() - 4) + "/tags";
    	return versionAddr;
    }
    private boolean gitClonePrj(PrjNameInfo objPrjName, String codelocation) {
        String clonedir = jobConService.getPrjBasePath() + File.separator + objPrjName.getPrjShortName();
        String localCodeAddr = codelocation;
        if (codelocation.indexOf("git://") == 0){
        	String sTmp = "https" + codelocation.substring(3, codelocation.length());
        	localCodeAddr = sTmp;
        }
        String versionAddr = getVersionRestUrl(localCodeAddr);
        getPrjVersion(objPrjName.getPrjName(), versionAddr);
        String cmdstring = jobConService.getShellExecutePath() + File.separator + "gitcloneprj.sh " + clonedir + " " + localCodeAddr;
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmdstring);
            proc.waitFor(); //阻塞，直到上述命令执行完
        } catch (IOException e) {
            LOG.error(e);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        return true;
    }

    private boolean getPrjGithubRecords(PrjNameInfo objPrjName, String codelocation) {
        String clonedir = jobConService.getPrjBasePath() + File.separator + objPrjName.getPrjShortName();
        String prjLocalSrcDir = "";
        File file = new File(clonedir);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                prjLocalSrcDir = tempList[i].getAbsolutePath();
                getCodeLocationRecords(objPrjName, prjLocalSrcDir);
                analyseGithubRecordsToDb(objPrjName, prjLocalSrcDir);
            }
        }
        return true;
    }

    private boolean getCodeLocationRecords(PrjNameInfo objPrjName, String prjLocalSrcDir) {
        String cmdstring = jobConService.getShellExecutePath() + File.separator + "getgithublogs.sh " + prjLocalSrcDir + " " + objPrjName.getPrjShortName() + ".gitlog";
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmdstring);
            proc.waitFor(); //阻塞，直到上述命令执行完
        } catch (IOException e) {
            LOG.error(e);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        return true;
    }

    private boolean analyseGithubRecordsToDb(PrjNameInfo objPrjName, String prjLocalSrcDir) {
        String logFile = prjLocalSrcDir + File.separator + objPrjName.getPrjShortName() + ".gitlog";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(logFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line != null && line.length() > 0) {
                    String[] details = line.trim().split("~~");
                    if (details.length < 3)
                        continue;
                    writeGithubRecordsToDb(objPrjName, details);
                    System.out.println(line);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean writeGithubRecordsToDb(PrjNameInfo objPrjName, String[] details) {
        if (details.length < 4)
            return false;
        PrjChangeInfo projectChangeInfo = new PrjChangeInfo();
        projectChangeInfo.setPrjname(objPrjName.getPrjName());
        projectChangeInfo.setIssuename(details[0].trim());
        String tmpCommitTime = details[1].trim().split(" ")[0];
        projectChangeInfo.setCommittime(tmpCommitTime);
        projectChangeInfo.setContributor(details[2].trim());
        projectChangeInfo.setCommitter(details[3].trim());
        projectChangeInfo.setMail(details[4].trim());
        projectChangeInfo.setUpdatetime(Utils.getCurrentTime());
        dao.addPrjChangeInfo(projectChangeInfo);
        return true;
    }

    private VersionInfo getPrjVersion(String prjName, String Url) {
    	PrjVersionUtil prjVer = new PrjVersionUtil();
    	VersionInfo versionInfo = new VersionInfo();
    	try{
    		versionInfo = prjVer.getPrjVersionInfo(prjName, Url);
    		writePrjVersionToDb(versionInfo);
    	}
    	catch(Exception e){
    		LOG.error(e);
    	}
        return versionInfo;
    }

    private boolean writePrjVersionToDb(VersionInfo versionInfo) {
    	dao.addVersionInfo(versionInfo);
        return true;
    }
    
    public static void main(String[] args) {
    	String codeUrl = "https://github.com/vim/vim.git";
    	String versionAddr = codeUrl.replaceFirst("//github.com", "//api.github.com/repos");
    	String endStr = versionAddr.substring(versionAddr.length() - 4, versionAddr.length());
    	if (endStr.equals(".git"))
    		versionAddr = versionAddr.substring(0, versionAddr.length() - 4) + "/tags";
    	return ;
	}
}
