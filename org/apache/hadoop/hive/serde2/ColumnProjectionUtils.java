// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.hadoop.util.StringUtils;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import com.google.common.base.Joiner;

public final class ColumnProjectionUtils
{
    public static final String READ_COLUMN_IDS_CONF_STR = "hive.io.file.readcolumn.ids";
    public static final String READ_ALL_COLUMNS = "hive.io.file.read.all.columns";
    public static final String READ_COLUMN_NAMES_CONF_STR = "hive.io.file.readcolumn.names";
    private static final String READ_COLUMN_IDS_CONF_STR_DEFAULT = "";
    private static final boolean READ_ALL_COLUMNS_DEFAULT = true;
    private static final Joiner CSV_JOINER;
    
    @Deprecated
    public static void setFullyReadColumns(final Configuration conf) {
        setReadAllColumns(conf);
    }
    
    @Deprecated
    public static void setReadColumnIDs(final Configuration conf, final List<Integer> ids) {
        setReadColumnIDConf(conf, "");
        appendReadColumns(conf, ids);
    }
    
    @Deprecated
    public static void appendReadColumnIDs(final Configuration conf, final List<Integer> ids) {
        appendReadColumns(conf, ids);
    }
    
    public static void setReadAllColumns(final Configuration conf) {
        conf.setBoolean("hive.io.file.read.all.columns", true);
        setReadColumnIDConf(conf, "");
    }
    
    public static boolean isReadAllColumns(final Configuration conf) {
        return conf.getBoolean("hive.io.file.read.all.columns", true);
    }
    
    public static void appendReadColumns(final Configuration conf, final List<Integer> ids) {
        final String id = toReadColumnIDString(ids);
        final String old = conf.get("hive.io.file.readcolumn.ids", null);
        String newConfStr = id;
        if (old != null) {
            newConfStr = newConfStr + "," + old;
        }
        setReadColumnIDConf(conf, newConfStr);
        conf.setBoolean("hive.io.file.read.all.columns", false);
    }
    
    public static void appendReadColumns(final Configuration conf, final List<Integer> ids, final List<String> names) {
        appendReadColumns(conf, ids);
        appendReadColumnNames(conf, names);
    }
    
    public static void appendReadColumns(final StringBuilder readColumnsBuffer, final StringBuilder readColumnNamesBuffer, final List<Integer> ids, final List<String> names) {
        ColumnProjectionUtils.CSV_JOINER.appendTo(readColumnsBuffer, (Iterable<?>)ids);
        ColumnProjectionUtils.CSV_JOINER.appendTo(readColumnNamesBuffer, (Iterable<?>)names);
    }
    
    public static List<Integer> getReadColumnIDs(final Configuration conf) {
        final String skips = conf.get("hive.io.file.readcolumn.ids", "");
        final String[] list = StringUtils.split(skips);
        final List<Integer> result = new ArrayList<Integer>(list.length);
        for (final String element : list) {
            final Integer toAdd = Integer.parseInt(element);
            if (!result.contains(toAdd)) {
                result.add(toAdd);
            }
        }
        return result;
    }
    
    public static List<String> getReadColumnNames(final Configuration conf) {
        final String colNames = conf.get("hive.io.file.readcolumn.names", "");
        if (colNames != null && !colNames.isEmpty()) {
            return Arrays.asList(colNames.split(","));
        }
        return (List<String>)Lists.newArrayList();
    }
    
    private static void setReadColumnIDConf(final Configuration conf, final String id) {
        if (id.trim().isEmpty()) {
            conf.set("hive.io.file.readcolumn.ids", "");
        }
        else {
            conf.set("hive.io.file.readcolumn.ids", id);
        }
    }
    
    private static void appendReadColumnNames(final Configuration conf, final List<String> cols) {
        final String old = conf.get("hive.io.file.readcolumn.names", "");
        final StringBuilder result = new StringBuilder(old);
        boolean first = old.isEmpty();
        for (final String col : cols) {
            if (first) {
                first = false;
            }
            else {
                result.append(',');
            }
            result.append(col);
        }
        conf.set("hive.io.file.readcolumn.names", result.toString());
    }
    
    private static String toReadColumnIDString(final List<Integer> ids) {
        String id = "";
        for (int i = 0; i < ids.size(); ++i) {
            if (i == 0) {
                id += ids.get(i);
            }
            else {
                id = id + "," + ids.get(i);
            }
        }
        return id;
    }
    
    private ColumnProjectionUtils() {
    }
    
    static {
        CSV_JOINER = Joiner.on(",").skipNulls();
    }
}
