// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import org.apache.derby.catalog.UUID;

public class XPLAINStatementTimingsDescriptor extends XPLAINTableDescriptor
{
    private UUID timing_id;
    private Long parse_time;
    private Long bind_time;
    private Long optimize_time;
    private Long generate_time;
    private Long compile_time;
    private Long execute_time;
    private Timestamp begin_comp_time;
    private Timestamp end_comp_time;
    private Timestamp begin_exe_time;
    private Timestamp end_exe_time;
    static final String TABLENAME_STRING = "SYSXPLAIN_STATEMENT_TIMINGS";
    private static final String[][] indexColumnNames;
    
    public XPLAINStatementTimingsDescriptor() {
    }
    
    public XPLAINStatementTimingsDescriptor(final UUID timing_id, final Long parse_time, final Long bind_time, final Long optimize_time, final Long generate_time, final Long compile_time, final Long execute_time, final Timestamp begin_comp_time, final Timestamp end_comp_time, final Timestamp begin_exe_time, final Timestamp end_exe_time) {
        this.timing_id = timing_id;
        this.parse_time = parse_time;
        this.bind_time = bind_time;
        this.optimize_time = optimize_time;
        this.generate_time = generate_time;
        this.compile_time = compile_time;
        this.execute_time = execute_time;
        this.begin_comp_time = begin_comp_time;
        this.end_comp_time = end_comp_time;
        this.begin_exe_time = begin_exe_time;
        this.end_exe_time = end_exe_time;
    }
    
    public void setStatementParameters(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.timing_id.toString());
        if (this.parse_time != null) {
            preparedStatement.setLong(2, this.parse_time);
        }
        else {
            preparedStatement.setNull(2, -5);
        }
        if (this.bind_time != null) {
            preparedStatement.setLong(3, this.bind_time);
        }
        else {
            preparedStatement.setNull(3, -5);
        }
        if (this.optimize_time != null) {
            preparedStatement.setLong(4, this.optimize_time);
        }
        else {
            preparedStatement.setNull(4, -5);
        }
        if (this.generate_time != null) {
            preparedStatement.setLong(5, this.generate_time);
        }
        else {
            preparedStatement.setNull(5, -5);
        }
        if (this.compile_time != null) {
            preparedStatement.setLong(6, this.compile_time);
        }
        else {
            preparedStatement.setNull(6, -5);
        }
        if (this.execute_time != null) {
            preparedStatement.setLong(7, this.execute_time);
        }
        else {
            preparedStatement.setNull(7, -5);
        }
        preparedStatement.setTimestamp(8, this.begin_comp_time);
        preparedStatement.setTimestamp(9, this.end_comp_time);
        preparedStatement.setTimestamp(10, this.begin_exe_time);
        preparedStatement.setTimestamp(11, this.end_exe_time);
    }
    
    public String getCatalogName() {
        return "SYSXPLAIN_STATEMENT_TIMINGS";
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("TIMING_ID", false), SystemColumnImpl.getColumn("PARSE_TIME", -5, false), SystemColumnImpl.getColumn("BIND_TIME", -5, false), SystemColumnImpl.getColumn("OPTIMIZE_TIME", -5, false), SystemColumnImpl.getColumn("GENERATE_TIME", -5, false), SystemColumnImpl.getColumn("COMPILE_TIME", -5, false), SystemColumnImpl.getColumn("EXECUTE_TIME", -5, false), SystemColumnImpl.getColumn("BEGIN_COMP_TIME", 93, false), SystemColumnImpl.getColumn("END_COMP_TIME", 93, false), SystemColumnImpl.getColumn("BEGIN_EXE_TIME", 93, false), SystemColumnImpl.getColumn("END_EXE_TIME", 93, false) };
    }
    
    static {
        indexColumnNames = new String[][] { { "TIMING_ID" } };
    }
}
