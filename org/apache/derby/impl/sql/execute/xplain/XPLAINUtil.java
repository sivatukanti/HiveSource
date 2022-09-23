// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.xplain;

import org.apache.derby.impl.sql.catalog.XPLAINSortPropsDescriptor;
import java.util.Properties;
import org.apache.derby.impl.sql.catalog.XPLAINScanPropsDescriptor;
import org.apache.derby.iapi.services.i18n.MessageService;

public class XPLAINUtil
{
    public static final String ISOLATION_READ_UNCOMMITED = "RU";
    public static final String ISOLATION_READ_COMMIT = "RC";
    public static final String ISOLATION_REPEAT_READ = "RR";
    public static final String ISOLATION_SERIALIZABLE = "SE";
    public static final String LOCK_MODE_EXCLUSIVE = "EX";
    public static final String LOCK_MODE_INSTANTENOUS_EXCLUSIVE = "IX";
    public static final String LOCK_MODE_SHARE = "SH";
    public static final String LOCK_MODE_INSTANTENOUS_SHARE = "IS";
    public static final String LOCK_GRANULARITY_TABLE = "T";
    public static final String LOCK_GRANULARITY_ROW = "R";
    public static final String OP_TABLESCAN = "TABLESCAN";
    public static final String OP_INDEXSCAN = "INDEXSCAN";
    public static final String OP_HASHSCAN = "HASHSCAN";
    public static final String OP_DISTINCTSCAN = "DISTINCTSCAN";
    public static final String OP_LASTINDEXKEYSCAN = "LASTINDEXKEYSCAN";
    public static final String OP_HASHTABLE = "HASHTABLE";
    public static final String OP_ROWIDSCAN = "ROWIDSCAN";
    public static final String OP_CONSTRAINTSCAN = "CONSTRAINTSCAN";
    public static final String OP_JOIN_NL = "NLJOIN";
    public static final String OP_JOIN_HASH = "HASHJOIN";
    public static final String OP_JOIN_NL_LO = "LONLJOIN";
    public static final String OP_JOIN_HASH_LO = "LOHASHJOIN";
    public static final String OP_UNION = "UNION";
    public static final String OP_SET = "SET";
    public static final String OP_SET_INTERSECT = "EXCEPT";
    public static final String OP_SET_EXCEPT = "INTERSECT";
    public static final String OP_INSERT = "INSERT";
    public static final String OP_UPDATE = "UPDATE";
    public static final String OP_DELETE = "DELETE";
    public static final String OP_CASCADE = "CASCADE";
    public static final String OP_VTI = "VTI";
    public static final String OP_BULK = "BULK";
    public static final String OP_DISTINCT = "DISTINCT";
    public static final String OP_NORMALIZE = "NORMALIZE";
    public static final String OP_ANY = "ANY";
    public static final String OP_SCROLL = "SCROLL";
    public static final String OP_MATERIALIZE = "MATERIALIZE";
    public static final String OP_ONCE = "ONCE";
    public static final String OP_VTI_RS = "VTI";
    public static final String OP_ROW = "ROW";
    public static final String OP_PROJECT = "PROJECTION";
    public static final String OP_FILTER = "FILTER";
    public static final String OP_AGGREGATE = "AGGREGATION";
    public static final String OP_PROJ_RESTRICT = "PROJECT-FILTER";
    public static final String OP_SORT = "SORT";
    public static final String OP_GROUP = "GROUPBY";
    public static final String OP_CURRENT_OF = "CURRENT-OF";
    public static final String OP_ROW_COUNT = "ROW-COUNT";
    public static final String OP_WINDOW = "WINDOW";
    public static final String SCAN_HEAP = "HEAP";
    public static final String SCAN_BTREE = "BTREE";
    public static final String SCAN_SORT = "SORT";
    public static final String SCAN_BITSET_ALL = "ALL";
    public static final String SELECT_STMT_TYPE = "S";
    public static final String SELECT_APPROXIMATE_STMT_TYPE = "SA";
    public static final String INSERT_STMT_TYPE = "I";
    public static final String UPDATE_STMT_TYPE = "U";
    public static final String DELETE_STMT_TYPE = "D";
    public static final String CALL_STMT_TYPE = "C";
    public static final String DDL_STMT_TYPE = "DDL";
    public static final String XPLAIN_ONLY = "O";
    public static final String XPLAIN_FULL = "F";
    public static final String SORT_EXTERNAL = "EX";
    public static final String SORT_INTERNAL = "IN";
    public static final String YES_CODE = "Y";
    public static final String NO_CODE = "N";
    
