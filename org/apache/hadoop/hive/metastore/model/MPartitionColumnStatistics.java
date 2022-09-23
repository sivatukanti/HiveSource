// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.model;

import javax.jdo.JDODetachedFieldAccessException;
import java.util.BitSet;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.StateManager;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;

public class MPartitionColumnStatistics implements Detachable, PersistenceCapable
{
    private MPartition partition;
    private String dbName;
    private String tableName;
    private String partitionName;
    private String colName;
    private String colType;
    private Long longLowValue;
    private Long longHighValue;
    private Double doubleLowValue;
    private Double doubleHighValue;
    private String decimalLowValue;
    private String decimalHighValue;
    private Long numNulls;
    private Long numDVs;
    private Double avgColLen;
    private Long maxColLen;
    private Long numTrues;
    private Long numFalses;
    private long lastAnalyzed;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public String getTableName() {
        return jdoGettableName(this);
    }
    
    public void setTableName(final String tableName) {
        jdoSettableName(this, tableName);
    }
    
    public String getColName() {
        return jdoGetcolName(this);
    }
    
    public void setColName(final String colName) {
        jdoSetcolName(this, colName);
    }
    
    public Long getNumNulls() {
        return jdoGetnumNulls(this);
    }
    
    public void setNumNulls(final long numNulls) {
        jdoSetnumNulls(this, numNulls);
    }
    
    public Long getNumDVs() {
        return jdoGetnumDVs(this);
    }
    
    public void setNumDVs(final long numDVs) {
        jdoSetnumDVs(this, numDVs);
    }
    
    public Double getAvgColLen() {
        return jdoGetavgColLen(this);
    }
    
    public void setAvgColLen(final double avgColLen) {
        jdoSetavgColLen(this, avgColLen);
    }
    
    public Long getMaxColLen() {
        return jdoGetmaxColLen(this);
    }
    
    public void setMaxColLen(final long maxColLen) {
        jdoSetmaxColLen(this, maxColLen);
    }
    
    public Long getNumTrues() {
        return jdoGetnumTrues(this);
    }
    
    public void setNumTrues(final long numTrues) {
        jdoSetnumTrues(this, numTrues);
    }
    
    public Long getNumFalses() {
        return jdoGetnumFalses(this);
    }
    
    public void setNumFalses(final long numFalses) {
        jdoSetnumFalses(this, numFalses);
    }
    
    public long getLastAnalyzed() {
        return jdoGetlastAnalyzed(this);
    }
    
    public void setLastAnalyzed(final long lastAnalyzed) {
        jdoSetlastAnalyzed(this, lastAnalyzed);
    }
    
    public String getDbName() {
        return jdoGetdbName(this);
    }
    
    public void setDbName(final String dbName) {
        jdoSetdbName(this, dbName);
    }
    
    public MPartition getPartition() {
        return jdoGetpartition(this);
    }
    
    public void setPartition(final MPartition partition) {
        jdoSetpartition(this, partition);
    }
    
    public String getPartitionName() {
        return jdoGetpartitionName(this);
    }
    
    public void setPartitionName(final String partitionName) {
        jdoSetpartitionName(this, partitionName);
    }
    
    public String getColType() {
        return jdoGetcolType(this);
    }
    
    public void setColType(final String colType) {
        jdoSetcolType(this, colType);
    }
    
    public void setBooleanStats(final Long numTrues, final Long numFalses, final Long numNulls) {
        jdoSetnumTrues(this, numTrues);
        jdoSetnumFalses(this, numFalses);
        jdoSetnumNulls(this, numNulls);
    }
    
    public void setLongStats(final Long numNulls, final Long numNDVs, final Long lowValue, final Long highValue) {
        jdoSetnumNulls(this, numNulls);
        jdoSetnumDVs(this, numNDVs);
        jdoSetlongLowValue(this, lowValue);
        jdoSetlongHighValue(this, highValue);
    }
    
