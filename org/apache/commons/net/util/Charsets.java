// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.util;

import java.nio.charset.Charset;

public class Charsets
{
    public static Charset toCharset(final String charsetName) {
        return (charsetName == null) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }
    
    public static Charset toCharset(final String charsetName, final String defaultCharsetName) {
        return (charsetName == null) ? Charset.forName(defaultCharsetName) : Charset.forName(charsetName);
    }
}
