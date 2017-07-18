/**
 * <p><owner>洪一帆</owner> </p>
 * <p><createdate>2017-6-29</createdate></p>
 * <p>文件名称: AddSourceDaoImpl.java</p>
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

package com.zte.hacker.dao.impl;

import com.zte.hacker.common.bean.PrjChangeInfo;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.common.db.jdbc.JdbcTemplate;
import com.zte.hacker.common.db.jdbc.JdbcTemplate.ResultSetTransformer;
import com.zte.hacker.common.db.jdbc.JdbcUtils;
import com.zte.hacker.common.exception.OpenSourceException;
import com.zte.hacker.dao.AddSourceDao;
import com.zte.hacker.fetchchangeinfo.bean.ReleaseInfo;
import com.zte.hacker.fetchchangeinfo.bean.UserCompany;
import com.zte.hacker.fetchchangeinfo.bean.VersionInfo;
import com.zte.hacker.rest.service.impl.OpenSourceServiceImpl;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

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
public class AddSourceDaoImpl implements AddSourceDao, InitializingBean {

    private static final String DBCP_CONFIG_FILE = "dbcp.properties";
    private static final Logger LOG = Logger.getLogger(OpenSourceServiceImpl.class);

    private JdbcTemplate jdbcTemplate;

    public void init() {
        InputStream fileStream = null;
        Properties properties = null;
        try {
            properties = new Properties();
            fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DBCP_CONFIG_FILE);
            properties.load(fileStream);
            LOG.debug("load " + DBCP_CONFIG_FILE + ", driverClassName:" + properties.getProperty("driverClassName") + ", url:" + properties.getProperty("url") + ", username:"
                    + properties.getProperty("username"));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new OpenSourceException("load configuration [dbcp.properties] failed.", e);
        } finally {
            close(fileStream);
        }

        jdbcTemplate = new JdbcTemplate(getDataSource(properties));
    }

    public DataSource getDataSource(Properties properties) {
        try {
            DataSource dataSource = BasicDataSourceFactory.createDataSource(properties);

            if (dataSource != null) {
                Connection conn = dataSource.getConnection();

                DatabaseMetaData mdm = conn.getMetaData();

                LOG.info("Connected to " + mdm.getDatabaseProductName() + " " + mdm.getDatabaseProductVersion());

                JdbcUtils.close(conn);

                return dataSource;
            }
        } catch (Exception e) {
            LOG.error("DBCP create datasource failed.", e);
            return null;
        }
        return null;
    }

    private void close(InputStream fileStream) {
        try {
            if (fileStream != null) {
                fileStream.close();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void addPrjBaseInfo(ProjectBaseInfo projectBaseInfo) {
        String sql = "insert into prjbaseinfo (prjname,prjurl,orgsourceaddr,licensename,downloadurl,vitality,communityname,communityurl,foundationname,updatetime) values(?,?,?,?,?,?,?,?,?,?)";
        Object[] obj = new Object[10];
        obj[0] = projectBaseInfo.getProjectName();
        obj[1] = projectBaseInfo.getPrjUrl();
        obj[2] = projectBaseInfo.getOrgsourceAddr();
        obj[3] = projectBaseInfo.getLicenseName();
        obj[4] = projectBaseInfo.getDownloadUrl();
        obj[5] = projectBaseInfo.getVitality();
        obj[6] = projectBaseInfo.getCommunityName();
        obj[7] = projectBaseInfo.getCommunityUrl();
        obj[8] = projectBaseInfo.getFoundationName();
        obj[9] = projectBaseInfo.getUpdateTime();
        try {
            jdbcTemplate.executeUpdate(sql, obj);
        } catch (SQLException e) {
            LOG.error(e);
        }
    }

    public void addPrjChangeInfo(PrjChangeInfo projectChangeInfo) {
        String sql = "insert into prjchangeinfo (prjname,issuename,committime,contributor,committer,mail,updatetime) values(?,?,?,?,?,?,?)";
        Object[] obj = new Object[7];
        obj[0] = projectChangeInfo.getPrjname();
        obj[1] = projectChangeInfo.getIssuename();
        obj[2] = projectChangeInfo.getCommittime();
        obj[3] = projectChangeInfo.getContributor();
        obj[4] = projectChangeInfo.getCommitter();
        obj[5] = projectChangeInfo.getMail();
        obj[6] = "2017-07-01 15:43:20";
        try {
            jdbcTemplate.executeUpdate(sql, obj);
        } catch (SQLException e) {
            LOG.error(e);
        }
    }
    public void addVersionInfo(VersionInfo versionInfo){
    	for (ReleaseInfo relInfo: versionInfo.getReleaseInfos()){
	    	String sql = "insert into versioninfo (prjname,releasetime,releasebegintime,versionname,updatetime) values(?,?,?,?,?)";
	        Object[] obj = new Object[5];
	        obj[0] = versionInfo.getPrjName();
	        obj[1] = relInfo.getReleasetime();
	        obj[2] = relInfo.getReleasebegintime();
	        obj[3] = relInfo.getVersionName();
	        obj[4] = "2017-07-01 15:43:20";
	        try {
	            jdbcTemplate.executeUpdate(sql, obj);
	        } catch (SQLException e) {
	            LOG.error(e);
	        }
    	}
    }

    public void afterPropertiesSet() throws Exception {
        init();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.zte.hacker.dao.AddSourceDao#queryUserCompanyByEmail(java.lang.String)
     */
    public UserCompany queryUserCompanyByEmail(String email) throws SQLException {
        String sql = "select * from usercompany where email = ?";
        UserCompany userCompany = jdbcTemplate.query4Object(sql, new Object[] { email }, new ResultSetTransformer<UserCompany>() {
            public UserCompany transform(ResultSet rs) throws SQLException {
                if (rs != null) {
                    UserCompany userCompany = new UserCompany();
                    userCompany.setEmail(rs.getString("email"));
                    userCompany.setCompany(rs.getString("Company"));
                    return userCompany;
                }
                return null;
            }
        });
        return userCompany;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.zte.hacker.dao.AddSourceDao#addUserCompany(com.zte.hacker.fetchchangeinfo
     * .bean.UserCompany)
     */
    public void addUserCompany(UserCompany userCompany) throws SQLException {
        String sql = "insert into usercompany (email,company) values(?,?)";
        jdbcTemplate.executeUpdate(sql, new Object[] { userCompany.getEmail(), userCompany.getCompany() });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.zte.hacker.dao.AddSourceDao#queryPrjchangeinfos()
     */
    public List<PrjChangeInfo> queryPrjchangeinfos() throws SQLException {
        String sql = "select * from prjchangeinfo";
        List<PrjChangeInfo> prjChangeInfos = jdbcTemplate.query4List(sql, new Object[] {}, new ResultSetTransformer<PrjChangeInfo>() {
            public PrjChangeInfo transform(ResultSet rs) throws SQLException {
                PrjChangeInfo prjChangeInfo = new PrjChangeInfo();
                prjChangeInfo.setMail(rs.getString("mail"));
                return prjChangeInfo;
            }
        }, false, null, -1);
        return prjChangeInfos;
    }

    public void deleteCommitsAndVersion(String prjName) {
        String sql = "delete from prjchangeinfo where prjname = ?";
        try {
            jdbcTemplate.executeUpdate(sql, new Object[] { prjName });
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sql = "delete from versioninfo where prjname = ?";
        try {
            jdbcTemplate.executeUpdate(sql, new Object[] { prjName });
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
