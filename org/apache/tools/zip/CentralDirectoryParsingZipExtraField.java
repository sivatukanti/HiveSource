// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.zip;

import java.util.zip.ZipException;

public interface CentralDirectoryParsingZipExtraField extends ZipExtraField
{
    void parseFromCentralDirectoryData(final byte[] p0, final int p1, final int p2) throws ZipException;
}
