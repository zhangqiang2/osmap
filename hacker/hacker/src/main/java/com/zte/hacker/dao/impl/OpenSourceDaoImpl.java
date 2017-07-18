package com.zte.hacker.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.zte.hacker.common.bean.ContributeInfoByVersion;
import com.zte.hacker.common.bean.ContributeInfoByZTE;
import com.zte.hacker.common.bean.Contributes;
import com.zte.hacker.common.bean.Person;
import com.zte.hacker.common.bean.PrjChangeInfo;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.common.bean.Version;
import com.zte.hacker.common.bean.VersionCompanyContribute;
import com.zte.hacker.common.bean.VersionPersonContribute;
import com.zte.hacker.common.db.jdbc.JdbcTemplate;
import com.zte.hacker.common.db.jdbc.JdbcTemplate.ResultSetTransformer;
import com.zte.hacker.common.db.jdbc.JdbcUtils;
import com.zte.hacker.common.exception.OpenSourceException;
import com.zte.hacker.common.http.HttpClient;
import com.zte.hacker.dao.OpenSourceDao;
import com.zte.hacker.rest.bean.RequestByCompany;
import com.zte.hacker.rest.bean.RequestByContributor;
import com.zte.hacker.rest.bean.RequestByVersion;
import com.zte.hacker.rest.bean.RequestZTEContribute;
import com.zte.hacker.rest.service.impl.OpenSourceServiceImpl;

public class OpenSourceDaoImpl implements OpenSourceDao,InitializingBean {

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
			LOG.debug("load " + DBCP_CONFIG_FILE + ", driverClassName:" + properties.getProperty("driverClassName")
					+ ", url:" + properties.getProperty("url") + ", username:" + properties.getProperty("username"));
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
	

