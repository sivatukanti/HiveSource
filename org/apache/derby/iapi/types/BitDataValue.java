// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.sql.Blob;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.StreamStorable;

public interface BitDataValue extends ConcatableDataValue, StreamStorable
{
    BitDataValue concatenate(final BitDataValue p0, final BitDataValue p1, final BitDataValue p2) throws StandardException;
    
    void setValue(final Blob p0) throws StandardException;
}
