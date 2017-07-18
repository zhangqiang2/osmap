package com.zte.hacker.fetchchangeinfo;

import com.zte.hacker.common.exception.OpenSourceException;
import com.zte.hacker.rest.controller.OpensourceController;
import com.zte.hacker.rest.service.impl.OpenSourceServiceImpl;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p><owner>张谦</owner> </p>
 * <p><createdate>2017/7/1</createdate></p>
 * <p>文件名称: CompanyNameUtil </p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 *
 * @author 10191175
 * @version 1.0
 */
public class CompanyNameUtil {
    private static final String COMPANY_EMAIL_CONFIG_FILE = "company-email.properties";
    private static Logger logger = Logger.getLogger(OpensourceController.class);

    public String getCompanyFromEmail(String email){
        String company = "independent";
        InputStream fileStream = null;
        Properties properties = null;
        try {
            properties = new Properties();
            fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(COMPANY_EMAIL_CONFIG_FILE);
            properties.load(fileStream);
            logger.debug("load " + COMPANY_EMAIL_CONFIG_FILE);
            String companyTemp = properties.getProperty((email.split("@"))[1]);
            if(null != companyTemp){
                company = companyTemp;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new OpenSourceException("load configuration ["+COMPANY_EMAIL_CONFIG_FILE+"] failed.", e);
        } finally {
            close(fileStream);
        }
        return company;
    }

    private void close(InputStream fileStream) {
        try {
            if (fileStream != null) {
                fileStream.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