	public List<ProjectBaseInfo> queryProBaseInfo(String prjname) {
		LOG.info("enter func OpenSourceDaoImpl.queryProBaseInfo query. prjname is " + prjname);
		List<ProjectBaseInfo> projectBaseInfo = null;
		try{
			projectBaseInfo = queryProjectInfoByPrjName(prjname);
			
			if(CollectionUtils.isEmpty(projectBaseInfo)){
				String result = null;
				try {
					 result = HttpClient.request("http://10.43.139.138:5000/rest/openhub?q=" + prjname);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(StringUtils.isBlank(result)){
					return projectBaseInfo;
				}else{
					projectBaseInfo = queryProjectInfoBySpecialProjectName(result);
				}
				LOG.info(" http://10.43.139.138:5000/rest/openhub result is " + result);
			}
			
			setMemberAndVersionInfo(projectBaseInfo);
			
			setVitality(projectBaseInfo);
			
			
		}catch(SQLException e){
			LOG.error("exception occurs when query" + prjname, e);
			return null;
		}

		LOG.info("exit func OpenSourceDaoImpl.queryProBaseInfo. projectinfo is " +  projectBaseInfo);
		return projectBaseInfo;
	}

	private List<ProjectBaseInfo> queryProjectInfoByPrjName(String prjname) throws SQLException {
		List<ProjectBaseInfo> projectBaseInfo;
		projectBaseInfo =  jdbcTemplate.query4List("select * from prjbaseinfo where LOWER(prjname) like ?", new Object[]{"%" + prjname.toLowerCase()+"%"},
				new ResultSetTransformer<ProjectBaseInfo>() {
			public ProjectBaseInfo transform(ResultSet rs) throws SQLException {
				ProjectBaseInfo prjInfo = new ProjectBaseInfo();
				prjInfo.setProjectName(rs.getString("prjname"));
				prjInfo.setPrjUrl(rs.getString("prjurl"));
				prjInfo.setOrgsourceAddr(rs.getString("orgsourceaddr"));
				prjInfo.setLicenseName(rs.getString("licensename"));
				prjInfo.setLicenseInternalUrl(rs.getString("licenseinternalurl"));
				prjInfo.setDownloadUrl(rs.getString("downloadurl"));
				prjInfo.setCommunityName(rs.getString("communityname"));
				prjInfo.setCommunityUrl(rs.getString("communityurl"));
				prjInfo.setFoundationName(rs.getString("foundationname"));
				prjInfo.setUpdateTime(rs.getString("updatetime"));
				prjInfo.setUpdateuser(rs.getString("updateuser"));
				return prjInfo;
			}
		},false, null, -1);
		sort(projectBaseInfo);
		return projectBaseInfo;
	}
	
	private void sort(List<ProjectBaseInfo> projectBaseinfo){
		Collections.sort(projectBaseinfo, new Comparator<ProjectBaseInfo>() {
			@Override
			public int compare(ProjectBaseInfo o1, ProjectBaseInfo o2) {
				return o1.getProjectName().length() > o2.getProjectName().length()? 1:-1;
			}
		});
	}
	
	private void setVitality(List<ProjectBaseInfo> projectBaseInfo) throws SQLException{
		if(CollectionUtils.isNotEmpty(projectBaseInfo)){
			for(ProjectBaseInfo projInfo : projectBaseInfo){
				String sql = "select count(*) as commitNum from prjchangeinfo where prjname = ? and committime between ? and ?";
				Integer commitNum3Months = jdbcTemplate.query4Object(sql, new Object[]{projInfo.getProjectName(),get3MonthsAgoTime(),getCurrentTime()},
						new ResultSetTransformer<Integer>() {
					public Integer transform(ResultSet rs) throws SQLException {
						return Integer.valueOf(rs.getString("commitNum"));
					}
				});
				projInfo.setCommitNum3Months(commitNum3Months);
				String vitality = "0";
				if(commitNum3Months >= 500){
					vitality = "5";
				}
				else if ( commitNum3Months >= 300){
					vitality = "4";
				}else if (commitNum3Months >= 200){
					vitality = "3";
				}else if (commitNum3Months >= 100){
					vitality = "2";
				}else{
					vitality = "1";
				}
				projInfo.setVitality(vitality);
			}
		}
	}
	

	private void setMemberAndVersionInfo(List<ProjectBaseInfo> projectBaseInfo) throws SQLException {
		if(CollectionUtils.isNotEmpty(projectBaseInfo)){

			for(final ProjectBaseInfo projectInfo : projectBaseInfo){
				//版本信息
				List<Version> versions = jdbcTemplate.query4List("select * from versioninfo where LOWER(prjname) = ?", new Object[]{projectInfo.getProjectName().toLowerCase()}, new ResultSetTransformer<Version>(){
					public Version transform(ResultSet rs) throws SQLException {
						Version version = new Version();
						version.setPrjName(projectInfo.getProjectName());
						version.setReleaseTime(rs.getString("releasetime"));
						version.setReleaseBegintime(rs.getString("releasebegintime"));
						version.setUpdateTime(rs.getString("updatetime"));
						version.setVersionName(rs.getString("versionname"));
						return version;
					}
				}, false, null, -1);

				if(CollectionUtils.isNotEmpty(versions)){
					projectInfo.setVersions(versions);
				}

				//贡献信息
				List<PrjChangeInfo> zteChangeInfo = jdbcTemplate.query4List("select * from prjchangeinfo where prjname = ? and LOWER(mail) like ?",
						new Object[]{projectInfo.getProjectName(),"%zte%"},
				  new ResultSetTransformer<PrjChangeInfo>(){
					public PrjChangeInfo transform(ResultSet rs) throws SQLException {
						PrjChangeInfo projectChangeInfo = new PrjChangeInfo();
						
						projectChangeInfo.setCommitter(rs.getString("committer"));
						projectChangeInfo.setIssuename(rs.getString("issuename"));
						projectChangeInfo.setContributor(rs.getString("contributor"));
						projectChangeInfo.setMail(rs.getString("mail"));
						projectChangeInfo.setCompany("zte");
						projectChangeInfo.setUpdatetime(rs.getString("updatetime"));
						return projectChangeInfo;
					}
				}, false, null, -1);
				
				//活跃度

				if(CollectionUtils.isNotEmpty(zteChangeInfo)){
					projectInfo.setProjectChangeInfo(zteChangeInfo);
				}
			}
		}
	}

	public List<ProjectBaseInfo> queryProBaseInfoByUrl(String url){
		LOG.info("enter func OpenSourceDaoImpl.queryProBaseInfoByUrl. url is " + url);
		List<ProjectBaseInfo> projectBaseInfo = null;
		try{
			projectBaseInfo = queryProjectInfoByUrl(url);
			
			if(CollectionUtils.isEmpty(projectBaseInfo)){
				String result = null;
				try {
					 result = HttpClient.request("http://10.43.139.138:5000/rest/openhub?q=" + url);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(StringUtils.isBlank(result)){
					return projectBaseInfo;
				}else{
					projectBaseInfo = queryProjectInfoBySpecialProjectName(result);
				}
				LOG.info(" http://10.43.139.138:5000/rest/openhub result is " + result);
			}
			
//			sendRequestWhenBaseInfoIsEmpty(projectBaseInfo, url, true);

			setMemberAndVersionInfo(projectBaseInfo);
			
			setVitality(projectBaseInfo);

		}catch(SQLException e){
			LOG.error("exception occurs when query" + url, e);
			return null;
		}

		LOG.info("exit func OpenSourceDaoImpl.queryProBaseInfoByUrl. projectBaseInfo is " +  projectBaseInfo);
		return projectBaseInfo;
	}
	
	
	private List<ProjectBaseInfo> queryProjectInfoBySpecialProjectName(String name) throws SQLException {
		List<ProjectBaseInfo> projectBaseInfo;
		projectBaseInfo =  jdbcTemplate.query4List("select * from prjbaseinfo where LOWER(prjname) = ?",
				new Object[]{name.toLowerCase()},
				new ResultSetTransformer<ProjectBaseInfo>() {
			public ProjectBaseInfo transform(ResultSet rs) throws SQLException {
				ProjectBaseInfo prjInfo = new ProjectBaseInfo();
				prjInfo.setProjectName(rs.getString("prjname"));
				prjInfo.setPrjUrl(rs.getString("prjurl"));
				prjInfo.setOrgsourceAddr(rs.getString("orgsourceaddr"));
				prjInfo.setLicenseName(rs.getString("licensename"));
				prjInfo.setLicenseInternalUrl(rs.getString("licenseinternalurl"));
				prjInfo.setDownloadUrl(rs.getString("downloadurl"));
				prjInfo.setCommunityName(rs.getString("communityname"));
				prjInfo.setCommunityUrl(rs.getString("communityurl"));
				prjInfo.setFoundationName(rs.getString("foundationname"));
				prjInfo.setUpdateTime(rs.getString("updatetime"));
				prjInfo.setUpdateuser(rs.getString("updateuser"));
				return prjInfo;
			}
		},false, null, -1);
		LOG.info("------------queryProjectInfoBySpecialProjectName result is " + projectBaseInfo);
		return projectBaseInfo;
	}

	private List<ProjectBaseInfo> queryProjectInfoByUrl(String url) throws SQLException {
		List<ProjectBaseInfo> projectBaseInfo;
		String searchUrl = "%" + url.toLowerCase()+ "%";
		projectBaseInfo =  jdbcTemplate.query4List("select * from prjbaseinfo where LOWER(prjurl) like ? or LOWER(downloadurl) like ? or LOWER(communityurl) like ?",
				new Object[]{searchUrl,searchUrl,searchUrl},
				new ResultSetTransformer<ProjectBaseInfo>() {
			public ProjectBaseInfo transform(ResultSet rs) throws SQLException {
				ProjectBaseInfo prjInfo = new ProjectBaseInfo();
				prjInfo.setProjectName(rs.getString("prjname"));
				prjInfo.setPrjUrl(rs.getString("prjurl"));
				prjInfo.setOrgsourceAddr(rs.getString("orgsourceaddr"));
				prjInfo.setLicenseName(rs.getString("licensename"));
				prjInfo.setLicenseInternalUrl(rs.getString("licenseinternalurl"));
				prjInfo.setDownloadUrl(rs.getString("downloadurl"));
				prjInfo.setCommunityName(rs.getString("communityname"));
				prjInfo.setCommunityUrl(rs.getString("communityurl"));
				prjInfo.setFoundationName(rs.getString("foundationname"));
				prjInfo.setUpdateTime(rs.getString("updatetime"));
				prjInfo.setUpdateuser(rs.getString("updateuser"));
				return prjInfo;
			}
		},false, null, -1);
		return projectBaseInfo;
	}
	
	public ContributeInfoByZTE queryContributeByZte(RequestZTEContribute requestZteContribute){
		LOG.info("enter func OpenSourceDaoImpl.queryContributeByZte query. requestZteContribute is " + requestZteContribute);
		ContributeInfoByZTE  contributeInfoByZTE = new ContributeInfoByZTE();
		
		String version = requestZteContribute.getVersionname();
		try{
			if(version.equals("all")){
				String sql = "select a.contributor, count(*) as commitnum  from prjchangeinfo a, usercompany b where LOWER(a.prjname) = ? and a.mail=b.email and LOWER(b.company) like '%zte%' group by a.contributor"
						+ " order by commitnum desc";
				List<VersionPersonContribute> contributeInfoByZTEList =  jdbcTemplate.query4List(sql, new Object[]{requestZteContribute.getProjectName().toLowerCase()}, 
						  new ResultSetTransformer<VersionPersonContribute>() {
							public VersionPersonContribute transform(ResultSet rs) throws SQLException {
								VersionPersonContribute versionPersonContribute = new VersionPersonContribute();
								versionPersonContribute.setContributor(rs.getString("contributor"));
								versionPersonContribute.setCommitNumber(rs.getInt("commitnum"));
								return versionPersonContribute;
							}
						},false, null, -1);
				contributeInfoByZTE.setVersionPersonContribute(contributeInfoByZTEList);
			}
			else if(version.equals("current")){
				String sql = "select releasetime from versioninfo where LOWER(prjname) = ? order by unix_timestamp(releasetime) desc limit 1";
				Object[] params = new Object[]{requestZteContribute.getProjectName().toLowerCase()};
				String currentVersionRealasetime = jdbcTemplate.query4Object(sql, params, new ResultSetTransformer<String>() {
					public String transform(ResultSet rs) throws SQLException {
						return rs.getString("releasetime");
					}
				});
				
				sql = "select a.contributor, count(*) as commitnum  from prjchangeinfo a, usercompany b where LOWER(a.prjname) = ? and a.mail=b.email and LOWER(b.company) like '%zte%' and a.committime between ? and ? group by a.contributor"
						+ " order by commitnum desc";
				List<VersionPersonContribute> contributeInfoByZTEList =  jdbcTemplate.query4List(sql, new Object[]{requestZteContribute.getProjectName().toLowerCase(),currentVersionRealasetime,getCurrentTime()}, 
						  new ResultSetTransformer<VersionPersonContribute>() {
							public VersionPersonContribute transform(ResultSet rs) throws SQLException {
								VersionPersonContribute versionPersonContribute = new VersionPersonContribute();
								versionPersonContribute.setContributor(rs.getString("contributor"));
								versionPersonContribute.setCommitNumber(rs.getInt("commitnum"));
								return versionPersonContribute;
							}
						},false, null, -1);
				contributeInfoByZTE.setVersionPersonContribute(contributeInfoByZTEList);
			}else{
				String sql = "select * from versioninfo where LOWER(prjname) = ? and versionname=?";
				Object[] params = new Object[]{requestZteContribute.getProjectName().toLowerCase(), requestZteContribute.getVersionname()};
				Version versionInfo = jdbcTemplate.query4Object(sql, params, new ResultSetTransformer<Version>() {
					@Override
					public Version transform(ResultSet rs) throws SQLException {
						Version version = new Version();
						version.setReleaseBegintime(rs.getString("releasebegintime"));
						version.setReleaseTime(rs.getString("releasetime"));
						return version;
					}
				});
				if(null == versionInfo ){
					return contributeInfoByZTE;
				}
				
				sql = "select a.contributor, count(*) as commitnum  from prjchangeinfo a, usercompany b where LOWER(a.prjname) = ? and a.mail=b.email and LOWER(b.company) like '%zte%' and a.committime between ? and ? group by a.contributor"
						+ " order by commitnum desc";
				List<VersionPersonContribute> contributeInfoByZTEList =  jdbcTemplate.query4List(sql, new Object[]{requestZteContribute.getProjectName().toLowerCase(),versionInfo.getReleaseBegintime(),versionInfo.getReleaseTime()}, 
						  new ResultSetTransformer<VersionPersonContribute>() {
							public VersionPersonContribute transform(ResultSet rs) throws SQLException {
								VersionPersonContribute versionPersonContribute = new VersionPersonContribute();
								versionPersonContribute.setContributor(rs.getString("contributor"));
								versionPersonContribute.setCommitNumber(rs.getInt("commitnum"));
								return versionPersonContribute;
							}
						},false, null, -1);
				contributeInfoByZTE.setVersionPersonContribute(contributeInfoByZTEList);
			}
				
			
		}catch(SQLException e){
			LOG.error("exception occurs when query" + requestZteContribute, e);
		}
		
		return contributeInfoByZTE;
	}
	

public List<ContributeInfoByVersion> queryCommitsByVersion(RequestByVersion openSource) {
		LOG.info("enter func OpenSourceDaoImpl.queryCommitsByVersion query. RequestByVersion is " + openSource.toString());
		String sqlCompany = null;
		String sqlPerson = null;
		Object[] params = null; 
		
		ContributeInfoByVersion contributeInfoByVersion = new ContributeInfoByVersion();
		contributeInfoByVersion.setProjectName(openSource.getProjectName());
		contributeInfoByVersion.setVersionName(openSource.getVersionname());
		List<ContributeInfoByVersion> contributeInfoByVersions = new ArrayList<ContributeInfoByVersion>();
		

		try{
		if(openSource.getVersionname().equals("all")){
			//全部版本的个人排名
			sqlPerson= "select contributor,count(*) as commitnum from prjchangeinfo where LOWER(prjname) = ? group by contributor order by commitnum desc";
			//全部版本公司排名
			sqlCompany= "select b.company as company, count(*) as commitnum  from prjchangeinfo a, usercompany b where LOWER(a.prjname) = ? and a.mail=b.email group by b.company"
					+ " order by commitnum desc";
			
				List<VersionPersonContribute> listPersonContribute =  jdbcTemplate.query4List(sqlPerson, new Object[]{openSource.getProjectName().toLowerCase()}, 
				  new ResultSetTransformer<VersionPersonContribute>() {
					public VersionPersonContribute transform(ResultSet rs) throws SQLException {
						VersionPersonContribute contribute = new VersionPersonContribute();
						contribute.setContributor(rs.getString("contributor"));
						contribute.setCommitNumber(rs.getInt("commitnum"));
						return contribute;
					}
				},false, null, -1);
				
				List<VersionCompanyContribute> listCompanyContribute =  jdbcTemplate.query4List(sqlCompany, new Object[]{openSource.getProjectName().toLowerCase()}, 
						  new ResultSetTransformer<VersionCompanyContribute>() {
							public VersionCompanyContribute transform(ResultSet rs) throws SQLException {
								VersionCompanyContribute contribute = new VersionCompanyContribute();
								contribute.setCompany(rs.getString("company"));
								contribute.setCommitNumber(rs.getInt("commitnum"));
								return contribute;
							}
						},false, null, -1);
				
				contributeInfoByVersion.setVersionPersonContribute(listPersonContribute);
				contributeInfoByVersion.setVersionCompanyContribute(listCompanyContribute);
				contributeInfoByVersions.add(contributeInfoByVersion);
				
			
		}else if(openSource.getVersionname().equals("current")){
				//TODO 当前版本
			String sql = "select releasetime from versioninfo where LOWER(prjname) = ? order by unix_timestamp(releasetime) desc limit 1";
				params = new Object[]{openSource.getProjectName().toLowerCase()};
				String currentVersionRealasetime = jdbcTemplate.query4Object(sql, params, new ResultSetTransformer<String>() {
					public String transform(ResultSet rs) throws SQLException {
						return rs.getString("releasetime");
					}
				});
				
				//当前版本的个人排名
				sqlPerson= "select contributor,count(*) as commitnum from prjchangeinfo where LOWER(prjname) = ?  and committime between ? and ? group by contributor order by commitnum desc";
				//当前版本公司排名
				sqlCompany= "select b.company as company, count(*) as commitnum  from prjchangeinfo a, usercompany b where LOWER(a.prjname) = ? and a.mail=b.email"
						+ " and a.committime between ? and ? group by b.company order by commitnum desc";
				
						
				List<VersionPersonContribute> listPersonContribute =  jdbcTemplate.query4List(sqlPerson, new Object[]{openSource.getProjectName().toLowerCase(),
						currentVersionRealasetime,getCurrentTime()}, 
						  new ResultSetTransformer<VersionPersonContribute>() {
							public VersionPersonContribute transform(ResultSet rs) throws SQLException {
								VersionPersonContribute contribute = new VersionPersonContribute();
								contribute.setContributor(rs.getString("contributor"));
								contribute.setCommitNumber(rs.getInt("commitnum"));
								return contribute;
							}
						},false, null, -1);
						
						List<VersionCompanyContribute> listCompanyContribute =  jdbcTemplate.query4List(sqlCompany, new Object[]{openSource.getProjectName().toLowerCase(),
								currentVersionRealasetime,getCurrentTime()}, 
								  new ResultSetTransformer<VersionCompanyContribute>() {
									public VersionCompanyContribute transform(ResultSet rs) throws SQLException {
										VersionCompanyContribute contribute = new VersionCompanyContribute();
										contribute.setCompany(rs.getString("company"));
										contribute.setCommitNumber(rs.getInt("commitnum"));
										return contribute;
									}
								},false, null, -1);
						
						contributeInfoByVersion.setVersionPersonContribute(listPersonContribute);
						contributeInfoByVersion.setVersionCompanyContribute(listCompanyContribute);
						contributeInfoByVersions.add(contributeInfoByVersion);
				
		}else{
			String sql = "select * from versioninfo where LOWER(prjname) = ? and versionname=?";
			params = new Object[]{openSource.getProjectName().toLowerCase(), openSource.getVersionname()};
			Version version = jdbcTemplate.query4Object(sql, params, new ResultSetTransformer<Version>() {
				@Override
				public Version transform(ResultSet rs) throws SQLException {
					Version version = new Version();
					version.setReleaseBegintime(rs.getString("releasebegintime"));
					version.setReleaseTime(rs.getString("releasetime"));
					return version;
				}
			});
			if(null == version ){
				return contributeInfoByVersions;
			}
			
			//特定版本的个人排名
			sqlPerson= "select contributor,count(*) as commitnum from prjchangeinfo where LOWER(prjname) = ?  and committime between ? and ? group by contributor order by commitnum desc";
			
			//特定版本公司排名
			sqlCompany= "select b.company as company, count(*) as commitnum  from prjchangeinfo a, usercompany b where LOWER(a.prjname) = ? and a.mail=b.email"
					+ " and a.committime between ? and ? group by b.company order by commitnum desc";
			
					
			List<VersionPersonContribute> listPersonContribute =  jdbcTemplate.query4List(sqlPerson, new Object[]{openSource.getProjectName().toLowerCase(),
					version.getReleaseBegintime(),version.getReleaseTime()}, 
					  new ResultSetTransformer<VersionPersonContribute>() {
						public VersionPersonContribute transform(ResultSet rs) throws SQLException {
							VersionPersonContribute contribute = new VersionPersonContribute();
							contribute.setContributor(rs.getString("contributor"));
							contribute.setCommitNumber(rs.getInt("commitnum"));
							return contribute;
						}
					},false, null, -1);
					
					List<VersionCompanyContribute> listCompanyContribute =  jdbcTemplate.query4List(sqlCompany, new Object[]{openSource.getProjectName().toLowerCase(),
							version.getReleaseBegintime(),version.getReleaseTime()}, 
							  new ResultSetTransformer<VersionCompanyContribute>() {
								public VersionCompanyContribute transform(ResultSet rs) throws SQLException {
									VersionCompanyContribute contribute = new VersionCompanyContribute();
									contribute.setCompany(rs.getString("company"));
									contribute.setCommitNumber(rs.getInt("commitnum"));
									return contribute;
								}
							},false, null, -1);
					
					contributeInfoByVersion.setVersionPersonContribute(listPersonContribute);
					contributeInfoByVersion.setVersionCompanyContribute(listCompanyContribute);
					contributeInfoByVersions.add(contributeInfoByVersion);
		}
		}catch(SQLException e){
			LOG.error("exception occurs when query" + openSource.toString(), e);
			return null;
		}
		
		return contributeInfoByVersions;
		
	}


	public List<Contributes> queryCommitsbyCompany(RequestByCompany requestByCompany){
		LOG.info("enter func OpenSourceDaoImpl.queryCommitsbyCompany query. requestByCompany is " + requestByCompany);
		List<Contributes> contributes = new ArrayList<Contributes>();
		try{
			contributes = jdbcTemplate.query4List(
					"select  count(*) from prjchangeinfo a, prjmembers b where a.contributor=b.contributorname and b.organization = ? and a.prjname = ?",
					new Object[]{requestByCompany.getCompanyName(),requestByCompany.getProjectName()},
			  new ResultSetTransformer<Contributes>(){
				public Contributes transform(ResultSet rs) throws SQLException {
					Contributes companyContributes = new Contributes();
					companyContributes.setCommits(rs.getInt(1));
					return companyContributes;
				}
			}, false, null, -1);
		}catch(SQLException e){
			LOG.error("exception occurs when query" + requestByCompany, e);
			return null;
		}
		return contributes;
	}


	public List<Contributes> queryCommitsbyPerson(RequestByContributor requestByPerson){
		LOG.info("enter func OpenSourceDaoImpl.queryCommitsbyPerson query. requestByPerson is " + requestByPerson);
		List<Contributes> contributes = new ArrayList<Contributes>();
		try{
			contributes = jdbcTemplate.query4List(
					"select count(*) from prjchangeinfo where contributor = ? and prjname = ?",
					new Object[]{requestByPerson.getContributor(),requestByPerson.getProjectName()},
			  new ResultSetTransformer<Contributes>(){
				public Contributes transform(ResultSet rs) throws SQLException {
					Contributes personContributes = new Contributes();
					personContributes.setCommits(rs.getInt(1));
					return personContributes;
				}
			}, false, null, -1);
		}catch(SQLException e){
			LOG.error("exception occurs when query" + requestByPerson, e);
			return null;
		}
		return contributes;
	}

	public void insertProjectBase(ProjectBaseInfo projectBaseInfo) throws SQLException {
		LOG.info("enter func OpenSourceDaoImpl.insertProjectBase. projectBaseInfo is " + projectBaseInfo);
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
		obj[9] = getCurrentTime();
		try {
			jdbcTemplate.executeUpdate(sql, obj);
		} catch (SQLException e) {
			LOG.error("exception occurs when insertProjectBase" , e);
			throw e;
		}
	}

	public String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new Date());
	}
	
