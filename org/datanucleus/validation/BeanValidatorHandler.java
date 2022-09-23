// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.validation;

import javax.validation.groups.Default;
import javax.validation.ConstraintViolationException;
import javax.validation.ConstraintViolation;
import java.util.Set;
import org.datanucleus.util.StringUtils;
import javax.validation.TraversableResolver;
import javax.validation.ValidatorFactory;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.ClassLoaderResolver;
import javax.validation.Validator;
import org.datanucleus.state.CallbackHandler;

public class BeanValidatorHandler implements CallbackHandler
{
    Validator validator;
    ClassLoaderResolver clr;
    PersistenceConfiguration conf;
    
    public BeanValidatorHandler(final ExecutionContext ec, final ValidatorFactory factory) {
        this.conf = ec.getNucleusContext().getPersistenceConfiguration();
        this.clr = ec.getClassLoaderResolver();
        this.validator = factory.usingContext().traversableResolver((TraversableResolver)new PersistenceTraversalResolver(ec)).getValidator();
    }
    
    public void validate(final Object pc, final String callbackName, final Class<?>[] groups) {
        if (this.validator == null) {
            return;
        }
        final Set<ConstraintViolation<Object>> violations = (Set<ConstraintViolation<Object>>)this.validator.validate(pc, (Class[])groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed for " + StringUtils.toJVMIDString(pc) + " during " + callbackName + " for groups " + StringUtils.objectArrayToString(groups) + " - exceptions are attached", (Set)violations);
        }
    }
    
    @Override
    public void preDelete(final Object pc) {
        final Class<?>[] groups = this.getGroups(this.conf.getStringProperty("datanucleus.validation.group.pre-remove"), "pre-remove");
        if (groups != null) {
            this.validate(pc, "pre-remove", groups);
        }
    }
    
    @Override
    public void preStore(final Object pc) {
        final Class<?>[] groups = this.getGroups(this.conf.getStringProperty("datanucleus.validation.group.pre-update"), "pre-update");
        if (groups != null) {
            this.validate(pc, "pre-update", groups);
        }
    }
    
    @Override
    public void prePersist(final Object pc) {
        final Class<?>[] groups = this.getGroups(this.conf.getStringProperty("datanucleus.validation.group.pre-persist"), "pre-persist");
        if (groups != null) {
            this.validate(pc, "pre-persist", groups);
        }
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void setValidationListener(final CallbackHandler handler) {
    }
    
    @Override
    public void addListener(final Object listener, final Class[] classes) {
    }
    
    @Override
    public void removeListener(final Object listener) {
    }
    
    @Override
    public void postAttach(final Object pc, final Object detachedPC) {
    }
    
    @Override
    public void postClear(final Object pc) {
    }
    
    @Override
    public void postCreate(final Object pc) {
    }
    
    @Override
    public void postDelete(final Object pc) {
    }
    
    @Override
    public void postDetach(final Object pc, final Object detachedPC) {
    }
    
    @Override
    public void postDirty(final Object pc) {
    }
    
    @Override
    public void postLoad(final Object pc) {
    }
    
    @Override
    public void postRefresh(final Object pc) {
    }
    
    @Override
    public void postStore(final Object pc) {
    }
    
    @Override
    public void preAttach(final Object detachedPC) {
    }
    
    @Override
    public void preClear(final Object pc) {
    }
    
    @Override
    public void preDetach(final Object pc) {
    }
    
    @Override
    public void preDirty(final Object pc) {
    }
    
    private Class<?>[] getGroups(final String property, final String eventName) {
        if (property != null && property.trim().length() != 0) {
            final String[] classNames = property.trim().split(",");
            final Class<?>[] groups = (Class<?>[])new Class[classNames.length];
            for (int i = 0; i < classNames.length; ++i) {
                groups[i] = (Class<?>)this.clr.classForName(classNames[i].trim());
            }
            return groups;
        }
        if (eventName.equals("pre-persist") || eventName.equals("pre-update")) {
            return (Class<?>[])new Class[] { Default.class };
        }
        return null;
    }
}
