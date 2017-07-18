package com.zte.hacker.fetchchangeinfo.dao;

import com.zte.hacker.common.bean.ProjectBaseInfo;
import com.zte.hacker.fetchchangeinfo.DBHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10069681 on 2017/7/1.
 */
public class DbImpl {
    public static List<ProjectBaseInfo> getProjectBaseInfo(){
        String sql = "select prjname, orgsourceaddr from prjbaseinfo ";//SQL语句
        DBHelper db1 = new DBHelper(sql);//创建DBHelper对象
        List<ProjectBaseInfo> dbPrjBaseInfos = new ArrayList<ProjectBaseInfo>();
        try {
            ResultSet ret = db1.pst.executeQuery();//执行语句，得到结果集
            while (ret.next()) {
                ProjectBaseInfo dbPrjBaseInfo = new ProjectBaseInfo();
                String url = ret.getString(2);
                if (url == null || url.length() < 1)
                    continue;
                dbPrjBaseInfo.setProjectName(ret.getString(1));
                dbPrjBaseInfo.setOrgsourceAddr(ret.getString(2));
                dbPrjBaseInfos.add(dbPrjBaseInfo);
            }//显示数据
            ret.close();
            db1.close();//关闭连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbPrjBaseInfos;
    }
}
