// 
// Decompiled by Procyon v0.5.36
// 

package jersey.repackaged.org.objectweb.asm;

public final class Handle
{
    final int tag;
    final String owner;
    final String name;
    final String desc;
    
    public Handle(final int tag, final String owner, final String name, final String desc) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }
    
    public int getTag() {
        return this.tag;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Handle)) {
            return false;
        }
        final Handle h = (Handle)obj;
        return this.tag == h.tag && this.owner.equals(h.owner) && this.name.equals(h.name) && this.desc.equals(h.desc);
    }
    
    @Override
    public int hashCode() {
        return this.tag + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
    }
    
    @Override
    public String toString() {
        return this.owner + '.' + this.name + this.desc + " (" + this.tag + ')';
    }
}
