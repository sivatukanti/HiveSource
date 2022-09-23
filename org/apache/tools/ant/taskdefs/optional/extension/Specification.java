// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.Collection;
import java.util.Arrays;
import org.apache.tools.ant.util.StringUtils;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.jar.Manifest;
import org.apache.tools.ant.util.DeweyDecimal;
import java.util.jar.Attributes;

public final class Specification
{
    private static final String MISSING = "Missing ";
    public static final Attributes.Name SPECIFICATION_TITLE;
    public static final Attributes.Name SPECIFICATION_VERSION;
    public static final Attributes.Name SPECIFICATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_TITLE;
    public static final Attributes.Name IMPLEMENTATION_VERSION;
    public static final Attributes.Name IMPLEMENTATION_VENDOR;
    public static final Compatibility COMPATIBLE;
    public static final Compatibility REQUIRE_SPECIFICATION_UPGRADE;
    public static final Compatibility REQUIRE_VENDOR_SWITCH;
    public static final Compatibility REQUIRE_IMPLEMENTATION_CHANGE;
    public static final Compatibility INCOMPATIBLE;
    private String specificationTitle;
    private DeweyDecimal specificationVersion;
    private String specificationVendor;
    private String implementationTitle;
    private String implementationVendor;
    private String implementationVersion;
    private String[] sections;
    
    public static Specification[] getSpecifications(final Manifest manifest) throws ParseException {
        if (null == manifest) {
            return new Specification[0];
        }
        final ArrayList results = new ArrayList();
        final Map entries = manifest.getEntries();
        for (final String key : entries.keySet()) {
            final Attributes attributes = entries.get(key);
            final Specification specification = getSpecification(key, attributes);
            if (null != specification) {
                results.add(specification);
            }
        }
        final ArrayList trimmedResults = removeDuplicates(results);
        return trimmedResults.toArray(new Specification[trimmedResults.size()]);
    }
    
    public Specification(final String specificationTitle, final String specificationVersion, final String specificationVendor, final String implementationTitle, final String implementationVersion, final String implementationVendor) {
        this(specificationTitle, specificationVersion, specificationVendor, implementationTitle, implementationVersion, implementationVendor, null);
    }
    
    public Specification(final String specificationTitle, final String specificationVersion, final String specificationVendor, final String implementationTitle, final String implementationVersion, final String implementationVendor, final String[] sections) {
        this.specificationTitle = specificationTitle;
        this.specificationVendor = specificationVendor;
        if (null != specificationVersion) {
            try {
                this.specificationVersion = new DeweyDecimal(specificationVersion);
            }
            catch (NumberFormatException nfe) {
                final String error = "Bad specification version format '" + specificationVersion + "' in '" + specificationTitle + "'. (Reason: " + nfe + ")";
                throw new IllegalArgumentException(error);
            }
        }
        this.implementationTitle = implementationTitle;
        this.implementationVendor = implementationVendor;
        this.implementationVersion = implementationVersion;
        if (null == this.specificationTitle) {
            throw new NullPointerException("specificationTitle");
        }
        String[] copy = null;
        if (null != sections) {
            copy = new String[sections.length];
            System.arraycopy(sections, 0, copy, 0, sections.length);
        }
        this.sections = copy;
    }
    
    public String getSpecificationTitle() {
        return this.specificationTitle;
    }
    
    public String getSpecificationVendor() {
        return this.specificationVendor;
    }
    
    public String getImplementationTitle() {
        return this.implementationTitle;
    }
    
    public DeweyDecimal getSpecificationVersion() {
        return this.specificationVersion;
    }
    
    public String getImplementationVendor() {
        return this.implementationVendor;
    }
    
    public String getImplementationVersion() {
        return this.implementationVersion;
    }
    
    public String[] getSections() {
        if (null == this.sections) {
            return null;
        }
        final String[] newSections = new String[this.sections.length];
        System.arraycopy(this.sections, 0, newSections, 0, this.sections.length);
        return newSections;
    }
    
    public Compatibility getCompatibilityWith(final Specification other) {
        if (!this.specificationTitle.equals(other.getSpecificationTitle())) {
            return Specification.INCOMPATIBLE;
        }
        final DeweyDecimal otherSpecificationVersion = other.getSpecificationVersion();
        if (null != this.specificationVersion && (null == otherSpecificationVersion || !this.isCompatible(this.specificationVersion, otherSpecificationVersion))) {
            return Specification.REQUIRE_SPECIFICATION_UPGRADE;
        }
        final String otherImplementationVendor = other.getImplementationVendor();
        if (null != this.implementationVendor && (null == otherImplementationVendor || !this.implementationVendor.equals(otherImplementationVendor))) {
            return Specification.REQUIRE_VENDOR_SWITCH;
        }
        final String otherImplementationVersion = other.getImplementationVersion();
        if (null != this.implementationVersion && (null == otherImplementationVersion || !this.implementationVersion.equals(otherImplementationVersion))) {
            return Specification.REQUIRE_IMPLEMENTATION_CHANGE;
        }
        return Specification.COMPATIBLE;
    }
    
    public boolean isCompatibleWith(final Specification other) {
        return Specification.COMPATIBLE == this.getCompatibilityWith(other);
    }
    
