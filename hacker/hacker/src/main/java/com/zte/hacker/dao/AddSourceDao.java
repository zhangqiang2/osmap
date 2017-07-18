/**
 * <p><owner>洪一帆</owner> </p>
 * <p><createdate>2017-6-29</createdate></p>
 * <p>文件名称: AddSourceDao.java</p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 * <p>创建日期：2017-6-29</p>
 * <p>完成日期：2017-6-29</p>
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

package com.zte.hacker.dao;

import java.sql.SQLException;
import java.util.List;

import com.zte.hacker.common.bean.PrjChangeInfo;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.fetchchangeinfo.bean.UserCompany;
import com.zte.hacker.fetchchangeinfo.bean.VersionInfo;

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
public interface AddSourceDao {
    public void addPrjBaseInfo(ProjectBaseInfo projectBaseInfo);

    public void addPrjChangeInfo(PrjChangeInfo projectChangeInfo);
    public void addVersionInfo(VersionInfo versionInfo);
    public void deleteCommitsAndVersion(String prjName);
    
    public UserCompany queryUserCompanyByEmail(String email) throws SQLException;

    public void addUserCompany(UserCompany userCompany) throws SQLException;

    public List<PrjChangeInfo> queryPrjchangeinfos() throws SQLException;
}
