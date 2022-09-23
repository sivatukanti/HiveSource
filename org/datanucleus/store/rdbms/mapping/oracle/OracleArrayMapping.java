// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import org.datanucleus.ExecutionContext;
import java.io.IOException;
import java.sql.SQLException;
import java.util.BitSet;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.datanucleus.util.TypeConversionHelper;
import org.datanucleus.store.rdbms.mapping.datastore.BlobImpl;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.io.Serializable;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.mapping.java.ArrayMapping;

public class OracleArrayMapping extends ArrayMapping
{
    @Override
    public void postInsert(final ObjectProvider ownerOP) {
        if (this.containerIsStoredInSingleColumn()) {
            final Object value = ownerOP.provideField(this.mmd.getAbsoluteFieldNumber());
            if (value == null) {
                return;
            }
            final ExecutionContext ec = ownerOP.getExecutionContext();
            SCOUtils.validateObjectsForWriting(ec, value);
            byte[] bytes = new byte[0];
            try {
                if (this.mmd.isSerialized()) {
                    if (!(value instanceof Serializable)) {
                        throw new NucleusDataStoreException(OracleArrayMapping.LOCALISER.msg("055005", value.getClass().getName()));
                    }
                    final BlobImpl b = new BlobImpl(value);
                    bytes = b.getBytes(0L, (int)b.length());
                }
                else if (value instanceof boolean[]) {
                    bytes = TypeConversionHelper.getByteArrayFromBooleanArray(value);
                }
                else if (value instanceof char[]) {
                    bytes = TypeConversionHelper.getByteArrayFromCharArray(value);
                }
                else if (value instanceof double[]) {
                    bytes = TypeConversionHelper.getByteArrayFromDoubleArray(value);
                }
                else if (value instanceof float[]) {
                    bytes = TypeConversionHelper.getByteArrayFromFloatArray(value);
                }
                else if (value instanceof int[]) {
                    bytes = TypeConversionHelper.getByteArrayFromIntArray(value);
                }
                else if (value instanceof long[]) {
                    bytes = TypeConversionHelper.getByteArrayFromLongArray(value);
                }
                else if (value instanceof short[]) {
                    bytes = TypeConversionHelper.getByteArrayFromShortArray(value);
                }
                else if (value instanceof Boolean[]) {
                    bytes = TypeConversionHelper.getByteArrayFromBooleanObjectArray(value);
                }
                else if (value instanceof Byte[]) {
                    bytes = TypeConversionHelper.getByteArrayFromByteObjectArray(value);
                }
                else if (value instanceof Character[]) {
                    bytes = TypeConversionHelper.getByteArrayFromCharObjectArray(value);
                }
                else if (value instanceof Double[]) {
                    bytes = TypeConversionHelper.getByteArrayFromDoubleObjectArray(value);
                }
                else if (value instanceof Float[]) {
                    bytes = TypeConversionHelper.getByteArrayFromFloatObjectArray(value);
                }
                else if (value instanceof Integer[]) {
                    bytes = TypeConversionHelper.getByteArrayFromIntObjectArray(value);
                }
                else if (value instanceof Long[]) {
                    bytes = TypeConversionHelper.getByteArrayFromLongObjectArray(value);
                }
                else if (value instanceof Short[]) {
                    bytes = TypeConversionHelper.getByteArrayFromShortObjectArray(value);
                }
                else if (value instanceof BigDecimal[]) {
                    bytes = TypeConversionHelper.getByteArrayFromBigDecimalArray(value);
                }
                else if (value instanceof BigInteger[]) {
                    bytes = TypeConversionHelper.getByteArrayFromBigIntegerArray(value);
                }
                else if (value instanceof byte[]) {
                    bytes = (byte[])value;
                }
                else if (value instanceof BitSet) {
                    bytes = TypeConversionHelper.getByteArrayFromBooleanArray(TypeConversionHelper.getBooleanArrayFromBitSet((BitSet)value));
                }
                else {
                    if (!(value instanceof Serializable)) {
                        throw new NucleusDataStoreException(OracleArrayMapping.LOCALISER.msg("055005", value.getClass().getName()));
                    }
                    final BlobImpl b = new BlobImpl(value);
                    bytes = b.getBytes(0L, (int)b.length());
                }
            }
            catch (SQLException e) {
                throw new NucleusDataStoreException(OracleArrayMapping.LOCALISER.msg("055001", "Object", "" + value, this.mmd, e.getMessage()), e);
            }
            catch (IOException ex) {}
            OracleBlobRDBMSMapping.updateBlobColumn(ownerOP, this.getTable(), this.getDatastoreMapping(0), bytes);
        }
        else {
            super.postInsert(ownerOP);
        }
    }
    
    @Override
    public void postUpdate(final ObjectProvider sm) {
        if (this.containerIsStoredInSingleColumn()) {
            this.postInsert(sm);
        }
        else {
            super.postUpdate(sm);
        }
    }
}
