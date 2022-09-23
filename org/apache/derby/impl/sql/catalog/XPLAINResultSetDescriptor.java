// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.derby.catalog.UUID;

public class XPLAINResultSetDescriptor extends XPLAINTableDescriptor
{
    private UUID rs_id;
    private String op_identifier;
    private String op_details;
    private Integer no_opens;
    private Integer no_index_updates;
    private String lock_granularity;
    private String lock_mode;
    private UUID parent_rs_id;
    private Double est_row_count;
    private Double est_cost;
    private Integer affected_rows;
    private String deferred_rows;
    private Integer input_rows;
    private Integer seen_rows;
    private Integer seen_rows_right;
    private Integer filtered_rows;
    private Integer returned_rows;
    private Integer empty_right_rows;
    private String index_key_optimization;
    private UUID scan_rs_id;
    private UUID sort_rs_id;
    private UUID stmt_id;
    private UUID timing_id;
    static final String TABLENAME_STRING = "SYSXPLAIN_RESULTSETS";
    private static final String[][] indexColumnNames;
    
    public XPLAINResultSetDescriptor() {
    }
    
    public XPLAINResultSetDescriptor(final UUID rs_id, final String op_identifier, final String op_details, final Integer no_opens, final Integer no_index_updates, final String lock_mode, final String lock_granularity, final UUID parent_rs_id, final Double est_row_count, final Double est_cost, final Integer affected_rows, final String deferred_rows, final Integer input_rows, final Integer seen_rows, final Integer seen_rows_right, final Integer filtered_rows, final Integer returned_rows, final Integer empty_right_rows, final String index_key_optimization, final UUID scan_rs_id, final UUID sort_rs_id, final UUID stmt_id, final UUID timing_id) {
        this.rs_id = rs_id;
        this.op_identifier = op_identifier;
        this.op_details = op_details;
        this.no_opens = no_opens;
        this.no_index_updates = no_index_updates;
        this.lock_granularity = lock_granularity;
        this.lock_mode = lock_mode;
        this.parent_rs_id = parent_rs_id;
        this.est_row_count = est_row_count;
        this.est_cost = est_cost;
        this.affected_rows = affected_rows;
        this.deferred_rows = deferred_rows;
        this.input_rows = input_rows;
        this.seen_rows = seen_rows;
        this.seen_rows_right = seen_rows_right;
        this.filtered_rows = filtered_rows;
        this.returned_rows = returned_rows;
        this.empty_right_rows = empty_right_rows;
        this.index_key_optimization = index_key_optimization;
        this.scan_rs_id = scan_rs_id;
        this.sort_rs_id = sort_rs_id;
        this.stmt_id = stmt_id;
        this.timing_id = timing_id;
    }
    
    public void setStatementParameters(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.rs_id.toString());
        preparedStatement.setString(2, this.op_identifier);
        preparedStatement.setString(3, this.op_details);
        if (this.no_opens != null) {
            preparedStatement.setInt(4, this.no_opens);
        }
        else {
            preparedStatement.setNull(4, 4);
        }
        if (this.no_index_updates != null) {
            preparedStatement.setInt(5, this.no_index_updates);
        }
        else {
            preparedStatement.setNull(5, 4);
        }
        preparedStatement.setString(6, this.lock_mode);
        preparedStatement.setString(7, this.lock_granularity);
        preparedStatement.setString(8, (this.parent_rs_id != null) ? this.parent_rs_id.toString() : null);
        if (this.est_row_count != null) {
            preparedStatement.setDouble(9, this.est_row_count);
        }
        else {
            preparedStatement.setNull(9, 8);
        }
        if (this.est_cost != null) {
            preparedStatement.setDouble(10, this.est_cost);
        }
        else {
            preparedStatement.setNull(10, 8);
        }
        if (this.affected_rows != null) {
            preparedStatement.setInt(11, this.affected_rows);
        }
        else {
            preparedStatement.setNull(11, 4);
        }
        preparedStatement.setString(12, this.deferred_rows);
        if (this.input_rows != null) {
            preparedStatement.setInt(13, this.input_rows);
        }
        else {
            preparedStatement.setNull(13, 4);
        }
        if (this.seen_rows != null) {
            preparedStatement.setInt(14, this.seen_rows);
        }
        else {
            preparedStatement.setNull(14, 4);
        }
        if (this.seen_rows_right != null) {
            preparedStatement.setInt(15, this.seen_rows_right);
        }
        else {
            preparedStatement.setNull(15, 4);
        }
        if (this.filtered_rows != null) {
            preparedStatement.setInt(16, this.filtered_rows);
        }
        else {
            preparedStatement.setNull(16, 4);
        }
        if (this.returned_rows != null) {
            preparedStatement.setInt(17, this.returned_rows);
        }
        else {
            preparedStatement.setNull(17, 4);
        }
        if (this.empty_right_rows != null) {
            preparedStatement.setInt(18, this.empty_right_rows);
        }
        else {
            preparedStatement.setNull(18, 4);
        }
        preparedStatement.setString(19, this.index_key_optimization);
        preparedStatement.setString(20, (this.scan_rs_id != null) ? this.scan_rs_id.toString() : null);
        preparedStatement.setString(21, (this.sort_rs_id != null) ? this.sort_rs_id.toString() : null);
        preparedStatement.setString(22, (this.stmt_id != null) ? this.stmt_id.toString() : null);
        preparedStatement.setString(23, (this.timing_id != null) ? this.timing_id.toString() : null);
    }
    
    public String getCatalogName() {
        return "SYSXPLAIN_RESULTSETS";
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("RS_ID", false), SystemColumnImpl.getColumn("OP_IDENTIFIER", 12, false, 32672), SystemColumnImpl.getColumn("OP_DETAILS", 12, true, 32672), SystemColumnImpl.getColumn("NO_OPENS", 4, true), SystemColumnImpl.getColumn("NO_INDEX_UPDATES", 4, true), SystemColumnImpl.getColumn("LOCK_MODE", 1, true, 2), SystemColumnImpl.getColumn("LOCK_GRANULARITY", 1, true, 1), SystemColumnImpl.getUUIDColumn("PARENT_RS_ID", true), SystemColumnImpl.getColumn("EST_ROW_COUNT", 8, true), SystemColumnImpl.getColumn("EST_COST", 8, true), SystemColumnImpl.getColumn("AFFECTED_ROWS", 4, true), SystemColumnImpl.getColumn("DEFERRED_ROWS", 1, true, 1), SystemColumnImpl.getColumn("INPUT_ROWS", 4, true), SystemColumnImpl.getColumn("SEEN_ROWS", 4, true), SystemColumnImpl.getColumn("SEEN_ROWS_RIGHT", 4, true), SystemColumnImpl.getColumn("FILTERED_ROWS", 4, true), SystemColumnImpl.getColumn("RETURNED_ROWS", 4, true), SystemColumnImpl.getColumn("EMPTY_RIGHT_ROWS", 4, true), SystemColumnImpl.getColumn("INDEX_KEY_OPT", 1, true, 1), SystemColumnImpl.getUUIDColumn("SCAN_RS_ID", true), SystemColumnImpl.getUUIDColumn("SORT_RS_ID", true), SystemColumnImpl.getUUIDColumn("STMT_ID", false), SystemColumnImpl.getUUIDColumn("TIMING_ID", true) };
    }
    
    static {
        indexColumnNames = new String[][] { { "RS_ID" } };
    }
}
