// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.DeweyDecimal;
import org.apache.tools.ant.types.DataType;

public class ExtensionAdapter extends DataType
{
    private String extensionName;
    private DeweyDecimal specificationVersion;
    private String specificationVendor;
    private String implementationVendorID;
    private String implementationVendor;
    private DeweyDecimal implementationVersion;
    private String implementationURL;
    
    public void setExtensionName(final String extensionName) {
        this.verifyNotAReference();
        this.extensionName = extensionName;
    }
    
    public void setSpecificationVersion(final String specificationVersion) {
        this.verifyNotAReference();
        this.specificationVersion = new DeweyDecimal(specificationVersion);
    }
    
    public void setSpecificationVendor(final String specificationVendor) {
        this.verifyNotAReference();
        this.specificationVendor = specificationVendor;
    }
    
    public void setImplementationVendorId(final String implementationVendorID) {
        this.verifyNotAReference();
        this.implementationVendorID = implementationVendorID;
    }
    
    public void setImplementationVendor(final String implementationVendor) {
        this.verifyNotAReference();
        this.implementationVendor = implementationVendor;
    }
    
    public void setImplementationVersion(final String implementationVersion) {
        this.verifyNotAReference();
        this.implementationVersion = new DeweyDecimal(implementationVersion);
    }
    
    public void setImplementationUrl(final String implementationURL) {
        this.verifyNotAReference();
        this.implementationURL = implementationURL;
    }
    
    @Override
    public void setRefid(final Reference reference) throws BuildException {
        if (null != this.extensionName || null != this.specificationVersion || null != this.specificationVendor || null != this.implementationVersion || null != this.implementationVendorID || null != this.implementationVendor || null != this.implementationURL) {
            throw this.tooManyAttributes();
        }
        super.setRefid(reference);
    }
    
    private void verifyNotAReference() throws BuildException {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
    }
    
    Extension toExtension() throws BuildException {
        if (this.isReference()) {
            return ((ExtensionAdapter)this.getCheckedRef()).toExtension();
        }
        this.dieOnCircularReference();
        if (null == this.extensionName) {
            final String message = "Extension is missing name.";
            throw new BuildException("Extension is missing name.");
        }
        String specificationVersionString = null;
        if (null != this.specificationVersion) {
            specificationVersionString = this.specificationVersion.toString();
        }
        String implementationVersionString = null;
        if (null != this.implementationVersion) {
            implementationVersionString = this.implementationVersion.toString();
        }
        return new Extension(this.extensionName, specificationVersionString, this.specificationVendor, implementationVersionString, this.implementationVendor, this.implementationVendorID, this.implementationURL);
    }
    
    @Override
    public String toString() {
        return "{" + this.toExtension().toString() + "}";
    }
}
