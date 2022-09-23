// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.nio.charset.Charset;

public final class StandardCharset
{
    public static final Charset UTF_8;
    
    static {
        UTF_8 = Charset.forName("UTF-8");
    }
    
    private StandardCharset() {
    }
}
