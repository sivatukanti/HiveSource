// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.derby.catalog.UUID;

public class XPLAINSortPropsDescriptor extends XPLAINTableDescriptor
{
    private UUID sort_rs_id;
    private String sort_type;
    private Integer no_input_rows;
    private Integer no_output_rows;
    private Integer no_merge_runs;
    private String merge_run_details;
    private String eliminate_dups;
    private String in_sort_order;
    private String distinct_aggregate;
    static final String TABLENAME_STRING = "SYSXPLAIN_SORT_PROPS";
    private static final String[][] indexColumnNames;
    
    public XPLAINSortPropsDescriptor() {
    }
    
    public XPLAINSortPropsDescriptor(final UUID sort_rs_id, final String sort_type, final Integer no_input_rows, final Integer no_output_rows, final Integer no_merge_runs, final String merge_run_details, final String eliminate_dups, final String in_sort_order, final String distinct_aggregate) {
        this.sort_rs_id = sort_rs_id;
        this.sort_type = sort_type;
        this.no_input_rows = no_input_rows;
        this.no_output_rows = no_output_rows;
        this.no_merge_runs = no_merge_runs;
        this.merge_run_details = merge_run_details;
        this.eliminate_dups = eliminate_dups;
        this.in_sort_order = in_sort_order;
        this.distinct_aggregate = distinct_aggregate;
    }
    
    public void setStatementParameters(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.sort_rs_id.toString());
        preparedStatement.setString(2, this.sort_type);
        if (this.no_input_rows != null) {
            preparedStatement.setInt(3, this.no_input_rows);
        }
        else {
            preparedStatement.setNull(3, 4);
        }
        if (this.no_output_rows != null) {
            preparedStatement.setInt(4, this.no_output_rows);
        }
        else {
            preparedStatement.setNull(4, 4);
        }
        if (this.no_merge_runs != null) {
            preparedStatement.setInt(5, this.no_merge_runs);
        }
        else {
            preparedStatement.setNull(5, 4);
        }
        preparedStatement.setString(6, this.merge_run_details);
        preparedStatement.setString(7, this.eliminate_dups);
        preparedStatement.setString(8, this.in_sort_order);
        preparedStatement.setString(9, this.distinct_aggregate);
    }
    
    public void setSort_type(final String sort_type) {
        this.sort_type = sort_type;
    }
    
    public void setNo_input_rows(final Integer no_input_rows) {
        this.no_input_rows = no_input_rows;
    }
    
    public void setNo_output_rows(final Integer no_output_rows) {
        this.no_output_rows = no_output_rows;
    }
    
    public void setNo_merge_runs(final Integer no_merge_runs) {
        this.no_merge_runs = no_merge_runs;
    }
    
    public void setMerge_run_details(final String merge_run_details) {
        this.merge_run_details = merge_run_details;
    }
    
    public String getCatalogName() {
        return "SYSXPLAIN_SORT_PROPS";
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("SORT_RS_ID", false), SystemColumnImpl.getColumn("SORT_TYPE", 1, true, 2), SystemColumnImpl.getColumn("NO_INPUT_ROWS", 4, true), SystemColumnImpl.getColumn("NO_OUTPUT_ROWS", 4, true), SystemColumnImpl.getColumn("NO_MERGE_RUNS", 4, true), SystemColumnImpl.getColumn("MERGE_RUN_DETAILS", 12, true, 32672), SystemColumnImpl.getColumn("ELIMINATE_DUPLICATES", 1, true, 1), SystemColumnImpl.getColumn("IN_SORT_ORDER", 1, true, 1), SystemColumnImpl.getColumn("DISTINCT_AGGREGATE", 1, true, 1) };
    }
    
    static {
        indexColumnNames = new String[][] { { "SORT_RS_ID" } };
    }
}
