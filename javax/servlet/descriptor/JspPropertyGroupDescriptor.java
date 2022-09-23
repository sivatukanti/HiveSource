// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.descriptor;

import java.util.Collection;

public interface JspPropertyGroupDescriptor
{
    Collection<String> getUrlPatterns();
    
    String getElIgnored();
    
    String getPageEncoding();
    
    String getScriptingInvalid();
    
    String getIsXml();
    
    Collection<String> getIncludePreludes();
    
    Collection<String> getIncludeCodas();
    
    String getDeferredSyntaxAllowedAsLiteral();
    
    String getTrimDirectiveWhitespaces();
    
    String getDefaultContentType();
    
    String getBuffer();
    
    String getErrorOnUndeclaredNamespace();
}
