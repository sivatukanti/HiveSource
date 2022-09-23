// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.stream.events;

public interface ProcessingInstruction extends XMLEvent
{
    String getTarget();
    
    String getData();
}
