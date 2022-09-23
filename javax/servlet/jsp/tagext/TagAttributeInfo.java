// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

public class TagAttributeInfo
{
    public static final String ID = "id";
    private String name;
    private String type;
    private boolean reqTime;
    private boolean required;
    private boolean fragment;
    private boolean deferredValue;
    private boolean deferredMethod;
    private String expectedTypeName;
    private String methodSignature;
    private String description;
    
    public TagAttributeInfo(final String name, final boolean required, final String type, final boolean reqTime) {
        this.name = name;
        this.required = required;
        this.type = type;
        this.reqTime = reqTime;
    }
    
    public TagAttributeInfo(final String name, final boolean required, final String type, final boolean reqTime, final boolean fragment) {
        this(name, required, type, reqTime);
        this.fragment = fragment;
    }
    
    public TagAttributeInfo(final String name, final boolean required, final String type, final boolean reqTime, final boolean fragment, final String description, final boolean deferredValue, final boolean deferredMethod, final String expectedTypeName, final String methodSignature) {
        this(name, required, type, reqTime, fragment);
        this.description = description;
        this.deferredValue = deferredValue;
        this.deferredMethod = deferredMethod;
        this.expectedTypeName = expectedTypeName;
        this.methodSignature = methodSignature;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTypeName() {
        return this.type;
    }
    
    public boolean canBeRequestTime() {
        return this.reqTime;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public static TagAttributeInfo getIdAttribute(final TagAttributeInfo[] a) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i].getName().equals("id")) {
                return a[i];
            }
        }
        return null;
    }
    
    public boolean isFragment() {
        return this.fragment;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isDeferredValue() {
        return this.deferredValue;
    }
    
    public boolean isDeferredMethod() {
        return this.deferredMethod;
    }
    
    public String getExpectedTypeName() {
        return this.expectedTypeName;
    }
    
    public String getMethodSignature() {
        return this.methodSignature;
    }
    
    @Override
    public String toString() {
        final StringBuffer b = new StringBuffer();
        b.append("name = " + this.name + " ");
        b.append("type = " + this.type + " ");
        b.append("reqTime = " + this.reqTime + " ");
        b.append("required = " + this.required + " ");
        b.append("fragment = " + this.fragment + " ");
        b.append("deferredValue = " + this.deferredValue + " ");
        b.append("deferredMethod = " + this.deferredMethod + " ");
        b.append("expectedTypeName = " + this.expectedTypeName + " ");
        b.append("methodSignature = " + this.methodSignature + " ");
        return b.toString();
    }
}
