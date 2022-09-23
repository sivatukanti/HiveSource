// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class StringTrimLeftMethod extends SimpleStringMethod
{
    @Override
    protected String getFunctionName() {
        return "LTRIM";
    }
}
