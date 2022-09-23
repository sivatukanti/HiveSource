// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import java.net.MalformedURLException;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import java.util.Iterator;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import java.io.File;
import org.apache.tools.ant.BuildException;
import java.util.HashMap;

public class SchemaValidate extends XMLValidateTask
{
    private HashMap schemaLocations;
    private boolean fullChecking;
    private boolean disableDTD;
    private SchemaLocation anonymousSchema;
    public static final String ERROR_SAX_1 = "SAX1 parsers are not supported";
    public static final String ERROR_NO_XSD_SUPPORT = "Parser does not support Xerces or JAXP schema features";
    public static final String ERROR_TOO_MANY_DEFAULT_SCHEMAS = "Only one of defaultSchemaFile and defaultSchemaURL allowed";
    public static final String ERROR_PARSER_CREATION_FAILURE = "Could not create parser";
    public static final String MESSAGE_ADDING_SCHEMA = "Adding schema ";
    public static final String ERROR_DUPLICATE_SCHEMA = "Duplicate declaration of schema ";
    
    public SchemaValidate() {
        this.schemaLocations = new HashMap();
        this.fullChecking = true;
        this.disableDTD = false;
    }
    
    @Override
    public void init() throws BuildException {
        super.init();
        this.setLenient(false);
    }
    
    public boolean enableXercesSchemaValidation() {
        try {
            this.setFeature("http://apache.org/xml/features/validation/schema", true);
            this.setNoNamespaceSchemaProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
        }
        catch (BuildException e) {
            this.log(e.toString(), 3);
            return false;
        }
        return true;
    }
    
    private void setNoNamespaceSchemaProperty(final String property) {
        final String anonSchema = this.getNoNamespaceSchemaURL();
        if (anonSchema != null) {
            this.setProperty(property, anonSchema);
        }
    }
    
    public boolean enableJAXP12SchemaValidation() {
        try {
            this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            this.setNoNamespaceSchemaProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
        }
        catch (BuildException e) {
            this.log(e.toString(), 3);
            return false;
        }
        return true;
    }
    
    public void addConfiguredSchema(final SchemaLocation location) {
        this.log("adding schema " + location, 4);
        location.validateNamespace();
        final SchemaLocation old = this.schemaLocations.get(location.getNamespace());
        if (old != null && !old.equals(location)) {
            throw new BuildException("Duplicate declaration of schema " + location);
        }
        this.schemaLocations.put(location.getNamespace(), location);
    }
    
    public void setFullChecking(final boolean fullChecking) {
        this.fullChecking = fullChecking;
    }
    
    protected void createAnonymousSchema() {
        if (this.anonymousSchema == null) {
            this.anonymousSchema = new SchemaLocation();
        }
        this.anonymousSchema.setNamespace("(no namespace)");
    }
    
    public void setNoNamespaceURL(final String defaultSchemaURL) {
        this.createAnonymousSchema();
        this.anonymousSchema.setUrl(defaultSchemaURL);
    }
    
    public void setNoNamespaceFile(final File defaultSchemaFile) {
        this.createAnonymousSchema();
        this.anonymousSchema.setFile(defaultSchemaFile);
    }
    
    public void setDisableDTD(final boolean disableDTD) {
        this.disableDTD = disableDTD;
    }
    
    @Override
    protected void initValidator() {
        super.initValidator();
        if (this.isSax1Parser()) {
            throw new BuildException("SAX1 parsers are not supported");
        }
        this.setFeature("http://xml.org/sax/features/namespaces", true);
        if (!this.enableXercesSchemaValidation() && !this.enableJAXP12SchemaValidation()) {
            throw new BuildException("Parser does not support Xerces or JAXP schema features");
        }
        this.setFeature("http://apache.org/xml/features/validation/schema-full-checking", this.fullChecking);
        this.setFeatureIfSupported("http://apache.org/xml/features/disallow-doctype-decl", this.disableDTD);
        this.addSchemaLocations();
    }
    
    @Override
    protected XMLReader createDefaultReader() {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        XMLReader reader = null;
        try {
            final SAXParser saxParser = factory.newSAXParser();
            reader = saxParser.getXMLReader();
        }
        catch (ParserConfigurationException e) {
            throw new BuildException("Could not create parser", e);
        }
        catch (SAXException e2) {
            throw new BuildException("Could not create parser", e2);
        }
        return reader;
    }
    
