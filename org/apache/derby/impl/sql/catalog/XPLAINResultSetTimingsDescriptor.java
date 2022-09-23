// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.derby.catalog.UUID;

public class XPLAINResultSetTimingsDescriptor extends XPLAINTableDescriptor
{
    private UUID timing_id;
    private Long constructor_time;
    private Long open_time;
    private Long next_time;
    private Long close_time;
    private Long execute_time;
    private Long avg_next_time_per_row;
    private Long projection_time;
    private Long restriction_time;
    private Long temp_cong_create_time;
    private Long temp_cong_fetch_time;
    static final String TABLENAME_STRING = "SYSXPLAIN_RESULTSET_TIMINGS";
    private static final String[][] indexColumnNames;
    
    public XPLAINResultSetTimingsDescriptor() {
    }
    
    public XPLAINResultSetTimingsDescriptor(final UUID timing_id, final Long constructor_time, final Long open_time, final Long next_time, final Long close_time, final Long execute_time, final Long avg_next_time_per_row, final Long projection_time, final Long restriction_time, final Long temp_cong_create_time, final Long temp_cong_fetch_time) {
        this.timing_id = timing_id;
        this.constructor_time = constructor_time;
        this.open_time = open_time;
        this.next_time = next_time;
        this.close_time = close_time;
        this.execute_time = execute_time;
        this.avg_next_time_per_row = avg_next_time_per_row;
        this.projection_time = projection_time;
        this.restriction_time = restriction_time;
        this.temp_cong_create_time = temp_cong_create_time;
        this.temp_cong_fetch_time = temp_cong_fetch_time;
    }
    
    public void setStatementParameters(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.timing_id.toString());
        if (this.constructor_time != null) {
            preparedStatement.setLong(2, this.constructor_time);
        }
        else {
            preparedStatement.setNull(2, -5);
        }
        if (this.open_time != null) {
            preparedStatement.setLong(3, this.open_time);
        }
        else {
            preparedStatement.setNull(3, -5);
        }
        if (this.next_time != null) {
            preparedStatement.setLong(4, this.next_time);
        }
        else {
            preparedStatement.setNull(4, -5);
        }
        if (this.close_time != null) {
            preparedStatement.setLong(5, this.close_time);
        }
        else {
            preparedStatement.setNull(5, -5);
        }
        if (this.execute_time != null) {
            preparedStatement.setLong(6, this.execute_time);
        }
        else {
            preparedStatement.setNull(6, -5);
        }
        if (this.avg_next_time_per_row != null) {
            preparedStatement.setLong(7, this.avg_next_time_per_row);
        }
        else {
            preparedStatement.setNull(7, -5);
        }
        if (this.projection_time != null) {
            preparedStatement.setLong(8, this.projection_time);
        }
        else {
            preparedStatement.setNull(8, -5);
        }
        if (this.restriction_time != null) {
            preparedStatement.setLong(9, this.restriction_time);
        }
        else {
            preparedStatement.setNull(9, -5);
        }
        if (this.temp_cong_create_time != null) {
            preparedStatement.setLong(10, this.temp_cong_create_time);
        }
        else {
            preparedStatement.setNull(10, -5);
        }
        if (this.temp_cong_fetch_time != null) {
            preparedStatement.setLong(11, this.temp_cong_fetch_time);
        }
        else {
            preparedStatement.setNull(11, -5);
        }
    }
    
    public String getCatalogName() {
        return "SYSXPLAIN_RESULTSET_TIMINGS";
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("TIMING_ID", false), SystemColumnImpl.getColumn("CONSTRUCTOR_TIME", -5, true), SystemColumnImpl.getColumn("OPEN_TIME", -5, true), SystemColumnImpl.getColumn("NEXT_TIME", -5, true), SystemColumnImpl.getColumn("CLOSE_TIME", -5, true), SystemColumnImpl.getColumn("EXECUTE_TIME", -5, true), SystemColumnImpl.getColumn("AVG_NEXT_TIME_PER_ROW", -5, true), SystemColumnImpl.getColumn("PROJECTION_TIME", -5, true), SystemColumnImpl.getColumn("RESTRICTION_TIME", -5, true), SystemColumnImpl.getColumn("TEMP_CONG_CREATE_TIME", -5, true), SystemColumnImpl.getColumn("TEMP_CONG_FETCH_TIME", -5, true) };
    }
    
    static {
        indexColumnNames = new String[][] { { "TIMING_ID" } };
    }
}
