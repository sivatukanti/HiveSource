// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.math.BigInteger;
import org.apache.hadoop.hive.metastore.api.Decimal;
import java.nio.ByteBuffer;
import java.math.BigDecimal;
import org.apache.hadoop.hive.metastore.model.MPartition;
import org.apache.hadoop.hive.metastore.api.Date;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsData;
import org.apache.hadoop.hive.metastore.model.MPartitionColumnStatistics;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.DateColumnStatsData;
import org.apache.hadoop.hive.metastore.api.BinaryColumnStatsData;
import org.apache.hadoop.hive.metastore.api.StringColumnStatsData;
import org.apache.hadoop.hive.metastore.api.DecimalColumnStatsData;
import org.apache.hadoop.hive.metastore.api.DoubleColumnStatsData;
import org.apache.hadoop.hive.metastore.api.LongColumnStatsData;
import org.apache.hadoop.hive.metastore.api.BooleanColumnStatsData;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.model.MTableColumnStatistics;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsDesc;
import org.apache.hadoop.hive.metastore.model.MTable;

public class StatObjectConverter
{
    public static MTableColumnStatistics convertToMTableColumnStatistics(final MTable table, final ColumnStatisticsDesc statsDesc, final ColumnStatisticsObj statsObj) throws NoSuchObjectException, MetaException, InvalidObjectException {
        if (statsObj == null || statsDesc == null) {
            throw new InvalidObjectException("Invalid column stats object");
        }
        final MTableColumnStatistics mColStats = new MTableColumnStatistics();
        mColStats.setTable(table);
        mColStats.setDbName(statsDesc.getDbName());
        mColStats.setTableName(statsDesc.getTableName());
        mColStats.setLastAnalyzed(statsDesc.getLastAnalyzed());
        mColStats.setColName(statsObj.getColName());
        mColStats.setColType(statsObj.getColType());
        if (statsObj.getStatsData().isSetBooleanStats()) {
            final BooleanColumnStatsData boolStats = statsObj.getStatsData().getBooleanStats();
            mColStats.setBooleanStats(boolStats.isSetNumTrues() ? Long.valueOf(boolStats.getNumTrues()) : null, boolStats.isSetNumFalses() ? Long.valueOf(boolStats.getNumFalses()) : null, boolStats.isSetNumNulls() ? Long.valueOf(boolStats.getNumNulls()) : null);
        }
        else if (statsObj.getStatsData().isSetLongStats()) {
            final LongColumnStatsData longStats = statsObj.getStatsData().getLongStats();
            mColStats.setLongStats(longStats.isSetNumNulls() ? Long.valueOf(longStats.getNumNulls()) : null, longStats.isSetNumDVs() ? Long.valueOf(longStats.getNumDVs()) : null, longStats.isSetLowValue() ? Long.valueOf(longStats.getLowValue()) : null, longStats.isSetHighValue() ? Long.valueOf(longStats.getHighValue()) : null);
        }
        else if (statsObj.getStatsData().isSetDoubleStats()) {
            final DoubleColumnStatsData doubleStats = statsObj.getStatsData().getDoubleStats();
            mColStats.setDoubleStats(doubleStats.isSetNumNulls() ? Long.valueOf(doubleStats.getNumNulls()) : null, doubleStats.isSetNumDVs() ? Long.valueOf(doubleStats.getNumDVs()) : null, doubleStats.isSetLowValue() ? Double.valueOf(doubleStats.getLowValue()) : null, doubleStats.isSetHighValue() ? Double.valueOf(doubleStats.getHighValue()) : null);
        }
        else if (statsObj.getStatsData().isSetDecimalStats()) {
            final DecimalColumnStatsData decimalStats = statsObj.getStatsData().getDecimalStats();
            final String low = decimalStats.isSetLowValue() ? createJdoDecimalString(decimalStats.getLowValue()) : null;
            final String high = decimalStats.isSetHighValue() ? createJdoDecimalString(decimalStats.getHighValue()) : null;
            mColStats.setDecimalStats(decimalStats.isSetNumNulls() ? Long.valueOf(decimalStats.getNumNulls()) : null, decimalStats.isSetNumDVs() ? Long.valueOf(decimalStats.getNumDVs()) : null, low, high);
        }
        else if (statsObj.getStatsData().isSetStringStats()) {
            final StringColumnStatsData stringStats = statsObj.getStatsData().getStringStats();
            mColStats.setStringStats(stringStats.isSetNumNulls() ? Long.valueOf(stringStats.getNumNulls()) : null, stringStats.isSetNumDVs() ? Long.valueOf(stringStats.getNumDVs()) : null, stringStats.isSetMaxColLen() ? Long.valueOf(stringStats.getMaxColLen()) : null, stringStats.isSetAvgColLen() ? Double.valueOf(stringStats.getAvgColLen()) : null);
        }
        else if (statsObj.getStatsData().isSetBinaryStats()) {
            final BinaryColumnStatsData binaryStats = statsObj.getStatsData().getBinaryStats();
            mColStats.setBinaryStats(binaryStats.isSetNumNulls() ? Long.valueOf(binaryStats.getNumNulls()) : null, binaryStats.isSetMaxColLen() ? Long.valueOf(binaryStats.getMaxColLen()) : null, binaryStats.isSetAvgColLen() ? Double.valueOf(binaryStats.getAvgColLen()) : null);
        }
        else if (statsObj.getStatsData().isSetDateStats()) {
            final DateColumnStatsData dateStats = statsObj.getStatsData().getDateStats();
            mColStats.setDateStats(dateStats.isSetNumNulls() ? Long.valueOf(dateStats.getNumNulls()) : null, dateStats.isSetNumDVs() ? Long.valueOf(dateStats.getNumDVs()) : null, dateStats.isSetLowValue() ? Long.valueOf(dateStats.getLowValue().getDaysSinceEpoch()) : null, dateStats.isSetHighValue() ? Long.valueOf(dateStats.getHighValue().getDaysSinceEpoch()) : null);
        }
        return mColStats;
    }
    
