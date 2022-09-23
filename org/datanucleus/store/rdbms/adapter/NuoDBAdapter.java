// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.Index;
import java.sql.DatabaseMetaData;

public class NuoDBAdapter extends BaseDatastoreAdapter
{
    public NuoDBAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.remove("DeferredConstraints");
        if (this.maxTableNameLength <= 0) {
            this.maxTableNameLength = 128;
        }
        if (this.maxColumnNameLength <= 0) {
            this.maxColumnNameLength = 128;
        }
        if (this.maxConstraintNameLength <= 0) {
            this.maxConstraintNameLength = 128;
        }
        if (this.maxIndexNameLength <= 0) {
            this.maxIndexNameLength = 128;
        }
    }
    
    @Override
    public String getCreateIndexStatement(final Index idx, final IdentifierFactory factory) {
        final String idxIdentifier = factory.getIdentifierInAdapterCase(idx.getName());
        return "CREATE " + (idx.getUnique() ? "UNIQUE " : "") + "INDEX " + idxIdentifier + " ON " + idx.getTable().toString() + ' ' + idx + ((idx.getExtendedIndexSettings() == null) ? "" : (" " + idx.getExtendedIndexSettings()));
    }
}
