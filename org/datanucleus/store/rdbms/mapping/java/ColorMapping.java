// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.NucleusContext;
import java.awt.Color;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class ColorMapping extends SingleFieldMultiMapping
{
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(mmd, table, clr);
        this.addColumns();
    }
    
    @Override
    public void initialize(final RDBMSStoreManager storeMgr, final String type) {
        super.initialize(storeMgr, type);
        this.addColumns();
    }
    
    protected void addColumns() {
        this.addColumns(ClassNameConstants.INT);
        this.addColumns(ClassNameConstants.INT);
        this.addColumns(ClassNameConstants.INT);
        this.addColumns(ClassNameConstants.INT);
    }
    
    @Override
    public Class getJavaType() {
        return Color.class;
    }
    
    @Override
    public Object getValueForDatastoreMapping(final NucleusContext nucleusCtx, final int index, final Object value) {
        if (index == 0) {
            return ((Color)value).getRed();
        }
        if (index == 1) {
            return ((Color)value).getRed();
        }
        if (index == 2) {
            return ((Color)value).getRed();
        }
        if (index == 3) {
            return ((Color)value).getRed();
        }
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        final Color color = (Color)value;
        if (color == null) {
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], null);
            this.getDatastoreMapping(1).setObject(ps, exprIndex[1], null);
            this.getDatastoreMapping(2).setObject(ps, exprIndex[2], null);
            this.getDatastoreMapping(3).setObject(ps, exprIndex[3], null);
        }
        else {
            this.getDatastoreMapping(0).setInt(ps, exprIndex[0], color.getRed());
            this.getDatastoreMapping(1).setInt(ps, exprIndex[1], color.getGreen());
            this.getDatastoreMapping(2).setInt(ps, exprIndex[2], color.getBlue());
            this.getDatastoreMapping(3).setInt(ps, exprIndex[3], color.getAlpha());
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        try {
            if (this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]) == null) {
                return null;
            }
        }
        catch (Exception ex) {}
        final int red = this.getDatastoreMapping(0).getInt(resultSet, exprIndex[0]);
        final int green = this.getDatastoreMapping(1).getInt(resultSet, exprIndex[1]);
        final int blue = this.getDatastoreMapping(2).getInt(resultSet, exprIndex[2]);
        final int alpha = this.getDatastoreMapping(3).getInt(resultSet, exprIndex[3]);
        return new Color(red, green, blue, alpha);
    }
}
