// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.types.BooleanDataValue;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.TupleFilter;

public class DropDependencyFilter implements TupleFilter
{
    UUID providerID;
    UUIDFactory uuidFactory;
    DataValueFactory dataValueFactory;
    BooleanDataValue trueValue;
    BooleanDataValue falseValue;
    
    public DropDependencyFilter(final UUID providerID) {
        this.uuidFactory = null;
        this.dataValueFactory = null;
        this.providerID = providerID;
    }
    
    public void init(final ExecRow execRow) throws StandardException {
    }
    
    public BooleanDataValue execute(final ExecRow execRow) throws StandardException {
        if (this.providerID.equals(this.getUUIDFactory().recreateUUID(execRow.getColumn(3).getString()))) {
            return this.getTrueValue();
        }
        return this.getFalseValue();
    }
    
    private UUIDFactory getUUIDFactory() throws StandardException {
        if (this.uuidFactory == null) {
            this.uuidFactory = Monitor.getMonitor().getUUIDFactory();
        }
        return this.uuidFactory;
    }
    
    private BooleanDataValue getTrueValue() throws StandardException {
        if (this.trueValue == null) {
            this.trueValue = new SQLBoolean(true);
        }
        return this.trueValue;
    }
    
    private BooleanDataValue getFalseValue() throws StandardException {
        if (this.falseValue == null) {
            this.falseValue = new SQLBoolean(false);
        }
        return this.falseValue;
    }
}
