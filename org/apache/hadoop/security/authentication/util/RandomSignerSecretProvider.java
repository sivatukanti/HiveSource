// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import com.google.common.annotations.VisibleForTesting;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.Private
public class RandomSignerSecretProvider extends RolloverSignerSecretProvider
{
    private final Random rand;
    
    public RandomSignerSecretProvider() {
        this.rand = new SecureRandom();
    }
    
    @VisibleForTesting
    public RandomSignerSecretProvider(final long seed) {
        this.rand = new Random(seed);
    }
    
    @Override
    protected byte[] generateNewSecret() {
        final byte[] secret = new byte[32];
        this.rand.nextBytes(secret);
        return secret;
    }
}
