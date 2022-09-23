// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class RootElementWrapper
{
    public static InputStream wrapInput(final InputStream inputStream, final String rootName) throws UnsupportedEncodingException {
        final SequenceInputStream sis = new SequenceInputStream(new ByteArrayInputStream(String.format("{\"%s\":", rootName).getBytes("UTF-8")), inputStream);
        return new SequenceInputStream(sis, new ByteArrayInputStream("}".getBytes("UTF-8")));
    }
    
    public static InputStream unwrapInput(final InputStream inputStream) throws IOException {
        return new JsonRootEatingInputStreamFilter(inputStream);
    }
    
    public static OutputStream unwrapOutput(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException("to be implemented yet");
    }
    
    public static OutputStream wrapOutput(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException("to be implemented yet");
    }
}
