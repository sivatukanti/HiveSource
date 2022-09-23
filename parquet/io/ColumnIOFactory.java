// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.schema.PrimitiveType;
import java.util.Iterator;
import parquet.schema.GroupType;
import java.util.ArrayList;
import parquet.schema.Type;
import java.util.List;
import parquet.schema.TypeVisitor;
import parquet.schema.MessageType;

public class ColumnIOFactory
{
    private final boolean validating;
    
    public ColumnIOFactory() {
        this(false);
    }
    
    public ColumnIOFactory(final boolean validating) {
        this.validating = validating;
    }
    
    public MessageColumnIO getColumnIO(final MessageType requestedSchema, final MessageType fileSchema) {
        return this.getColumnIO(requestedSchema, fileSchema, true);
    }
    
    public MessageColumnIO getColumnIO(final MessageType requestedSchema, final MessageType fileSchema, final boolean strict) {
        final ColumnIOCreatorVisitor visitor = new ColumnIOCreatorVisitor(this.validating, requestedSchema, strict);
        fileSchema.accept(visitor);
        return visitor.getColumnIO();
    }
    
    public MessageColumnIO getColumnIO(final MessageType schema) {
        return this.getColumnIO(schema, schema);
    }
    
    public class ColumnIOCreatorVisitor implements TypeVisitor
    {
        private MessageColumnIO columnIO;
        private GroupColumnIO current;
        private List<PrimitiveColumnIO> leaves;
        private final boolean validating;
        private final MessageType requestedSchema;
        private int currentRequestedIndex;
        private Type currentRequestedType;
        private boolean strictTypeChecking;
        
        public ColumnIOCreatorVisitor(final ColumnIOFactory columnIOFactory, final boolean validating, final MessageType requestedSchema) {
            this(columnIOFactory, validating, requestedSchema, true);
        }
        
        public ColumnIOCreatorVisitor(final boolean validating, final MessageType requestedSchema, final boolean strictTypeChecking) {
            this.leaves = new ArrayList<PrimitiveColumnIO>();
            this.validating = validating;
            this.requestedSchema = requestedSchema;
            this.strictTypeChecking = strictTypeChecking;
        }
        
        @Override
        public void visit(final MessageType messageType) {
            this.visitChildren(this.columnIO = new MessageColumnIO(this.requestedSchema, this.validating), messageType, this.requestedSchema);
            this.columnIO.setLevels();
            this.columnIO.setLeaves(this.leaves);
        }
        
        @Override
        public void visit(final GroupType groupType) {
            if (this.currentRequestedType.isPrimitive()) {
                this.incompatibleSchema(groupType, this.currentRequestedType);
            }
            final GroupColumnIO newIO = new GroupColumnIO(groupType, this.current, this.currentRequestedIndex);
            this.current.add(newIO);
            this.visitChildren(newIO, groupType, this.currentRequestedType.asGroupType());
        }
        
        private void visitChildren(final GroupColumnIO newIO, final GroupType groupType, final GroupType requestedGroupType) {
            final GroupColumnIO oldIO = this.current;
            this.current = newIO;
            for (final Type type : groupType.getFields()) {
                if (requestedGroupType.containsField(type.getName())) {
                    this.currentRequestedIndex = requestedGroupType.getFieldIndex(type.getName());
                    this.currentRequestedType = requestedGroupType.getType(this.currentRequestedIndex);
                    if (this.currentRequestedType.getRepetition().isMoreRestrictiveThan(type.getRepetition())) {
                        this.incompatibleSchema(type, this.currentRequestedType);
                    }
                    type.accept(this);
                }
            }
            this.current = oldIO;
        }
        
        @Override
        public void visit(final PrimitiveType primitiveType) {
            if (!this.currentRequestedType.isPrimitive() || (this.strictTypeChecking && this.currentRequestedType.asPrimitiveType().getPrimitiveTypeName() != primitiveType.getPrimitiveTypeName())) {
                this.incompatibleSchema(primitiveType, this.currentRequestedType);
            }
            final PrimitiveColumnIO newIO = new PrimitiveColumnIO(primitiveType, this.current, this.currentRequestedIndex, this.leaves.size());
            this.current.add(newIO);
            this.leaves.add(newIO);
        }
        
        private void incompatibleSchema(final Type fileType, final Type requestedType) {
            throw new ParquetDecodingException("The requested schema is not compatible with the file schema. incompatible types: " + requestedType + " != " + fileType);
        }
        
        public MessageColumnIO getColumnIO() {
            return this.columnIO;
        }
    }
}
