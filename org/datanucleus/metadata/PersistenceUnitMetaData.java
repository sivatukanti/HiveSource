// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import java.util.Map;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.net.URI;

public class PersistenceUnitMetaData extends MetaData
{
    String name;
    URI rootURI;
    TransactionType transactionType;
    String description;
    String provider;
    String validationMode;
    String jtaDataSource;
    String nonJtaDataSource;
    Set<String> classNames;
    Set jarFiles;
    Set<String> mappingFileNames;
    Properties properties;
    boolean excludeUnlistedClasses;
    String caching;
    
    public PersistenceUnitMetaData(final String name, final String transactionType, final URI rootURI) {
        this.name = null;
        this.rootURI = null;
        this.transactionType = null;
        this.description = null;
        this.provider = null;
        this.validationMode = null;
        this.jtaDataSource = null;
        this.nonJtaDataSource = null;
        this.classNames = null;
        this.jarFiles = null;
        this.mappingFileNames = null;
        this.properties = null;
        this.excludeUnlistedClasses = false;
        this.caching = "UNSPECIFIED";
        this.name = name;
        this.transactionType = TransactionType.getValue(transactionType);
        this.rootURI = rootURI;
    }
    
    public String getName() {
        return this.name;
    }
    
    public URI getRootURI() {
        return this.rootURI;
    }
    
    public TransactionType getTransactionType() {
        return this.transactionType;
    }
    
    public String getCaching() {
        return this.caching;
    }
    
    public void setCaching(final String cache) {
        this.caching = cache;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String desc) {
        this.description = desc;
    }
    
    public String getProvider() {
        return this.provider;
    }
    
    public void setProvider(final String provider) {
        this.provider = provider;
    }
    
    public String getJtaDataSource() {
        return this.jtaDataSource;
    }
    
    public void setJtaDataSource(final String data) {
        this.jtaDataSource = data;
    }
    
    public String getNonJtaDataSource() {
        return this.nonJtaDataSource;
    }
    
    public void setNonJtaDataSource(final String data) {
        this.nonJtaDataSource = data;
    }
    
    public void setValidationMode(final String validationMode) {
        this.validationMode = validationMode;
    }
    
    public String getValidationMode() {
        return this.validationMode;
    }
    
    public void setExcludeUnlistedClasses() {
        this.excludeUnlistedClasses = true;
    }
    
    public boolean getExcludeUnlistedClasses() {
        return this.excludeUnlistedClasses;
    }
    
    public void addClassName(final String className) {
        if (this.classNames == null) {
            this.classNames = new HashSet<String>();
        }
        this.classNames.add(className);
    }
    
    public void addClassNames(final Set<String> classNames) {
        if (classNames == null) {
            this.classNames = new HashSet<String>();
        }
        this.classNames.addAll(classNames);
    }
    
    public void addJarFile(final String jarName) {
        if (this.jarFiles == null) {
            this.jarFiles = new HashSet();
        }
        this.jarFiles.add(jarName);
    }
    
    public void addJarFiles(final Set<String> jarNames) {
        if (this.jarFiles == null) {
            this.jarFiles = new HashSet();
        }
        this.jarFiles.addAll(jarNames);
    }
    
    public void addJarFile(final URL jarURL) {
        if (this.jarFiles == null) {
            this.jarFiles = new HashSet();
        }
        this.jarFiles.add(jarURL);
    }
    
    public void clearJarFiles() {
        if (this.jarFiles != null) {
            this.jarFiles.clear();
        }
        this.jarFiles = null;
    }
    
    public void addMappingFile(final String mappingFile) {
        if (this.mappingFileNames == null) {
            this.mappingFileNames = new HashSet<String>();
        }
        this.mappingFileNames.add(mappingFile);
    }
    
    public void addProperty(final String key, final String value) {
        if (key == null || value == null) {
            return;
        }
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.setProperty(key, value);
    }
    
    public Set<String> getClassNames() {
        return this.classNames;
    }
    
    public Set<String> getMappingFiles() {
        return this.mappingFileNames;
    }
    
    public Set getJarFiles() {
        return this.jarFiles;
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<persistence-unit name=\"" + this.name + "\"");
        if (this.transactionType != null) {
            sb.append(" transaction-type=\"" + this.transactionType + "\"");
        }
        sb.append(">\n");
        if (this.description != null) {
            sb.append(prefix).append(indent).append("<description>" + this.description + "</description>\n");
        }
        if (this.provider != null) {
            sb.append(prefix).append(indent).append("<provider>" + this.provider + "</provider>\n");
        }
        if (this.jtaDataSource != null) {
            sb.append(prefix).append(indent).append("<jta-data-source>" + this.jtaDataSource + "</jta-data-source>\n");
        }
        if (this.nonJtaDataSource != null) {
            sb.append(prefix).append(indent).append("<non-jta-data-source>" + this.nonJtaDataSource + "</non-jta-data-source>\n");
        }
        if (this.classNames != null) {
            for (final String className : this.classNames) {
                sb.append(prefix).append(indent).append("<class>" + className + "</class>\n");
            }
        }
        if (this.mappingFileNames != null) {
            for (final String mappingFileName : this.mappingFileNames) {
                sb.append(prefix).append(indent).append("<mapping-file>" + mappingFileName + "</mapping-file>\n");
            }
        }
        if (this.jarFiles != null) {
            for (final Object jarFile : this.jarFiles) {
                sb.append(prefix).append(indent).append("<jar-file>" + jarFile + "</jar-file>\n");
            }
        }
        if (this.properties != null) {
            sb.append(prefix).append(indent).append("<properties>\n");
            final Set entries = this.properties.entrySet();
            for (final Map.Entry entry : entries) {
                sb.append(prefix).append(indent).append(indent).append("<property name=" + entry.getKey() + " value=" + entry.getValue() + "</property>\n");
            }
            sb.append(prefix).append(indent).append("</properties>\n");
        }
        if (this.excludeUnlistedClasses) {
            sb.append(prefix).append(indent).append("<exclude-unlisted-classes/>\n");
        }
        sb.append(prefix).append("</persistence-unit>\n");
        return sb.toString();
    }
}
