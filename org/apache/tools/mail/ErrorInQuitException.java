// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.mail;

import java.io.IOException;

public class ErrorInQuitException extends IOException
{
    public ErrorInQuitException(final IOException e) {
        super(e.getMessage());
    }
}
