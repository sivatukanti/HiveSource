// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

public interface TypeVisitor
{
    void visit(final GroupType p0);
    
    void visit(final MessageType p0);
    
    void visit(final PrimitiveType p0);
}
