// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.index;

class IndexRecord
{
    final long unpadded;
    final long uncompressed;
    
    IndexRecord(final long unpadded, final long uncompressed) {
        this.unpadded = unpadded;
        this.uncompressed = uncompressed;
    }
}
