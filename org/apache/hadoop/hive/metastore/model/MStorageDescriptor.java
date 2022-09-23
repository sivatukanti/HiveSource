// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.model;

import javax.jdo.JDODetachedFieldAccessException;
import java.util.BitSet;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.StateManager;
import java.util.Map;
import java.util.List;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;

public class MStorageDescriptor implements Detachable, PersistenceCapable
{
    private MColumnDescriptor cd;
    private String location;
    private String inputFormat;
    private String outputFormat;
    private boolean isCompressed;
    private int numBuckets;
    private MSerDeInfo serDeInfo;
    private List<String> bucketCols;
    private List<MOrder> sortCols;
    private Map<String, String> parameters;
    private List<String> skewedColNames;
    private List<MStringList> skewedColValues;
    private Map<MStringList, String> skewedColValueLocationMaps;
    private boolean isStoredAsSubDirectories;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MStorageDescriptor() {
        this.isCompressed = false;
        this.numBuckets = 1;
    }
    
    public MStorageDescriptor(final MColumnDescriptor cd, final String location, final String inputFormat, final String outputFormat, final boolean isCompressed, final int numBuckets, final MSerDeInfo serDeInfo, final List<String> bucketCols, final List<MOrder> sortOrder, final Map<String, String> parameters, final List<String> skewedColNames, final List<MStringList> skewedColValues, final Map<MStringList, String> skewedColValueLocationMaps, final boolean storedAsSubDirectories) {
        this.isCompressed = false;
        this.numBuckets = 1;
        this.cd = cd;
        this.location = location;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.isCompressed = isCompressed;
        this.numBuckets = numBuckets;
        this.serDeInfo = serDeInfo;
        this.bucketCols = bucketCols;
        this.sortCols = sortOrder;
        this.parameters = parameters;
        this.skewedColNames = skewedColNames;
        this.skewedColValues = skewedColValues;
        this.skewedColValueLocationMaps = skewedColValueLocationMaps;
        this.isStoredAsSubDirectories = storedAsSubDirectories;
    }
    
    public String getLocation() {
        return jdoGetlocation(this);
    }
    
    public void setLocation(final String location) {
        jdoSetlocation(this, location);
    }
    
    public boolean isCompressed() {
        return jdoGetisCompressed(this);
    }
    
    public void setCompressed(final boolean isCompressed) {
        jdoSetisCompressed(this, isCompressed);
    }
    
    public int getNumBuckets() {
        return jdoGetnumBuckets(this);
    }
    
    public void setNumBuckets(final int numBuckets) {
        jdoSetnumBuckets(this, numBuckets);
    }
    
    public List<String> getBucketCols() {
        return (List<String>)jdoGetbucketCols(this);
    }
    
    public void setBucketCols(final List<String> bucketCols) {
        jdoSetbucketCols(this, bucketCols);
    }
    
    public Map<String, String> getParameters() {
        return (Map<String, String>)jdoGetparameters(this);
    }
    
    public void setParameters(final Map<String, String> parameters) {
        jdoSetparameters(this, parameters);
    }
    
    public String getInputFormat() {
        return jdoGetinputFormat(this);
    }
    
    public void setInputFormat(final String inputFormat) {
        jdoSetinputFormat(this, inputFormat);
    }
    
    public String getOutputFormat() {
        return jdoGetoutputFormat(this);
    }
    
    public void setOutputFormat(final String outputFormat) {
        jdoSetoutputFormat(this, outputFormat);
    }
    
    public MColumnDescriptor getCD() {
        return jdoGetcd(this);
    }
    
    public void setCD(final MColumnDescriptor cd) {
        jdoSetcd(this, cd);
    }
    
    public MSerDeInfo getSerDeInfo() {
        return jdoGetserDeInfo(this);
    }
    
    public void setSerDeInfo(final MSerDeInfo serDe) {
        jdoSetserDeInfo(this, serDe);
    }
    
    public void setSortCols(final List<MOrder> sortOrder) {
        jdoSetsortCols(this, sortOrder);
    }
    
    public List<MOrder> getSortCols() {
        return (List<MOrder>)jdoGetsortCols(this);
    }
    
    public List<String> getSkewedColNames() {
        return (List<String>)jdoGetskewedColNames(this);
    }
    
    public void setSkewedColNames(final List<String> skewedColNames) {
        jdoSetskewedColNames(this, skewedColNames);
    }
    
    public List<MStringList> getSkewedColValues() {
        return (List<MStringList>)jdoGetskewedColValues(this);
    }
    
