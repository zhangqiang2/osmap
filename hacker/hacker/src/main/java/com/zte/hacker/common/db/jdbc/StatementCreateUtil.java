package com.zte.hacker.common.db.jdbc;

import java.io.StringWriter;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建statement的工具类
 *
 * @author 10180976
 *
 */
public class StatementCreateUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatementCreateUtil.class);

  public static void setParameterValue(PreparedStatement pstmt, int index, Object value)
      throws SQLException {
    if (null == value) {
      setNullValue(pstmt, index);
    }
    else
    {
      Class<?> klass = value.getClass();
      if (isStringValue(klass)) {
        pstmt.setString(index, value.toString());
      } else if (isDateValue(klass)) {
        pstmt.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) value).getTime()));
      } else {
        pstmt.setObject(index, value);
      }
    }
  }

  public static void setNullValue(PreparedStatement pstmt, int index) throws SQLException {

    boolean useSetObject = false;
    int sqlType = Types.NULL;

    try {
      DatabaseMetaData dbmd = pstmt.getConnection().getMetaData();
      String databaseProductName = dbmd.getDatabaseProductName();
      String jdbcDriverName = dbmd.getDriverName();
      if (databaseProductName.startsWith("Informix")
          || jdbcDriverName.startsWith("Microsoft SQL Server")) {
        useSetObject = true;
      } else if (databaseProductName.startsWith("DB2") || jdbcDriverName.startsWith("jConnect")
          || jdbcDriverName.startsWith("SQLServer")
          || jdbcDriverName.startsWith("Apache Derby Embedded")) {
        sqlType = Types.VARCHAR;
      }
    } catch (Exception ex) {
      LOGGER.debug("Could not check database or driver name", ex);
    }

    if (useSetObject) {
      pstmt.setObject(index, null);
    }
    else {
      pstmt.setNull(index, sqlType);
    }
  }

  private static boolean isDateValue(Class<?> inValueType) {
    return (java.util.Date.class.isAssignableFrom(inValueType) && !(java.sql.Date.class
        .isAssignableFrom(inValueType) || java.sql.Time.class.isAssignableFrom(inValueType) || java.sql.Timestamp.class
        .isAssignableFrom(inValueType)));
  }

  private static boolean isStringValue(Class<?> inValueType) {
    return (CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class
        .isAssignableFrom(inValueType));
  }

}
