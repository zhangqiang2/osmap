package com.zte.hacker.common.db.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;

import com.zte.hacker.common.db.jdbc.JdbcTemplate.PreparedStatementSetter;


/**
 *
 * @author 10180976
 *
 */
public class ArgPreparedStatementSetter implements PreparedStatementSetter {

  private Object[] args;

  public ArgPreparedStatementSetter(Object[] args)
  {
    this.args = args;
  }

  public void setValues(PreparedStatement ps) throws SQLException {
    if (ArrayUtils.isEmpty(args)) {
      return;
    }
    for (int i = 0; i < args.length; i++) {
      StatementCreateUtil.setParameterValue(ps, i + 1, args[i]);
    }
  }

}
