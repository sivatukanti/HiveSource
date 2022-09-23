// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.util;

import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallerImpl;

public class ClassLoaderRetriever
{
    public static ClassLoader getClassLoader() {
        ClassLoader cl = UnmarshallerImpl.class.getClassLoader();
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        return cl;
    }
}