    public static void setFieldsIntoOldStats(final MTableColumnStatistics mStatsObj, final MTableColumnStatistics oldStatsObj) {
        if (mStatsObj.getAvgColLen() != null) {
            oldStatsObj.setAvgColLen(mStatsObj.getAvgColLen());
        }
        if (mStatsObj.getLongHighValue() != null) {
            oldStatsObj.setLongHighValue(mStatsObj.getLongHighValue());
        }
        if (mStatsObj.getLongLowValue() != null) {
            oldStatsObj.setLongLowValue(mStatsObj.getLongLowValue());
        }
        if (mStatsObj.getDoubleLowValue() != null) {
            oldStatsObj.setDoubleLowValue(mStatsObj.getDoubleLowValue());
        }
        if (mStatsObj.getDoubleHighValue() != null) {
            oldStatsObj.setDoubleHighValue(mStatsObj.getDoubleHighValue());
        }
        if (mStatsObj.getDecimalLowValue() != null) {
            oldStatsObj.setDecimalLowValue(mStatsObj.getDecimalLowValue());
        }
        if (mStatsObj.getDecimalHighValue() != null) {
            oldStatsObj.setDecimalHighValue(mStatsObj.getDecimalHighValue());
        }
        if (mStatsObj.getMaxColLen() != null) {
            oldStatsObj.setMaxColLen(mStatsObj.getMaxColLen());
        }
        if (mStatsObj.getNumDVs() != null) {
            oldStatsObj.setNumDVs(mStatsObj.getNumDVs());
        }
        if (mStatsObj.getNumFalses() != null) {
            oldStatsObj.setNumFalses(mStatsObj.getNumFalses());
        }
        if (mStatsObj.getNumTrues() != null) {
            oldStatsObj.setNumTrues(mStatsObj.getNumTrues());
        }
        if (mStatsObj.getNumNulls() != null) {
            oldStatsObj.setNumNulls(mStatsObj.getNumNulls());
        }
        oldStatsObj.setLastAnalyzed(mStatsObj.getLastAnalyzed());
    }
    
