// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.view;

public class Viewable
{
    private final String templateName;
    private final Object model;
    private final Class<?> resolvingClass;
    
    public Viewable(final String templateName) {
        this(templateName, null, null);
    }
    
    public Viewable(final String templateName, final Object model) {
        this(templateName, model, null);
    }
    
    public Viewable(final String templateName, final Object model, final Class<?> resolvingClass) throws IllegalArgumentException {
        if (templateName == null) {
            throw new IllegalArgumentException("The template name MUST not be null");
        }
        this.templateName = templateName;
        this.model = model;
        this.resolvingClass = resolvingClass;
    }
    
    public String getTemplateName() {
        return this.templateName;
    }
    
    public Object getModel() {
        return this.model;
    }
    
    public Class<?> getResolvingClass() {
        return this.resolvingClass;
    }
    
    public boolean isTemplateNameAbsolute() {
        return this.templateName.length() > 0 && this.templateName.charAt(0) == '/';
    }
}
