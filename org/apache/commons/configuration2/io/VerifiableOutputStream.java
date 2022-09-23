// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract class VerifiableOutputStream extends OutputStream
{
    public abstract void verify() throws IOException;
}
