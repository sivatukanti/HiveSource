// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import javax.jdo.JDOUserException;
import javax.jdo.spi.JDOImplHelper;

public class ObjectIdentity extends SingleFieldIdentity
{
    private static JDOImplHelper helper;
    private static final String STRING_DELIMITER = ":";
    
    public ObjectIdentity(final Class pcClass, final Object param) {
        super(pcClass);
        this.assertKeyNotNull(param);
        String paramString = null;
        String keyString = null;
        String className = null;
        if (param instanceof String) {
            paramString = (String)param;
            if (paramString.length() < 3) {
                throw new JDOUserException(ObjectIdentity.msg.msg("EXC_ObjectIdentityStringConstructionTooShort") + ObjectIdentity.msg.msg("EXC_ObjectIdentityStringConstructionUsage", paramString));
            }
            final int indexOfDelimiter = paramString.indexOf(":");
            if (indexOfDelimiter < 0) {
                throw new JDOUserException(ObjectIdentity.msg.msg("EXC_ObjectIdentityStringConstructionNoDelimiter") + ObjectIdentity.msg.msg("EXC_ObjectIdentityStringConstructionUsage", paramString));
            }
            keyString = paramString.substring(indexOfDelimiter + 1);
            className = paramString.substring(0, indexOfDelimiter);
            final JDOImplHelper helper = ObjectIdentity.helper;
            this.keyAsObject = JDOImplHelper.construct(className, keyString);
        }
        else {
            this.keyAsObject = param;
        }
        this.hashCode = (this.hashClassName() ^ this.keyAsObject.hashCode());
    }
    
    public ObjectIdentity() {
    }
    
    public Object getKey() {
        return this.keyAsObject;
    }
    
    @Override
    public String toString() {
        return this.keyAsObject.getClass().getName() + ":" + this.keyAsObject.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ObjectIdentity other = (ObjectIdentity)obj;
        return this.keyAsObject.equals(other.keyAsObject);
    }
    
    @Override
    public int hashCode() {
        return this.keyAsObject.hashCode();
    }
    
    public int compareTo(final Object o) {
        if (o instanceof ObjectIdentity) {
            final ObjectIdentity other = (ObjectIdentity)o;
            final int result = super.compare(other);
            if (result != 0) {
                return result;
            }
            if (other.keyAsObject instanceof Comparable && this.keyAsObject instanceof Comparable) {
                return ((Comparable)this.keyAsObject).compareTo(other.keyAsObject);
            }
            throw new ClassCastException("The key class (" + this.keyAsObject.getClass().getName() + ") does not implement Comparable");
        }
        else {
            if (o == null) {
                throw new ClassCastException("object is null");
            }
            throw new ClassCastException(this.getClass().getName() + " != " + o.getClass().getName());
        }
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(this.keyAsObject);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.keyAsObject = in.readObject();
    }
    
    static {
        ObjectIdentity.helper = AccessController.doPrivileged((PrivilegedAction<JDOImplHelper>)new PrivilegedAction<JDOImplHelper>() {
            public JDOImplHelper run() {
                return JDOImplHelper.getInstance();
            }
        });
    }
}
