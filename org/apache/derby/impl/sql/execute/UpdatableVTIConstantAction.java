// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.catalog.UUID;
import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;

public class UpdatableVTIConstantAction extends WriteCursorConstantAction
{
    public int[] changedColumnIds;
    public int statementType;
    
    public UpdatableVTIConstantAction() {
    }
    
    public UpdatableVTIConstantAction(final int statementType, final boolean b, final int[] changedColumnIds) {
        super(0L, null, null, null, null, null, b, null, null, 0, null, null, null, null, null, false);
        this.statementType = statementType;
        this.changedColumnIds = changedColumnIds;
    }
    
    public int getTypeFormatId() {
        return 375;
    }
}
