/**
 * <p><owner>洪一帆</owner> </p>
 * <p><createdate>2017-7-1</createdate></p>
 * <p>文件名称: GetCompany.java</p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 * <p>创建日期：2017-7-1</p>
 * <p>完成日期：2017-7-1</p>
 * <p>修改记录1: // 修改历史记录，包括修改日期、修改者及修改内容</p>
 * <pre>
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 * <p>评审记录1: // 评审历史记录，包括评审日期、评审人及评审内容</p>
 * <pre>
 *    评审日期：
 *    版 本 号：
 *    评 审 人：
 *    评审内容：
 * </pre>
 * @version 1.0
 * @author 洪一帆
 */

package com.zte.hacker.fetchchangeinfo;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.zte.hacker.common.bean.PrjChangeInfo;
import com.zte.hacker.fetchchangeinfo.bean.UserCompany;
import com.zte.hacker.rest.controller.OpensourceController;
import com.zte.hacker.rest.service.ImportFileService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 功能描述:<br>
 * <p/>
 * <p/>
 * <p/>
 * Note:
 *
 * @author 洪一帆
 * @version 1.0
 */
public class UpdateUserCompany extends Task {
    private Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress("10.43.163.32", 2222));
    private static Logger logger = Logger.getLogger(OpensourceController.class);
    @Autowired
    private ImportFileService importFileService;


    public ImportFileService getImportFileService() {
        return importFileService;
    }

    public void setImportFileService(ImportFileService importFileService) {
        this.importFileService = importFileService;
    }

    /*
     * (non-Javadoc)
     *
     * @see it.sauronsoftware.cron4j.Task#execute(it.sauronsoftware.cron4j.
     * TaskExecutionContext)
     */
    @Override
    public void execute(TaskExecutionContext arg0) throws RuntimeException {
        try {
            updateUserCompany();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserCompany() throws SQLException {
        logger.info("================updateUserCompany begin");
        List<String> allEmails = getAllEmails();
        logger.info("================getAllEmailes");
        List<String> emails = new ArrayList<String>();
        for (int i = 0; i < allEmails.size(); i++) {
            UserCompany usercompany = importFileService.queryUserCompanyByEmail(allEmails.get(i));
            if (usercompany == null) {
                emails.add(allEmails.get(i));
            }
        }
        logger.info("================not added email");
        int n = 0;
        UserCompany usercompany = new UserCompany();
        JSONArray userInfos = getUserInfos(n);
        logger.info("================getUserInfos");
        while (userInfos.size() > 0) {
            for (int i = 0; i < userInfos.size(); i++) {
                JSONObject userInfo = getUserInfo(userInfos.getJSONObject(i).getString("login"));
                logger.info("================getUserInfo n=" + n + ",i=" + i);
                if (userInfo.containsKey("email") && userInfo.getString("email") != null && userInfo.containsKey("company") && userInfo.getString("company") != null
                        && emails.contains(userInfo.getString("email"))) {
                    usercompany.setEmail(userInfo.getString("email"));
                    usercompany.setCompany(userInfo.getString("company"));
                    importFileService.addUserCompany(usercompany);
                }
                if (i == userInfos.size() - 1) {
                    n = userInfo.getInt("id");
                }
            }
            userInfos = getUserInfos(n);
        }

    }

    public List<String> getAllEmails() throws SQLException {
        List<PrjChangeInfo> prjChangeInfos = importFileService.queryPrjchangeinfos();
        List<String> emails = new ArrayList<String>();
        for (PrjChangeInfo prjChangeInfo : prjChangeInfos) {
            if (prjChangeInfo.getMail() != null) {
                emails.add(prjChangeInfo.getMail());
            }
        }
        return emails;
    }

    public JSONArray getUserInfos(int since) {
        String path = "https://api.github.com/users?since=" + since;
        HttpURLConnection httpConn = null;
        BufferedReader in = null;
        JSONArray userInfos = null;
        try {
            URL url = new URL(path);
            StringBuffer content = new StringBuffer();
            String tempStr = "";
            httpConn = (HttpURLConnection) url.openConnection(proxy);
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.connect();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            while ((tempStr = in.readLine()) != null) {
                content.append(tempStr);
            }
            userInfos = JSONArray.fromObject(content.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userInfos;
    }

    public JSONObject getUserInfo(String userName) {
        String path = "https://api.github.com/users/" + userName;
        HttpURLConnection httpConn = null;
        BufferedReader in = null;
        JSONObject userInfo = null;
        try {
            URL url = new URL(path);
            StringBuffer content = new StringBuffer();
            String tempStr = "";
            httpConn = (HttpURLConnection) url.openConnection(proxy);
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.connect();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            while ((tempStr = in.readLine()) != null) {
                content.append(tempStr);
            }
            userInfo = JSONObject.fromObject(content.toString());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userInfo;
    }

}
