// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.derby.catalog.UUID;

public class XPLAINScanPropsDescriptor extends XPLAINTableDescriptor
{
    private UUID scan_rs_id;
    private String scan_object_name;
    private String scan_object_type;
    private String scan_type;
    private String isolation_level;
    private Integer no_visited_pages;
    private Integer no_visited_rows;
    private Integer no_qualified_rows;
    private Integer no_visited_deleted_rows;
    private Integer no_fetched_columns;
    private String bitset_of_fetched_columns;
    private Integer btree_height;
    private Integer fetch_size;
    private String start_position;
    private String stop_position;
    private String scan_qualifiers;
    private String next_qualifiers;
    private String hash_key_column_numbers;
    private Integer hash_table_size;
    static final String TABLENAME_STRING = "SYSXPLAIN_SCAN_PROPS";
    private static final String[][] indexColumnNames;
    
    public XPLAINScanPropsDescriptor() {
    }
    
    public XPLAINScanPropsDescriptor(final UUID scan_rs_id, final String scan_object_name, final String scan_object_type, final String scan_type, final String isolation_level, final Integer no_visited_pages, final Integer no_visited_rows, final Integer no_qualified_rows, final Integer no_visited_deleted_rows, final Integer no_fetched_columns, final String bitset_of_fetched_columns, final Integer btree_height, final Integer fetch_size, final String start_position, final String stop_position, final String scan_qualifiers, final String next_qualifiers, final String hash_key_column_numbers, final Integer hash_table_size) {
        this.scan_rs_id = scan_rs_id;
        this.scan_object_name = scan_object_name;
        this.scan_object_type = scan_object_type;
        this.scan_type = scan_type;
        this.isolation_level = isolation_level;
        this.no_visited_pages = no_visited_pages;
        this.no_visited_rows = no_visited_rows;
        this.no_qualified_rows = no_qualified_rows;
        this.no_visited_deleted_rows = no_visited_deleted_rows;
        this.no_fetched_columns = no_fetched_columns;
        this.bitset_of_fetched_columns = bitset_of_fetched_columns;
        this.btree_height = btree_height;
        this.fetch_size = fetch_size;
        this.start_position = start_position;
        this.stop_position = stop_position;
        this.scan_qualifiers = scan_qualifiers;
        this.next_qualifiers = next_qualifiers;
        this.hash_key_column_numbers = hash_key_column_numbers;
        this.hash_table_size = hash_table_size;
    }
    
    public void setStatementParameters(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.scan_rs_id.toString());
        preparedStatement.setString(2, this.scan_object_name);
        preparedStatement.setString(3, this.scan_object_type);
        preparedStatement.setString(4, this.scan_type);
        preparedStatement.setString(5, this.isolation_level);
        if (this.no_visited_pages != null) {
            preparedStatement.setInt(6, this.no_visited_pages);
        }
        else {
            preparedStatement.setNull(6, 4);
        }
        if (this.no_visited_rows != null) {
            preparedStatement.setInt(7, this.no_visited_rows);
        }
        else {
            preparedStatement.setNull(7, 4);
        }
        if (this.no_qualified_rows != null) {
            preparedStatement.setInt(8, this.no_qualified_rows);
        }
        else {
            preparedStatement.setNull(8, 4);
        }
        if (this.no_visited_deleted_rows != null) {
            preparedStatement.setInt(9, this.no_visited_deleted_rows);
        }
        else {
            preparedStatement.setNull(9, 4);
        }
        if (this.no_fetched_columns != null) {
            preparedStatement.setInt(10, this.no_fetched_columns);
        }
        else {
            preparedStatement.setNull(10, 4);
        }
        preparedStatement.setString(11, this.bitset_of_fetched_columns);
        if (this.btree_height != null) {
            preparedStatement.setInt(12, this.btree_height);
        }
        else {
            preparedStatement.setNull(12, 4);
        }
        if (this.fetch_size != null) {
            preparedStatement.setInt(13, this.fetch_size);
        }
        else {
            preparedStatement.setNull(13, 4);
        }
        preparedStatement.setString(14, this.start_position);
        preparedStatement.setString(15, this.stop_position);
        preparedStatement.setString(16, this.scan_qualifiers);
        preparedStatement.setString(17, this.next_qualifiers);
        preparedStatement.setString(18, this.hash_key_column_numbers);
        if (this.hash_table_size != null) {
            preparedStatement.setInt(19, this.hash_table_size);
        }
        else {
            preparedStatement.setNull(19, 4);
        }
    }
    
    public void setScan_type(final String scan_type) {
        this.scan_type = scan_type;
    }
    
    public void setNo_visited_pages(final Integer no_visited_pages) {
        this.no_visited_pages = no_visited_pages;
    }
    
    public void setNo_visited_rows(final Integer no_visited_rows) {
        this.no_visited_rows = no_visited_rows;
    }
    
    public void setNo_qualified_rows(final Integer no_qualified_rows) {
        this.no_qualified_rows = no_qualified_rows;
    }
    
    public void setNo_fetched_columns(final Integer no_fetched_columns) {
        this.no_fetched_columns = no_fetched_columns;
    }
    
    public void setNo_visited_deleted_rows(final Integer no_visited_deleted_rows) {
        this.no_visited_deleted_rows = no_visited_deleted_rows;
    }
    
    public void setBtree_height(final Integer btree_height) {
        this.btree_height = btree_height;
    }
    
    public void setBitset_of_fetched_columns(final String bitset_of_fetched_columns) {
        this.bitset_of_fetched_columns = bitset_of_fetched_columns;
    }
    
    public String getCatalogName() {
        return "SYSXPLAIN_SCAN_PROPS";
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("SCAN_RS_ID", false), SystemColumnImpl.getIdentifierColumn("SCAN_OBJECT_NAME", false), SystemColumnImpl.getIndicatorColumn("SCAN_OBJECT_TYPE"), SystemColumnImpl.getColumn("SCAN_TYPE", 1, false, 8), SystemColumnImpl.getColumn("ISOLATION_LEVEL", 1, true, 3), SystemColumnImpl.getColumn("NO_VISITED_PAGES", 4, true), SystemColumnImpl.getColumn("NO_VISITED_ROWS", 4, true), SystemColumnImpl.getColumn("NO_QUALIFIED_ROWS", 4, true), SystemColumnImpl.getColumn("NO_VISITED_DELETED_ROWS", 4, true), SystemColumnImpl.getColumn("NO_FETCHED_COLUMNS", 4, true), SystemColumnImpl.getColumn("BITSET_OF_FETCHED_COLUMNS", 12, true, 32672), SystemColumnImpl.getColumn("BTREE_HEIGHT", 4, true), SystemColumnImpl.getColumn("FETCH_SIZE", 4, true), SystemColumnImpl.getColumn("START_POSITION", 12, true, 32672), SystemColumnImpl.getColumn("STOP_POSITION", 12, true, 32672), SystemColumnImpl.getColumn("SCAN_QUALIFIERS", 12, true, 32672), SystemColumnImpl.getColumn("NEXT_QUALIFIERS", 12, true, 32672), SystemColumnImpl.getColumn("HASH_KEY_COLUMN_NUMBERS", 12, true, 32672), SystemColumnImpl.getColumn("HASH_TABLE_SIZE", 4, true) };
    }
    
    static {
        indexColumnNames = new String[][] { { "SCAN_RS_ID" } };
    }
}
