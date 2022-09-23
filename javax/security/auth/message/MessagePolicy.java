// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message;

public class MessagePolicy
{
    private final TargetPolicy[] targetPolicies;
    private final boolean mandatory;
    
    public MessagePolicy(final TargetPolicy[] targetPolicies, final boolean mandatory) throws IllegalArgumentException {
        if (targetPolicies == null) {
            throw new IllegalArgumentException("targetPolicies is null");
        }
        this.targetPolicies = targetPolicies;
        this.mandatory = mandatory;
    }
    
    public TargetPolicy[] getTargetPolicies() {
        if (this.targetPolicies.length == 0) {
            return null;
        }
        return this.targetPolicies;
    }
    
    public boolean isMandatory() {
        return this.mandatory;
    }
    
    public static class TargetPolicy
    {
        private final Target[] targets;
        private final ProtectionPolicy protectionPolicy;
        
        public TargetPolicy(final Target[] targets, final ProtectionPolicy protectionPolicy) throws IllegalArgumentException {
            if (protectionPolicy == null) {
                throw new IllegalArgumentException("protectionPolicy is null");
            }
            this.targets = targets;
            this.protectionPolicy = protectionPolicy;
        }
        
        public Target[] getTargets() {
            if (this.targets == null || this.targets.length == 0) {
                return null;
            }
            return this.targets;
        }
        
        public ProtectionPolicy getProtectionPolicy() {
            return this.protectionPolicy;
        }
    }
    
    public interface Target
    {
        Object get(final MessageInfo p0);
        
        void put(final MessageInfo p0, final Object p1);
        
        void remove(final MessageInfo p0);
    }
    
    public interface ProtectionPolicy
    {
        public static final String AUTHENTICATE_CONTENT = "#authenticateContent";
        public static final String AUTHENTICATE_RECIPIENT = "#authenticateRecipient";
        public static final String AUTHENTICATE_SENDER = "#authenticateSender";
        
        String getID();
    }
}
