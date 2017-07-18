/**
 * <p><owner>10208178</owner> </p>
 * <p><createdate>2017/6/28</createdate></p> 
 * <p>文件名称: HelloController.java</p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 * <p>创建日期：2017/6/28</p>
 * <p>完成日期：2017/6/28</p>
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
 * @author 周明
 */
package com.zte.hacker.rest.controller;

import com.zte.hacker.common.json.JsonBeanConverter;
import com.zte.hacker.rest.bean.OpenSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 功能描述:<br>
 * <p/>
 * <p/>
 * <p/>
 * Note:
 *
 * @author 10208178
 * @version 1.0
 */
@RestController
@RequestMapping("/test")
public class HelloController
{
    @ResponseBody
    @RequestMapping(value = "/queryResource", method = { RequestMethod.POST,
        RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String queryResource(@RequestBody String request) throws IOException
    {
        OpenSource openSource=new OpenSource("Apache","http://www.apache.org/");
        return JsonBeanConverter.convertBeanToJsonStr(openSource);
    }
}