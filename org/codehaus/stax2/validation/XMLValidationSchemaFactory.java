// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;
import java.util.Enumeration;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import javax.xml.stream.FactoryConfigurationError;
import java.util.HashMap;

public abstract class XMLValidationSchemaFactory
{
    public static final String INTERNAL_ID_SCHEMA_DTD = "dtd";
    public static final String INTERNAL_ID_SCHEMA_RELAXNG = "relaxng";
    public static final String INTERNAL_ID_SCHEMA_W3C = "w3c";
    public static final String INTERNAL_ID_SCHEMA_TREX = "trex";
    static final HashMap sSchemaIds;
    static final String JAXP_PROP_FILENAME = "jaxp.properties";
    public static final String SYSTEM_PROPERTY_FOR_IMPL = "org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String SERVICE_DEFINITION_PATH = "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String P_IS_NAMESPACE_AWARE = "org.codehaus2.stax2.validation.isNamespaceAware";
    public static final String P_ENABLE_CACHING = "org.codehaus2.stax2.validation.enableCaching";
    protected final String mSchemaType;
    
    protected XMLValidationSchemaFactory(final String mSchemaType) {
        this.mSchemaType = mSchemaType;
    }
    
    public static XMLValidationSchemaFactory newInstance(final String s) throws FactoryConfigurationError {
        return newInstance(s, Thread.currentThread().getContextClassLoader());
    }
    
    public static XMLValidationSchemaFactory newInstance(final String s, final ClassLoader classLoader) throws FactoryConfigurationError {
        final String s2 = XMLValidationSchemaFactory.sSchemaIds.get(s);
        if (s2 == null) {
            throw new FactoryConfigurationError("Unrecognized schema type (id '" + s + "')");
        }
        final String string = "org.codehaus.stax2.validation.XMLValidationSchemaFactory." + s2;
        Exception ex = null;
        try {
            final String property = System.getProperty(string);
            if (property != null && property.length() > 0) {
                return createNewInstance(classLoader, property);
            }
        }
        catch (SecurityException ex2) {
            ex = ex2;
        }
        try {
            final File file = new File(new File(new File(System.getProperty("java.home")), "lib"), "jaxp.properties");
            if (file.exists()) {
                try {
                    final Properties properties = new Properties();
                    properties.load(new FileInputStream(file));
                    final String property2 = properties.getProperty(string);
                    if (property2 != null && property2.length() > 0) {
                        return createNewInstance(classLoader, property2);
                    }
                }
                catch (IOException ex5) {}
            }
        }
        catch (SecurityException ex3) {
            ex = ex3;
        }
        final String string2 = "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory." + s2;
        try {
            Enumeration<URL> enumeration;
            if (classLoader == null) {
                enumeration = ClassLoader.getSystemResources(string2);
            }
            else {
                enumeration = classLoader.getResources(string2);
            }
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(enumeration.nextElement().openStream(), "ISO-8859-1"));
                    String s3 = null;
                    try {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            final String trim = line.trim();
                            if (trim.length() > 0 && trim.charAt(0) != '#') {
                                s3 = trim;
                                break;
                            }
                        }
                    }
                    finally {
                        bufferedReader.close();
                    }
                    if (s3 != null && s3.length() > 0) {
                        return createNewInstance(classLoader, s3);
                    }
                }
            }
        }
        catch (SecurityException ex4) {
            ex = ex4;
        }
        catch (IOException ex6) {}
        final String string3 = "No XMLValidationSchemaFactory implementation class specified or accessible (via system property '" + string + "', or service definition under '" + string2 + "')";
        if (ex != null) {
            throw new FactoryConfigurationError(string3 + " (possibly caused by: " + ex + ")", ex);
        }
        throw new FactoryConfigurationError(string3);
    }
    
    public XMLValidationSchema createSchema(final InputStream inputStream) throws XMLStreamException {
        return this.createSchema(inputStream, null);
    }
    
    public XMLValidationSchema createSchema(final InputStream inputStream, final String s) throws XMLStreamException {
        return this.createSchema(inputStream, s, null, null);
    }
    
    public abstract XMLValidationSchema createSchema(final InputStream p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    public XMLValidationSchema createSchema(final Reader reader) throws XMLStreamException {
        return this.createSchema(reader, null, null);
    }
    
    public abstract XMLValidationSchema createSchema(final Reader p0, final String p1, final String p2) throws XMLStreamException;
    
    public abstract XMLValidationSchema createSchema(final URL p0) throws XMLStreamException;
    
    public abstract XMLValidationSchema createSchema(final File p0) throws XMLStreamException;
    
    public abstract boolean isPropertySupported(final String p0);
    
    public abstract boolean setProperty(final String p0, final Object p1);
    
    public abstract Object getProperty(final String p0);
    
    public final String getSchemaType() {
        return this.mSchemaType;
    }
    
    private static XMLValidationSchemaFactory createNewInstance(final ClassLoader classLoader, final String s) throws FactoryConfigurationError {
        try {
            Class<?> clazz;
            if (classLoader == null) {
                clazz = Class.forName(s);
            }
            else {
                clazz = classLoader.loadClass(s);
            }
            return (XMLValidationSchemaFactory)clazz.newInstance();
        }
        catch (ClassNotFoundException e) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + s + "' not found (missing jar in classpath?)", e);
        }
        catch (Exception ex) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + s + "' could not be instantiated: " + ex, ex);
        }
    }
    
    static {
        (sSchemaIds = new HashMap()).put("http://www.w3.org/XML/1998/namespace", "dtd");
        XMLValidationSchemaFactory.sSchemaIds.put("http://relaxng.org/ns/structure/0.9", "relaxng");
        XMLValidationSchemaFactory.sSchemaIds.put("http://www.w3.org/2001/XMLSchema", "w3c");
        XMLValidationSchemaFactory.sSchemaIds.put("http://www.thaiopensource.com/trex", "trex");
    }
}
