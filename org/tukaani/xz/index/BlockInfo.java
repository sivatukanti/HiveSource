// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.index;

import org.tukaani.xz.common.StreamFlags;

public class BlockInfo
{
    public StreamFlags streamFlags;
    public long compressedOffset;
    public long uncompressedOffset;
    public long unpaddedSize;
    public long uncompressedSize;
}
