// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import parquet.io.InvalidRecordException;
import java.util.List;
import parquet.Preconditions;

public abstract class Type
{
    private final String name;
    private final Repetition repetition;
    private final OriginalType originalType;
    private final ID id;
    
    @Deprecated
    public Type(final String name, final Repetition repetition) {
        this(name, repetition, null, null);
    }
    
    @Deprecated
    public Type(final String name, final Repetition repetition, final OriginalType originalType) {
        this(name, repetition, originalType, null);
    }
    
    Type(final String name, final Repetition repetition, final OriginalType originalType, final ID id) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.repetition = Preconditions.checkNotNull(repetition, "repetition");
        this.originalType = originalType;
        this.id = id;
    }
    
    public abstract Type withId(final int p0);
    
    public String getName() {
        return this.name;
    }
    
    public boolean isRepetition(final Repetition rep) {
        return this.repetition == rep;
    }
    
    public Repetition getRepetition() {
        return this.repetition;
    }
    
    public ID getId() {
        return this.id;
    }
    
    public OriginalType getOriginalType() {
        return this.originalType;
    }
    
    public abstract boolean isPrimitive();
    
    public GroupType asGroupType() {
        if (this.isPrimitive()) {
            throw new ClassCastException(this + " is not a group");
        }
        return (GroupType)this;
    }
    
    public PrimitiveType asPrimitiveType() {
        if (!this.isPrimitive()) {
            throw new ClassCastException(this + " is not primitive");
        }
        return (PrimitiveType)this;
    }
    
    public abstract void writeToStringBuilder(final StringBuilder p0, final String p1);
    
    public abstract void accept(final TypeVisitor p0);
    
    @Deprecated
    protected abstract int typeHashCode();
    
    @Deprecated
    protected abstract boolean typeEquals(final Type p0);
    
    @Override
    public int hashCode() {
        int c = this.repetition.hashCode();
        c = 31 * c + this.name.hashCode();
        if (this.originalType != null) {
            c = 31 * c + this.originalType.hashCode();
        }
        if (this.id != null) {
            c = 31 * c + this.id.hashCode();
        }
        return c;
    }
    
    protected boolean equals(final Type other) {
        return this.name.equals(other.name) && this.repetition == other.repetition && this.eqOrBothNull(this.repetition, other.repetition) && this.eqOrBothNull(this.id, other.id);
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof Type && other != null && this.equals((Type)other);
    }
    
    protected boolean eqOrBothNull(final Object o1, final Object o2) {
        return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2));
    }
    
    protected abstract int getMaxRepetitionLevel(final String[] p0, final int p1);
    
    protected abstract int getMaxDefinitionLevel(final String[] p0, final int p1);
    
    protected abstract Type getType(final String[] p0, final int p1);
    
    protected abstract List<String[]> getPaths(final int p0);
    
    protected abstract boolean containsPath(final String[] p0, final int p1);
    
    protected abstract Type union(final Type p0);
    
    protected abstract Type union(final Type p0, final boolean p1);
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        this.writeToStringBuilder(sb, "");
        return sb.toString();
    }
    
    void checkContains(final Type subType) {
        if (!this.name.equals(subType.name) || this.repetition != subType.repetition) {
            throw new InvalidRecordException(subType + " found: expected " + this);
        }
    }
    
    abstract <T> T convert(final List<GroupType> p0, final TypeConverter<T> p1);
    
    public static final class ID
    {
        private final int id;
        
        public ID(final int id) {
            this.id = id;
        }
        
        public int intValue() {
            return this.id;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof ID && ((ID)obj).id == this.id;
        }
        
        @Override
        public int hashCode() {
            return this.id;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.id);
        }
    }
    
    public enum Repetition
    {
        REQUIRED {
            @Override
            public boolean isMoreRestrictiveThan(final Repetition other) {
                return other != Type$Repetition$1.REQUIRED;
            }
        }, 
        OPTIONAL {
            @Override
            public boolean isMoreRestrictiveThan(final Repetition other) {
                return other == Type$Repetition$2.REPEATED;
            }
        }, 
        REPEATED {
            @Override
            public boolean isMoreRestrictiveThan(final Repetition other) {
                return false;
            }
        };
        
        public abstract boolean isMoreRestrictiveThan(final Repetition p0);
    }
}
