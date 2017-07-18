package com.zte.hacker.fetchchangeinfo;

import com.zte.hacker.common.bean.PrjChangeInfo;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.dao.AddSourceDao;
import com.zte.hacker.rest.service.OpenSourceService;
import com.zte.hacker.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;

/**
 * Created by 10069681 on 2017/6/30.
 */
public class FetchIssues {
    private static final Logger LOG = Logger.getLogger(ExecuteJobTask.class);
    @Autowired
    private OpenSourceService openSourceService;

    @Autowired
    private AddSourceDao dao;

    public static void main(String args[]){
        System.out.println("==============================begin main===========================================");
        FetchIssues fetchIssues = new FetchIssues();
        fetchIssues.start();
        System.out.println("==============================end main===========================================");
    }

    public void start(){
        Thread thread = new Thread(new FetchIssueThread());
        thread.start();
    }
    public class FetchIssueThread implements Runnable {
        public void run() {
            LOG.error("===ExecuteJobTask begin to execute============");
            List<ProjectBaseInfo> dbPrjBaseInfos = openSourceService.getProjectBaseInfo("%");
            for (ProjectBaseInfo dbPrjBaseInfo : dbPrjBaseInfos) {
                gitCommitsInfos(dbPrjBaseInfo);
            }
        }
        private boolean gitCommitsInfos(ProjectBaseInfo dbPrjBaseInfo) {
            String prjName = dbPrjBaseInfo.getProjectName().replace(" ", "");
            String[] codelocations = dbPrjBaseInfo.getOrgsourceAddr().split(",");
            for (String codelocation : codelocations) {
                gitClonePrj(prjName, codelocation);
                getPrjGithubRecords(prjName, codelocation);
                analyseGithubRecordsToDb(prjName, codelocation);
            }

            return true;
        }

        private boolean gitClonePrj(String prjName, String codelocation) {
//            String clonedir = JobCronService.getInstance().getPrjBasePath() + File.separator + prjName;
//            String cmdstring = "/home/pengjianhua/competition/test/gitcloneprj.sh " + clonedir + " " + codelocation;
//            Process proc = null;
//            try {
//                proc = Runtime.getRuntime().exec(cmdstring);
//                proc.waitFor(); //阻塞，直到上述命令执行完
//            } catch (IOException e) {
//                LOG.error(e);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//            }
            return true;
        }

        private boolean getPrjGithubRecords(String prjName, String codelocation) {
//            String clonedir = JobCronService.getInstance().getPrjBasePath() + File.separator + prjName;
//            String prjLocalSrcDir = "";
//            File file = new File(clonedir);
//            File[] tempList = file.listFiles();
//            for (int i = 0; i < tempList.length; i++) {
//                if (tempList[i].isDirectory()) {
//                    prjLocalSrcDir = clonedir + File.separator + tempList[i];
//                }
//            }
//            String cmdstring = "/home/pengjianhua/competition/test/getgithublogs.sh " + clonedir;
            return true;
        }

        private boolean getCodeLocationRecords(String prjName, String prjLocalSrcDir) {
//            String cmdstring = "./getgithublogs.sh " + prjLocalSrcDir + " " + prjName;
//            Process proc = null;
//            try {
//                proc = Runtime.getRuntime().exec(cmdstring);
//                proc.waitFor(); //阻塞，直到上述命令执行完
//            } catch (IOException e) {
//                LOG.error(e);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//            }
            return true;
        }

        private boolean analyseGithubRecordsToDb(String prjName, String prjLocalSrcDir) {
            String logFile = prjLocalSrcDir + File.separator + prjName;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(logFile));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line != null && line.length() > 0) {
                        String[] details = line.trim().split("~~");
                        if (details.length < 3)
                            continue;
                        writeGithubRecordsToDb(prjName, details);
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

        private boolean writeGithubRecordsToDb(String prjName, String[] details) {
            if (details.length < 4)
                return false;
            PrjChangeInfo projectChangeInfo = new PrjChangeInfo();
            projectChangeInfo.setPrjname(prjName);
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

        private boolean getPrjVersion(String prjName, String codelocation) {
            return true;
        }

        private boolean writePrjVersionToDb(String prjName, String codelocation) {
            return true;
        }
    }
}
