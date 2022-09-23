// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.services.compiler.LocalField;

class BCLocalField implements LocalField
{
    final int cpi;
    final Type type;
    
    BCLocalField(final Type type, final int cpi) {
        this.cpi = cpi;
        this.type = type;
    }
}
