// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import org.apache.tools.ant.util.CollectionUtils;
import java.util.Vector;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Iterator;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.util.LinkedHashMap;
import java.io.InputStream;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStreamReader;
import org.apache.tools.ant.BuildException;
import java.util.Map;

public class Manifest
{
    public static final String ATTRIBUTE_MANIFEST_VERSION = "Manifest-Version";
    public static final String ATTRIBUTE_SIGNATURE_VERSION = "Signature-Version";
    public static final String ATTRIBUTE_NAME = "Name";
    public static final String ATTRIBUTE_FROM = "From";
    public static final String ATTRIBUTE_CLASSPATH = "Class-Path";
    public static final String DEFAULT_MANIFEST_VERSION = "1.0";
    public static final int MAX_LINE_LENGTH = 72;
    public static final int MAX_SECTION_LENGTH = 70;
    public static final String EOL = "\r\n";
    public static final String ERROR_FROM_FORBIDDEN = "Manifest attributes should not start with \"From\" in \"";
    public static final String JAR_ENCODING = "UTF-8";
    private static final String ATTRIBUTE_MANIFEST_VERSION_LC;
    private static final String ATTRIBUTE_NAME_LC;
    private static final String ATTRIBUTE_FROM_LC;
    private static final String ATTRIBUTE_CLASSPATH_LC;
    private String manifestVersion;
    private Section mainSection;
    private Map<String, Section> sections;
    
    public static Manifest getDefaultManifest() throws BuildException {
        InputStream in = null;
        InputStreamReader insr = null;
        try {
            final String defManifest = "/org/apache/tools/ant/defaultManifest.mf";
            in = Manifest.class.getResourceAsStream(defManifest);
            if (in == null) {
                throw new BuildException("Could not find default manifest: " + defManifest);
            }
            try {
                insr = new InputStreamReader(in, "UTF-8");
                final Manifest defaultManifest = new Manifest(insr);
                String version = System.getProperty("java.runtime.version");
                if (version == null) {
                    version = System.getProperty("java.vm.version");
                }
                final Attribute createdBy = new Attribute("Created-By", version + " (" + System.getProperty("java.vm.vendor") + ")");
                defaultManifest.getMainSection().storeAttribute(createdBy);
                return defaultManifest;
            }
            catch (UnsupportedEncodingException e3) {
                insr = new InputStreamReader(in);
                return new Manifest(insr);
            }
        }
        catch (ManifestException e) {
            throw new BuildException("Default manifest is invalid !!", e);
        }
        catch (IOException e2) {
            throw new BuildException("Unable to read default manifest", e2);
        }
        finally {
            FileUtils.close(insr);
            FileUtils.close(in);
        }
    }
    
    public Manifest() {
        this.manifestVersion = "1.0";
        this.mainSection = new Section();
        this.sections = new LinkedHashMap<String, Section>();
        this.manifestVersion = null;
    }
    