    public static void setFieldsIntoOldStats(final MPartitionColumnStatistics mStatsObj, final MPartitionColumnStatistics oldStatsObj) {
        if (mStatsObj.getAvgColLen() != null) {
            oldStatsObj.setAvgColLen(mStatsObj.getAvgColLen());
        }
        if (mStatsObj.getLongHighValue() != null) {
            oldStatsObj.setLongHighValue(mStatsObj.getLongHighValue());
        }
        if (mStatsObj.getDoubleHighValue() != null) {
            oldStatsObj.setDoubleHighValue(mStatsObj.getDoubleHighValue());
        }
        oldStatsObj.setLastAnalyzed(mStatsObj.getLastAnalyzed());
        if (mStatsObj.getLongLowValue() != null) {
            oldStatsObj.setLongLowValue(mStatsObj.getLongLowValue());
        }
        if (mStatsObj.getDoubleLowValue() != null) {
            oldStatsObj.setDoubleLowValue(mStatsObj.getDoubleLowValue());
        }
        if (mStatsObj.getDecimalLowValue() != null) {
            oldStatsObj.setDecimalLowValue(mStatsObj.getDecimalLowValue());
        }
        if (mStatsObj.getDecimalHighValue() != null) {
            oldStatsObj.setDecimalHighValue(mStatsObj.getDecimalHighValue());
        }
        if (mStatsObj.getMaxColLen() != null) {
            oldStatsObj.setMaxColLen(mStatsObj.getMaxColLen());
        }
        if (mStatsObj.getNumDVs() != null) {
            oldStatsObj.setNumDVs(mStatsObj.getNumDVs());
        }
        if (mStatsObj.getNumFalses() != null) {
            oldStatsObj.setNumFalses(mStatsObj.getNumFalses());
        }
        if (mStatsObj.getNumTrues() != null) {
            oldStatsObj.setNumTrues(mStatsObj.getNumTrues());
        }
        if (mStatsObj.getNumNulls() != null) {
            oldStatsObj.setNumNulls(mStatsObj.getNumNulls());
        }
    }
    
    public static ColumnStatisticsObj getTableColumnStatisticsObj(final MTableColumnStatistics mStatsObj) {
        final ColumnStatisticsObj statsObj = new ColumnStatisticsObj();
        statsObj.setColType(mStatsObj.getColType());
        statsObj.setColName(mStatsObj.getColName());
        final String colType = mStatsObj.getColType().toLowerCase();
        final ColumnStatisticsData colStatsData = new ColumnStatisticsData();
        if (colType.equals("boolean")) {
            final BooleanColumnStatsData boolStats = new BooleanColumnStatsData();
            boolStats.setNumFalses(mStatsObj.getNumFalses());
            boolStats.setNumTrues(mStatsObj.getNumTrues());
            boolStats.setNumNulls(mStatsObj.getNumNulls());
            colStatsData.setBooleanStats(boolStats);
        }
        else if (colType.equals("string") || colType.startsWith("varchar") || colType.startsWith("char")) {
            final StringColumnStatsData stringStats = new StringColumnStatsData();
            stringStats.setNumNulls(mStatsObj.getNumNulls());
            stringStats.setAvgColLen(mStatsObj.getAvgColLen());
            stringStats.setMaxColLen(mStatsObj.getMaxColLen());
            stringStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setStringStats(stringStats);
        }
        else if (colType.equals("binary")) {
            final BinaryColumnStatsData binaryStats = new BinaryColumnStatsData();
            binaryStats.setNumNulls(mStatsObj.getNumNulls());
            binaryStats.setAvgColLen(mStatsObj.getAvgColLen());
            binaryStats.setMaxColLen(mStatsObj.getMaxColLen());
            colStatsData.setBinaryStats(binaryStats);
        }
        else if (colType.equals("bigint") || colType.equals("int") || colType.equals("smallint") || colType.equals("tinyint") || colType.equals("timestamp")) {
            final LongColumnStatsData longStats = new LongColumnStatsData();
            longStats.setNumNulls(mStatsObj.getNumNulls());
            final Long longHighValue = mStatsObj.getLongHighValue();
            if (longHighValue != null) {
                longStats.setHighValue(longHighValue);
            }
            final Long longLowValue = mStatsObj.getLongLowValue();
            if (longLowValue != null) {
                longStats.setLowValue(longLowValue);
            }
            longStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setLongStats(longStats);
        }
        else if (colType.equals("double") || colType.equals("float")) {
            final DoubleColumnStatsData doubleStats = new DoubleColumnStatsData();
            doubleStats.setNumNulls(mStatsObj.getNumNulls());
            final Double doubleHighValue = mStatsObj.getDoubleHighValue();
            if (doubleHighValue != null) {
                doubleStats.setHighValue(doubleHighValue);
            }
            final Double doubleLowValue = mStatsObj.getDoubleLowValue();
            if (doubleLowValue != null) {
                doubleStats.setLowValue(doubleLowValue);
            }
            doubleStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setDoubleStats(doubleStats);
        }
        else if (colType.startsWith("decimal")) {
            final DecimalColumnStatsData decimalStats = new DecimalColumnStatsData();
            decimalStats.setNumNulls(mStatsObj.getNumNulls());
            final String decimalHighValue = mStatsObj.getDecimalHighValue();
            if (decimalHighValue != null) {
                decimalStats.setHighValue(createThriftDecimal(decimalHighValue));
            }
            final String decimalLowValue = mStatsObj.getDecimalLowValue();
            if (decimalLowValue != null) {
                decimalStats.setLowValue(createThriftDecimal(decimalLowValue));
            }
            decimalStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setDecimalStats(decimalStats);
        }
        else if (colType.equals("date")) {
            final DateColumnStatsData dateStats = new DateColumnStatsData();
            dateStats.setNumNulls(mStatsObj.getNumNulls());
            final Long highValue = mStatsObj.getLongHighValue();
            if (highValue != null) {
                dateStats.setHighValue(new Date(highValue));
            }
            final Long lowValue = mStatsObj.getLongLowValue();
            if (lowValue != null) {
                dateStats.setLowValue(new Date(lowValue));
            }
            dateStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setDateStats(dateStats);
        }
        statsObj.setStatsData(colStatsData);
        return statsObj;
    }
    