    public void setSkewedColValues(final List<MStringList> skewedColValues) {
        jdoSetskewedColValues(this, skewedColValues);
    }
    
    public Map<MStringList, String> getSkewedColValueLocationMaps() {
        return (Map<MStringList, String>)jdoGetskewedColValueLocationMaps(this);
    }
    
    public void setSkewedColValueLocationMaps(final Map<MStringList, String> listBucketColValuesMapping) {
        jdoSetskewedColValueLocationMaps(this, listBucketColValuesMapping);
    }
    
    public boolean isStoredAsSubDirectories() {
        return jdoGetisStoredAsSubDirectories(this);
    }
    
    public void setStoredAsSubDirectories(final boolean storedAsSubDirectories) {
        jdoSetisStoredAsSubDirectories(this, storedAsSubDirectories);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MStorageDescriptor"), MStorageDescriptor.jdoFieldNames, MStorageDescriptor.jdoFieldTypes, MStorageDescriptor.jdoFieldFlags, MStorageDescriptor.jdoPersistenceCapableSuperclass, new MStorageDescriptor());
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
            while (i < MStorageDescriptor.jdoFieldNames.length) {
                if (MStorageDescriptor.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MStorageDescriptor.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MStorageDescriptor.jdoInheritedFieldCount);
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
        final MStorageDescriptor result = new MStorageDescriptor();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MStorageDescriptor result = new MStorageDescriptor();
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
                this.bucketCols = (List<String>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 1: {
                this.cd = (MColumnDescriptor)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 2: {
                this.inputFormat = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 3: {
                this.isCompressed = this.jdoStateManager.replacingBooleanField(this, index);
                break;
            }
            case 4: {
                this.isStoredAsSubDirectories = this.jdoStateManager.replacingBooleanField(this, index);
                break;
            }
            case 5: {
                this.location = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 6: {
                this.numBuckets = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 7: {
                this.outputFormat = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 8: {
                this.parameters = (Map<String, String>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 9: {
                this.serDeInfo = (MSerDeInfo)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 10: {
                this.skewedColNames = (List<String>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 11: {
                this.skewedColValueLocationMaps = (Map<MStringList, String>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 12: {
                this.skewedColValues = (List<MStringList>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 13: {
                this.sortCols = (List<MOrder>)this.jdoStateManager.replacingObjectField(this, index);
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
                this.jdoStateManager.providedObjectField(this, index, this.bucketCols);
                break;
            }
            case 1: {
                this.jdoStateManager.providedObjectField(this, index, this.cd);
                break;
            }
            case 2: {
                this.jdoStateManager.providedStringField(this, index, this.inputFormat);
                break;
            }
            case 3: {
                this.jdoStateManager.providedBooleanField(this, index, this.isCompressed);
                break;
            }
            case 4: {
                this.jdoStateManager.providedBooleanField(this, index, this.isStoredAsSubDirectories);
                break;
            }
            case 5: {
                this.jdoStateManager.providedStringField(this, index, this.location);
                break;
            }
            case 6: {
                this.jdoStateManager.providedIntField(this, index, this.numBuckets);
                break;
            }
            case 7: {
                this.jdoStateManager.providedStringField(this, index, this.outputFormat);
                break;
            }
            case 8: {
                this.jdoStateManager.providedObjectField(this, index, this.parameters);
                break;
            }
            case 9: {
                this.jdoStateManager.providedObjectField(this, index, this.serDeInfo);
                break;
            }
            case 10: {
                this.jdoStateManager.providedObjectField(this, index, this.skewedColNames);
                break;
            }
            case 11: {
                this.jdoStateManager.providedObjectField(this, index, this.skewedColValueLocationMaps);
                break;
            }
            case 12: {
                this.jdoStateManager.providedObjectField(this, index, this.skewedColValues);
                break;
            }
            case 13: {
                this.jdoStateManager.providedObjectField(this, index, this.sortCols);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MStorageDescriptor obj, final int index) {
        switch (index) {
            case 0: {
                this.bucketCols = obj.bucketCols;
                break;
            }
            case 1: {
                this.cd = obj.cd;
                break;
            }
            case 2: {
                this.inputFormat = obj.inputFormat;
                break;
            }
            case 3: {
                this.isCompressed = obj.isCompressed;
                break;
            }
            case 4: {
                this.isStoredAsSubDirectories = obj.isStoredAsSubDirectories;
                break;
            }
            case 5: {
                this.location = obj.location;
                break;
            }
            case 6: {
                this.numBuckets = obj.numBuckets;
                break;
            }
            case 7: {
                this.outputFormat = obj.outputFormat;
                break;
            }
            case 8: {
                this.parameters = obj.parameters;
                break;
            }
            case 9: {
                this.serDeInfo = obj.serDeInfo;
                break;
            }
            case 10: {
                this.skewedColNames = obj.skewedColNames;
                break;
            }
            case 11: {
                this.skewedColValueLocationMaps = obj.skewedColValueLocationMaps;
                break;
            }
            case 12: {
                this.skewedColValues = obj.skewedColValues;
                break;
            }
            case 13: {
                this.sortCols = obj.sortCols;
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
        if (!(obj instanceof MStorageDescriptor)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MStorageDescriptor");
        }
        final MStorageDescriptor other = (MStorageDescriptor)obj;
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
        return new String[] { "bucketCols", "cd", "inputFormat", "isCompressed", "isStoredAsSubDirectories", "location", "numBuckets", "outputFormat", "parameters", "serDeInfo", "skewedColNames", "skewedColValueLocationMaps", "skewedColValues", "sortCols" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { ___jdo$loadClass("java.util.List"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MColumnDescriptor"), ___jdo$loadClass("java.lang.String"), Boolean.TYPE, Boolean.TYPE, ___jdo$loadClass("java.lang.String"), Integer.TYPE, ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.util.Map"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MSerDeInfo"), ___jdo$loadClass("java.util.List"), ___jdo$loadClass("java.util.Map"), ___jdo$loadClass("java.util.List"), ___jdo$loadClass("java.util.List") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 10, 10, 21, 21, 21, 21, 21, 21, 10, 10, 10, 10, 10, 10 };
    }
    
    protected static int __jdoGetInheritedFieldCount() {
        return 0;
    }
    
    protected static int jdoGetManagedFieldCount() {
        return 14;
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
        final MStorageDescriptor o = (MStorageDescriptor)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static List jdoGetbucketCols(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return (List)objPC.jdoStateManager.getObjectField(objPC, 0, objPC.bucketCols);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0) && !((BitSet)objPC.jdoDetachedState[3]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"bucketCols\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.bucketCols;
    }
    
    private static void jdoSetbucketCols(final MStorageDescriptor objPC, final List val) {
        if (objPC.jdoStateManager == null) {
            objPC.bucketCols = (List<String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 0, objPC.bucketCols, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(0);
        }
    }
    
    private static MColumnDescriptor jdoGetcd(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return (MColumnDescriptor)objPC.jdoStateManager.getObjectField(objPC, 1, objPC.cd);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1) && !((BitSet)objPC.jdoDetachedState[3]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"cd\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.cd;
    }
    
    private static void jdoSetcd(final MStorageDescriptor objPC, final MColumnDescriptor val) {
        if (objPC.jdoStateManager == null) {
            objPC.cd = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 1, objPC.cd, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(1);
        }
    }
    
    private static String jdoGetinputFormat(final MStorageDescriptor objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return objPC.jdoStateManager.getStringField(objPC, 2, objPC.inputFormat);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"inputFormat\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.inputFormat;
    }
    
    private static void jdoSetinputFormat(final MStorageDescriptor objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 2, objPC.inputFormat, val);
        }
        else {
            objPC.inputFormat = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(2);
            }
        }
    }
    
    private static boolean jdoGetisCompressed(final MStorageDescriptor objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getBooleanField(objPC, 3, objPC.isCompressed);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"isCompressed\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.isCompressed;
    }
    
    private static void jdoSetisCompressed(final MStorageDescriptor objPC, final boolean val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setBooleanField(objPC, 3, objPC.isCompressed, val);
        }
        else {
            objPC.isCompressed = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static boolean jdoGetisStoredAsSubDirectories(final MStorageDescriptor objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return objPC.jdoStateManager.getBooleanField(objPC, 4, objPC.isStoredAsSubDirectories);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"isStoredAsSubDirectories\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.isStoredAsSubDirectories;
    }
    
    private static void jdoSetisStoredAsSubDirectories(final MStorageDescriptor objPC, final boolean val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setBooleanField(objPC, 4, objPC.isStoredAsSubDirectories, val);
        }
        else {
            objPC.isStoredAsSubDirectories = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(4);
            }
        }
    }
    
    private static String jdoGetlocation(final MStorageDescriptor objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return objPC.jdoStateManager.getStringField(objPC, 5, objPC.location);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"location\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.location;
    }
    
    private static void jdoSetlocation(final MStorageDescriptor objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 5, objPC.location, val);
        }
        else {
            objPC.location = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(5);
            }
        }
    }
    
    private static int jdoGetnumBuckets(final MStorageDescriptor objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return objPC.jdoStateManager.getIntField(objPC, 6, objPC.numBuckets);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"numBuckets\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.numBuckets;
    }
    
    private static void jdoSetnumBuckets(final MStorageDescriptor objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 6, objPC.numBuckets, val);
        }
        else {
            objPC.numBuckets = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(6);
            }
        }
    }
    
    private static String jdoGetoutputFormat(final MStorageDescriptor objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 7)) {
            return objPC.jdoStateManager.getStringField(objPC, 7, objPC.outputFormat);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(7)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"outputFormat\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.outputFormat;
    }
    
    private static void jdoSetoutputFormat(final MStorageDescriptor objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 7, objPC.outputFormat, val);
        }
        else {
            objPC.outputFormat = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(7);
            }
        }
    }
    
    private static Map jdoGetparameters(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 8)) {
            return (Map)objPC.jdoStateManager.getObjectField(objPC, 8, objPC.parameters);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(8) && !((BitSet)objPC.jdoDetachedState[3]).get(8)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"parameters\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.parameters;
    }
    
    private static void jdoSetparameters(final MStorageDescriptor objPC, final Map val) {
        if (objPC.jdoStateManager == null) {
            objPC.parameters = (Map<String, String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 8, objPC.parameters, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(8);
        }
    }
    
    private static MSerDeInfo jdoGetserDeInfo(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 9)) {
            return (MSerDeInfo)objPC.jdoStateManager.getObjectField(objPC, 9, objPC.serDeInfo);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(9) && !((BitSet)objPC.jdoDetachedState[3]).get(9)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"serDeInfo\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.serDeInfo;
    }
    
    private static void jdoSetserDeInfo(final MStorageDescriptor objPC, final MSerDeInfo val) {
        if (objPC.jdoStateManager == null) {
            objPC.serDeInfo = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 9, objPC.serDeInfo, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(9);
        }
    }
    
    private static List jdoGetskewedColNames(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 10)) {
            return (List)objPC.jdoStateManager.getObjectField(objPC, 10, objPC.skewedColNames);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(10) && !((BitSet)objPC.jdoDetachedState[3]).get(10)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"skewedColNames\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.skewedColNames;
    }
    
    private static void jdoSetskewedColNames(final MStorageDescriptor objPC, final List val) {
        if (objPC.jdoStateManager == null) {
            objPC.skewedColNames = (List<String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 10, objPC.skewedColNames, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(10);
        }
    }
    
    private static Map jdoGetskewedColValueLocationMaps(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 11)) {
            return (Map)objPC.jdoStateManager.getObjectField(objPC, 11, objPC.skewedColValueLocationMaps);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(11) && !((BitSet)objPC.jdoDetachedState[3]).get(11)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"skewedColValueLocationMaps\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.skewedColValueLocationMaps;
    }
    
    private static void jdoSetskewedColValueLocationMaps(final MStorageDescriptor objPC, final Map val) {
        if (objPC.jdoStateManager == null) {
            objPC.skewedColValueLocationMaps = (Map<MStringList, String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 11, objPC.skewedColValueLocationMaps, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(11);
        }
    }
    
    private static List jdoGetskewedColValues(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 12)) {
            return (List)objPC.jdoStateManager.getObjectField(objPC, 12, objPC.skewedColValues);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(12) && !((BitSet)objPC.jdoDetachedState[3]).get(12)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"skewedColValues\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.skewedColValues;
    }
    
    private static void jdoSetskewedColValues(final MStorageDescriptor objPC, final List val) {
        if (objPC.jdoStateManager == null) {
            objPC.skewedColValues = (List<MStringList>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 12, objPC.skewedColValues, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(12);
        }
    }
    
    private static List jdoGetsortCols(final MStorageDescriptor objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 13)) {
            return (List)objPC.jdoStateManager.getObjectField(objPC, 13, objPC.sortCols);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(13) && !((BitSet)objPC.jdoDetachedState[3]).get(13)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"sortCols\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.sortCols;
    }
    
    private static void jdoSetsortCols(final MStorageDescriptor objPC, final List val) {
        if (objPC.jdoStateManager == null) {
            objPC.sortCols = (List<MOrder>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 13, objPC.sortCols, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(13);
        }
    }
}