    public void setDoubleStats(final Long numNulls, final Long numNDVs, final Double lowValue, final Double highValue) {
        jdoSetnumNulls(this, numNulls);
        jdoSetnumDVs(this, numNDVs);
        jdoSetdoubleLowValue(this, lowValue);
        jdoSetdoubleHighValue(this, highValue);
    }
    
    public void setDecimalStats(final Long numNulls, final Long numNDVs, final String lowValue, final String highValue) {
        jdoSetnumNulls(this, numNulls);
        jdoSetnumDVs(this, numNDVs);
        jdoSetdecimalLowValue(this, lowValue);
        jdoSetdecimalHighValue(this, highValue);
    }
    
    public void setStringStats(final Long numNulls, final Long numNDVs, final Long maxColLen, final Double avgColLen) {
        jdoSetnumNulls(this, numNulls);
        jdoSetnumDVs(this, numNDVs);
        jdoSetmaxColLen(this, maxColLen);
        jdoSetavgColLen(this, avgColLen);
    }
    
    public void setBinaryStats(final Long numNulls, final Long maxColLen, final Double avgColLen) {
        jdoSetnumNulls(this, numNulls);
        jdoSetmaxColLen(this, maxColLen);
        jdoSetavgColLen(this, avgColLen);
    }
    
    public void setDateStats(final Long numNulls, final Long numNDVs, final Long lowValue, final Long highValue) {
        jdoSetnumNulls(this, numNulls);
        jdoSetnumDVs(this, numNDVs);
        jdoSetlongLowValue(this, lowValue);
        jdoSetlongHighValue(this, highValue);
    }
    
    public Long getLongLowValue() {
        return jdoGetlongLowValue(this);
    }
    
    public void setLongLowValue(final Long longLowValue) {
        jdoSetlongLowValue(this, longLowValue);
    }
    
    public Long getLongHighValue() {
        return jdoGetlongHighValue(this);
    }
    
    public void setLongHighValue(final Long longHighValue) {
        jdoSetlongHighValue(this, longHighValue);
    }
    
    public Double getDoubleLowValue() {
        return jdoGetdoubleLowValue(this);
    }
    
    public void setDoubleLowValue(final Double doubleLowValue) {
        jdoSetdoubleLowValue(this, doubleLowValue);
    }
    
    public Double getDoubleHighValue() {
        return jdoGetdoubleHighValue(this);
    }
    
    public void setDoubleHighValue(final Double doubleHighValue) {
        jdoSetdoubleHighValue(this, doubleHighValue);
    }
    
    public String getDecimalLowValue() {
        return jdoGetdecimalLowValue(this);
    }
    
    public void setDecimalLowValue(final String decimalLowValue) {
        jdoSetdecimalLowValue(this, decimalLowValue);
    }
    
    public String getDecimalHighValue() {
        return jdoGetdecimalHighValue(this);
    }
    
    public void setDecimalHighValue(final String decimalHighValue) {
        jdoSetdecimalHighValue(this, decimalHighValue);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MPartitionColumnStatistics"), MPartitionColumnStatistics.jdoFieldNames, MPartitionColumnStatistics.jdoFieldTypes, MPartitionColumnStatistics.jdoFieldFlags, MPartitionColumnStatistics.jdoPersistenceCapableSuperclass, new MPartitionColumnStatistics());
    }
    
    @Override
    public void jdoCopyKeyFieldsFromObjectId(final ObjectIdFieldConsumer fc, final Object oid) {
    }
    
    protected void jdoCopyKeyFieldsFromObjectId(final Object oid) {
    }
    
    @Override
    public void jdoCopyKeyFieldsToObjectId(final Object oid) {
    }
    
    @Override
    public void jdoCopyKeyFieldsToObjectId(final ObjectIdFieldSupplier fs, final Object oid) {
    }
    
