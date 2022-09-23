// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.types.TypeManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.types.converters.TypeConverter;

public class TypeConverterMapping extends SingleFieldMapping
{
    TypeConverter converter;
    
    @Override
    public void initialize(final RDBMSStoreManager storeMgr, final String type) {
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        final Class fieldType = clr.classForName(type);
        this.converter = storeMgr.getNucleusContext().getTypeManager().getDefaultTypeConverterForType(fieldType);
        super.initialize(storeMgr, type);
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        this.initialize(fmd, table, clr, null);
    }
    
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr, final TypeConverter conv) {
        if (conv != null) {
            this.converter = conv;
        }
        else if (fmd.getTypeConverterName() != null) {
            this.converter = table.getStoreManager().getNucleusContext().getTypeManager().getTypeConverterForName(fmd.getTypeConverterName());
        }
        else {
            this.converter = table.getStoreManager().getNucleusContext().getTypeManager().getDefaultTypeConverterForType(fmd.getType());
            if (this.converter == null) {
                throw new NucleusException("Attempt to create TypeConverterMapping when no type converter defined for member " + fmd.getFullFieldName());
            }
        }
        super.initialize(fmd, table, clr);
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return TypeManager.getDatastoreTypeForTypeConverter(this.converter, this.getJavaType()).getName();
    }
    
    @Override
    public Class getJavaType() {
        return this.mmd.getType();
    }
    
    @Override
    public void setBoolean(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final boolean value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setBoolean(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public boolean getBoolean(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return false;
        }
        final Boolean datastoreValue = this.getDatastoreMapping(0).getBoolean(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setByte(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final byte value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setByte(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public byte getByte(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return 0;
        }
        final Byte datastoreValue = this.getDatastoreMapping(0).getByte(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setChar(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final char value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setChar(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public char getChar(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return '\0';
        }
        final Character datastoreValue = this.getDatastoreMapping(0).getChar(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setDouble(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final double value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setDouble(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public double getDouble(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return 0.0;
        }
        final Double datastoreValue = this.getDatastoreMapping(0).getDouble(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setFloat(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final float value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setFloat(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public float getFloat(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return 0.0f;
        }
        final Float datastoreValue = this.getDatastoreMapping(0).getFloat(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setInt(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final int value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setInt(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public int getInt(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return 0;
        }
        final Integer datastoreValue = this.getDatastoreMapping(0).getInt(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setLong(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final long value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setLong(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public long getLong(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return 0L;
        }
        final Long datastoreValue = this.getDatastoreMapping(0).getLong(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setShort(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final short value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setShort(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public short getShort(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return 0;
        }
        final Short datastoreValue = this.getDatastoreMapping(0).getShort(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setString(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final String value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setString(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public String getString(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return null;
        }
        final String datastoreValue = this.getDatastoreMapping(0).getString(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        if (exprIndex == null) {
            return;
        }
        this.getDatastoreMapping(0).setObject(ps, exprIndex[0], this.converter.toDatastoreType(value));
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return null;
        }
        final Object datastoreValue = this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        return (datastoreValue != null) ? this.converter.toMemberType(datastoreValue) : null;
    }
}
