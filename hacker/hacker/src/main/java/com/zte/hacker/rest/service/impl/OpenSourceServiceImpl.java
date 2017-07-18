package com.zte.hacker.rest.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.zte.hacker.common.bean.ContributeInfoByVersion;
import com.zte.hacker.common.bean.ContributeInfoByZTE;
import com.zte.hacker.common.bean.Contributes;
import com.zte.hacker.common.bean.Person;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.dao.OpenSourceDao;
import com.zte.hacker.rest.bean.RequestByCompany;
import com.zte.hacker.rest.bean.RequestByContributor;
import com.zte.hacker.rest.bean.RequestByVersion;
import com.zte.hacker.rest.bean.RequestZTEContribute;
import com.zte.hacker.rest.service.OpenSourceService;

public class OpenSourceServiceImpl implements OpenSourceService, InitializingBean {

	@Autowired
	private OpenSourceDao dao;

	private static final Logger LOG = Logger.getLogger(OpenSourceServiceImpl.class);

	public List<ProjectBaseInfo> getProjectBaseInfo(String projectName) {
		LOG.info("enter func OpenSourceServiceImpl.getProjectBaseInfo(), param is " + projectName);
		return dao.queryProBaseInfo(projectName);
	}
	
	public List<ProjectBaseInfo> getProjectByUrl(String url){
		LOG.info("enter func OpenSourceServiceImpl.getProjectByUrl(), param is " + url);
		return dao.queryProBaseInfoByUrl(url);
	}
	

	public List<ContributeInfoByVersion> getPrjChangeInfo(RequestByVersion openSource){
		LOG.info("enter func OpenSourceServiceImpl.getPrjChangeInfo(), param is " + openSource.toString());
		return dao.queryCommitsByVersion(openSource);
	}
	
	public List<Contributes> queryCommitsbyCompany(RequestByCompany requestByCompany){
		LOG.info("enter func OpenSourceServiceImpl.queryCommitsbyCompany(), param is " + requestByCompany);
		return dao.queryCommitsbyCompany(requestByCompany);
	}
	
	public List<Contributes> queryCommitsbyPerson(RequestByContributor requestByPerson){
		LOG.info("enter func OpenSourceServiceImpl.queryCommitsbyPerson(), param is " + requestByPerson);
		return dao.queryCommitsbyPerson(requestByPerson);
	}
	
	public void insertProjectBase(ProjectBaseInfo projectBaseInfo) throws SQLException{
		LOG.info("enter func OpenSourceServiceImpl.insertProjectBase(), param is " + projectBaseInfo);
	    dao.insertProjectBase(projectBaseInfo);
	}
	public void updateProjectBase(ProjectBaseInfo projectBaseInfo) throws SQLException{
		LOG.info("enter func OpenSourceServiceImpl.updateProjectBase(), param is " + projectBaseInfo);
		dao.updateProjectBase(projectBaseInfo);
	}
	
	public List<Person> queryPersonByCompany(String company){
		LOG.info("enter func OpenSourceServiceImpl.queryPersonByCompany(), param is " + company);
		return dao.queryPersonByCompany(company);
	}
	
	public ContributeInfoByZTE queryContributeByZte(RequestZTEContribute requestZteContribute){
		LOG.info("enter func OpenSourceServiceImpl.queryContributeByZte(), param is " + requestZteContribute);
		return dao.queryContributeByZte(requestZteContribute);
	}

	public void afterPropertiesSet() throws Exception {

	}

	public OpenSourceDao getDao() {
		return dao;
	}

	public void setDao(OpenSourceDao dao) {
		this.dao = dao;
	}

}
