package com.zte.hacker.rest.service;

import java.sql.SQLException;
import java.util.List;

import com.zte.hacker.common.bean.ContributeInfoByVersion;
import com.zte.hacker.common.bean.ContributeInfoByZTE;
import com.zte.hacker.common.bean.Contributes;
import com.zte.hacker.common.bean.Person;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.rest.bean.RequestByCompany;
import com.zte.hacker.rest.bean.RequestByContributor;
import com.zte.hacker.rest.bean.RequestByVersion;
import com.zte.hacker.rest.bean.RequestZTEContribute;

public interface OpenSourceService {
	List<ProjectBaseInfo> getProjectBaseInfo(String projectName);
	List<ProjectBaseInfo> getProjectByUrl(String url);
	List<ContributeInfoByVersion> getPrjChangeInfo(RequestByVersion openSource);
	List<Contributes> queryCommitsbyCompany(RequestByCompany requestByCompany);
	List<Contributes> queryCommitsbyPerson(RequestByContributor requestByPerson);
	void insertProjectBase(ProjectBaseInfo projectBaseInfo) throws SQLException;
	void updateProjectBase(ProjectBaseInfo projectBaseInfo) throws SQLException;
	
	List<Person> queryPersonByCompany(String company);
	
	ContributeInfoByZTE queryContributeByZte(RequestZTEContribute requestZteContribute);
}
