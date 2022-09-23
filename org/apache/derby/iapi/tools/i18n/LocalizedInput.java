// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.tools.i18n;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;

public class LocalizedInput extends BufferedReader
{
    private InputStream in;
    
    public LocalizedInput(final InputStream inputStream) {
        super(new InputStreamReader(inputStream));
        this.in = inputStream;
    }
    
    LocalizedInput(final InputStream inputStream, final String charsetName) throws UnsupportedEncodingException {
        super(new InputStreamReader(inputStream, charsetName));
        this.in = inputStream;
    }
    
    public boolean isStandardInput() {
        return this.in == System.in;
    }
    
    public void close() throws IOException {
        if (!this.isStandardInput()) {
            super.close();
        }
    }
}
