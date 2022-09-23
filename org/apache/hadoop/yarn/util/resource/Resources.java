// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util.resource;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
@InterfaceStability.Unstable
public class Resources
{
    private static final Resource NONE;
    private static final Resource UNBOUNDED;
    
    public static Resource createResource(final int memory) {
        return createResource(memory, (memory > 0) ? 1 : 0);
    }
    
    public static Resource createResource(final int memory, final int cores) {
        final Resource resource = Records.newRecord(Resource.class);
        resource.setMemory(memory);
        resource.setVirtualCores(cores);
        return resource;
    }
    
    public static Resource none() {
        return Resources.NONE;
    }
    
    public static Resource unbounded() {
        return Resources.UNBOUNDED;
    }
    
    public static Resource clone(final Resource res) {
        return createResource(res.getMemory(), res.getVirtualCores());
    }
    
    public static Resource addTo(final Resource lhs, final Resource rhs) {
        lhs.setMemory(lhs.getMemory() + rhs.getMemory());
        lhs.setVirtualCores(lhs.getVirtualCores() + rhs.getVirtualCores());
        return lhs;
    }
    
    public static Resource add(final Resource lhs, final Resource rhs) {
        return addTo(clone(lhs), rhs);
    }
    
    public static Resource subtractFrom(final Resource lhs, final Resource rhs) {
        lhs.setMemory(lhs.getMemory() - rhs.getMemory());
        lhs.setVirtualCores(lhs.getVirtualCores() - rhs.getVirtualCores());
        return lhs;
    }
    
    public static Resource subtract(final Resource lhs, final Resource rhs) {
        return subtractFrom(clone(lhs), rhs);
    }
    
    public static Resource negate(final Resource resource) {
        return subtract(Resources.NONE, resource);
    }
    
    public static Resource multiplyTo(final Resource lhs, final double by) {
        lhs.setMemory((int)(lhs.getMemory() * by));
        lhs.setVirtualCores((int)(lhs.getVirtualCores() * by));
        return lhs;
    }
    
    public static Resource multiply(final Resource lhs, final double by) {
        return multiplyTo(clone(lhs), by);
    }
    
    public static Resource multiplyAndNormalizeUp(final ResourceCalculator calculator, final Resource lhs, final double by, final Resource factor) {
        return calculator.multiplyAndNormalizeUp(lhs, by, factor);
    }
    
    public static Resource multiplyAndNormalizeDown(final ResourceCalculator calculator, final Resource lhs, final double by, final Resource factor) {
        return calculator.multiplyAndNormalizeDown(lhs, by, factor);
    }
    
    public static Resource multiplyAndRoundDown(final Resource lhs, final double by) {
        final Resource out = clone(lhs);
        out.setMemory((int)(lhs.getMemory() * by));
        out.setVirtualCores((int)(lhs.getVirtualCores() * by));
        return out;
    }
    
    public static Resource normalize(final ResourceCalculator calculator, final Resource lhs, final Resource min, final Resource max, final Resource increment) {
        return calculator.normalize(lhs, min, max, increment);
    }
    
    public static Resource roundUp(final ResourceCalculator calculator, final Resource lhs, final Resource factor) {
        return calculator.roundUp(lhs, factor);
    }
    
    public static Resource roundDown(final ResourceCalculator calculator, final Resource lhs, final Resource factor) {
        return calculator.roundDown(lhs, factor);
    }
    
    public static boolean isInvalidDivisor(final ResourceCalculator resourceCalculator, final Resource divisor) {
        return resourceCalculator.isInvalidDivisor(divisor);
    }
    
    public static float ratio(final ResourceCalculator resourceCalculator, final Resource lhs, final Resource rhs) {
        return resourceCalculator.ratio(lhs, rhs);
    }
    
    public static float divide(final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource lhs, final Resource rhs) {
        return resourceCalculator.divide(clusterResource, lhs, rhs);
    }
    
    public static Resource divideAndCeil(final ResourceCalculator resourceCalculator, final Resource lhs, final int rhs) {
        return resourceCalculator.divideAndCeil(lhs, rhs);
    }
    
    public static boolean equals(final Resource lhs, final Resource rhs) {
        return lhs.equals(rhs);
    }
    
    public static boolean lessThan(final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource lhs, final Resource rhs) {
        return resourceCalculator.compare(clusterResource, lhs, rhs) < 0;
    }
    
    public static boolean lessThanOrEqual(final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource lhs, final Resource rhs) {
        return resourceCalculator.compare(clusterResource, lhs, rhs) <= 0;
    }
    
    public static boolean greaterThan(final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource lhs, final Resource rhs) {
        return resourceCalculator.compare(clusterResource, lhs, rhs) > 0;
    }
    
    public static boolean greaterThanOrEqual(final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource lhs, final Resource rhs) {
        return resourceCalculator.compare(clusterResource, lhs, rhs) >= 0;
    }
    
    public static Resource min(final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource lhs, final Resource rhs) {
        return (resourceCalculator.compare(clusterResource, lhs, rhs) <= 0) ? lhs : rhs;
    }
    
    public static Resource max(final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource lhs, final Resource rhs) {
        return (resourceCalculator.compare(clusterResource, lhs, rhs) >= 0) ? lhs : rhs;
    }
    
    public static boolean fitsIn(final Resource smaller, final Resource bigger) {
        return smaller.getMemory() <= bigger.getMemory() && smaller.getVirtualCores() <= bigger.getVirtualCores();
    }
    
    public static Resource componentwiseMin(final Resource lhs, final Resource rhs) {
        return createResource(Math.min(lhs.getMemory(), rhs.getMemory()), Math.min(lhs.getVirtualCores(), rhs.getVirtualCores()));
    }
    
    static {
        NONE = new Resource() {
            @Override
            public int getMemory() {
                return 0;
            }
            
            @Override
            public void setMemory(final int memory) {
                throw new RuntimeException("NONE cannot be modified!");
            }
            
            @Override
            public int getVirtualCores() {
                return 0;
            }
            
            @Override
            public void setVirtualCores(final int cores) {
                throw new RuntimeException("NONE cannot be modified!");
            }
            
            @Override
            public int compareTo(final Resource o) {
                int diff = 0 - o.getMemory();
                if (diff == 0) {
                    diff = 0 - o.getVirtualCores();
                }
                return diff;
            }
        };
        UNBOUNDED = new Resource() {
            @Override
            public int getMemory() {
                return Integer.MAX_VALUE;
            }
            
            @Override
            public void setMemory(final int memory) {
                throw new RuntimeException("NONE cannot be modified!");
            }
            
            @Override
            public int getVirtualCores() {
                return Integer.MAX_VALUE;
            }
            
            @Override
            public void setVirtualCores(final int cores) {
                throw new RuntimeException("NONE cannot be modified!");
            }
            
            @Override
            public int compareTo(final Resource o) {
                int diff = 0 - o.getMemory();
                if (diff == 0) {
                    diff = 0 - o.getVirtualCores();
                }
                return diff;
            }
        };
    }
}
