/**
 * <p><owner>洪一帆</owner> </p>
 * <p><createdate>2017-6-30</createdate></p>
 * <p>文件名称: DownloadUtil.java</p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 * <p>创建日期：2017-6-30</p>
 * <p>完成日期：2017-6-30</p>
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

package com.zte.hacker.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

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
public class DownloadUtil {

    public void downloadFile(HttpServletResponse response, String filePath) throws IOException {
        File downloadFile = new File(filePath);
        String downFileName = downloadFile.getName();
        streamFileDownload(response, filePath, downFileName);
        downloadFile.delete();
    }

    private void streamFileDownload(HttpServletResponse response, String clientFilePath, String downFileName) throws IOException {
        response.setContentType("application/octet-stream;charset=UTF-8");
        OutputStream out = null;
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(downFileName, "UTF-8"));
            out = response.getOutputStream();
            out.write(FileUtils.readFileToByteArray(new File(clientFilePath)));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }
}
