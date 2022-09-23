// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface Metadata
{
    ExtensionMetadata newExtensionMetadata(final String p0, final String p1, final String p2);
    
    int getNumberOfExtensions();
    
    ExtensionMetadata[] getExtensions();
    
    Metadata getParent();
}
