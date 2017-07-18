package com.zte.hacker.common.db.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * jdbc操作工具类
 * @author 10180976
 *
 */
public abstract class JdbcUtils {


  private static final Log LOGGER = LogFactory.getLog(JdbcUtils.class);

  public static void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
      }
      catch (SQLException ex) {
        LOGGER.error("Could not close JDBC Connection", ex);
      }
    }
  }

  public static void closeStatement(Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      }
      catch (SQLException ex) {
        LOGGER.error("Could not close JDBC Statement", ex);
      }
    }
  }

  public static void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (SQLException ex) {
        LOGGER.error("Could not close JDBC ResultSet", ex);
      }
    }
  }
  
  public static void close(Connection connection) {
	    try {
	      if (null != connection) {
	        connection.close();
	        connection = null;
	      }
	    } catch (SQLException e) {
	    	LOGGER.error("Close the connection failed, caused by ", e);
	    }
	  }

}