    @Override
    public final Object jdoGetObjectId() {
        if (this.jdoStateManager != null) {
            return this.jdoStateManager.getObjectId(this);
        }
        if (!this.jdoIsDetached()) {
            return null;
        }
        return this.jdoDetachedState[0];
    }
    
    @Override
    public final Object jdoGetVersion() {
        if (this.jdoStateManager != null) {
            return this.jdoStateManager.getVersion(this);
        }
        if (!this.jdoIsDetached()) {
            return null;
        }
        return this.jdoDetachedState[1];
    }
    
    protected final void jdoPreSerialize() {
        if (this.jdoStateManager != null) {
            this.jdoStateManager.preSerialize(this);
        }
    }
    
    @Override
    public final PersistenceManager jdoGetPersistenceManager() {
        return (this.jdoStateManager != null) ? this.jdoStateManager.getPersistenceManager(this) : null;
    }
    
    @Override
    public final Object jdoGetTransactionalObjectId() {
        return (this.jdoStateManager != null) ? this.jdoStateManager.getTransactionalObjectId(this) : null;
    }
    
    @Override
    public final boolean jdoIsDeleted() {
        return this.jdoStateManager != null && this.jdoStateManager.isDeleted(this);
    }
    
    @Override
    public final boolean jdoIsDirty() {
        if (this.jdoStateManager != null) {
            return this.jdoStateManager.isDirty(this);
        }
        return this.jdoIsDetached() && ((BitSet)this.jdoDetachedState[3]).length() > 0;
    }
    
    @Override
    public final boolean jdoIsNew() {
        return this.jdoStateManager != null && this.jdoStateManager.isNew(this);
    }
    
    @Override
    public final boolean jdoIsPersistent() {
        return this.jdoStateManager != null && this.jdoStateManager.isPersistent(this);
    }
    
    @Override
    public final boolean jdoIsTransactional() {
        return this.jdoStateManager != null && this.jdoStateManager.isTransactional(this);
    }
    