    public static ColumnStatisticsDesc getTableColumnStatisticsDesc(final MTableColumnStatistics mStatsObj) {
        final ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
        statsDesc.setIsTblLevel(true);
        statsDesc.setDbName(mStatsObj.getDbName());
        statsDesc.setTableName(mStatsObj.getTableName());
        statsDesc.setLastAnalyzed(mStatsObj.getLastAnalyzed());
        return statsDesc;
    }
    
    public static MPartitionColumnStatistics convertToMPartitionColumnStatistics(final MPartition partition, final ColumnStatisticsDesc statsDesc, final ColumnStatisticsObj statsObj) throws MetaException, NoSuchObjectException {
        if (statsDesc == null || statsObj == null) {
            return null;
        }
        final MPartitionColumnStatistics mColStats = new MPartitionColumnStatistics();
        mColStats.setPartition(partition);
        mColStats.setDbName(statsDesc.getDbName());
        mColStats.setTableName(statsDesc.getTableName());
        mColStats.setPartitionName(statsDesc.getPartName());
        mColStats.setLastAnalyzed(statsDesc.getLastAnalyzed());
        mColStats.setColName(statsObj.getColName());
        mColStats.setColType(statsObj.getColType());
        if (statsObj.getStatsData().isSetBooleanStats()) {
            final BooleanColumnStatsData boolStats = statsObj.getStatsData().getBooleanStats();
            mColStats.setBooleanStats(boolStats.isSetNumTrues() ? Long.valueOf(boolStats.getNumTrues()) : null, boolStats.isSetNumFalses() ? Long.valueOf(boolStats.getNumFalses()) : null, boolStats.isSetNumNulls() ? Long.valueOf(boolStats.getNumNulls()) : null);
        }
        else if (statsObj.getStatsData().isSetLongStats()) {
            final LongColumnStatsData longStats = statsObj.getStatsData().getLongStats();
            mColStats.setLongStats(longStats.isSetNumNulls() ? Long.valueOf(longStats.getNumNulls()) : null, longStats.isSetNumDVs() ? Long.valueOf(longStats.getNumDVs()) : null, longStats.isSetLowValue() ? Long.valueOf(longStats.getLowValue()) : null, longStats.isSetHighValue() ? Long.valueOf(longStats.getHighValue()) : null);
        }
        else if (statsObj.getStatsData().isSetDoubleStats()) {
            final DoubleColumnStatsData doubleStats = statsObj.getStatsData().getDoubleStats();
            mColStats.setDoubleStats(doubleStats.isSetNumNulls() ? Long.valueOf(doubleStats.getNumNulls()) : null, doubleStats.isSetNumDVs() ? Long.valueOf(doubleStats.getNumDVs()) : null, doubleStats.isSetLowValue() ? Double.valueOf(doubleStats.getLowValue()) : null, doubleStats.isSetHighValue() ? Double.valueOf(doubleStats.getHighValue()) : null);
        }
        else if (statsObj.getStatsData().isSetDecimalStats()) {
            final DecimalColumnStatsData decimalStats = statsObj.getStatsData().getDecimalStats();
            final String low = decimalStats.isSetLowValue() ? createJdoDecimalString(decimalStats.getLowValue()) : null;
            final String high = decimalStats.isSetHighValue() ? createJdoDecimalString(decimalStats.getHighValue()) : null;
            mColStats.setDecimalStats(decimalStats.isSetNumNulls() ? Long.valueOf(decimalStats.getNumNulls()) : null, decimalStats.isSetNumDVs() ? Long.valueOf(decimalStats.getNumDVs()) : null, low, high);
        }
        else if (statsObj.getStatsData().isSetStringStats()) {
            final StringColumnStatsData stringStats = statsObj.getStatsData().getStringStats();
            mColStats.setStringStats(stringStats.isSetNumNulls() ? Long.valueOf(stringStats.getNumNulls()) : null, stringStats.isSetNumDVs() ? Long.valueOf(stringStats.getNumDVs()) : null, stringStats.isSetMaxColLen() ? Long.valueOf(stringStats.getMaxColLen()) : null, stringStats.isSetAvgColLen() ? Double.valueOf(stringStats.getAvgColLen()) : null);
        }
        else if (statsObj.getStatsData().isSetBinaryStats()) {
            final BinaryColumnStatsData binaryStats = statsObj.getStatsData().getBinaryStats();
            mColStats.setBinaryStats(binaryStats.isSetNumNulls() ? Long.valueOf(binaryStats.getNumNulls()) : null, binaryStats.isSetMaxColLen() ? Long.valueOf(binaryStats.getMaxColLen()) : null, binaryStats.isSetAvgColLen() ? Double.valueOf(binaryStats.getAvgColLen()) : null);
        }
        else if (statsObj.getStatsData().isSetDateStats()) {
            final DateColumnStatsData dateStats = statsObj.getStatsData().getDateStats();
            mColStats.setDateStats(dateStats.isSetNumNulls() ? Long.valueOf(dateStats.getNumNulls()) : null, dateStats.isSetNumDVs() ? Long.valueOf(dateStats.getNumDVs()) : null, dateStats.isSetLowValue() ? Long.valueOf(dateStats.getLowValue().getDaysSinceEpoch()) : null, dateStats.isSetHighValue() ? Long.valueOf(dateStats.getHighValue().getDaysSinceEpoch()) : null);
        }
        return mColStats;
    }
    
