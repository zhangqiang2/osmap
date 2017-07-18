package com.zte.hacker.fetchchangeinfo;

import com.zte.hacker.common.bean.PrjChangeInfo;
import com.zte.hacker.dao.AddSourceDao;
import com.zte.hacker.dao.impl.AddSourceDaoImpl;
import com.zte.hacker.fetchchangeinfo.bean.UserCompany;
import com.zte.hacker.rest.controller.OpensourceController;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * <p><owner>张谦</owner> </p>
 * <p><createdate>2017/7/1</createdate></p>
 * <p>文件名称: GetCompanyJobTask </p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 *
 * @author 10191175
 * @version 1.0
 */
public class GetCompanyJobTask extends Task{
    private static Logger logger = Logger.getLogger(OpensourceController.class);
    private AddSourceDao dao;
    private CompanyNameUtil companyNameUtil;
    private boolean running = false;

    public GetCompanyJobTask(AddSourceDao dao){
        this.dao = dao;
        companyNameUtil = new CompanyNameUtil();
    }

    @Override
    public void execute(TaskExecutionContext taskExecutionContext) throws RuntimeException {
        logger.debug("===GetCompanyJobTask begin to execute============");
        if (running)
            return;
        running = true;
        try{
            List<PrjChangeInfo> prjChangeInfoList = dao.queryPrjchangeinfos();
            for(PrjChangeInfo prjChangeInfo : prjChangeInfoList){
                String email = prjChangeInfo.getMail();
                if(null != email){
                    String company = companyNameUtil.getCompanyFromEmail(email);
                    UserCompany userCompany = new UserCompany();
                    userCompany.setEmail(email);
                    userCompany.setCompany(company);
                    dao.addUserCompany(userCompany);
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            running = false;
            logger.debug("===GetCompanyJobTask finished============");
        }
    }
}