    @Override
    public void jdoMakeDirty(final String fieldName) {
        if (this.jdoStateManager != null) {
            this.jdoStateManager.makeDirty(this, fieldName);
        }
        if (this.jdoIsDetached() && fieldName != null) {
            String fldName = null;
            if (fieldName.indexOf(46) >= 0) {
                fldName = fieldName.substring(fieldName.lastIndexOf(46) + 1);
            }
            else {
                fldName = fieldName;
            }
            int i = 0;
            while (i < MPartitionColumnStatistics.jdoFieldNames.length) {
                if (MPartitionColumnStatistics.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MPartitionColumnStatistics.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MPartitionColumnStatistics.jdoInheritedFieldCount);
                        return;
                    }
                    throw new JDODetachedFieldAccessException("You have just attempted to access a field/property that hasn't been detached. Please detach it first before performing this operation");
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    @Override
    public Object jdoNewObjectIdInstance() {
        return null;
    }
    
    @Override
    public Object jdoNewObjectIdInstance(final Object key) {
        return null;
    }
    
    @Override
    public final void jdoProvideFields(final int[] indices) {
        if (indices == null) {
            throw new IllegalArgumentException("argment is null");
        }
        int i = indices.length - 1;
        if (i >= 0) {
            do {
                this.jdoProvideField(indices[i]);
            } while (--i >= 0);
        }
    }
    
    @Override
    public final void jdoReplaceFields(final int[] indices) {
        if (indices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        final int i = indices.length;
        if (i > 0) {
            int j = 0;
            do {
                this.jdoReplaceField(indices[j]);
            } while (++j < i);
        }
    }
    
    @Override
    public final void jdoReplaceFlags() {
        if (this.jdoStateManager != null) {
            this.jdoFlags = this.jdoStateManager.replacingFlags(this);
        }
    }
    
    @Override
    public final synchronized void jdoReplaceStateManager(final StateManager sm) {
        if (this.jdoStateManager != null) {
            this.jdoStateManager = this.jdoStateManager.replacingStateManager(this, sm);
        }
        else {
            JDOImplHelper.checkAuthorizedStateManager(sm);
            this.jdoStateManager = sm;
            this.jdoFlags = 1;
        }
    }
    
    @Override
    public final synchronized void jdoReplaceDetachedState() {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        this.jdoDetachedState = this.jdoStateManager.replacingDetachedState(this, this.jdoDetachedState);
    }
    
    @Override
    public boolean jdoIsDetached() {
        return this.jdoStateManager == null && this.jdoDetachedState != null;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm) {
        final MPartitionColumnStatistics result = new MPartitionColumnStatistics();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MPartitionColumnStatistics result = new MPartitionColumnStatistics();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        result.jdoCopyKeyFieldsFromObjectId(obj);
        return result;
    }
    
    @Override
    public void jdoReplaceField(final int index) {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        switch (index) {
            case 0: {
                this.avgColLen = (Double)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 1: {
                this.colName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 2: {
                this.colType = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 3: {
                this.dbName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 4: {
                this.decimalHighValue = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 5: {
                this.decimalLowValue = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 6: {
                this.doubleHighValue = (Double)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 7: {
                this.doubleLowValue = (Double)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 8: {
                this.lastAnalyzed = this.jdoStateManager.replacingLongField(this, index);
                break;
            }
            case 9: {
                this.longHighValue = (Long)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 10: {
                this.longLowValue = (Long)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 11: {
                this.maxColLen = (Long)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 12: {
                this.numDVs = (Long)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 13: {
                this.numFalses = (Long)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 14: {
                this.numNulls = (Long)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 15: {
                this.numTrues = (Long)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 16: {
                this.partition = (MPartition)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 17: {
                this.partitionName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 18: {
                this.tableName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    @Override
    public void jdoProvideField(final int index) {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        switch (index) {
            case 0: {
                this.jdoStateManager.providedObjectField(this, index, this.avgColLen);
                break;
            }
            case 1: {
                this.jdoStateManager.providedStringField(this, index, this.colName);
                break;
            }
            case 2: {
                this.jdoStateManager.providedStringField(this, index, this.colType);
                break;
            }
            case 3: {
                this.jdoStateManager.providedStringField(this, index, this.dbName);
                break;
            }
            case 4: {
                this.jdoStateManager.providedStringField(this, index, this.decimalHighValue);
                break;
            }
            case 5: {
                this.jdoStateManager.providedStringField(this, index, this.decimalLowValue);
                break;
            }
            case 6: {
                this.jdoStateManager.providedObjectField(this, index, this.doubleHighValue);
                break;
            }
            case 7: {
                this.jdoStateManager.providedObjectField(this, index, this.doubleLowValue);
                break;
            }
            case 8: {
                this.jdoStateManager.providedLongField(this, index, this.lastAnalyzed);
                break;
            }
            case 9: {
                this.jdoStateManager.providedObjectField(this, index, this.longHighValue);
                break;
            }
            case 10: {
                this.jdoStateManager.providedObjectField(this, index, this.longLowValue);
                break;
            }
            case 11: {
                this.jdoStateManager.providedObjectField(this, index, this.maxColLen);
                break;
            }
            case 12: {
                this.jdoStateManager.providedObjectField(this, index, this.numDVs);
                break;
            }
            case 13: {
                this.jdoStateManager.providedObjectField(this, index, this.numFalses);
                break;
            }
            case 14: {
                this.jdoStateManager.providedObjectField(this, index, this.numNulls);
                break;
            }
            case 15: {
                this.jdoStateManager.providedObjectField(this, index, this.numTrues);
                break;
            }
            case 16: {
                this.jdoStateManager.providedObjectField(this, index, this.partition);
                break;
            }
            case 17: {
                this.jdoStateManager.providedStringField(this, index, this.partitionName);
                break;
            }
            case 18: {
                this.jdoStateManager.providedStringField(this, index, this.tableName);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MPartitionColumnStatistics obj, final int index) {
        switch (index) {
            case 0: {
                this.avgColLen = obj.avgColLen;
                break;
            }
            case 1: {
                this.colName = obj.colName;
                break;
            }
            case 2: {
                this.colType = obj.colType;
                break;
            }
            case 3: {
                this.dbName = obj.dbName;
                break;
            }
            case 4: {
                this.decimalHighValue = obj.decimalHighValue;
                break;
            }
            case 5: {
                this.decimalLowValue = obj.decimalLowValue;
                break;
            }
            case 6: {
                this.doubleHighValue = obj.doubleHighValue;
                break;
            }
            case 7: {
                this.doubleLowValue = obj.doubleLowValue;
                break;
            }
            case 8: {
                this.lastAnalyzed = obj.lastAnalyzed;
                break;
            }
            case 9: {
                this.longHighValue = obj.longHighValue;
                break;
            }
            case 10: {
                this.longLowValue = obj.longLowValue;
                break;
            }
            case 11: {
                this.maxColLen = obj.maxColLen;
                break;
            }
            case 12: {
                this.numDVs = obj.numDVs;
                break;
            }
            case 13: {
                this.numFalses = obj.numFalses;
                break;
            }
            case 14: {
                this.numNulls = obj.numNulls;
                break;
            }
            case 15: {
                this.numTrues = obj.numTrues;
                break;
            }
            case 16: {
                this.partition = obj.partition;
                break;
            }
            case 17: {
                this.partitionName = obj.partitionName;
                break;
            }
            case 18: {
                this.tableName = obj.tableName;
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    @Override
    public void jdoCopyFields(final Object obj, final int[] indices) {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        if (indices == null) {
            throw new IllegalStateException("fieldNumbers is null");
        }
        if (!(obj instanceof MPartitionColumnStatistics)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MPartitionColumnStatistics");
        }
        final MPartitionColumnStatistics other = (MPartitionColumnStatistics)obj;
        if (this.jdoStateManager != other.jdoStateManager) {
            throw new IllegalArgumentException("state managers do not match");
        }
        int i = indices.length - 1;
        if (i >= 0) {
            do {
                this.jdoCopyField(other, indices[i]);
            } while (--i >= 0);
        }
    }
    
    private static final String[] __jdoFieldNamesInit() {
        return new String[] { "avgColLen", "colName", "colType", "dbName", "decimalHighValue", "decimalLowValue", "doubleHighValue", "doubleLowValue", "lastAnalyzed", "longHighValue", "longLowValue", "maxColLen", "numDVs", "numFalses", "numNulls", "numTrues", "partition", "partitionName", "tableName" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { ___jdo$loadClass("java.lang.Double"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.Double"), ___jdo$loadClass("java.lang.Double"), Long.TYPE, ___jdo$loadClass("java.lang.Long"), ___jdo$loadClass("java.lang.Long"), ___jdo$loadClass("java.lang.Long"), ___jdo$loadClass("java.lang.Long"), ___jdo$loadClass("java.lang.Long"), ___jdo$loadClass("java.lang.Long"), ___jdo$loadClass("java.lang.Long"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MPartition"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 10, 21, 21 };
    }
    
    protected static int __jdoGetInheritedFieldCount() {
        return 0;
    }
    
    protected static int jdoGetManagedFieldCount() {
        return 19;
    }
    
    private static Class __jdoPersistenceCapableSuperclassInit() {
        return null;
    }
    
    public static Class ___jdo$loadClass(final String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
    
    private Object jdoSuperClone() throws CloneNotSupportedException {
        final MPartitionColumnStatistics o = (MPartitionColumnStatistics)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static Double jdoGetavgColLen(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return (Double)objPC.jdoStateManager.getObjectField(objPC, 0, objPC.avgColLen);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"avgColLen\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.avgColLen;
    }
    
    private static void jdoSetavgColLen(final MPartitionColumnStatistics objPC, final Double val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 0, objPC.avgColLen, val);
        }
        else {
            objPC.avgColLen = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(0);
            }
        }
    }
    
    private static String jdoGetcolName(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return objPC.jdoStateManager.getStringField(objPC, 1, objPC.colName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"colName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.colName;
    }
    
    private static void jdoSetcolName(final MPartitionColumnStatistics objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 1, objPC.colName, val);
        }
        else {
            objPC.colName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(1);
            }
        }
    }
    
    private static String jdoGetcolType(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return objPC.jdoStateManager.getStringField(objPC, 2, objPC.colType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"colType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.colType;
    }
    
    private static void jdoSetcolType(final MPartitionColumnStatistics objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 2, objPC.colType, val);
        }
        else {
            objPC.colType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(2);
            }
        }
    }
    
    private static String jdoGetdbName(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getStringField(objPC, 3, objPC.dbName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"dbName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.dbName;
    }
    
    private static void jdoSetdbName(final MPartitionColumnStatistics objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 3, objPC.dbName, val);
        }
        else {
            objPC.dbName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static String jdoGetdecimalHighValue(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return objPC.jdoStateManager.getStringField(objPC, 4, objPC.decimalHighValue);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"decimalHighValue\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.decimalHighValue;
    }
    
    private static void jdoSetdecimalHighValue(final MPartitionColumnStatistics objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 4, objPC.decimalHighValue, val);
        }
        else {
            objPC.decimalHighValue = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(4);
            }
        }
    }
    
    private static String jdoGetdecimalLowValue(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return objPC.jdoStateManager.getStringField(objPC, 5, objPC.decimalLowValue);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"decimalLowValue\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.decimalLowValue;
    }
    
    private static void jdoSetdecimalLowValue(final MPartitionColumnStatistics objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 5, objPC.decimalLowValue, val);
        }
        else {
            objPC.decimalLowValue = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(5);
            }
        }
    }
    
    private static Double jdoGetdoubleHighValue(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return (Double)objPC.jdoStateManager.getObjectField(objPC, 6, objPC.doubleHighValue);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"doubleHighValue\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.doubleHighValue;
    }
    
    private static void jdoSetdoubleHighValue(final MPartitionColumnStatistics objPC, final Double val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 6, objPC.doubleHighValue, val);
        }
        else {
            objPC.doubleHighValue = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(6);
            }
        }
    }
    
    private static Double jdoGetdoubleLowValue(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 7)) {
            return (Double)objPC.jdoStateManager.getObjectField(objPC, 7, objPC.doubleLowValue);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(7)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"doubleLowValue\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.doubleLowValue;
    }
    
    private static void jdoSetdoubleLowValue(final MPartitionColumnStatistics objPC, final Double val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 7, objPC.doubleLowValue, val);
        }
        else {
            objPC.doubleLowValue = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(7);
            }
        }
    }
    
    private static long jdoGetlastAnalyzed(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 8)) {
            return objPC.jdoStateManager.getLongField(objPC, 8, objPC.lastAnalyzed);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(8)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"lastAnalyzed\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.lastAnalyzed;
    }
    
    private static void jdoSetlastAnalyzed(final MPartitionColumnStatistics objPC, final long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setLongField(objPC, 8, objPC.lastAnalyzed, val);
        }
        else {
            objPC.lastAnalyzed = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(8);
            }
        }
    }
    
    private static Long jdoGetlongHighValue(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 9)) {
            return (Long)objPC.jdoStateManager.getObjectField(objPC, 9, objPC.longHighValue);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(9)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"longHighValue\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.longHighValue;
    }
    
    private static void jdoSetlongHighValue(final MPartitionColumnStatistics objPC, final Long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 9, objPC.longHighValue, val);
        }
        else {
            objPC.longHighValue = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(9);
            }
        }
    }
    
    private static Long jdoGetlongLowValue(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 10)) {
            return (Long)objPC.jdoStateManager.getObjectField(objPC, 10, objPC.longLowValue);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(10)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"longLowValue\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.longLowValue;
    }
    
    private static void jdoSetlongLowValue(final MPartitionColumnStatistics objPC, final Long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 10, objPC.longLowValue, val);
        }
        else {
            objPC.longLowValue = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(10);
            }
        }
    }
    
    private static Long jdoGetmaxColLen(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 11)) {
            return (Long)objPC.jdoStateManager.getObjectField(objPC, 11, objPC.maxColLen);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(11)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"maxColLen\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.maxColLen;
    }
    
    private static void jdoSetmaxColLen(final MPartitionColumnStatistics objPC, final Long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 11, objPC.maxColLen, val);
        }
        else {
            objPC.maxColLen = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(11);
            }
        }
    }
    
    private static Long jdoGetnumDVs(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 12)) {
            return (Long)objPC.jdoStateManager.getObjectField(objPC, 12, objPC.numDVs);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(12)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"numDVs\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.numDVs;
    }
    
    private static void jdoSetnumDVs(final MPartitionColumnStatistics objPC, final Long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 12, objPC.numDVs, val);
        }
        else {
            objPC.numDVs = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(12);
            }
        }
    }
    
    private static Long jdoGetnumFalses(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 13)) {
            return (Long)objPC.jdoStateManager.getObjectField(objPC, 13, objPC.numFalses);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(13)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"numFalses\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.numFalses;
    }
    
    private static void jdoSetnumFalses(final MPartitionColumnStatistics objPC, final Long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 13, objPC.numFalses, val);
        }
        else {
            objPC.numFalses = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(13);
            }
        }
    }
    
    private static Long jdoGetnumNulls(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 14)) {
            return (Long)objPC.jdoStateManager.getObjectField(objPC, 14, objPC.numNulls);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(14)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"numNulls\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.numNulls;
    }
    
    private static void jdoSetnumNulls(final MPartitionColumnStatistics objPC, final Long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 14, objPC.numNulls, val);
        }
        else {
            objPC.numNulls = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(14);
            }
        }
    }
    
    private static Long jdoGetnumTrues(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 15)) {
            return (Long)objPC.jdoStateManager.getObjectField(objPC, 15, objPC.numTrues);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(15)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"numTrues\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.numTrues;
    }
    
    private static void jdoSetnumTrues(final MPartitionColumnStatistics objPC, final Long val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setObjectField(objPC, 15, objPC.numTrues, val);
        }
        else {
            objPC.numTrues = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(15);
            }
        }
    }
    
    private static MPartition jdoGetpartition(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 16)) {
            return (MPartition)objPC.jdoStateManager.getObjectField(objPC, 16, objPC.partition);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(16) && !((BitSet)objPC.jdoDetachedState[3]).get(16)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"partition\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.partition;
    }
    
    private static void jdoSetpartition(final MPartitionColumnStatistics objPC, final MPartition val) {
        if (objPC.jdoStateManager == null) {
            objPC.partition = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 16, objPC.partition, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(16);
        }
    }
    
    private static String jdoGetpartitionName(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 17)) {
            return objPC.jdoStateManager.getStringField(objPC, 17, objPC.partitionName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(17)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"partitionName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.partitionName;
    }
    
    private static void jdoSetpartitionName(final MPartitionColumnStatistics objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 17, objPC.partitionName, val);
        }
        else {
            objPC.partitionName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(17);
            }
        }
    }
    
    private static String jdoGettableName(final MPartitionColumnStatistics objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 18)) {
            return objPC.jdoStateManager.getStringField(objPC, 18, objPC.tableName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(18)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"tableName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.tableName;
    }
    
    private static void jdoSettableName(final MPartitionColumnStatistics objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 18, objPC.tableName, val);
        }
        else {
            objPC.tableName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(18);
            }
        }
    }
}