    public static ColumnStatisticsObj getPartitionColumnStatisticsObj(final MPartitionColumnStatistics mStatsObj) {
        final ColumnStatisticsObj statsObj = new ColumnStatisticsObj();
        statsObj.setColType(mStatsObj.getColType());
        statsObj.setColName(mStatsObj.getColName());
        final String colType = mStatsObj.getColType().toLowerCase();
        final ColumnStatisticsData colStatsData = new ColumnStatisticsData();
        if (colType.equals("boolean")) {
            final BooleanColumnStatsData boolStats = new BooleanColumnStatsData();
            boolStats.setNumFalses(mStatsObj.getNumFalses());
            boolStats.setNumTrues(mStatsObj.getNumTrues());
            boolStats.setNumNulls(mStatsObj.getNumNulls());
            colStatsData.setBooleanStats(boolStats);
        }
        else if (colType.equals("string") || colType.startsWith("varchar") || colType.startsWith("char")) {
            final StringColumnStatsData stringStats = new StringColumnStatsData();
            stringStats.setNumNulls(mStatsObj.getNumNulls());
            stringStats.setAvgColLen(mStatsObj.getAvgColLen());
            stringStats.setMaxColLen(mStatsObj.getMaxColLen());
            stringStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setStringStats(stringStats);
        }
        else if (colType.equals("binary")) {
            final BinaryColumnStatsData binaryStats = new BinaryColumnStatsData();
            binaryStats.setNumNulls(mStatsObj.getNumNulls());
            binaryStats.setAvgColLen(mStatsObj.getAvgColLen());
            binaryStats.setMaxColLen(mStatsObj.getMaxColLen());
            colStatsData.setBinaryStats(binaryStats);
        }
        else if (colType.equals("tinyint") || colType.equals("smallint") || colType.equals("int") || colType.equals("bigint") || colType.equals("timestamp")) {
            final LongColumnStatsData longStats = new LongColumnStatsData();
            longStats.setNumNulls(mStatsObj.getNumNulls());
            if (mStatsObj.getLongHighValue() != null) {
                longStats.setHighValue(mStatsObj.getLongHighValue());
            }
            if (mStatsObj.getLongLowValue() != null) {
                longStats.setLowValue(mStatsObj.getLongLowValue());
            }
            longStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setLongStats(longStats);
        }
        else if (colType.equals("double") || colType.equals("float")) {
            final DoubleColumnStatsData doubleStats = new DoubleColumnStatsData();
            doubleStats.setNumNulls(mStatsObj.getNumNulls());
            if (mStatsObj.getDoubleHighValue() != null) {
                doubleStats.setHighValue(mStatsObj.getDoubleHighValue());
            }
            if (mStatsObj.getDoubleLowValue() != null) {
                doubleStats.setLowValue(mStatsObj.getDoubleLowValue());
            }
            doubleStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setDoubleStats(doubleStats);
        }
        else if (colType.startsWith("decimal")) {
            final DecimalColumnStatsData decimalStats = new DecimalColumnStatsData();
            decimalStats.setNumNulls(mStatsObj.getNumNulls());
            if (mStatsObj.getDecimalHighValue() != null) {
                decimalStats.setHighValue(createThriftDecimal(mStatsObj.getDecimalHighValue()));
            }
            if (mStatsObj.getDecimalLowValue() != null) {
                decimalStats.setLowValue(createThriftDecimal(mStatsObj.getDecimalLowValue()));
            }
            decimalStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setDecimalStats(decimalStats);
        }
        else if (colType.equals("date")) {
            final DateColumnStatsData dateStats = new DateColumnStatsData();
            dateStats.setNumNulls(mStatsObj.getNumNulls());
            dateStats.setHighValue(new Date(mStatsObj.getLongHighValue()));
            dateStats.setLowValue(new Date(mStatsObj.getLongLowValue()));
            dateStats.setNumDVs(mStatsObj.getNumDVs());
            colStatsData.setDateStats(dateStats);
        }
        statsObj.setStatsData(colStatsData);
        return statsObj;
    }
    