    protected void addSchemaLocations() {
        final Iterator it = this.schemaLocations.values().iterator();
        final StringBuffer buffer = new StringBuffer();
        int count = 0;
        while (it.hasNext()) {
            if (count > 0) {
                buffer.append(' ');
            }
            final SchemaLocation schemaLocation = it.next();
            final String tuple = schemaLocation.getURIandLocation();
            buffer.append(tuple);
            this.log("Adding schema " + tuple, 3);
            ++count;
        }
        if (count > 0) {
            this.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", buffer.toString());
        }
    }
    
    protected String getNoNamespaceSchemaURL() {
        if (this.anonymousSchema == null) {
            return null;
        }
        return this.anonymousSchema.getSchemaLocationURL();
    }
    
    protected void setFeatureIfSupported(final String feature, final boolean value) {
        try {
            this.getXmlReader().setFeature(feature, value);
        }
        catch (SAXNotRecognizedException e) {
            this.log("Not recognizied: " + feature, 3);
        }
        catch (SAXNotSupportedException e2) {
            this.log("Not supported: " + feature, 3);
        }
    }
    
    @Override
    protected void onSuccessfulValidation(final int fileProcessed) {
        this.log(fileProcessed + " file(s) have been successfully validated.", 3);
    }
    
    public static class SchemaLocation
    {
        private String namespace;
        private File file;
        private String url;
        public static final String ERROR_NO_URI = "No namespace URI";
        public static final String ERROR_TWO_LOCATIONS = "Both URL and File were given for schema ";
        public static final String ERROR_NO_FILE = "File not found: ";
        public static final String ERROR_NO_URL_REPRESENTATION = "Cannot make a URL of ";
        public static final String ERROR_NO_LOCATION = "No file or URL supplied for the schema ";
        
        public String getNamespace() {
            return this.namespace;
        }
        
        public void setNamespace(final String namespace) {
            this.namespace = namespace;
        }
        
        public File getFile() {
            return this.file;
        }
        
        public void setFile(final File file) {
            this.file = file;
        }
        
        public String getUrl() {
            return this.url;
        }
        
        public void setUrl(final String url) {
            this.url = url;
        }
        
        public String getSchemaLocationURL() {
            final boolean hasFile = this.file != null;
            final boolean hasURL = this.isSet(this.url);
            if (!hasFile && !hasURL) {
                throw new BuildException("No file or URL supplied for the schema " + this.namespace);
            }
            if (hasFile && hasURL) {
                throw new BuildException("Both URL and File were given for schema " + this.namespace);
            }
            String schema = this.url;
            if (hasFile) {
                if (!this.file.exists()) {
                    throw new BuildException("File not found: " + this.file);
                }
                try {
                    schema = FileUtils.getFileUtils().getFileURL(this.file).toString();
                }
                catch (MalformedURLException e) {
                    throw new BuildException("Cannot make a URL of " + this.file, e);
                }
            }
            return schema;
        }
        
        public String getURIandLocation() throws BuildException {
            this.validateNamespace();
            final StringBuffer buffer = new StringBuffer();
            buffer.append(this.namespace);
            buffer.append(' ');
            buffer.append(this.getSchemaLocationURL());
            return new String(buffer);
        }
        
        public void validateNamespace() {
            if (!this.isSet(this.getNamespace())) {
                throw new BuildException("No namespace URI");
            }
        }
        
        private boolean isSet(final String property) {
            return property != null && property.length() != 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SchemaLocation)) {
                return false;
            }
            final SchemaLocation schemaLocation = (SchemaLocation)o;
            Label_0054: {
                if (this.file != null) {
                    if (this.file.equals(schemaLocation.file)) {
                        break Label_0054;
                    }
                }
                else if (schemaLocation.file == null) {
                    break Label_0054;
                }
                return false;
            }
            Label_0087: {
                if (this.namespace != null) {
                    if (this.namespace.equals(schemaLocation.namespace)) {
                        break Label_0087;
                    }
                }
                else if (schemaLocation.namespace == null) {
                    break Label_0087;
                }
                return false;
            }
            if (this.url != null) {
                if (this.url.equals(schemaLocation.url)) {
                    return true;
                }
            }
            else if (schemaLocation.url == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = (this.namespace != null) ? this.namespace.hashCode() : 0;
            result = 29 * result + ((this.file != null) ? this.file.hashCode() : 0);
            result = 29 * result + ((this.url != null) ? this.url.hashCode() : 0);
            return result;
        }
        
        @Override
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append((this.namespace != null) ? this.namespace : "(anonymous)");
            buffer.append(' ');
            buffer.append((this.url != null) ? (this.url + " ") : "");
            buffer.append((this.file != null) ? this.file.getAbsolutePath() : "");
            return buffer.toString();
        }
    }
}
