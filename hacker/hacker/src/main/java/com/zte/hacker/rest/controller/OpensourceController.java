package com.zte.hacker.rest.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zte.hacker.fetchchangeinfo.JobConServiceInterface;
import com.zte.hacker.rest.bean.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.zte.hacker.common.bean.ContributeInfoByVersion;
import com.zte.hacker.common.bean.ContributeInfoByZTE;
import com.zte.hacker.common.bean.Contributes;
import com.zte.hacker.common.bean.Person;
import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.common.bean.RetMessage;
import com.zte.hacker.common.json.JsonBeanConverter;
import com.zte.hacker.rest.service.ImportFileService;
import com.zte.hacker.rest.service.OpenSourceService;
import com.zte.hacker.util.DownloadUtil;

/**
 *
 * @author 10180976
 *
 */
@RestController
@RequestMapping("/opensource")
public class OpensourceController {

    private static Logger logger = Logger.getLogger(OpensourceController.class);
    private static String PATH = "/home/hacker";

    @Autowired
    private OpenSourceService openSourceService;

    @Autowired
    private ImportFileService importFileService;

    @Autowired
    private JobConServiceInterface jobService;

    @ResponseBody
    @RequestMapping(value = "/queryResourceByName", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String queryResourceByName(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.queryResource(), request is" + request);
        List<ProjectBaseInfo> projectBaseInfo = new ArrayList<ProjectBaseInfo>();
        if (StringUtils.isNotBlank(request)) {
            RequestByName openSource = (RequestByName) JsonBeanConverter.convertJsonStrToBean(request, RequestByName.class);
            List<ProjectBaseInfo> dbInfos = openSourceService.getProjectBaseInfo(openSource.getProjectName());
            if (null != dbInfos && !dbInfos.isEmpty()) {
                projectBaseInfo = dbInfos;
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(projectBaseInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/queryResourceByUrl", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String queryResourceByUrl(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.queryResourceByUrl(), request is" + request);
        List<ProjectBaseInfo> projectBaseInfo = new ArrayList<ProjectBaseInfo>();
        if (StringUtils.isNotBlank(request)) {
            RequestByUrl requestByUrl = (RequestByUrl) JsonBeanConverter.convertJsonStrToBean(request, RequestByUrl.class);
            List<ProjectBaseInfo> dbInfos = openSourceService.getProjectByUrl(requestByUrl.getUrl());
            if (null != dbInfos && !dbInfos.isEmpty()) {
                projectBaseInfo = dbInfos;
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(projectBaseInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/queryChangesByVersion", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String queryChangesByVersion(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.queryCommitsbycompany(), request is" + request);
        List<ContributeInfoByVersion> prjChangeInfo = new ArrayList<ContributeInfoByVersion>();
        if (StringUtils.isNotBlank(request)) {
            RequestByVersion openSource = (RequestByVersion) JsonBeanConverter.convertJsonStrToBean(request, RequestByVersion.class);
            List<ContributeInfoByVersion> dbChangeInfos = openSourceService.getPrjChangeInfo(openSource);
            if (null != dbChangeInfos && !dbChangeInfos.isEmpty()) {
                prjChangeInfo = dbChangeInfos;
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(prjChangeInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/queryCommitsByCompany", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String queryCommitsByCompany(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.queryCommitsbycompany(), request is" + request);
        List<Contributes> companyContributes = new ArrayList<Contributes>();
        if (StringUtils.isNotBlank(request)) {
            RequestByCompany requestByCompany = (RequestByCompany) JsonBeanConverter.convertJsonStrToBean(request, RequestByCompany.class);
            if (StringUtils.isNotBlank(requestByCompany.getCompanyName())) {
                List<Contributes> dbChangeInfos = openSourceService.queryCommitsbyCompany(requestByCompany);
                if (null != dbChangeInfos && !dbChangeInfos.isEmpty()) {
                    companyContributes = dbChangeInfos;
                }
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(companyContributes);
    }

    @ResponseBody
    @RequestMapping(value = "/queryCommitsByContributor", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String queryCommitsContributor(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.queryCommitsByContributor(), request is" + request);
        List<Contributes> companyContributes = new ArrayList<Contributes>();
        if (StringUtils.isNotBlank(request)) {
            RequestByContributor requestByContributor = (RequestByContributor) JsonBeanConverter.convertJsonStrToBean(request, RequestByContributor.class);
            if (StringUtils.isNotBlank(requestByContributor.getContributor())) {
                List<Contributes> dbChangeInfos = openSourceService.queryCommitsbyPerson(requestByContributor);
                if (null != dbChangeInfos && !dbChangeInfos.isEmpty()) {
                    companyContributes = dbChangeInfos;
                }
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(companyContributes);
    }

    @ResponseBody
    @RequestMapping(value = "/insertProjectBase", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String insertProjectBase(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.insertProjectBase(), request is" + request);
        RetMessage measge = new RetMessage();
        if (StringUtils.isNotBlank(request)) {
            ProjectBaseInfo projectBaseInfo = (ProjectBaseInfo) JsonBeanConverter.convertJsonStrToBean(request, ProjectBaseInfo.class);
            if (StringUtils.isNotBlank(projectBaseInfo.getProjectName())) {
                try {
                    openSourceService.insertProjectBase(projectBaseInfo);
                } catch (SQLException e) {
                    measge.setRetCode(-1);
                }
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(measge);
    }

    @ResponseBody
    @RequestMapping(value = "/modifyProjectBase", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String modifyProjectBase(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.modifyProjectBase(), request is" + request);
        RetMessage measge = new RetMessage();
        if (StringUtils.isNotBlank(request)) {
            ProjectBaseInfo projectBaseInfo = (ProjectBaseInfo) JsonBeanConverter.convertJsonStrToBean(request, ProjectBaseInfo.class);
            if (StringUtils.isNotBlank(projectBaseInfo.getProjectName())) {
                try {
                    openSourceService.updateProjectBase(projectBaseInfo);
                } catch (SQLException e) {
                    measge.setRetCode(-1);
                }
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(measge);
    }

    @ResponseBody
    @RequestMapping(value = "/queryPeopleByCompany", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String queryPeopleByCompany(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.queryPeopleByCompany(), request is" + request);
        List<Person> persons = new ArrayList<Person>();
        if (StringUtils.isNotBlank(request)) {
            QueryPeopleByCompany queryPeopleByCompany = (QueryPeopleByCompany) JsonBeanConverter.convertJsonStrToBean(request, QueryPeopleByCompany.class);
            if (StringUtils.isNotBlank(queryPeopleByCompany.getCompanyName())) {
                persons = openSourceService.queryPersonByCompany(queryPeopleByCompany.getCompanyName());
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(persons);
    }

    @ResponseBody
    @RequestMapping(value = "/importFile", method = RequestMethod.POST)
    public String importFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("fileField");
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(fileName);

        InputStream in = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = "";
        ProjectBaseInfo projectBaseInfo = new ProjectBaseInfo();
        if (fileType.equals("csv")) {
            while ((line = reader.readLine()) != null) {
                String arr[] = line.split(",");
                if (arr.length == 9) {
                    projectBaseInfo.setProjectName(arr[0]);
                    projectBaseInfo.setPrjUrl(arr[1]);
                    projectBaseInfo.setOrgsourceAddr(arr[2]);
                    projectBaseInfo.setLicenseName(arr[3]);
                    projectBaseInfo.setDownloadUrl(arr[4]);
                    projectBaseInfo.setVitality(arr[5]);
                    projectBaseInfo.setCommunityName(arr[6]);
                    projectBaseInfo.setCommunityUrl(arr[7]);
                    projectBaseInfo.setFoundationName(arr[8]);
                    projectBaseInfo.setUpdateTime(getCurrentTime());
                }
                importFileService.addProBaseInfo(projectBaseInfo);
            }
        } else {
            StringBuffer sb = new StringBuffer("");
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONArray jsonArray = JSONArray.fromObject(sb.toString());
            for (Object obj : jsonArray) {
                JSONObject jsonObj = (JSONObject) obj;
                projectBaseInfo.setProjectName(getValue(jsonObj, "prjname"));
                projectBaseInfo.setPrjUrl(getValue(jsonObj, "prjurl"));
                projectBaseInfo.setOrgsourceAddr(getValue(jsonObj, "orgsourceaddr"));
                projectBaseInfo.setLicenseName(getValue(jsonObj, "licensename"));
                projectBaseInfo.setDownloadUrl(getValue(jsonObj, "downloadurl"));
                projectBaseInfo.setVitality(getValue(jsonObj, "vitality"));
                projectBaseInfo.setCommunityName(getValue(jsonObj, "communityname"));
                projectBaseInfo.setCommunityUrl(getValue(jsonObj, "communityurl"));
                projectBaseInfo.setFoundationName(getValue(jsonObj, "foundationname"));
                projectBaseInfo.setUpdateTime(getCurrentTime());
                importFileService.addProBaseInfo(projectBaseInfo);
            }
        }
        reader.close();
        in.close();
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/downloadFile", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;")
    public String downloadFile(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String type = request.getParameter("type");
        String value = request.getParameter("value");
        String fileType = request.getParameter("fileType");
        List<ProjectBaseInfo> projectBaseInfos;
        if (type.equals("name")) {
            projectBaseInfos = openSourceService.getProjectBaseInfo(value);
        } else {
            projectBaseInfos = openSourceService.getProjectByUrl(value);
        }
        String filePath = getPath(fileType, value);
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(filePath);
        if (fileType.equals("csv")) {
            for (ProjectBaseInfo projectBaseInfo : projectBaseInfos) {
                StringBuffer sb = new StringBuffer("");
                sb.append(projectBaseInfo.getProjectName());
                sb.append(",");
                sb.append(projectBaseInfo.getPrjUrl());
                sb.append(",");
                sb.append(projectBaseInfo.getOrgsourceAddr());
                sb.append(",");
                sb.append(projectBaseInfo.getLicenseName());
                sb.append(",");
                sb.append(projectBaseInfo.getDownloadUrl());
                sb.append(",");
                sb.append(projectBaseInfo.getVitality());
                sb.append(",");
                sb.append(projectBaseInfo.getCommunityName());
                sb.append(",");
                sb.append(projectBaseInfo.getCommunityUrl());
                sb.append(",");
                sb.append(projectBaseInfo.getFoundationName());
                sb.append("\n");
                fw.write(sb.toString());
            }
        } else {
            fw.write("[\n");
            for (int i = 0; i < projectBaseInfos.size(); i++) {
                ProjectBaseInfo projectBaseInfo = projectBaseInfos.get(i);
                fw.write("  {\n");
                fw.write("      \"prjname\" : \"" + projectBaseInfo.getProjectName() + "\",\n");
                fw.write("      \"prjurl\" : \"" + projectBaseInfo.getPrjUrl() + "\",\n");
                fw.write("      \"orgsourceaddr\" : \"" + projectBaseInfo.getOrgsourceAddr() + "\",\n");
                fw.write("      \"licensename\" : \"" + projectBaseInfo.getLicenseName() + "\",\n");
                fw.write("      \"downloadurl\" : \"" + projectBaseInfo.getDownloadUrl() + "\",\n");
                fw.write("      \"vitality\" : \"" + projectBaseInfo.getVitality() + "\",\n");
                fw.write("      \"communityname\" : \"" + projectBaseInfo.getCommunityName() + "\",\n");
                fw.write("      \"communityurl\" : \"" + projectBaseInfo.getCommunityUrl() + "\",\n");
                fw.write("      \"foundationname\" : \"" + projectBaseInfo.getFoundationName() + "\"\n");
                if (i < projectBaseInfos.size() - 1) {
                    fw.write("  },\n");
                } else {
                    fw.write("  }\n");
                }
            }
            fw.write("]");
        }
        fw.close();
        new DownloadUtil().downloadFile(response, filePath);
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/controlGatherGithubLogs", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String controlGatherGithubLogs(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.controlGatherGithubLogs(), request is" + request);
        if (StringUtils.isNotBlank(request)) {
            RequestGatherLogsAction reqAction = (RequestGatherLogsAction) JsonBeanConverter.convertJsonStrToBean(request, RequestGatherLogsAction.class);
            if (reqAction.getAction().trim().equals("start")) {
                jobService.start();
            } else if (reqAction.getAction().trim().equals("stop")) {
                jobService.stop();
            }
        }
        return "";
    }
    
    @ResponseBody
    @RequestMapping(value = "/getZteContribute", method = { RequestMethod.POST, RequestMethod.GET }, produces = "text/plain;charset=UTF-8;", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getZteContribute(@RequestBody String request) throws IOException {
        logger.info("enter func OpensourceController.getZteContribute(), request is" + request);
        ContributeInfoByZTE contributeInfoByZTE = new ContributeInfoByZTE();
        if (StringUtils.isNotBlank(request)) {
        	
        	RequestZTEContribute requestZTEContribute = (RequestZTEContribute) JsonBeanConverter.convertJsonStrToBean(request, RequestZTEContribute.class);
        	
            if(null != requestZTEContribute){
            	contributeInfoByZTE = openSourceService.queryContributeByZte(requestZTEContribute);
            }
        }
        return JsonBeanConverter.convertBeanToJsonStr(contributeInfoByZTE);
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);
        resolver.setMaxInMemorySize(40960);
        resolver.setMaxUploadSize(50 * 1024 * 1024);
        return resolver;
    }

    public String getFileType(String fileName) {
        String[] names = fileName.split("\\.");
        return names[names.length - 1];
    }

    public String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }

    public String getValue(JSONObject jsonObj, String key) {
        if (jsonObj.containsKey(key)) {
            return jsonObj.getString(key);
        }
        return "";
    }

    public String getPath(String fileType, String value) {
        return PATH + "/" + new Date().getTime() + "." + fileType;
    }
}