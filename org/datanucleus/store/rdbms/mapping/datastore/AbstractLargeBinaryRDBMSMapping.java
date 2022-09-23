// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.sql.ResultSet;
import java.io.IOException;
import java.io.InputStream;
import org.datanucleus.ClassNameConstants;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.datanucleus.util.TypeConversionHelper;
import java.io.Serializable;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public abstract class AbstractLargeBinaryRDBMSMapping extends AbstractDatastoreMapping
{
    public AbstractLargeBinaryRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    protected void initialize() {
        this.initTypeInfo();
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int param, final Object value) {
        if (value == null) {
            try {
                if (this.column != null && this.column.isDefaultable() && this.column.getDefaultValue() != null) {
                    ps.setBytes(param, this.column.getDefaultValue().toString().trim().getBytes());
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType());
                }
                return;
            }
            catch (SQLException sqle) {
                throw new NucleusDataStoreException(AbstractLargeBinaryRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + param, this.column, sqle.getMessage()), sqle);
            }
        }
        try {
            if (this.getJavaTypeMapping().isSerialised()) {
                if (!(value instanceof Serializable)) {
                    throw new NucleusDataStoreException(AbstractLargeBinaryRDBMSMapping.LOCALISER_RDBMS.msg("055005", value.getClass().getName()));
                }
                final BlobImpl b = new BlobImpl(value);
                ps.setBytes(param, b.getBytes(0L, (int)b.length()));
            }
            else if (value instanceof boolean[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromBooleanArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof char[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromCharArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof double[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromDoubleArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof float[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromFloatArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof int[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromIntArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof long[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromLongArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof short[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromShortArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Boolean[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromBooleanObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Byte[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromByteObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Character[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromCharObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Double[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromDoubleObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Float[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromFloatObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Integer[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromIntObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Long[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromLongObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof Short[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromShortObjectArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof BigDecimal[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromBigDecimalArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof BigInteger[]) {
                final byte[] data = TypeConversionHelper.getByteArrayFromBigIntegerArray(value);
                ps.setBytes(param, data);
            }
            else if (value instanceof byte[]) {
                ps.setBytes(param, (byte[])value);
            }
            else if (value instanceof BitSet) {
                final byte[] data = TypeConversionHelper.getByteArrayFromBooleanArray(TypeConversionHelper.getBooleanArrayFromBitSet((BitSet)value));
                ps.setBytes(param, data);
            }
            else if (value instanceof BufferedImage) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
                ImageIO.write((RenderedImage)value, "jpg", baos);
                final byte[] buffer = baos.toByteArray();
                baos.close();
                final ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ps.setBytes(param, buffer);
                bais.close();
            }
            else {
                if (!(value instanceof Serializable)) {
                    throw new NucleusDataStoreException(AbstractLargeBinaryRDBMSMapping.LOCALISER_RDBMS.msg("055005", value.getClass().getName()));
                }
                final BlobImpl b = new BlobImpl(value);
                ps.setBytes(param, b.getBytes(0L, (int)b.length()));
            }
        }
        catch (Exception e) {
            throw new NucleusDataStoreException(AbstractLargeBinaryRDBMSMapping.LOCALISER_RDBMS.msg("055001", "Object", "" + value, this.column, e.getMessage()), e);
        }
    }
    
    protected Object getObjectForBytes(final byte[] bytes, final int param) {
        if (this.getJavaTypeMapping().isSerialised()) {
            try {
                final BlobImpl blob = new BlobImpl(bytes);
                return blob.getObject();
            }
            catch (SQLException sqle) {
                return null;
            }
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.BOOLEAN_ARRAY)) {
            return TypeConversionHelper.getBooleanArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.BYTE_ARRAY)) {
            return bytes;
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.CHAR_ARRAY)) {
            return TypeConversionHelper.getCharArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.DOUBLE_ARRAY)) {
            return TypeConversionHelper.getDoubleArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.FLOAT_ARRAY)) {
            return TypeConversionHelper.getFloatArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.INT_ARRAY)) {
            return TypeConversionHelper.getIntArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.LONG_ARRAY)) {
            return TypeConversionHelper.getLongArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.SHORT_ARRAY)) {
            return TypeConversionHelper.getShortArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_BOOLEAN_ARRAY)) {
            return TypeConversionHelper.getBooleanObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_BYTE_ARRAY)) {
            return TypeConversionHelper.getByteObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_CHARACTER_ARRAY)) {
            return TypeConversionHelper.getCharObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_DOUBLE_ARRAY)) {
            return TypeConversionHelper.getDoubleObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_FLOAT_ARRAY)) {
            return TypeConversionHelper.getFloatObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_INTEGER_ARRAY)) {
            return TypeConversionHelper.getIntObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_LONG_ARRAY)) {
            return TypeConversionHelper.getLongObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_SHORT_ARRAY)) {
            return TypeConversionHelper.getShortObjectArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(BigDecimal[].class.getName())) {
            return TypeConversionHelper.getBigDecimalArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getType().equals(BigInteger[].class.getName())) {
            return TypeConversionHelper.getBigIntegerArrayFromByteArray(bytes);
        }
        if (this.getJavaTypeMapping().getJavaType() != null && this.getJavaTypeMapping().getJavaType().getName().equals(BitSet.class.getName())) {
            return TypeConversionHelper.getBitSetFromBooleanArray((boolean[])TypeConversionHelper.getBooleanArrayFromByteArray(bytes));
        }
        if (this.getJavaTypeMapping().getJavaType() != null && this.getJavaTypeMapping().getJavaType().getName().equals(BufferedImage.class.getName())) {
            try {
                return ImageIO.read(new ByteArrayInputStream(bytes));
            }
            catch (IOException e) {
                throw new NucleusDataStoreException(AbstractLargeBinaryRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, e.getMessage()), e);
            }
        }
        try {
            final BlobImpl blob = new BlobImpl(bytes);
            return blob.getObject();
        }
        catch (SQLException sqle) {
            return null;
        }
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        byte[] bytes = null;
        try {
            bytes = rs.getBytes(param);
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException(AbstractLargeBinaryRDBMSMapping.LOCALISER_RDBMS.msg("055002", "Object", "" + param, this.column, sqle.getMessage()), sqle);
        }
        if (bytes == null) {
            return null;
        }
        return this.getObjectForBytes(bytes, param);
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int exprIndex, final String value) {
        this.setObject(ps, exprIndex, value);
    }
    
    @Override
    public String getString(final ResultSet resultSet, final int exprIndex) {
        final Object obj = this.getObject(resultSet, exprIndex);
        return (String)obj;
    }
}
