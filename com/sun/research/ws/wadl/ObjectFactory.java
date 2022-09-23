// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.research.ws.wadl;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory
{
    public Doc createDoc() {
        return new Doc();
    }
    
    public Method createMethod() {
        return new Method();
    }
    
    public Option createOption() {
        return new Option();
    }
    
    public Resources createResources() {
        return new Resources();
    }
    
    public Grammars createGrammars() {
        return new Grammars();
    }
    
    public ResourceType createResourceType() {
        return new ResourceType();
    }
    
    public Include createInclude() {
        return new Include();
    }
    
    public Resource createResource() {
        return new Resource();
    }
    
    public Param createParam() {
        return new Param();
    }
    
    public Link createLink() {
        return new Link();
    }
    
    public Representation createRepresentation() {
        return new Representation();
    }
    
    public Request createRequest() {
        return new Request();
    }
    
    public Response createResponse() {
        return new Response();
    }
    
    public Application createApplication() {
        return new Application();
    }
}
