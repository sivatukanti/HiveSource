// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

public interface DefaultInfo
{
    String getDefaultText();
    
    String[] getReferencedColumnNames();
    
    boolean isDefaultValueAutoinc();
    
    boolean isGeneratedColumn();
    
    String getOriginalCurrentSchema();
}
