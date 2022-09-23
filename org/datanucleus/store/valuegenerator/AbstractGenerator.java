// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.Properties;
import org.datanucleus.util.Localiser;

public abstract class AbstractGenerator implements ValueGenerator
{
    protected static final Localiser LOCALISER;
    protected String name;
    protected Properties properties;
    protected int allocationSize;
    protected int initialValue;
    protected ValueGenerationBlock block;
    protected boolean repositoryExists;
    
    public AbstractGenerator(final String name, final Properties props) {
        this.allocationSize = 5;
        this.initialValue = 0;
        this.repositoryExists = false;
        this.name = name;
        this.properties = props;
    }
    
    public static Class getStorageClass() {
        return Long.class;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public synchronized Object next() {
        if (this.block == null || !this.block.hasNext()) {
            this.block = this.obtainGenerationBlock();
        }
        return this.block.next().getValue();
    }
    
    @Override
    public synchronized Object current() {
        if (this.block == null) {
            return null;
        }
        return this.block.current().getValue();
    }
    
    @Override
    public long nextValue() {
        return this.getLongValueForObject(this.next());
    }
    
    @Override
    public long currentValue() {
        return this.getLongValueForObject(this.current());
    }
    
    private long getLongValueForObject(final Object oid) {
        if (oid instanceof Long) {
            return (long)oid;
        }
        if (oid instanceof Integer) {
            return (long)oid;
        }
        if (oid instanceof Short) {
            return (long)oid;
        }
        throw new NucleusDataStoreException(AbstractGenerator.LOCALISER.msg("040009", this.name));
    }
    
    @Override
    public synchronized void allocate(final int additional) {
        if (this.block == null) {
            this.block = this.obtainGenerationBlock(additional);
        }
        else {
            this.block.addBlock(this.obtainGenerationBlock(additional));
        }
    }
    
    protected ValueGenerationBlock obtainGenerationBlock() {
        return this.obtainGenerationBlock(-1);
    }
    
    protected ValueGenerationBlock obtainGenerationBlock(final int number) {
        ValueGenerationBlock block = null;
        boolean repository_exists = true;
        try {
            if (this.requiresRepository() && !this.repositoryExists && !(this.repositoryExists = this.repositoryExists())) {
                this.createRepository();
                this.repositoryExists = true;
            }
            try {
                if (number < 0) {
                    block = this.reserveBlock();
                }
                else {
                    block = this.reserveBlock(number);
                }
            }
            catch (ValueGenerationException vex) {
                NucleusLogger.VALUEGENERATION.info(AbstractGenerator.LOCALISER.msg("040003", vex.getMessage()));
                if (!this.requiresRepository()) {
                    throw vex;
                }
                repository_exists = false;
            }
            catch (RuntimeException ex) {
                NucleusLogger.VALUEGENERATION.info(AbstractGenerator.LOCALISER.msg("040003", ex.getMessage()));
                if (!this.requiresRepository()) {
                    throw ex;
                }
                repository_exists = false;
            }
        }
        finally {}
        if (!repository_exists) {
            try {
                NucleusLogger.VALUEGENERATION.info(AbstractGenerator.LOCALISER.msg("040005"));
                if (!this.createRepository()) {
                    throw new ValueGenerationException(AbstractGenerator.LOCALISER.msg("040002"));
                }
                if (number < 0) {
                    block = this.reserveBlock();
                }
                else {
                    block = this.reserveBlock(number);
                }
            }
            finally {}
        }
        return block;
    }
    
    protected ValueGenerationBlock reserveBlock() {
        return this.reserveBlock(this.allocationSize);
    }
    
    protected abstract ValueGenerationBlock reserveBlock(final long p0);
    
    protected boolean requiresRepository() {
        return false;
    }
    
    protected boolean repositoryExists() {
        return true;
    }
    
    protected boolean createRepository() {
        return true;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