    public static ColumnStatisticsDesc getPartitionColumnStatisticsDesc(final MPartitionColumnStatistics mStatsObj) {
        final ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
        statsDesc.setIsTblLevel(false);
        statsDesc.setDbName(mStatsObj.getDbName());
        statsDesc.setTableName(mStatsObj.getTableName());
        statsDesc.setPartName(mStatsObj.getPartitionName());
        statsDesc.setLastAnalyzed(mStatsObj.getLastAnalyzed());
        return statsDesc;
    }
    
    public static void fillColumnStatisticsData(String colType, final ColumnStatisticsData data, final Object llow, final Object lhigh, final Object dlow, final Object dhigh, final Object declow, final Object dechigh, final Object nulls, final Object dist, final Object avglen, final Object maxlen, final Object trues, final Object falses) throws MetaException {
        colType = colType.toLowerCase();
        if (colType.equals("boolean")) {
            final BooleanColumnStatsData boolStats = new BooleanColumnStatsData();
            boolStats.setNumFalses(MetaStoreDirectSql.extractSqlLong(falses));
            boolStats.setNumTrues(MetaStoreDirectSql.extractSqlLong(trues));
            boolStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            data.setBooleanStats(boolStats);
        }
        else if (colType.equals("string") || colType.startsWith("varchar") || colType.startsWith("char")) {
            final StringColumnStatsData stringStats = new StringColumnStatsData();
            stringStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            stringStats.setAvgColLen(MetaStoreDirectSql.extractSqlDouble(avglen));
            stringStats.setMaxColLen(MetaStoreDirectSql.extractSqlLong(maxlen));
            stringStats.setNumDVs(MetaStoreDirectSql.extractSqlLong(dist));
            data.setStringStats(stringStats);
        }
        else if (colType.equals("binary")) {
            final BinaryColumnStatsData binaryStats = new BinaryColumnStatsData();
            binaryStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            binaryStats.setAvgColLen(MetaStoreDirectSql.extractSqlDouble(avglen));
            binaryStats.setMaxColLen(MetaStoreDirectSql.extractSqlLong(maxlen));
            data.setBinaryStats(binaryStats);
        }
        else if (colType.equals("bigint") || colType.equals("int") || colType.equals("smallint") || colType.equals("tinyint") || colType.equals("timestamp")) {
            final LongColumnStatsData longStats = new LongColumnStatsData();
            longStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            if (lhigh != null) {
                longStats.setHighValue(MetaStoreDirectSql.extractSqlLong(lhigh));
            }
            if (llow != null) {
                longStats.setLowValue(MetaStoreDirectSql.extractSqlLong(llow));
            }
            longStats.setNumDVs(MetaStoreDirectSql.extractSqlLong(dist));
            data.setLongStats(longStats);
        }
        else if (colType.equals("double") || colType.equals("float")) {
            final DoubleColumnStatsData doubleStats = new DoubleColumnStatsData();
            doubleStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            if (dhigh != null) {
                doubleStats.setHighValue(MetaStoreDirectSql.extractSqlDouble(dhigh));
            }
            if (dlow != null) {
                doubleStats.setLowValue(MetaStoreDirectSql.extractSqlDouble(dlow));
            }
            doubleStats.setNumDVs(MetaStoreDirectSql.extractSqlLong(dist));
            data.setDoubleStats(doubleStats);
        }
        else if (colType.startsWith("decimal")) {
            final DecimalColumnStatsData decimalStats = new DecimalColumnStatsData();
            decimalStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            if (dechigh != null) {
                decimalStats.setHighValue(createThriftDecimal((String)dechigh));
            }
            if (declow != null) {
                decimalStats.setLowValue(createThriftDecimal((String)declow));
            }
            decimalStats.setNumDVs(MetaStoreDirectSql.extractSqlLong(dist));
            data.setDecimalStats(decimalStats);
        }
        else if (colType.equals("date")) {
            final DateColumnStatsData dateStats = new DateColumnStatsData();
            dateStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            if (lhigh != null) {
                dateStats.setHighValue(new Date(MetaStoreDirectSql.extractSqlLong(lhigh)));
            }
            if (llow != null) {
                dateStats.setLowValue(new Date(MetaStoreDirectSql.extractSqlLong(llow)));
            }
            dateStats.setNumDVs(MetaStoreDirectSql.extractSqlLong(dist));
            data.setDateStats(dateStats);
        }
    }
    
