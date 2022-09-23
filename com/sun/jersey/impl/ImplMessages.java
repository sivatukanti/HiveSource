// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.Localizer;
import com.sun.jersey.localization.LocalizableMessageFactory;

public final class ImplMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableERROR_NO_SUB_RES_METHOD_LOCATOR_FOUND(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.no.sub.res.method.locator.found", arg0);
    }
    
    public static String ERROR_NO_SUB_RES_METHOD_LOCATOR_FOUND(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_NO_SUB_RES_METHOD_LOCATOR_FOUND(arg0));
    }
    
    public static Localizable localizableQUALITY_GREATER_THAN_ONE(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("quality.greater.than.one", arg0);
    }
    
    public static String QUALITY_GREATER_THAN_ONE(final Object arg0) {
        return ImplMessages.localizer.localize(localizableQUALITY_GREATER_THAN_ONE(arg0));
    }
    
    public static Localizable localizableSAX_XDK_NO_SECURITY_FEATURES() {
        return ImplMessages.messageFactory.getMessage("sax.xdk.no.security.features", new Object[0]);
    }
    
    public static String SAX_XDK_NO_SECURITY_FEATURES() {
        return ImplMessages.localizer.localize(localizableSAX_XDK_NO_SECURITY_FEATURES());
    }
    
    public static Localizable localizableAMBIGUOUS_SRMS_OUT(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6, final Object arg7) {
        return ImplMessages.messageFactory.getMessage("ambiguous.srms.out", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }
    
    public static String AMBIGUOUS_SRMS_OUT(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6, final Object arg7) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_SRMS_OUT(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7));
    }
    
    public static Localizable localizableAMBIGUOUS_RMS_OUT(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6) {
        return ImplMessages.messageFactory.getMessage("ambiguous.rms.out", arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }
    
    public static String AMBIGUOUS_RMS_OUT(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_RMS_OUT(arg0, arg1, arg2, arg3, arg4, arg5, arg6));
    }
    
    public static Localizable localizableAMBIGUOUS_RMS_IN(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6) {
        return ImplMessages.messageFactory.getMessage("ambiguous.rms.in", arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }
    
    public static String AMBIGUOUS_RMS_IN(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_RMS_IN(arg0, arg1, arg2, arg3, arg4, arg5, arg6));
    }
    
    public static Localizable localizableERROR_GET_CONSUMES_FORM_PARAM(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.get.consumes.form.param", arg0);
    }
    
    public static String ERROR_GET_CONSUMES_FORM_PARAM(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_GET_CONSUMES_FORM_PARAM(arg0));
    }
    
    public static Localizable localizableDEFAULT_COULD_NOT_PROCESS_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("default.could.not.process.method", arg0, arg1);
    }
    
    public static String DEFAULT_COULD_NOT_PROCESS_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableDEFAULT_COULD_NOT_PROCESS_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableNESTED_ERROR(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("nested.error", arg0);
    }
    
    public static String NESTED_ERROR(final Object arg0) {
        return ImplMessages.localizer.localize(localizableNESTED_ERROR(arg0));
    }
    
    public static Localizable localizableAMBIGUOUS_SRMS_IN(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6, final Object arg7) {
        return ImplMessages.messageFactory.getMessage("ambiguous.srms.in", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }
    
    public static String AMBIGUOUS_SRMS_IN(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5, final Object arg6, final Object arg7) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_SRMS_IN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7));
    }
    
    public static Localizable localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("sub.res.method.treated.as.res.method", arg0, arg1);
    }
    
    public static String SUB_RES_METHOD_TREATED_AS_RES_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("multiple.http.method.designators", arg0, arg1);
    }
    
    public static String MULTIPLE_HTTP_METHOD_DESIGNATORS(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(arg0, arg1));
    }
    
    public static Localizable localizableAMBIGUOUS_CTORS(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("ambiguous.ctors", arg0);
    }
    
    public static String AMBIGUOUS_CTORS(final Object arg0) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_CTORS(arg0));
    }
    
    public static Localizable localizableFAILED_TO_CREATE_WEB_RESOURCE(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("failed.to.create.web.resource", arg0);
    }
    
    public static String FAILED_TO_CREATE_WEB_RESOURCE(final Object arg0) {
        return ImplMessages.localizer.localize(localizableFAILED_TO_CREATE_WEB_RESOURCE(arg0));
    }
    
    public static Localizable localizableERROR_GET_RETURNS_VOID(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.get.returns.void", arg0);
    }
    
    public static String ERROR_GET_RETURNS_VOID(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_GET_RETURNS_VOID(arg0));
    }
    
    public static Localizable localizableERROR_RES_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("error.res.uri.path.invalid", arg0, arg1);
    }
    
    public static String ERROR_RES_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableERROR_RES_URI_PATH_INVALID(arg0, arg1));
    }
    
    public static Localizable localizableILLEGAL_PROVIDER_CLASS_NAME(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("illegal.provider.class.name", arg0);
    }
    
    public static String ILLEGAL_PROVIDER_CLASS_NAME(final Object arg0) {
        return ImplMessages.localizer.localize(localizableILLEGAL_PROVIDER_CLASS_NAME(arg0));
    }
    
    public static Localizable localizableERROR_PROCESSING_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("error.processing.method", arg0, arg1);
    }
    
    public static String ERROR_PROCESSING_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableERROR_PROCESSING_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableBAD_URITEMPLATE(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("bad.uritemplate", arg0, arg1);
    }
    
    public static String BAD_URITEMPLATE(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableBAD_URITEMPLATE(arg0, arg1));
    }
    
    public static Localizable localizableRESOURCE_NOT_ACCEPTABLE(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("resource.not.acceptable", arg0, arg1);
    }
    
    public static String RESOURCE_NOT_ACCEPTABLE(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableRESOURCE_NOT_ACCEPTABLE(arg0, arg1));
    }
    
    public static Localizable localizableGENERIC_TYPE_NOT_SUPPORTED(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("generic.type.not.supported", arg0, arg1);
    }
    
    public static String GENERIC_TYPE_NOT_SUPPORTED(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableGENERIC_TYPE_NOT_SUPPORTED(arg0, arg1));
    }
    
    public static Localizable localizableBAD_CONTENT_TYPE(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("bad.content.type", arg0);
    }
    
    public static String BAD_CONTENT_TYPE(final Object arg0) {
        return ImplMessages.localizer.localize(localizableBAD_CONTENT_TYPE(arg0));
    }
    
    public static Localizable localizableERROR_SUBRES_LOC_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("error.subres.loc.uri.path.invalid", arg0, arg1);
    }
    
    public static String ERROR_SUBRES_LOC_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableERROR_SUBRES_LOC_URI_PATH_INVALID(arg0, arg1));
    }
    
    public static Localizable localizableNOT_VALID_HTTPMETHOD(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("not.valid.httpmethod", arg0, arg1, arg2);
    }
    
    public static String NOT_VALID_HTTPMETHOD(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableNOT_VALID_HTTPMETHOD(arg0, arg1, arg2));
    }
    
    public static Localizable localizableNON_PUB_SUB_RES_LOC(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("non.pub.sub.res.loc", arg0);
    }
    
    public static String NON_PUB_SUB_RES_LOC(final Object arg0) {
        return ImplMessages.localizer.localize(localizableNON_PUB_SUB_RES_LOC(arg0));
    }
    
    public static Localizable localizableERROR_GET_CONSUMES_ENTITY(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.get.consumes.entity", arg0);
    }
    
    public static String ERROR_GET_CONSUMES_ENTITY(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_GET_CONSUMES_ENTITY(arg0));
    }
    
    public static Localizable localizableWEB_APP_ALREADY_INITIATED() {
        return ImplMessages.messageFactory.getMessage("web.app.already.initiated", new Object[0]);
    }
    
    public static String WEB_APP_ALREADY_INITIATED() {
        return ImplMessages.localizer.localize(localizableWEB_APP_ALREADY_INITIATED());
    }
    
    public static Localizable localizableAMBIGUOUS_SRLS(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("ambiguous.srls", arg0, arg1, arg2);
    }
    
    public static String AMBIGUOUS_SRLS(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_SRLS(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_UNMARSHALLING_JAXB(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.unmarshalling.jaxb", arg0);
    }
    
    public static String ERROR_UNMARSHALLING_JAXB(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_UNMARSHALLING_JAXB(arg0));
    }
    
    public static Localizable localizableWARNING_LINKFILTER_PROCESSING(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("warning.linkfilter.processing", arg0);
    }
    
    public static String WARNING_LINKFILTER_PROCESSING(final Object arg0) {
        return ImplMessages.localizer.localize(localizableWARNING_LINKFILTER_PROCESSING(arg0));
    }
    
    public static Localizable localizableBAD_CLASS_CONSUMEMIME(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("bad.class.consumemime", arg0, arg1);
    }
    
    public static String BAD_CLASS_CONSUMEMIME(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableBAD_CLASS_CONSUMEMIME(arg0, arg1));
    }
    
    public static Localizable localizableERROR_SUBRES_METHOD_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("error.subres.method.uri.path.invalid", arg0, arg1);
    }
    
    public static String ERROR_SUBRES_METHOD_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableERROR_SUBRES_METHOD_URI_PATH_INVALID(arg0, arg1));
    }
    
    public static Localizable localizableBAD_METHOD_CONSUMEMIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("bad.method.consumemime", arg0, arg1, arg2);
    }
    
    public static String BAD_METHOD_CONSUMEMIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableBAD_METHOD_CONSUMEMIME(arg0, arg1, arg2));
    }
    
    public static Localizable localizableSAX_CANNOT_ENABLE_SECURITY_FEATURES() {
        return ImplMessages.messageFactory.getMessage("sax.cannot.enable.security.features", new Object[0]);
    }
    
    public static String SAX_CANNOT_ENABLE_SECURITY_FEATURES() {
        return ImplMessages.localizer.localize(localizableSAX_CANNOT_ENABLE_SECURITY_FEATURES());
    }
    
    public static Localizable localizableILLEGAL_INITIAL_CAPACITY(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("illegal.initial.capacity", arg0);
    }
    
    public static String ILLEGAL_INITIAL_CAPACITY(final Object arg0) {
        return ImplMessages.localizer.localize(localizableILLEGAL_INITIAL_CAPACITY(arg0));
    }
    
    public static Localizable localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("new.ar.created.by.introspection.modeler", arg0);
    }
    
    public static String NEW_AR_CREATED_BY_INTROSPECTION_MODELER(final Object arg0) {
        return ImplMessages.localizer.localize(localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(arg0));
    }
    
    public static Localizable localizableOBJECT_NOT_A_WEB_RESOURCE(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("object.not.a.webResource", arg0);
    }
    
    public static String OBJECT_NOT_A_WEB_RESOURCE(final Object arg0) {
        return ImplMessages.localizer.localize(localizableOBJECT_NOT_A_WEB_RESOURCE(arg0));
    }
    
    public static Localizable localizableAMBIGUOUS_PARAMETER(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("ambiguous.parameter", arg0, arg1);
    }
    
    public static String AMBIGUOUS_PARAMETER(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_PARAMETER(arg0, arg1));
    }
    
    public static Localizable localizableERROR_RES_URI_PATH_REQUIRED(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.res.uri.path.required", arg0);
    }
    
    public static String ERROR_RES_URI_PATH_REQUIRED(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_RES_URI_PATH_REQUIRED(arg0));
    }
    
    public static Localizable localizableQUALITY_MORE_THAN_THREE(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("quality.more.than.three", arg0);
    }
    
    public static String QUALITY_MORE_THAN_THREE(final Object arg0) {
        return ImplMessages.localizer.localize(localizableQUALITY_MORE_THAN_THREE(arg0));
    }
    
    public static Localizable localizableBAD_CONSUMEMIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("bad.consumemime", arg0, arg1, arg2);
    }
    
    public static String BAD_CONSUMEMIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableBAD_CONSUMEMIME(arg0, arg1, arg2));
    }
    
    public static Localizable localizableUNABLE_TO_WRITE_MIMEMULTIPART() {
        return ImplMessages.messageFactory.getMessage("unable.to.write.mimemultipart", new Object[0]);
    }
    
    public static String UNABLE_TO_WRITE_MIMEMULTIPART() {
        return ImplMessages.localizer.localize(localizableUNABLE_TO_WRITE_MIMEMULTIPART());
    }
    
    public static Localizable localizableNON_PUB_SUB_RES_METHOD(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("non.pub.sub.res.method", arg0);
    }
    
    public static String NON_PUB_SUB_RES_METHOD(final Object arg0) {
        return ImplMessages.localizer.localize(localizableNON_PUB_SUB_RES_METHOD(arg0));
    }
    
    public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("provider.could.not.be.created", arg0, arg1);
    }
    
    public static String PROVIDER_COULD_NOT_BE_CREATED(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1));
    }
    
    public static Localizable localizableBAD_MIME_TYPE(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("bad.mime.type", arg0);
    }
    
    public static String BAD_MIME_TYPE(final Object arg0) {
        return ImplMessages.localizer.localize(localizableBAD_MIME_TYPE(arg0));
    }
    
    public static Localizable localizableAMBIGUOUS_RR_PATH(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("ambiguous.rr.path", arg0, arg1);
    }
    
    public static String AMBIGUOUS_RR_PATH(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableAMBIGUOUS_RR_PATH(arg0, arg1));
    }
    
    public static Localizable localizableILLEGAL_LOAD_FACTOR(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("illegal.load.factor", arg0);
    }
    
    public static String ILLEGAL_LOAD_FACTOR(final Object arg0) {
        return ImplMessages.localizer.localize(localizableILLEGAL_LOAD_FACTOR(arg0));
    }
    
    public static Localizable localizableERROR_MARSHALLING_JAXB(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.marshalling.jaxb", arg0);
    }
    
    public static String ERROR_MARSHALLING_JAXB(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_MARSHALLING_JAXB(arg0));
    }
    
    public static Localizable localizableNOT_VALID_DYNAMICRESOLVINGMETHOD(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("not.valid.dynamicresolvingmethod", arg0, arg1, arg2);
    }
    
    public static String NOT_VALID_DYNAMICRESOLVINGMETHOD(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableNOT_VALID_DYNAMICRESOLVINGMETHOD(arg0, arg1, arg2));
    }
    
    public static Localizable localizableRESOURCE_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("resource.method", arg0, arg1);
    }
    
    public static String RESOURCE_METHOD(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableRESOURCE_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableNO_WEBRESOURCECLASS_IN_WEBXML() {
        return ImplMessages.messageFactory.getMessage("no.webresourceclass.in.webxml", new Object[0]);
    }
    
    public static String NO_WEBRESOURCECLASS_IN_WEBXML() {
        return ImplMessages.localizer.localize(localizableNO_WEBRESOURCECLASS_IN_WEBXML());
    }
    
    public static Localizable localizableILLEGAL_CONFIG_SYNTAX() {
        return ImplMessages.messageFactory.getMessage("illegal.config.syntax", new Object[0]);
    }
    
    public static String ILLEGAL_CONFIG_SYNTAX() {
        return ImplMessages.localizer.localize(localizableILLEGAL_CONFIG_SYNTAX());
    }
    
    public static Localizable localizableBAD_ACCEPT_FIELD(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("bad.accept.field", arg0);
    }
    
    public static String BAD_ACCEPT_FIELD(final Object arg0) {
        return ImplMessages.localizer.localize(localizableBAD_ACCEPT_FIELD(arg0));
    }
    
    public static Localizable localizableERROR_SUBRES_LOC_HAS_ENTITY_PARAM(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.subres.loc.has.entity.param", arg0);
    }
    
    public static String ERROR_SUBRES_LOC_HAS_ENTITY_PARAM(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_SUBRES_LOC_HAS_ENTITY_PARAM(arg0));
    }
    
    public static Localizable localizableROOT_RES_NO_PUBLIC_CTOR(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("root.res.no.public.ctor", arg0);
    }
    
    public static String ROOT_RES_NO_PUBLIC_CTOR(final Object arg0) {
        return ImplMessages.localizer.localize(localizableROOT_RES_NO_PUBLIC_CTOR(arg0));
    }
    
    public static Localizable localizableRESOURCE_MIMETYPE_NOT_IN_PRODUCE_MIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("resource.mimetype.not.in.produceMime", arg0, arg1, arg2);
    }
    
    public static String RESOURCE_MIMETYPE_NOT_IN_PRODUCE_MIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableRESOURCE_MIMETYPE_NOT_IN_PRODUCE_MIME(arg0, arg1, arg2));
    }
    
    public static Localizable localizableBAD_CLASS_PRODUCEMIME(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("bad.class.producemime", arg0, arg1);
    }
    
    public static String BAD_CLASS_PRODUCEMIME(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableBAD_CLASS_PRODUCEMIME(arg0, arg1));
    }
    
    public static Localizable localizableNON_PUB_RES_METHOD(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("non.pub.res.method", arg0);
    }
    
    public static String NON_PUB_RES_METHOD(final Object arg0) {
        return ImplMessages.localizer.localize(localizableNON_PUB_RES_METHOD(arg0));
    }
    
    public static Localizable localizableBYTE_ARRAY_CANNOT_BE_NULL() {
        return ImplMessages.messageFactory.getMessage("byte.array.cannot.be.null", new Object[0]);
    }
    
    public static String BYTE_ARRAY_CANNOT_BE_NULL() {
        return ImplMessages.localizer.localize(localizableBYTE_ARRAY_CANNOT_BE_NULL());
    }
    
    public static Localizable localizableBAD_METHOD_PRODUCEMIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("bad.method.producemime", arg0, arg1, arg2);
    }
    
    public static String BAD_METHOD_PRODUCEMIME(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableBAD_METHOD_PRODUCEMIME(arg0, arg1, arg2));
    }
    
    public static Localizable localizableNO_ROOT_RES_IN_RES_CFG() {
        return ImplMessages.messageFactory.getMessage("no.root.res.in.res.cfg", new Object[0]);
    }
    
    public static String NO_ROOT_RES_IN_RES_CFG() {
        return ImplMessages.localizer.localize(localizableNO_ROOT_RES_IN_RES_CFG());
    }
    
    public static Localizable localizableBAD_METHOD_HTTPMETHOD(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.messageFactory.getMessage("bad.method.httpmethod", arg0, arg1, arg2);
    }
    
    public static String BAD_METHOD_HTTPMETHOD(final Object arg0, final Object arg1, final Object arg2) {
        return ImplMessages.localizer.localize(localizableBAD_METHOD_HTTPMETHOD(arg0, arg1, arg2));
    }
    
    public static Localizable localizableDEFAULT_COULD_NOT_PROCESS_CONSTRUCTOR(final Object arg0, final Object arg1) {
        return ImplMessages.messageFactory.getMessage("default.could.not.process.constructor", arg0, arg1);
    }
    
    public static String DEFAULT_COULD_NOT_PROCESS_CONSTRUCTOR(final Object arg0, final Object arg1) {
        return ImplMessages.localizer.localize(localizableDEFAULT_COULD_NOT_PROCESS_CONSTRUCTOR(arg0, arg1));
    }
    
    public static Localizable localizableERROR_PARSING_ENTITY_TAG(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.parsing.entity.tag", arg0);
    }
    
    public static String ERROR_PARSING_ENTITY_TAG(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_PARSING_ENTITY_TAG(arg0));
    }
    
    public static Localizable localizableFATAL_ISSUES_FOUND_AT_RES_CLASS(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("fatal.issues.found.at.res.class", arg0);
    }
    
    public static String FATAL_ISSUES_FOUND_AT_RES_CLASS(final Object arg0) {
        return ImplMessages.localizer.localize(localizableFATAL_ISSUES_FOUND_AT_RES_CLASS(arg0));
    }
    
    public static Localizable localizableSAX_CANNOT_ENABLE_SECURE_PROCESSING_FEATURE() {
        return ImplMessages.messageFactory.getMessage("sax.cannot.enable.secure.processing.feature", new Object[0]);
    }
    
    public static String SAX_CANNOT_ENABLE_SECURE_PROCESSING_FEATURE() {
        return ImplMessages.localizer.localize(localizableSAX_CANNOT_ENABLE_SECURE_PROCESSING_FEATURE());
    }
    
    public static Localizable localizablePROVIDER_NOT_FOUND(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("provider.not.found", arg0);
    }
    
    public static String PROVIDER_NOT_FOUND(final Object arg0) {
        return ImplMessages.localizer.localize(localizablePROVIDER_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableERROR_CREATING_DEFAULT_WADL_GENERATOR() {
        return ImplMessages.messageFactory.getMessage("error.creating.default.wadl.generator", new Object[0]);
    }
    
    public static String ERROR_CREATING_DEFAULT_WADL_GENERATOR() {
        return ImplMessages.localizer.localize(localizableERROR_CREATING_DEFAULT_WADL_GENERATOR());
    }
    
    public static Localizable localizableERROR_SUBRES_LOC_RETURNS_VOID(final Object arg0) {
        return ImplMessages.messageFactory.getMessage("error.subres.loc.returns.void", arg0);
    }
    
    public static String ERROR_SUBRES_LOC_RETURNS_VOID(final Object arg0) {
        return ImplMessages.localizer.localize(localizableERROR_SUBRES_LOC_RETURNS_VOID(arg0));
    }
    
    public static Localizable localizableEXCEPTION_INVOKING_RESOURCE_METHOD() {
        return ImplMessages.messageFactory.getMessage("exception.invoking.resource.method", new Object[0]);
    }
    
    public static String EXCEPTION_INVOKING_RESOURCE_METHOD() {
        return ImplMessages.localizer.localize(localizableEXCEPTION_INVOKING_RESOURCE_METHOD());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.jersey.impl.impl");
        localizer = new Localizer();
    }
}
