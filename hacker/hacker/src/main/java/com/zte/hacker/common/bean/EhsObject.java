package com.zte.hacker.common.bean;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 实现了hashCode、equals和toString的类
 * @author 10180976
 *
 */
public abstract class EhsObject {
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);

  }
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(obj, this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
