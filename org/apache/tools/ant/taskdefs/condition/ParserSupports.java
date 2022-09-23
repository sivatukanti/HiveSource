// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.XMLReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

public class ParserSupports extends ProjectComponent implements Condition
{
    private String feature;
    private String property;
    private String value;
    public static final String ERROR_BOTH_ATTRIBUTES = "Property and feature attributes are exclusive";
    public static final String FEATURE = "feature";
    public static final String PROPERTY = "property";
    public static final String NOT_RECOGNIZED = " not recognized: ";
    public static final String NOT_SUPPORTED = " not supported: ";
    public static final String ERROR_NO_ATTRIBUTES = "Neither feature or property are set";
    public static final String ERROR_NO_VALUE = "A value is needed when testing for property support";
    
    public void setFeature(final String feature) {
        this.feature = feature;
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public boolean eval() throws BuildException {
        if (this.feature != null && this.property != null) {
            throw new BuildException("Property and feature attributes are exclusive");
        }
        if (this.feature == null && this.property == null) {
            throw new BuildException("Neither feature or property are set");
        }
        if (this.feature != null) {
            return this.evalFeature();
        }
        if (this.value == null) {
            throw new BuildException("A value is needed when testing for property support");
        }
        return this.evalProperty();
    }
    
    private XMLReader getReader() {
        JAXPUtils.getParser();
        return JAXPUtils.getXMLReader();
    }
    
    public boolean evalFeature() {
        final XMLReader reader = this.getReader();
        if (this.value == null) {
            this.value = "true";
        }
        final boolean v = Project.toBoolean(this.value);
        try {
            reader.setFeature(this.feature, v);
        }
        catch (SAXNotRecognizedException e) {
            this.log("feature not recognized: " + this.feature, 3);
            return false;
        }
        catch (SAXNotSupportedException e2) {
            this.log("feature not supported: " + this.feature, 3);
            return false;
        }
        return true;
    }
    
    public boolean evalProperty() {
        final XMLReader reader = this.getReader();
        try {
            reader.setProperty(this.property, this.value);
        }
        catch (SAXNotRecognizedException e) {
            this.log("property not recognized: " + this.property, 3);
            return false;
        }
        catch (SAXNotSupportedException e2) {
            this.log("property not supported: " + this.property, 3);
            return false;
        }
        return true;
    }
}
