// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.Localizer;
import com.sun.jersey.localization.LocalizableMessageFactory;

public final class SpiMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableILLEGAL_CONFIG_SYNTAX() {
        return SpiMessages.messageFactory.getMessage("illegal.config.syntax", new Object[0]);
    }
    
    public static String ILLEGAL_CONFIG_SYNTAX() {
        return SpiMessages.localizer.localize(localizableILLEGAL_CONFIG_SYNTAX());
    }
    
    public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.messageFactory.getMessage("provider.could.not.be.created", arg0, arg1, arg2);
    }
    
    public static String PROVIDER_COULD_NOT_BE_CREATED(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.localizer.localize(localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1, arg2));
    }
    
    public static Localizable localizableOSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(final Object arg0) {
        return SpiMessages.messageFactory.getMessage("osgi.registry.error.processing.resource.stream", arg0);
    }
    
    public static String OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(final Object arg0) {
        return SpiMessages.localizer.localize(localizableOSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(arg0));
    }
    
    public static Localizable localizableTEMPLATE_NAME_TO_VALUE_NOT_NULL() {
        return SpiMessages.messageFactory.getMessage("template.name.to.value.not.null", new Object[0]);
    }
    
    public static String TEMPLATE_NAME_TO_VALUE_NOT_NULL() {
        return SpiMessages.localizer.localize(localizableTEMPLATE_NAME_TO_VALUE_NOT_NULL());
    }
    
    public static Localizable localizableILLEGAL_PROVIDER_CLASS_NAME(final Object arg0) {
        return SpiMessages.messageFactory.getMessage("illegal.provider.class.name", arg0);
    }
    
    public static String ILLEGAL_PROVIDER_CLASS_NAME(final Object arg0) {
        return SpiMessages.localizer.localize(localizableILLEGAL_PROVIDER_CLASS_NAME(arg0));
    }
    
    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.messageFactory.getMessage("dependent.class.of.provider.format.error", arg0, arg1, arg2);
    }
    
    public static String DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(arg0, arg1, arg2));
    }
    
    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.messageFactory.getMessage("dependent.class.of.provider.not.found", arg0, arg1, arg2);
    }
    
    public static String DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(arg0, arg1, arg2));
    }
    
    public static Localizable localizableURITEMPLATE_CANNOT_BE_NULL() {
        return SpiMessages.messageFactory.getMessage("uritemplate.cannot.be.null", new Object[0]);
    }
    
    public static String URITEMPLATE_CANNOT_BE_NULL() {
        return SpiMessages.localizer.localize(localizableURITEMPLATE_CANNOT_BE_NULL());
    }
    
    public static Localizable localizablePROVIDER_NOT_FOUND(final Object arg0, final Object arg1) {
        return SpiMessages.messageFactory.getMessage("provider.not.found", arg0, arg1);
    }
    
    public static String PROVIDER_NOT_FOUND(final Object arg0, final Object arg1) {
        return SpiMessages.localizer.localize(localizablePROVIDER_NOT_FOUND(arg0, arg1));
    }
    
    public static Localizable localizableOSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(final Object arg0) {
        return SpiMessages.messageFactory.getMessage("osgi.registry.error.opening.resource.stream", arg0);
    }
    
    public static String OSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(final Object arg0) {
        return SpiMessages.localizer.localize(localizableOSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(arg0));
    }
    
    public static Localizable localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.messageFactory.getMessage("provider.class.could.not.be.loaded", arg0, arg1, arg2);
    }
    
    public static String PROVIDER_CLASS_COULD_NOT_BE_LOADED(final Object arg0, final Object arg1, final Object arg2) {
        return SpiMessages.localizer.localize(localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(arg0, arg1, arg2));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.jersey.impl.spi");
        localizer = new Localizer();
    }
}