    public static String getYesNoCharFromBoolean(final boolean b) {
        if (b) {
            return "Y";
        }
        return "N";
    }
    
    public static String getHashKeyColumnNumberString(final int[] array) {
        String s;
        if (array.length == 1) {
            s = MessageService.getTextMessage("43X53.U") + " " + array[0];
        }
        else {
            String s2 = MessageService.getTextMessage("43X54.U") + " (" + array[0];
            for (int i = 1; i < array.length; ++i) {
                s2 = s2 + "," + array[i];
            }
            s = s2 + ")";
        }
        return s;
    }
    
    public static String getLockModeCode(String upperCase) {
        upperCase = upperCase.toUpperCase();
        if (upperCase.startsWith("EXCLUSIVE")) {
            return "EX";
        }
        if (upperCase.startsWith("SHARE")) {
            return "SH";
        }
        if (!upperCase.startsWith("INSTANTANEOUS")) {
            return null;
        }
        final String substring = upperCase.substring("INSTANTANEOUS".length() + 1, upperCase.length());
        if (substring.startsWith("EXCLUSIVE")) {
            return "IX";
        }
        if (substring.startsWith("SHARE")) {
            return "IS";
        }
        return null;
    }
    
    public static String getIsolationLevelCode(final String s) {
        if (s == null) {
            return null;
        }
        if (s.equals(MessageService.getTextMessage("42Z80.U"))) {
            return "SE";
        }
        if (s.equals(MessageService.getTextMessage("42Z92"))) {
            return "RR";
        }
        if (s.equals(MessageService.getTextMessage("42Z81.U"))) {
            return "RC";
        }
        if (s.equals(MessageService.getTextMessage("42Z9A"))) {
            return "RU";
        }
        return null;
    }
    
    public static String getLockGranularityCode(String upperCase) {
        upperCase = upperCase.toUpperCase();
        if (upperCase.endsWith("TABLE")) {
            return "T";
        }
        return "R";
    }
    
    public static String getStatementType(final String s) {
        String s2 = "";
        final String trim = s.toUpperCase().trim();
        if (trim.startsWith("CALL")) {
            s2 = "C";
        }
        else if (trim.startsWith("SELECT")) {
            if (trim.indexOf("~") > -1) {
                s2 = "SA";
            }
            else {
                s2 = "S";
            }
        }
        else if (trim.startsWith("DELETE")) {
            s2 = "D";
        }
        else if (trim.startsWith("INSERT")) {
            s2 = "I";
        }
        else if (trim.startsWith("UPDATE")) {
            s2 = "U";
        }
        else if (trim.startsWith("CREATE") || trim.startsWith("ALTER") || trim.startsWith("DROP")) {
            s2 = "DDL";
        }
        return s2;
    }
    
