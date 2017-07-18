/**
 * <owner>王明宇</owner>
 * <createdate>2017-07-01</createdate>
 * 文件名称: UpdateProject.java
 * 文件描述: 无
 * 版权所有: 版权所有(C)2001-2020
 * 公司名称: 深圳市中兴通讯股份有限公司
 * 内容摘要: 无
 * 其他说明: 无
 *
 * @version 1.0
 * @author wang.mingyu111@zte.com.cn
 */
package com.zte.hacker.fetchchangeinfo;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * 功能描述：
 *
 * @author 王明宇
 * @version 1.0
 */
public class UpdateProject extends Task {

    private static final Logger LOG = Logger.getLogger(UpdateProject.class);

    public void execute(TaskExecutionContext taskExecutionContext) throws RuntimeException {
        String path = "http://10.43.139.138:5000/rest/update";
        LOG.info("begin to trigger update project");
        try {
            final URL url = new URL(path);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            final BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                LOG.info(inputLine);
            }
        } catch (IOException e) {
            LOG.error("error occurs to trigger update project");
        }
        LOG.info("finished to trigger update project");
    }
}
