// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

interface FilterCoder
{
    boolean changesSize();
    
    boolean nonLastOK();
    
    boolean lastOK();
}
