// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

abstract class TransactionStatementNode extends StatementNode
{
    int activationKind() {
        return 0;
    }
    
    public boolean isAtomic() {
        return false;
    }
    
    public boolean needsSavepoint() {
        return false;
    }
}
