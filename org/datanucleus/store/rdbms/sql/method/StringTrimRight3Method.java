// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

public class StringTrimRight3Method extends StringTrim3Method
{
    @Override
    protected String getTrimSpecKeyword() {
        return "TRAILING";
    }
}
