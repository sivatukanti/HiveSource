// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv.bean;

import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public interface MappingStrategy<T>
{
    PropertyDescriptor findDescriptor(final int p0) throws IntrospectionException;
    
    T createBean() throws InstantiationException, IllegalAccessException;
    
    void captureHeader(final CSVReader p0) throws IOException;
}
