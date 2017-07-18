package com.zte.hacker.common.db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.hacker.common.ErrorCode;
import com.zte.hacker.common.exception.OpenSourceException;


/**
 * jdbc操作模板类
 *
 * @author 10180976
 *
 */
public class JdbcTemplate {

  private DataSource dataSource;

  private Connection conn;

  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTemplate.class);

  public JdbcTemplate(){
  }

  public JdbcTemplate(Connection connection) {
    this.conn = connection;
  }

  public JdbcTemplate(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private Connection getConnection(){
    try{
      return null != this.dataSource ? this.dataSource.getConnection() : this.conn;
    }
    catch(SQLException e){
      LOGGER.error("database is not valid. exception : ",e);
      throw new OpenSourceException(ErrorCode.SqlErrorCode.SQL_EXCEPTION, "database is not valid.");
    }
  }

  /**
   *
   * @param <T>
   * @param sql
   * @param preparedStatementSetter
   * @param transformer
   * @param containColumnNames
   * @param columnTransformer
   * @param maxFetchSize -1代表默认不设置
   * @return
   * @throws SQLException
   */
  public <T> List<T> query4List(final String sql, PreparedStatementSetter preparedStatementSetter,
      ResultSetTransformer<T> transformer, boolean containColumnNames,
      ColumnTransformer<T> columnTransformer,int maxFetchSize) throws SQLException {
    Connection conn = getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql);

    if(-1 != maxFetchSize){
      stmt.setFetchSize(maxFetchSize);// 提示jdbc一次性获取的行数
      stmt.setMaxRows(maxFetchSize);// 获取的最大行数
    }

    preparedStatementSetter.setValues(stmt);
    List<T> result = new ArrayList<T>();
    ResultSet rs = null;
    try {

      rs = stmt.executeQuery();

      if(containColumnNames){
        result.add(columnTransformer.transform(rs));
      }

      while (rs.next()) {
        result.add(transformer.transform(rs));
      }
    } finally {
      JdbcUtils.closeResultSet(rs);
      JdbcUtils.closeStatement(stmt);
      JdbcUtils.closeConnection(conn);
    }

    return result;
  }

  /**
   *
   * @param <T>
   * @param sql
   * @param values
   * @param transformer
   * @param containColumnNames
   * @param columnTransformer
   * @param maxFetchSize
   * @return
   * @throws SQLException
   */
  public <T> List<T> query4List(final String sql, final Object[] values,
      ResultSetTransformer<T> transformer, boolean containColumnNames,
      ColumnTransformer<T> columnTransformer,int maxFetchSize) throws SQLException {

    return query4List(sql, new ArgPreparedStatementSetter(values), transformer, containColumnNames,
        columnTransformer,maxFetchSize);
  }

  /**
   * 查询一个对象
   *
   * @param <T>
   * @param sql
   * @param preparedStatementSetter
   * @param resultSetTransformer
   * @return
   * @throws SQLException
   */
  public <T> T query4Object(final String sql, PreparedStatementSetter preparedStatementSetter,
      ResultSetTransformer<T> resultSetTransformer) throws SQLException {
    Connection conn = getConnection();
    PreparedStatement pstmt = conn.prepareStatement(sql);
    preparedStatementSetter.setValues(pstmt);

    ResultSet rs = null;
    try {
      rs = pstmt.executeQuery();
      if (rs.next()) {
        return resultSetTransformer.transform(rs);
      }
      return null;
    } finally {
      JdbcUtils.closeResultSet(rs);
      JdbcUtils.closeStatement(pstmt);
      JdbcUtils.closeConnection(conn);
    }
  }

  /**
   * 查询一个对象
   *
   * @param <T>
   * @param sql
   * @param values
   * @param resultSetTransformer
   * @return
   * @throws SQLException
   */
  public <T> T query4Object(final String sql, final Object[] values,
      ResultSetTransformer<T> resultSetTransformer) throws SQLException {

    return query4Object(sql, new ArgPreparedStatementSetter(values), resultSetTransformer);
  }

  /**
   * 执行一个增加、修改或者删除的sql
   *
   * @param sql
   * @return
   * @throws SQLException
   */
  public void executeUpdate(final String sql, PreparedStatementSetter preparedStatementSetter)
      throws SQLException {
    Connection conn = getConnection();
    try {
      executeUpdateWithConn(conn, sql, preparedStatementSetter);
    } finally {
      JdbcUtils.closeConnection(conn);
    }
  }

  /**
   * 执行一个增加、修改或者删除的sql
   *
   * @param sql
   * @return
   * @throws SQLException
   */
  public void executeUpdate(final String sql, Object[] values) throws SQLException {
    Connection conn = getConnection();
    try {
       executeUpdateWithConn(conn, sql, values);
    } finally {
      JdbcUtils.closeConnection(conn);
    }
  }

  /**
   * 此方法用在自己传connection进来的时候，可以在外面进行事务控制
   *
   * @param sql
   * @param conn
   * @return
   * @throws SQLException
   */
  public void executeUpdateWithConn(Connection conn, final String sql,
      PreparedStatementSetter preparedStatementSetter) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(sql);
    preparedStatementSetter.setValues(stmt);
    try {
       stmt.executeUpdate();
    } finally {
      JdbcUtils.closeStatement(stmt);
    }
  }

  /**
   * 此方法用在自己传connection进来的时候，可以在外面进行事务控制
   *
   * @param sql
   * @param conn
   * @return
   * @throws SQLException
   */
  public void executeUpdateWithConn(Connection conn, final String sql, Object[] values)
      throws SQLException {
     executeUpdateWithConn(conn, sql, new ArgPreparedStatementSetter(values));
  }

  public static interface ResultSetTransformer<T> {
    public T transform(ResultSet rs) throws SQLException;
  }

  public static interface ColumnTransformer<T> {
    public T transform(ResultSet rs) throws SQLException;
  }

  public static interface PreparedStatementSetter {
    public void setValues(PreparedStatement ps) throws SQLException;
  }
}
