// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import org.apache.derby.catalog.UUID;

public class XPLAINStatementDescriptor extends XPLAINTableDescriptor
{
    private UUID stmt_id;
    private String stmt_name;
    private String stmt_type;
    private String stmt_text;
    private String jvm_id;
    private String os_id;
    private String xplain_mode;
    private Timestamp xplain_time;
    private String thread_id;
    private String xa_id;
    private String session_id;
    private String db_name;
    private String drda_id;
    private UUID timing_id;
    static final String TABLENAME_STRING = "SYSXPLAIN_STATEMENTS";
    private static final String[][] indexColumnNames;
    
    public XPLAINStatementDescriptor() {
    }
    
    public XPLAINStatementDescriptor(final UUID stmt_id, final String stmt_name, final String stmt_type, final String stmt_text, final String jvm_id, final String os_id, final String xplain_mode, final Timestamp xplain_time, final String thread_id, final String xa_id, final String session_id, final String db_name, final String drda_id, final UUID timing_id) {
        this.stmt_id = stmt_id;
        this.stmt_name = stmt_name;
        this.stmt_type = stmt_type;
        this.stmt_text = stmt_text;
        this.jvm_id = jvm_id;
        this.os_id = os_id;
        this.xplain_mode = xplain_mode;
        this.xplain_time = xplain_time;
        this.thread_id = thread_id;
        this.xa_id = xa_id;
        this.session_id = session_id;
        this.db_name = db_name;
        this.drda_id = drda_id;
        this.timing_id = timing_id;
    }
    
    public void setStatementParameters(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.stmt_id.toString());
        preparedStatement.setString(2, this.stmt_name);
        preparedStatement.setString(3, this.stmt_type);
        preparedStatement.setString(4, this.stmt_text);
        preparedStatement.setString(5, this.jvm_id);
        preparedStatement.setString(6, this.os_id);
        preparedStatement.setString(7, this.xplain_mode);
        preparedStatement.setTimestamp(8, this.xplain_time);
        preparedStatement.setString(9, this.thread_id);
        preparedStatement.setString(10, this.xa_id);
        preparedStatement.setString(11, this.session_id);
        preparedStatement.setString(12, this.db_name);
        preparedStatement.setString(13, this.drda_id);
        preparedStatement.setString(14, (this.timing_id != null) ? this.timing_id.toString() : null);
    }
    
    public String getCatalogName() {
        return "SYSXPLAIN_STATEMENTS";
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("STMT_ID", false), SystemColumnImpl.getIdentifierColumn("STMT_NAME", true), SystemColumnImpl.getColumn("STMT_TYPE", 1, false, 3), SystemColumnImpl.getColumn("STMT_TEXT", 12, false, 32672), SystemColumnImpl.getColumn("JVM_ID", 12, false, 32672), SystemColumnImpl.getColumn("OS_IDENTIFIER", 12, false, 32672), SystemColumnImpl.getColumn("XPLAIN_MODE", 1, true, 1), SystemColumnImpl.getColumn("XPLAIN_TIME", 93, true), SystemColumnImpl.getColumn("XPLAIN_THREAD_ID", 12, false, 32672), SystemColumnImpl.getColumn("TRANSACTION_ID", 12, false, 32672), SystemColumnImpl.getColumn("SESSION_ID", 12, false, 32672), SystemColumnImpl.getIdentifierColumn("DATABASE_NAME", false), SystemColumnImpl.getColumn("DRDA_ID", 12, true, 32672), SystemColumnImpl.getUUIDColumn("TIMING_ID", true) };
    }
    
    static {
        indexColumnNames = new String[][] { { "STMT_ID" } };
    }
}