    public Manifest(final Reader r) throws ManifestException, IOException {
        this.manifestVersion = "1.0";
        this.mainSection = new Section();
        this.sections = new LinkedHashMap<String, Section>();
        final BufferedReader reader = new BufferedReader(r);
        String nextSectionName = this.mainSection.read(reader);
        final String readManifestVersion = this.mainSection.getAttributeValue("Manifest-Version");
        if (readManifestVersion != null) {
            this.manifestVersion = readManifestVersion;
            this.mainSection.removeAttribute("Manifest-Version");
        }
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.length() == 0) {
                continue;
            }
            final Section section = new Section();
            if (nextSectionName == null) {
                final Attribute sectionName = new Attribute(line);
                if (!sectionName.getName().equalsIgnoreCase("Name")) {
                    throw new ManifestException("Manifest sections should start with a \"Name\" attribute and not \"" + sectionName.getName() + "\"");
                }
                nextSectionName = sectionName.getValue();
            }
            else {
                final Attribute firstAttribute = new Attribute(line);
                section.addAttributeAndCheck(firstAttribute);
            }
            section.setName(nextSectionName);
            nextSectionName = section.read(reader);
            this.addConfiguredSection(section);
        }
    }
    
    public void addConfiguredSection(final Section section) throws ManifestException {
        final String sectionName = section.getName();
        if (sectionName == null) {
            throw new BuildException("Sections must have a name");
        }
        this.sections.put(sectionName, section);
    }
    
    public void addConfiguredAttribute(final Attribute attribute) throws ManifestException {
        if (attribute.getKey() == null || attribute.getValue() == null) {
            throw new BuildException("Attributes must have name and value");
        }
        if (attribute.getKey().equals(Manifest.ATTRIBUTE_MANIFEST_VERSION_LC)) {
            this.manifestVersion = attribute.getValue();
        }
        else {
            this.mainSection.addConfiguredAttribute(attribute);
        }
    }
    
    public void merge(final Manifest other) throws ManifestException {
        this.merge(other, false);
    }
    
    public void merge(final Manifest other, final boolean overwriteMain) throws ManifestException {
        this.merge(other, overwriteMain, false);
    }
    
    public void merge(final Manifest other, final boolean overwriteMain, final boolean mergeClassPaths) throws ManifestException {
        if (other != null) {
            if (overwriteMain) {
                this.mainSection = (Section)other.mainSection.clone();
            }
            else {
                this.mainSection.merge(other.mainSection, mergeClassPaths);
            }
            if (other.manifestVersion != null) {
                this.manifestVersion = other.manifestVersion;
            }
            final Enumeration<String> e = other.getSectionNames();
            while (e.hasMoreElements()) {
                final String sectionName = e.nextElement();
                final Section ourSection = this.sections.get(sectionName);
                final Section otherSection = other.sections.get(sectionName);
                if (ourSection == null) {
                    if (otherSection == null) {
                        continue;
                    }
                    this.addConfiguredSection((Section)otherSection.clone());
                }
                else {
                    ourSection.merge(otherSection, mergeClassPaths);
                }
            }
        }
    }
    
    public void write(final PrintWriter writer) throws IOException {
        this.write(writer, false);
    }
    
    public void write(final PrintWriter writer, final boolean flatten) throws IOException {
        writer.print("Manifest-Version: " + this.manifestVersion + "\r\n");
        final String signatureVersion = this.mainSection.getAttributeValue("Signature-Version");
        if (signatureVersion != null) {
            writer.print("Signature-Version: " + signatureVersion + "\r\n");
            this.mainSection.removeAttribute("Signature-Version");
        }
        this.mainSection.write(writer, flatten);
        if (signatureVersion != null) {
            try {
                final Attribute svAttr = new Attribute("Signature-Version", signatureVersion);
                this.mainSection.addConfiguredAttribute(svAttr);
            }
            catch (ManifestException ex) {}
        }
        for (final String sectionName : this.sections.keySet()) {
            final Section section = this.getSection(sectionName);
            section.write(writer, flatten);
        }
    }
    
    @Override
    public String toString() {
        final StringWriter sw = new StringWriter();
        try {
            this.write(new PrintWriter(sw));
        }
        catch (IOException e) {
            return null;
        }
        return sw.toString();
    }
    
    public Enumeration<String> getWarnings() {
        final Vector<String> warnings = new Vector<String>();
        final Enumeration<String> warnEnum = this.mainSection.getWarnings();
        while (warnEnum.hasMoreElements()) {
            warnings.addElement(warnEnum.nextElement());
        }
        for (final Section section : this.sections.values()) {
            final Enumeration<String> e2 = section.getWarnings();
            while (e2.hasMoreElements()) {
                warnings.addElement(e2.nextElement());
            }
        }
        return warnings.elements();
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.manifestVersion != null) {
            hashCode += this.manifestVersion.hashCode();
        }
        hashCode += this.mainSection.hashCode();
        hashCode += this.sections.hashCode();
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object rhs) {
        if (rhs == null || rhs.getClass() != this.getClass()) {
            return false;
        }
        if (rhs == this) {
            return true;
        }
        final Manifest rhsManifest = (Manifest)rhs;
        if (this.manifestVersion == null) {
            if (rhsManifest.manifestVersion != null) {
                return false;
            }
        }
        else if (!this.manifestVersion.equals(rhsManifest.manifestVersion)) {
            return false;
        }
        return this.mainSection.equals(rhsManifest.mainSection) && this.sections.equals(rhsManifest.sections);
    }
    
    public String getManifestVersion() {
        return this.manifestVersion;
    }
    
    public Section getMainSection() {
        return this.mainSection;
    }
    
    public Section getSection(final String name) {
        return this.sections.get(name);
    }
    
    public Enumeration<String> getSectionNames() {
        return CollectionUtils.asEnumeration(this.sections.keySet().iterator());
    }
    
    static {
        ATTRIBUTE_MANIFEST_VERSION_LC = "Manifest-Version".toLowerCase(Locale.ENGLISH);
        ATTRIBUTE_NAME_LC = "Name".toLowerCase(Locale.ENGLISH);
        ATTRIBUTE_FROM_LC = "From".toLowerCase(Locale.ENGLISH);
        ATTRIBUTE_CLASSPATH_LC = "Class-Path".toLowerCase(Locale.ENGLISH);
    }
    
    public static class Attribute
    {
        private static final int MAX_NAME_VALUE_LENGTH = 68;
        private static final int MAX_NAME_LENGTH = 70;
        private String name;
        private Vector<String> values;
        private int currentIndex;
        
        public Attribute() {
            this.name = null;
            this.values = new Vector<String>();
            this.currentIndex = 0;
        }
        
        public Attribute(final String line) throws ManifestException {
            this.name = null;
            this.values = new Vector<String>();
            this.currentIndex = 0;
            this.parse(line);
        }
        
        public Attribute(final String name, final String value) {
            this.name = null;
            this.values = new Vector<String>();
            this.currentIndex = 0;
            this.name = name;
            this.setValue(value);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            if (this.name != null) {
                hashCode += this.getKey().hashCode();
            }
            hashCode += this.values.hashCode();
            return hashCode;
        }
        
        @Override
        public boolean equals(final Object rhs) {
            if (rhs == null || rhs.getClass() != this.getClass()) {
                return false;
            }
            if (rhs == this) {
                return true;
            }
            final Attribute rhsAttribute = (Attribute)rhs;
            final String lhsKey = this.getKey();
            final String rhsKey = rhsAttribute.getKey();
            return (lhsKey != null || rhsKey == null) && (lhsKey == null || lhsKey.equals(rhsKey)) && this.values.equals(rhsAttribute.values);
        }
        
        public void parse(final String line) throws ManifestException {
            final int index = line.indexOf(": ");
            if (index == -1) {
                throw new ManifestException("Manifest line \"" + line + "\" is not valid as it does not " + "contain a name and a value separated by ': ' ");
            }
            this.name = line.substring(0, index);
            this.setValue(line.substring(index + 2));
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getKey() {
            if (this.name == null) {
                return null;
            }
            return this.name.toLowerCase(Locale.ENGLISH);
        }
        
        public void setValue(final String value) {
            if (this.currentIndex >= this.values.size()) {
                this.values.addElement(value);
                this.currentIndex = this.values.size() - 1;
            }
            else {
                this.values.setElementAt(value, this.currentIndex);
            }
        }
        
        public String getValue() {
            if (this.values.size() == 0) {
                return null;
            }
            String fullValue = "";
            final Enumeration<String> e = this.getValues();
            while (e.hasMoreElements()) {
                final String value = e.nextElement();
                fullValue = fullValue + value + " ";
            }
            return fullValue.trim();
        }
        
        public void addValue(final String value) {
            ++this.currentIndex;
            this.setValue(value);
        }
        
        public Enumeration<String> getValues() {
            return this.values.elements();
        }
        
        public void addContinuation(final String line) {
            final String currentValue = this.values.elementAt(this.currentIndex);
            this.setValue(currentValue + line.substring(1));
        }
        
        public void write(final PrintWriter writer) throws IOException {
            this.write(writer, false);
        }
        
        public void write(final PrintWriter writer, final boolean flatten) throws IOException {
            if (!flatten) {
                final Enumeration<String> e = this.getValues();
                while (e.hasMoreElements()) {
                    this.writeValue(writer, e.nextElement());
                }
            }
            else {
                this.writeValue(writer, this.getValue());
            }
        }
        
        private void writeValue(final PrintWriter writer, final String value) throws IOException {
            String line = null;
            final int nameLength = this.name.getBytes("UTF-8").length;
            if (nameLength > 68) {
                if (nameLength > 70) {
                    throw new IOException("Unable to write manifest line " + this.name + ": " + value);
                }
                writer.print(this.name + ": " + "\r\n");
                line = " " + value;
            }
            else {
                line = this.name + ": " + value;
            }
            while (line.getBytes("UTF-8").length > 70) {
                int breakIndex = 70;
                if (breakIndex >= line.length()) {
                    breakIndex = line.length() - 1;
                }
                String section;
                for (section = line.substring(0, breakIndex); section.getBytes("UTF-8").length > 70 && breakIndex > 0; --breakIndex, section = line.substring(0, breakIndex)) {}
                if (breakIndex == 0) {
                    throw new IOException("Unable to write manifest line " + this.name + ": " + value);
                }
                writer.print(section + "\r\n");
                line = " " + line.substring(breakIndex);
            }
            writer.print(line + "\r\n");
        }
    }
    
    public static class Section
    {
        private Vector<String> warnings;
        private String name;
        private Map<String, Attribute> attributes;
        
        public Section() {
            this.warnings = new Vector<String>();
            this.name = null;
            this.attributes = new LinkedHashMap<String, Attribute>();
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String read(final BufferedReader reader) throws ManifestException, IOException {
            Attribute attribute = null;
            while (true) {
                final String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    return null;
                }
                if (line.charAt(0) == ' ') {
                    if (attribute == null) {
                        if (this.name == null) {
                            throw new ManifestException("Can't start an attribute with a continuation line " + line);
                        }
                        this.name += line.substring(1);
                    }
                    else {
                        attribute.addContinuation(line);
                    }
                }
                else {
                    attribute = new Attribute(line);
                    final String nameReadAhead = this.addAttributeAndCheck(attribute);
                    attribute = this.getAttribute(attribute.getKey());
                    if (nameReadAhead != null) {
                        return nameReadAhead;
                    }
                    continue;
                }
            }
        }
        
        public void merge(final Section section) throws ManifestException {
            this.merge(section, false);
        }
        
        public void merge(final Section section, final boolean mergeClassPaths) throws ManifestException {
            if ((this.name == null && section.getName() != null) || (this.name != null && section.getName() != null && !this.name.toLowerCase(Locale.ENGLISH).equals(section.getName().toLowerCase(Locale.ENGLISH)))) {
                throw new ManifestException("Unable to merge sections with different names");
            }
            final Enumeration<String> e = section.getAttributeKeys();
            Attribute classpathAttribute = null;
            while (e.hasMoreElements()) {
                final String attributeName = e.nextElement();
                final Attribute attribute = section.getAttribute(attributeName);
                if (attributeName.equalsIgnoreCase("Class-Path")) {
                    if (classpathAttribute == null) {
                        classpathAttribute = new Attribute();
                        classpathAttribute.setName("Class-Path");
                    }
                    final Enumeration<String> cpe = attribute.getValues();
                    while (cpe.hasMoreElements()) {
                        final String value = cpe.nextElement();
                        classpathAttribute.addValue(value);
                    }
                }
                else {
                    this.storeAttribute(attribute);
                }
            }
            if (classpathAttribute != null) {
                if (mergeClassPaths) {
                    final Attribute currentCp = this.getAttribute("Class-Path");
                    if (currentCp != null) {
                        final Enumeration<String> attribEnum = currentCp.getValues();
                        while (attribEnum.hasMoreElements()) {
                            final String value2 = attribEnum.nextElement();
                            classpathAttribute.addValue(value2);
                        }
                    }
                }
                this.storeAttribute(classpathAttribute);
            }
            final Enumeration<String> warnEnum = section.warnings.elements();
            while (warnEnum.hasMoreElements()) {
                this.warnings.addElement(warnEnum.nextElement());
            }
        }
        
        public void write(final PrintWriter writer) throws IOException {
            this.write(writer, false);
        }
        
        public void write(final PrintWriter writer, final boolean flatten) throws IOException {
            if (this.name != null) {
                final Attribute nameAttr = new Attribute("Name", this.name);
                nameAttr.write(writer);
            }
            final Enumeration<String> e = this.getAttributeKeys();
            while (e.hasMoreElements()) {
                final String key = e.nextElement();
                final Attribute attribute = this.getAttribute(key);
                attribute.write(writer, flatten);
            }
            writer.print("\r\n");
        }
        
        public Attribute getAttribute(final String attributeName) {
            return this.attributes.get(attributeName.toLowerCase(Locale.ENGLISH));
        }
        
        public Enumeration<String> getAttributeKeys() {
            return CollectionUtils.asEnumeration(this.attributes.keySet().iterator());
        }
        
        public String getAttributeValue(final String attributeName) {
            final Attribute attribute = this.getAttribute(attributeName.toLowerCase(Locale.ENGLISH));
            if (attribute == null) {
                return null;
            }
            return attribute.getValue();
        }
        
        public void removeAttribute(final String attributeName) {
            final String key = attributeName.toLowerCase(Locale.ENGLISH);
            this.attributes.remove(key);
        }
        
        public void addConfiguredAttribute(final Attribute attribute) throws ManifestException {
            final String check = this.addAttributeAndCheck(attribute);
            if (check != null) {
                throw new BuildException("Specify the section name using the \"name\" attribute of the <section> element rather than using a \"Name\" manifest attribute");
            }
        }
        
        public String addAttributeAndCheck(final Attribute attribute) throws ManifestException {
            if (attribute.getName() == null || attribute.getValue() == null) {
                throw new BuildException("Attributes must have name and value");
            }
            final String attributeKey = attribute.getKey();
            if (attributeKey.equals(Manifest.ATTRIBUTE_NAME_LC)) {
                this.warnings.addElement("\"Name\" attributes should not occur in the main section and must be the first element in all other sections: \"" + attribute.getName() + ": " + attribute.getValue() + "\"");
                return attribute.getValue();
            }
            if (attributeKey.startsWith(Manifest.ATTRIBUTE_FROM_LC)) {
                this.warnings.addElement("Manifest attributes should not start with \"From\" in \"" + attribute.getName() + ": " + attribute.getValue() + "\"");
            }
            else if (attributeKey.equals(Manifest.ATTRIBUTE_CLASSPATH_LC)) {
                final Attribute classpathAttribute = this.attributes.get(attributeKey);
                if (classpathAttribute == null) {
                    this.storeAttribute(attribute);
                }
                else {
                    this.warnings.addElement("Multiple Class-Path attributes are supported but violate the Jar specification and may not be correctly processed in all environments");
                    final Enumeration<String> e = attribute.getValues();
                    while (e.hasMoreElements()) {
                        final String value = e.nextElement();
                        classpathAttribute.addValue(value);
                    }
                }
            }
            else {
                if (this.attributes.containsKey(attributeKey)) {
                    throw new ManifestException("The attribute \"" + attribute.getName() + "\" may not occur more " + "than once in the same section");
                }
                this.storeAttribute(attribute);
            }
            return null;
        }
        
        public Object clone() {
            final Section cloned = new Section();
            cloned.setName(this.name);
            final Enumeration<String> e = this.getAttributeKeys();
            while (e.hasMoreElements()) {
                final String key = e.nextElement();
                final Attribute attribute = this.getAttribute(key);
                cloned.storeAttribute(new Attribute(attribute.getName(), attribute.getValue()));
            }
            return cloned;
        }
        
        private void storeAttribute(final Attribute attribute) {
            if (attribute == null) {
                return;
            }
            final String attributeKey = attribute.getKey();
            this.attributes.put(attributeKey, attribute);
        }
        
        public Enumeration<String> getWarnings() {
            return this.warnings.elements();
        }
        
        @Override
        public int hashCode() {
            return this.attributes.hashCode();
        }
        
        @Override
        public boolean equals(final Object rhs) {
            if (rhs == null || rhs.getClass() != this.getClass()) {
                return false;
            }
            if (rhs == this) {
                return true;
            }
            final Section rhsSection = (Section)rhs;
            return this.attributes.equals(rhsSection.attributes);
        }
    }
}