    @Override
    public String toString() {
        final String brace = ": ";
        final StringBuffer sb = new StringBuffer(Specification.SPECIFICATION_TITLE.toString());
        sb.append(": ");
        sb.append(this.specificationTitle);
        sb.append(StringUtils.LINE_SEP);
        if (null != this.specificationVersion) {
            sb.append(Specification.SPECIFICATION_VERSION);
            sb.append(": ");
            sb.append(this.specificationVersion);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.specificationVendor) {
            sb.append(Specification.SPECIFICATION_VENDOR);
            sb.append(": ");
            sb.append(this.specificationVendor);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.implementationTitle) {
            sb.append(Specification.IMPLEMENTATION_TITLE);
            sb.append(": ");
            sb.append(this.implementationTitle);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.implementationVersion) {
            sb.append(Specification.IMPLEMENTATION_VERSION);
            sb.append(": ");
            sb.append(this.implementationVersion);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.implementationVendor) {
            sb.append(Specification.IMPLEMENTATION_VENDOR);
            sb.append(": ");
            sb.append(this.implementationVendor);
            sb.append(StringUtils.LINE_SEP);
        }
        return sb.toString();
    }
    
    private boolean isCompatible(final DeweyDecimal first, final DeweyDecimal second) {
        return first.isGreaterThanOrEqual(second);
    }
    
    private static ArrayList removeDuplicates(final ArrayList list) {
        final ArrayList results = new ArrayList();
        final ArrayList sections = new ArrayList();
        while (list.size() > 0) {
            final Specification specification = list.remove(0);
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                final Specification other = iterator.next();
                if (isEqual(specification, other)) {
                    final String[] otherSections = other.getSections();
                    if (null != otherSections) {
                        sections.addAll(Arrays.asList(otherSections));
                    }
                    iterator.remove();
                }
            }
            final Specification merged = mergeInSections(specification, sections);
            results.add(merged);
            sections.clear();
        }
        return results;
    }
    
    private static boolean isEqual(final Specification specification, final Specification other) {
        return specification.getSpecificationTitle().equals(other.getSpecificationTitle()) && specification.getSpecificationVersion().isEqual(other.getSpecificationVersion()) && specification.getSpecificationVendor().equals(other.getSpecificationVendor()) && specification.getImplementationTitle().equals(other.getImplementationTitle()) && specification.getImplementationVersion().equals(other.getImplementationVersion()) && specification.getImplementationVendor().equals(other.getImplementationVendor());
    }
    
    private static Specification mergeInSections(final Specification specification, final ArrayList sectionsToAdd) {
        if (0 == sectionsToAdd.size()) {
            return specification;
        }
        sectionsToAdd.addAll(Arrays.asList(specification.getSections()));
        final String[] sections = sectionsToAdd.toArray(new String[sectionsToAdd.size()]);
        return new Specification(specification.getSpecificationTitle(), specification.getSpecificationVersion().toString(), specification.getSpecificationVendor(), specification.getImplementationTitle(), specification.getImplementationVersion(), specification.getImplementationVendor(), sections);
    }
    
    private static String getTrimmedString(final String value) {
        return (value == null) ? null : value.trim();
    }
    
    private static Specification getSpecification(final String section, final Attributes attributes) throws ParseException {
        final String name = getTrimmedString(attributes.getValue(Specification.SPECIFICATION_TITLE));
        if (null == name) {
            return null;
        }
        final String specVendor = getTrimmedString(attributes.getValue(Specification.SPECIFICATION_VENDOR));
        if (null == specVendor) {
            throw new ParseException("Missing " + Specification.SPECIFICATION_VENDOR, 0);
        }
        final String specVersion = getTrimmedString(attributes.getValue(Specification.SPECIFICATION_VERSION));
        if (null == specVersion) {
            throw new ParseException("Missing " + Specification.SPECIFICATION_VERSION, 0);
        }
        final String impTitle = getTrimmedString(attributes.getValue(Specification.IMPLEMENTATION_TITLE));
        if (null == impTitle) {
            throw new ParseException("Missing " + Specification.IMPLEMENTATION_TITLE, 0);
        }
        final String impVersion = getTrimmedString(attributes.getValue(Specification.IMPLEMENTATION_VERSION));
        if (null == impVersion) {
            throw new ParseException("Missing " + Specification.IMPLEMENTATION_VERSION, 0);
        }
        final String impVendor = getTrimmedString(attributes.getValue(Specification.IMPLEMENTATION_VENDOR));
        if (null == impVendor) {
            throw new ParseException("Missing " + Specification.IMPLEMENTATION_VENDOR, 0);
        }
        return new Specification(name, specVersion, specVendor, impTitle, impVersion, impVendor, new String[] { section });
    }
    
    static {
        SPECIFICATION_TITLE = Attributes.Name.SPECIFICATION_TITLE;
        SPECIFICATION_VERSION = Attributes.Name.SPECIFICATION_VERSION;
        SPECIFICATION_VENDOR = Attributes.Name.SPECIFICATION_VENDOR;
        IMPLEMENTATION_TITLE = Attributes.Name.IMPLEMENTATION_TITLE;
        IMPLEMENTATION_VERSION = Attributes.Name.IMPLEMENTATION_VERSION;
        IMPLEMENTATION_VENDOR = Attributes.Name.IMPLEMENTATION_VENDOR;
        COMPATIBLE = new Compatibility("COMPATIBLE");
        REQUIRE_SPECIFICATION_UPGRADE = new Compatibility("REQUIRE_SPECIFICATION_UPGRADE");
        REQUIRE_VENDOR_SWITCH = new Compatibility("REQUIRE_VENDOR_SWITCH");
        REQUIRE_IMPLEMENTATION_CHANGE = new Compatibility("REQUIRE_IMPLEMENTATION_CHANGE");
        INCOMPATIBLE = new Compatibility("INCOMPATIBLE");
    }
}
