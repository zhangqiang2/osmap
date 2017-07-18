package com.zte.hacker.fetchchangeinfo;

import com.zte.hacker.dao.AddSourceDao;
import com.zte.hacker.rest.service.OpenSourceService;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class JobCronServiceImpl implements JobConServiceInterface, InitializingBean {

    private static final Logger LOG = Logger.getLogger(JobCronServiceImpl.class);

    private static JobCronServiceImpl instance = null;

    private Scheduler scheduler = null;
    private String schedulingPattern = "0 0 23 * * ?";
    private String CRON_PATTERN = "0 1 * * *"; //"0 1 * * *";
    private String prjBasePath = "";
    private String shellExecutePath = "";

    @Autowired
    private OpenSourceService openSourceService;

    @Autowired
    private AddSourceDao dao;


    public String getPrjBasePath() {
        return prjBasePath;
    }

    public String getShellExecutePath() {
        return shellExecutePath;
    }

    public void setShellExecutePath(String shellExecutePath) {
        this.shellExecutePath = shellExecutePath;
    }

    public void start() {
        if (instance == null) {
            try {
                init();
            } catch (IOException e) {
                LOG.error(e);
            }
            instance = this;
        }

        if (scheduler.isStarted()) {
            LOG.warn("scheduling job already started! do nothing.");
            return;
        }

        scheduler.schedule(new SchedulingPattern(schedulingPattern), new ExecuteJobTask(this, openSourceService, dao));
        scheduler.schedule(new SchedulingPattern(CRON_PATTERN), new UpdateUserCompany());
        scheduler.schedule(new SchedulingPattern(CRON_PATTERN), new UpdateProject());
        scheduler.schedule(new SchedulingPattern(CRON_PATTERN), new GetCompanyJobTask(dao));
        scheduler.start();
        LOG.info("scheduling job started. pattern: " + schedulingPattern);
    }

    private void init() throws IOException {

        Properties properties = new Properties();
        InputStream fileStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("hacker-cron.properties");
        properties.load(fileStream);

//        fStream.close();
        this.schedulingPattern = properties.getProperty("schedule");
        this.prjBasePath = properties.getProperty("prjbasepath");
        this.shellExecutePath = properties.getProperty("shellpath");
        fileStream.close();

        scheduler = new Scheduler();
        LOG.error("===schedulingPattern:" + this.schedulingPattern);
        checkSchedulingPattern();
        LOG.error("=== ater checkSchedulingPattern(). schedulingPattern:" + this.schedulingPattern);
    }

    private void checkSchedulingPattern() {
        if (schedulingPattern == null || !SchedulingPattern.validate(schedulingPattern)) {
            LOG.error("Incorrect scheduling pattern configuration: " + schedulingPattern + ", use default insdead.");
            // 默认每天23:00执行
            this.schedulingPattern = "0 23 * * *";
        }
    }

    public void afterPropertiesSet() throws Exception {
        try {
            init();
        } catch (IOException e) {
            LOG.error("Failed to start scheduling job, caused by ", e);
        }
    }

    public void stop() {
        if (scheduler.isStarted()) {
            scheduler.stop();
            LOG.info("scheduling job stopped.");
        }
    }
}
