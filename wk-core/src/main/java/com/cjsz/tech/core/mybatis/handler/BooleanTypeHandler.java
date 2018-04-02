package com.cjsz.tech.core.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author java中的boolean和jdbc中的char之间转换;true-T;false-F
 */
@MappedTypes(Boolean.class)
public class BooleanTypeHandler implements TypeHandler {

  @Override
  public Object getResult(ResultSet rs, String columnName) throws SQLException {
    String str = rs.getString(columnName);
    Boolean rt = Boolean.FALSE;
    if (str.equalsIgnoreCase("T")) {
      rt = Boolean.TRUE;
    }
    return rt;
  }

  @Override
  public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
    return null;
  }

  @Override
  public Object getResult(CallableStatement arg0, int arg1)
          throws SQLException {
    Boolean b = arg0.getBoolean(arg1);
    return b == true ? "T" : "F";
  }

  @Override
  public void setParameter(PreparedStatement arg0, int arg1, Object arg2,
                           JdbcType arg3) throws SQLException {
    Boolean b = (Boolean) arg2;
    String value = (Boolean) b == true ? "T" : "F";
    arg0.setString(arg1, value);
  }
}