    public static void fillColumnStatisticsData(String colType, final ColumnStatisticsData data, final Object llow, final Object lhigh, final Object dlow, final Object dhigh, final Object declow, final Object dechigh, final Object nulls, final Object dist, final Object avglen, final Object maxlen, final Object trues, final Object falses, final Object avgLong, final Object avgDouble, final Object avgDecimal, final Object sumDist, final boolean useDensityFunctionForNDVEstimation) throws MetaException {
        colType = colType.toLowerCase();
        if (colType.equals("boolean")) {
            final BooleanColumnStatsData boolStats = new BooleanColumnStatsData();
            boolStats.setNumFalses(MetaStoreDirectSql.extractSqlLong(falses));
            boolStats.setNumTrues(MetaStoreDirectSql.extractSqlLong(trues));
            boolStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            data.setBooleanStats(boolStats);
        }
        else if (colType.equals("string") || colType.startsWith("varchar") || colType.startsWith("char")) {
            final StringColumnStatsData stringStats = new StringColumnStatsData();
            stringStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            stringStats.setAvgColLen(MetaStoreDirectSql.extractSqlDouble(avglen));
            stringStats.setMaxColLen(MetaStoreDirectSql.extractSqlLong(maxlen));
            stringStats.setNumDVs(MetaStoreDirectSql.extractSqlLong(dist));
            data.setStringStats(stringStats);
        }
        else if (colType.equals("binary")) {
            final BinaryColumnStatsData binaryStats = new BinaryColumnStatsData();
            binaryStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            binaryStats.setAvgColLen(MetaStoreDirectSql.extractSqlDouble(avglen));
            binaryStats.setMaxColLen(MetaStoreDirectSql.extractSqlLong(maxlen));
            data.setBinaryStats(binaryStats);
        }
        else if (colType.equals("bigint") || colType.equals("int") || colType.equals("smallint") || colType.equals("tinyint") || colType.equals("timestamp")) {
            final LongColumnStatsData longStats = new LongColumnStatsData();
            longStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            if (lhigh != null) {
                longStats.setHighValue(MetaStoreDirectSql.extractSqlLong(lhigh));
            }
            if (llow != null) {
                longStats.setLowValue(MetaStoreDirectSql.extractSqlLong(llow));
            }
            final long lowerBound = MetaStoreDirectSql.extractSqlLong(dist);
            final long higherBound = MetaStoreDirectSql.extractSqlLong(sumDist);
            if (useDensityFunctionForNDVEstimation && lhigh != null && llow != null && avgLong != null && MetaStoreDirectSql.extractSqlDouble(avgLong) != 0.0) {
                final long estimation = MetaStoreDirectSql.extractSqlLong((MetaStoreDirectSql.extractSqlLong(lhigh) - MetaStoreDirectSql.extractSqlLong(llow)) / MetaStoreDirectSql.extractSqlDouble(avgLong));
                if (estimation < lowerBound) {
                    longStats.setNumDVs(lowerBound);
                }
                else if (estimation > higherBound) {
                    longStats.setNumDVs(higherBound);
                }
                else {
                    longStats.setNumDVs(estimation);
                }
            }
            else {
                longStats.setNumDVs(lowerBound);
            }
            data.setLongStats(longStats);
        }
        else if (colType.equals("double") || colType.equals("float")) {
            final DoubleColumnStatsData doubleStats = new DoubleColumnStatsData();
            doubleStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            if (dhigh != null) {
                doubleStats.setHighValue(MetaStoreDirectSql.extractSqlDouble(dhigh));
            }
            if (dlow != null) {
                doubleStats.setLowValue(MetaStoreDirectSql.extractSqlDouble(dlow));
            }
            final long lowerBound = MetaStoreDirectSql.extractSqlLong(dist);
            final long higherBound = MetaStoreDirectSql.extractSqlLong(sumDist);
            if (useDensityFunctionForNDVEstimation && dhigh != null && dlow != null && avgDouble != null && MetaStoreDirectSql.extractSqlDouble(avgDouble) != 0.0) {
                final long estimation = MetaStoreDirectSql.extractSqlLong((MetaStoreDirectSql.extractSqlLong(dhigh) - MetaStoreDirectSql.extractSqlLong(dlow)) / MetaStoreDirectSql.extractSqlDouble(avgDouble));
                if (estimation < lowerBound) {
                    doubleStats.setNumDVs(lowerBound);
                }
                else if (estimation > higherBound) {
                    doubleStats.setNumDVs(higherBound);
                }
                else {
                    doubleStats.setNumDVs(estimation);
                }
            }
            else {
                doubleStats.setNumDVs(lowerBound);
            }
            data.setDoubleStats(doubleStats);
        }
        else if (colType.startsWith("decimal")) {
            final DecimalColumnStatsData decimalStats = new DecimalColumnStatsData();
            decimalStats.setNumNulls(MetaStoreDirectSql.extractSqlLong(nulls));
            Decimal low = null;
            Decimal high = null;
            BigDecimal blow = null;
            BigDecimal bhigh = null;
            if (dechigh instanceof BigDecimal) {
                bhigh = (BigDecimal)dechigh;
                high = new Decimal(ByteBuffer.wrap(bhigh.unscaledValue().toByteArray()), (short)bhigh.scale());
            }
            else if (dechigh instanceof String) {
                bhigh = new BigDecimal((String)dechigh);
                high = createThriftDecimal((String)dechigh);
            }
            decimalStats.setHighValue(high);
            if (declow instanceof BigDecimal) {
                blow = (BigDecimal)declow;
                low = new Decimal(ByteBuffer.wrap(blow.unscaledValue().toByteArray()), (short)blow.scale());
            }
            else if (dechigh instanceof String) {
                blow = new BigDecimal((String)declow);
                low = createThriftDecimal((String)declow);
            }
            decimalStats.setLowValue(low);
            final long lowerBound2 = MetaStoreDirectSql.extractSqlLong(dist);
            final long higherBound2 = MetaStoreDirectSql.extractSqlLong(sumDist);
            if (useDensityFunctionForNDVEstimation && dechigh != null && declow != null && avgDecimal != null && MetaStoreDirectSql.extractSqlDouble(avgDecimal) != 0.0) {
                final long estimation2 = MetaStoreDirectSql.extractSqlLong(MetaStoreDirectSql.extractSqlLong(bhigh.subtract(blow).floatValue() / MetaStoreDirectSql.extractSqlDouble(avgDecimal)));
                if (estimation2 < lowerBound2) {
                    decimalStats.setNumDVs(lowerBound2);
                }
                else if (estimation2 > higherBound2) {
                    decimalStats.setNumDVs(higherBound2);
                }
                else {
                    decimalStats.setNumDVs(estimation2);
                }
            }
            else {
                decimalStats.setNumDVs(lowerBound2);
            }
            data.setDecimalStats(decimalStats);
        }
    }
    
    private static Decimal createThriftDecimal(final String s) {
        final BigDecimal d = new BigDecimal(s);
        return new Decimal(ByteBuffer.wrap(d.unscaledValue().toByteArray()), (short)d.scale());
    }
    
    private static String createJdoDecimalString(final Decimal d) {
        return new BigDecimal(new BigInteger(d.getUnscaled()), d.getScale()).toString();
    }
}
