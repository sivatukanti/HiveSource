// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.stream.events;

public interface EntityReference extends XMLEvent
{
    EntityDeclaration getDeclaration();
    
    String getName();
}
