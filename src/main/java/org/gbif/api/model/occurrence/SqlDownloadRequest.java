package org.gbif.api.model.occurrence;

import java.util.Collection;
import javax.annotation.Nullable;
import javax.validation.Valid;

import com.google.common.base.MoreObjects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import com.google.common.base.Objects;

public class SqlDownloadRequest extends DownloadRequest {

  private String sql;

  // tab separated names of selected fields
  @JsonIgnore
  private String sqlHeader;

  public SqlDownloadRequest() {

  }

  /**
   * Full constructor. Used to create instances using JSON serialization.
   */
  @JsonCreator
  public SqlDownloadRequest(@JsonProperty("sql") String sql,
                            @JsonProperty("creator") @Nullable String creator,
                            @JsonProperty("notification_address") @Nullable Collection<String> notificationAddresses,
                            @JsonProperty("send_notification") @Nullable boolean sendNotification) {
    super(creator, notificationAddresses, sendNotification, DownloadFormat.SQL);
    this.sql = sql;
  }

  /**
   *
   * @return the sql query.
   */
  @Valid
  public String getSql() {
    return sql;
  }

  /**
   * This parameter when present provides the SQL query for custom download
   * @param sql
   */
  public void setSql(String sql) {
    this.sql = sql;
  }
  
  public String getSqlHeader() {
    return sqlHeader;
  }

  public void setSqlHeader(String sqlHeader) {
    this.sqlHeader = sqlHeader;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), sql, sqlHeader);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof SqlDownloadRequest)) {
      return false;
    }

    SqlDownloadRequest that = (SqlDownloadRequest) obj;
    return super.equals(that) && Objects.equal(this.sql, that.sql) && Objects.equal(this.sqlHeader, that.sqlHeader);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).addValue(super.toString())
      .add("sql", sql).add("sqlHeader", sqlHeader).toString();
  }
}
