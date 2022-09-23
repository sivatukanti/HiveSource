// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

public interface PathSegment
{
    String getPath();
    
    MultivaluedMap<String, String> getMatrixParameters();
}
