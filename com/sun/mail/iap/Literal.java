// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.iap;

import java.io.IOException;
import java.io.OutputStream;

public interface Literal
{
    int size();
    
    void writeTo(final OutputStream p0) throws IOException;
}