    public static XPLAINScanPropsDescriptor extractScanProps(final XPLAINScanPropsDescriptor xplainScanPropsDescriptor, final Properties properties) {
        String scan_type = "";
        final String property = properties.getProperty(MessageService.getTextMessage("XSAJ0.U"));
        if (property != null) {
            if (property.equalsIgnoreCase(MessageService.getTextMessage("XSAJG.U"))) {
                scan_type = "HEAP";
            }
            else if (property.equalsIgnoreCase(MessageService.getTextMessage("XSAJH.U"))) {
                scan_type = "SORT";
            }
            else if (property.equalsIgnoreCase(MessageService.getTextMessage("XSAJF.U"))) {
                scan_type = "BTREE";
            }
        }
        else {
            scan_type = null;
        }
        xplainScanPropsDescriptor.setScan_type(scan_type);
        final String property2 = properties.getProperty(MessageService.getTextMessage("XSAJ1.U"));
        if (property2 != null) {
            xplainScanPropsDescriptor.setNo_visited_pages(new Integer(property2));
        }
        final String property3 = properties.getProperty(MessageService.getTextMessage("XSAJ2.U"));
        if (property3 != null) {
            xplainScanPropsDescriptor.setNo_visited_rows(new Integer(property3));
        }
        final String property4 = properties.getProperty(MessageService.getTextMessage("XSAJ4.U"));
        if (property4 != null) {
            xplainScanPropsDescriptor.setNo_qualified_rows(new Integer(property4));
        }
        final String property5 = properties.getProperty(MessageService.getTextMessage("XSAJ5.U"));
        if (property5 != null) {
            xplainScanPropsDescriptor.setNo_fetched_columns(new Integer(property5));
        }
        final String property6 = properties.getProperty(MessageService.getTextMessage("XSAJ3.U"));
        if (property6 != null) {
            xplainScanPropsDescriptor.setNo_visited_deleted_rows(new Integer(property6));
        }
        final String property7 = properties.getProperty(MessageService.getTextMessage("XSAJ7.U"));
        if (property7 != null) {
            xplainScanPropsDescriptor.setBtree_height(new Integer(property7));
        }
        final String property8 = properties.getProperty(MessageService.getTextMessage("XSAJ6.U"));
        if (property8 != null) {
            if (property8.equalsIgnoreCase(MessageService.getTextMessage("XSAJE.U"))) {
                xplainScanPropsDescriptor.setBitset_of_fetched_columns("ALL");
            }
            else {
                xplainScanPropsDescriptor.setBitset_of_fetched_columns(property8);
            }
        }
        return xplainScanPropsDescriptor;
    }
    
    public static XPLAINSortPropsDescriptor extractSortProps(final XPLAINSortPropsDescriptor xplainSortPropsDescriptor, final Properties properties) {
        String sort_type = null;
        final String property = properties.getProperty(MessageService.getTextMessage("XSAJ8.U"));
        if (property != null) {
            if (property.equalsIgnoreCase(MessageService.getTextMessage("XSAJI.U"))) {
                sort_type = "EX";
            }
            else {
                sort_type = "IN";
            }
        }
        xplainSortPropsDescriptor.setSort_type(sort_type);
        final String property2 = properties.getProperty(MessageService.getTextMessage("XSAJA.U"));
        if (property2 != null) {
            xplainSortPropsDescriptor.setNo_input_rows(new Integer(property2));
        }
        final String property3 = properties.getProperty(MessageService.getTextMessage("XSAJB.U"));
        if (property3 != null) {
            xplainSortPropsDescriptor.setNo_output_rows(new Integer(property3));
        }
        if (sort_type == "EX") {
            final String property4 = properties.getProperty(MessageService.getTextMessage("XSAJC.U"));
            if (property4 != null) {
                xplainSortPropsDescriptor.setNo_merge_runs(new Integer(property4));
            }
            final String property5 = properties.getProperty(MessageService.getTextMessage("XSAJD.U"));
            if (property5 != null) {
                xplainSortPropsDescriptor.setMerge_run_details(property5);
            }
        }
        return xplainSortPropsDescriptor;
    }
    
    public static Long getAVGNextTime(final long n, final long n2) {
        if (n2 == 0L) {
            return null;
        }
        if (n == 0L) {
            return new Long(0L);
        }
        return new Long(n / n2);
    }
}