	public String get3MonthsAgoTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -3);
		Date dBefore = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(dBefore);
	}


	public void updateProjectBase(ProjectBaseInfo projectBaseInfo) throws SQLException{
		LOG.info("enter func OpenSourceDaoImpl.updateProjectBase. projectBaseInfo is " + projectBaseInfo);
		String sql = "update prjbaseinfo set prjurl=?,orgsourceaddr=?,licensename=?,downloadurl=?,vitality=?,communityname=?,communityurl=?,foundationname=?,updatetime=?, updateuser='admin' where prjname=?";
		Object[] obj = new Object[10];
		obj[0] = projectBaseInfo.getPrjUrl();
		obj[1] = projectBaseInfo.getOrgsourceAddr();
		obj[2] = projectBaseInfo.getLicenseName();
		obj[3] = projectBaseInfo.getDownloadUrl();
		obj[4] = projectBaseInfo.getVitality();
		obj[5] = projectBaseInfo.getCommunityName();
		obj[6] = projectBaseInfo.getCommunityUrl();
		obj[7] = projectBaseInfo.getFoundationName();
		obj[8] = getCurrentTime();
		obj[9] = projectBaseInfo.getProjectName();
		try {
			jdbcTemplate.executeUpdate(sql, obj);
		} catch (SQLException e) {
			LOG.error("exception occurs when updateProjectBase" , e);
			throw e;
		}
	}
	
	public List<Person> queryPersonByCompany(String company){
		LOG.info("enter func OpenSourceDaoImpl.queryPersonByCompany. company is " + company);
		List<Person> persons = null;
		
		try{
			persons = jdbcTemplate.query4List(
					"select contributorname from prjmembers where organization = ?",
					new Object[]{company},
			  new ResultSetTransformer<Person>(){
				public Person transform(ResultSet rs) throws SQLException {
					Person person = new Person();
					person.setPersonName(rs.getString("contributorname"));
					return person;
				}
			}, false, null, -1);
		}catch(SQLException e){
			LOG.error("exception occurs when query" + company, e);
			return null;
		}	
		return persons;
	}
	
	public void afterPropertiesSet() throws Exception {
		init();
	}
}
