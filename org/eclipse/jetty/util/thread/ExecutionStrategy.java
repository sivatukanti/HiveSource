// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import org.eclipse.jetty.util.log.Log;
import java.lang.reflect.Constructor;
import org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.log.Logger;
import java.util.concurrent.Executor;

public interface ExecutionStrategy
{
    void dispatch();
    
    void execute();
    
    public interface Factory
    {
        ExecutionStrategy newExecutionStrategy(final Producer p0, final Executor p1);
        
        default Factory getDefault() {
            return DefaultExecutionStrategyFactory.INSTANCE;
        }
        
        @Deprecated
        default ExecutionStrategy instanceFor(final Producer producer, final Executor executor) {
            return getDefault().newExecutionStrategy(producer, executor);
        }
    }
    
    public static class DefaultExecutionStrategyFactory implements Factory
    {
        private static final Logger LOG;
        private static final Factory INSTANCE;
        
        @Override
        public ExecutionStrategy newExecutionStrategy(final Producer producer, final Executor executor) {
            final String strategy = System.getProperty(producer.getClass().getName() + ".ExecutionStrategy");
            if (strategy != null) {
                try {
                    final Class<? extends ExecutionStrategy> c = (Class<? extends ExecutionStrategy>)Loader.loadClass(producer.getClass(), strategy);
                    final Constructor<? extends ExecutionStrategy> m = c.getConstructor(Producer.class, Executor.class);
                    DefaultExecutionStrategyFactory.LOG.info("Use {} for {}", c.getSimpleName(), producer.getClass().getName());
                    return (ExecutionStrategy)m.newInstance(producer, executor);
                }
                catch (Exception e) {
                    DefaultExecutionStrategyFactory.LOG.warn(e);
                }
            }
            return new ExecuteProduceConsume(producer, executor);
        }
        
        static {
            LOG = Log.getLogger(Factory.class);
            INSTANCE = new DefaultExecutionStrategyFactory();
        }
    }
    
    public interface Producer
    {
        Runnable produce();
    }
    
    public interface Rejectable
    {
        void reject();
    }
}
