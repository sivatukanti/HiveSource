// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.iapi.jdbc.ResourceAdapter;
import javax.sql.XADataSource;

public interface EmbeddedXADataSourceInterface extends EmbeddedDataSourceInterface, XADataSource
{
    ResourceAdapter getResourceAdapter();
}
