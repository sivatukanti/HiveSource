// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.concurrent.RejectedExecutionException;
import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;
import org.jboss.netty.logging.InternalLogger;

public class DefaultChannelPipeline implements ChannelPipeline
{
    static final InternalLogger logger;
    static final ChannelSink discardingSink;
    private volatile Channel channel;
    private volatile ChannelSink sink;
    private volatile DefaultChannelHandlerContext head;
    private volatile DefaultChannelHandlerContext tail;
    private final Map<String, DefaultChannelHandlerContext> name2ctx;
    
    public DefaultChannelPipeline() {
        this.name2ctx = new HashMap<String, DefaultChannelHandlerContext>(4);
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ChannelSink getSink() {
        final ChannelSink sink = this.sink;
        if (sink == null) {
            return DefaultChannelPipeline.discardingSink;
        }
        return sink;
    }
    
    public void attach(final Channel channel, final ChannelSink sink) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (sink == null) {
            throw new NullPointerException("sink");
        }
        if (this.channel != null || this.sink != null) {
            throw new IllegalStateException("attached already");
        }
        this.channel = channel;
        this.sink = sink;
    }
    
    public boolean isAttached() {
        return this.sink != null;
    }
    
    public synchronized void addFirst(final String name, final ChannelHandler handler) {
        if (this.name2ctx.isEmpty()) {
            this.init(name, handler);
        }
        else {
            this.checkDuplicateName(name);
            final DefaultChannelHandlerContext oldHead = this.head;
            final DefaultChannelHandlerContext newHead = new DefaultChannelHandlerContext(null, oldHead, name, handler);
            callBeforeAdd(newHead);
            oldHead.prev = newHead;
            this.head = newHead;
            this.name2ctx.put(name, newHead);
            this.callAfterAdd(newHead);
        }
    }
    
    public synchronized void addLast(final String name, final ChannelHandler handler) {
        if (this.name2ctx.isEmpty()) {
            this.init(name, handler);
        }
        else {
            this.checkDuplicateName(name);
            final DefaultChannelHandlerContext oldTail = this.tail;
            final DefaultChannelHandlerContext newTail = new DefaultChannelHandlerContext(oldTail, null, name, handler);
            callBeforeAdd(newTail);
            oldTail.next = newTail;
            this.tail = newTail;
            this.name2ctx.put(name, newTail);
            this.callAfterAdd(newTail);
        }
    }
    
    public synchronized void addBefore(final String baseName, final String name, final ChannelHandler handler) {
        final DefaultChannelHandlerContext ctx = this.getContextOrDie(baseName);
        if (ctx == this.head) {
            this.addFirst(name, handler);
        }
        else {
            this.checkDuplicateName(name);
            final DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(ctx.prev, ctx, name, handler);
            callBeforeAdd(newCtx);
            ctx.prev.next = newCtx;
            ctx.prev = newCtx;
            this.name2ctx.put(name, newCtx);
            this.callAfterAdd(newCtx);
        }
    }
    
    public synchronized void addAfter(final String baseName, final String name, final ChannelHandler handler) {
        final DefaultChannelHandlerContext ctx = this.getContextOrDie(baseName);
        if (ctx == this.tail) {
            this.addLast(name, handler);
        }
        else {
            this.checkDuplicateName(name);
            final DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(ctx, ctx.next, name, handler);
            callBeforeAdd(newCtx);
            ctx.next.prev = newCtx;
            ctx.next = newCtx;
            this.name2ctx.put(name, newCtx);
            this.callAfterAdd(newCtx);
        }
    }
    
    public synchronized void remove(final ChannelHandler handler) {
        this.remove(this.getContextOrDie(handler));
    }
    
    public synchronized ChannelHandler remove(final String name) {
        return this.remove(this.getContextOrDie(name)).getHandler();
    }
    
    public synchronized <T extends ChannelHandler> T remove(final Class<T> handlerType) {
        return (T)this.remove(this.getContextOrDie(handlerType)).getHandler();
    }
    
    private DefaultChannelHandlerContext remove(final DefaultChannelHandlerContext ctx) {
        if (this.head == this.tail) {
            callBeforeRemove(ctx);
            final DefaultChannelHandlerContext defaultChannelHandlerContext = null;
            this.tail = defaultChannelHandlerContext;
            this.head = defaultChannelHandlerContext;
            this.name2ctx.clear();
            callAfterRemove(ctx);
        }
        else if (ctx == this.head) {
            this.removeFirst();
        }
        else if (ctx == this.tail) {
            this.removeLast();
        }
        else {
            callBeforeRemove(ctx);
            final DefaultChannelHandlerContext prev = ctx.prev;
            final DefaultChannelHandlerContext next = ctx.next;
            prev.next = next;
            next.prev = prev;
            this.name2ctx.remove(ctx.getName());
            callAfterRemove(ctx);
        }
        return ctx;
    }
    
    public synchronized ChannelHandler removeFirst() {
        if (this.name2ctx.isEmpty()) {
            throw new NoSuchElementException();
        }
        final DefaultChannelHandlerContext oldHead = this.head;
        if (oldHead == null) {
            throw new NoSuchElementException();
        }
        callBeforeRemove(oldHead);
        if (oldHead.next == null) {
            final DefaultChannelHandlerContext defaultChannelHandlerContext = null;
            this.tail = defaultChannelHandlerContext;
            this.head = defaultChannelHandlerContext;
            this.name2ctx.clear();
        }
        else {
            oldHead.next.prev = null;
            this.head = oldHead.next;
            this.name2ctx.remove(oldHead.getName());
        }
        callAfterRemove(oldHead);
        return oldHead.getHandler();
    }
    
    public synchronized ChannelHandler removeLast() {
        if (this.name2ctx.isEmpty()) {
            throw new NoSuchElementException();
        }
        final DefaultChannelHandlerContext oldTail = this.tail;
        if (oldTail == null) {
            throw new NoSuchElementException();
        }
        callBeforeRemove(oldTail);
        if (oldTail.prev == null) {
            final DefaultChannelHandlerContext defaultChannelHandlerContext = null;
            this.tail = defaultChannelHandlerContext;
            this.head = defaultChannelHandlerContext;
            this.name2ctx.clear();
        }
        else {
            oldTail.prev.next = null;
            this.tail = oldTail.prev;
            this.name2ctx.remove(oldTail.getName());
        }
        callAfterRemove(oldTail);
        return oldTail.getHandler();
    }
    
    public synchronized void replace(final ChannelHandler oldHandler, final String newName, final ChannelHandler newHandler) {
        this.replace(this.getContextOrDie(oldHandler), newName, newHandler);
    }
    
    public synchronized ChannelHandler replace(final String oldName, final String newName, final ChannelHandler newHandler) {
        return this.replace(this.getContextOrDie(oldName), newName, newHandler);
    }
    
    public synchronized <T extends ChannelHandler> T replace(final Class<T> oldHandlerType, final String newName, final ChannelHandler newHandler) {
        return (T)this.replace(this.getContextOrDie(oldHandlerType), newName, newHandler);
    }
    
    private ChannelHandler replace(final DefaultChannelHandlerContext ctx, final String newName, final ChannelHandler newHandler) {
        if (ctx == this.head) {
            this.removeFirst();
            this.addFirst(newName, newHandler);
        }
        else if (ctx == this.tail) {
            this.removeLast();
            this.addLast(newName, newHandler);
        }
        else {
            final boolean sameName = ctx.getName().equals(newName);
            if (!sameName) {
                this.checkDuplicateName(newName);
            }
            final DefaultChannelHandlerContext prev = ctx.prev;
            final DefaultChannelHandlerContext next = ctx.next;
            final DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(prev, next, newName, newHandler);
            callBeforeRemove(ctx);
            callBeforeAdd(newCtx);
            prev.next = newCtx;
            next.prev = newCtx;
            if (!sameName) {
                this.name2ctx.remove(ctx.getName());
            }
            this.name2ctx.put(newName, newCtx);
            ChannelHandlerLifeCycleException removeException = null;
            ChannelHandlerLifeCycleException addException = null;
            boolean removed = false;
            try {
                callAfterRemove(ctx);
                removed = true;
            }
            catch (ChannelHandlerLifeCycleException e) {
                removeException = e;
            }
            boolean added = false;
            try {
                this.callAfterAdd(newCtx);
                added = true;
            }
            catch (ChannelHandlerLifeCycleException e2) {
                addException = e2;
            }
            if (!removed && !added) {
                DefaultChannelPipeline.logger.warn(removeException.getMessage(), removeException);
                DefaultChannelPipeline.logger.warn(addException.getMessage(), addException);
                throw new ChannelHandlerLifeCycleException("Both " + ctx.getHandler().getClass().getName() + ".afterRemove() and " + newCtx.getHandler().getClass().getName() + ".afterAdd() failed; see logs.");
            }
            if (!removed) {
                throw removeException;
            }
            if (!added) {
                throw addException;
            }
        }
        return ctx.getHandler();
    }
    
    private static void callBeforeAdd(final ChannelHandlerContext ctx) {
        if (!(ctx.getHandler() instanceof LifeCycleAwareChannelHandler)) {
            return;
        }
        final LifeCycleAwareChannelHandler h = (LifeCycleAwareChannelHandler)ctx.getHandler();
        try {
            h.beforeAdd(ctx);
        }
        catch (Throwable t) {
            throw new ChannelHandlerLifeCycleException(h.getClass().getName() + ".beforeAdd() has thrown an exception; not adding.", t);
        }
    }
    
    private void callAfterAdd(final ChannelHandlerContext ctx) {
        if (!(ctx.getHandler() instanceof LifeCycleAwareChannelHandler)) {
            return;
        }
        final LifeCycleAwareChannelHandler h = (LifeCycleAwareChannelHandler)ctx.getHandler();
        try {
            h.afterAdd(ctx);
        }
        catch (Throwable t3) {
            boolean removed = false;
            try {
                this.remove((DefaultChannelHandlerContext)ctx);
                removed = true;
            }
            catch (Throwable t2) {
                if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                    DefaultChannelPipeline.logger.warn("Failed to remove a handler: " + ctx.getName(), t2);
                }
            }
            if (removed) {
                throw new ChannelHandlerLifeCycleException(h.getClass().getName() + ".afterAdd() has thrown an exception; removed.", t3);
            }
            throw new ChannelHandlerLifeCycleException(h.getClass().getName() + ".afterAdd() has thrown an exception; also failed to remove.", t3);
        }
    }
    
    private static void callBeforeRemove(final ChannelHandlerContext ctx) {
        if (!(ctx.getHandler() instanceof LifeCycleAwareChannelHandler)) {
            return;
        }
        final LifeCycleAwareChannelHandler h = (LifeCycleAwareChannelHandler)ctx.getHandler();
        try {
            h.beforeRemove(ctx);
        }
        catch (Throwable t) {
            throw new ChannelHandlerLifeCycleException(h.getClass().getName() + ".beforeRemove() has thrown an exception; not removing.", t);
        }
    }
    
    private static void callAfterRemove(final ChannelHandlerContext ctx) {
        if (!(ctx.getHandler() instanceof LifeCycleAwareChannelHandler)) {
            return;
        }
        final LifeCycleAwareChannelHandler h = (LifeCycleAwareChannelHandler)ctx.getHandler();
        try {
            h.afterRemove(ctx);
        }
        catch (Throwable t) {
            throw new ChannelHandlerLifeCycleException(h.getClass().getName() + ".afterRemove() has thrown an exception.", t);
        }
    }
    
    public synchronized ChannelHandler getFirst() {
        final DefaultChannelHandlerContext head = this.head;
        if (head == null) {
            return null;
        }
        return head.getHandler();
    }
    
    public synchronized ChannelHandler getLast() {
        final DefaultChannelHandlerContext tail = this.tail;
        if (tail == null) {
            return null;
        }
        return tail.getHandler();
    }
    
    public synchronized ChannelHandler get(final String name) {
        final DefaultChannelHandlerContext ctx = this.name2ctx.get(name);
        if (ctx == null) {
            return null;
        }
        return ctx.getHandler();
    }
    
    public synchronized <T extends ChannelHandler> T get(final Class<T> handlerType) {
        final ChannelHandlerContext ctx = this.getContext(handlerType);
        if (ctx == null) {
            return null;
        }
        final T handler = (T)ctx.getHandler();
        return handler;
    }
    
    public synchronized ChannelHandlerContext getContext(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return this.name2ctx.get(name);
    }
    
    public synchronized ChannelHandlerContext getContext(final ChannelHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        if (this.name2ctx.isEmpty()) {
            return null;
        }
        DefaultChannelHandlerContext ctx = this.head;
        while (ctx.getHandler() != handler) {
            ctx = ctx.next;
            if (ctx == null) {
                return null;
            }
        }
        return ctx;
    }
    
    public synchronized ChannelHandlerContext getContext(final Class<? extends ChannelHandler> handlerType) {
        if (handlerType == null) {
            throw new NullPointerException("handlerType");
        }
        if (this.name2ctx.isEmpty()) {
            return null;
        }
        DefaultChannelHandlerContext ctx = this.head;
        while (!handlerType.isAssignableFrom(ctx.getHandler().getClass())) {
            ctx = ctx.next;
            if (ctx == null) {
                return null;
            }
        }
        return ctx;
    }
    
    public List<String> getNames() {
        final List<String> list = new ArrayList<String>();
        if (this.name2ctx.isEmpty()) {
            return list;
        }
        DefaultChannelHandlerContext ctx = this.head;
        do {
            list.add(ctx.getName());
            ctx = ctx.next;
        } while (ctx != null);
        return list;
    }
    
    public Map<String, ChannelHandler> toMap() {
        final Map<String, ChannelHandler> map = new LinkedHashMap<String, ChannelHandler>();
        if (this.name2ctx.isEmpty()) {
            return map;
        }
        DefaultChannelHandlerContext ctx = this.head;
        do {
            map.put(ctx.getName(), ctx.getHandler());
            ctx = ctx.next;
        } while (ctx != null);
        return map;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append('{');
        DefaultChannelHandlerContext ctx = this.head;
        if (ctx != null) {
            while (true) {
                buf.append('(');
                buf.append(ctx.getName());
                buf.append(" = ");
                buf.append(ctx.getHandler().getClass().getName());
                buf.append(')');
                ctx = ctx.next;
                if (ctx == null) {
                    break;
                }
                buf.append(", ");
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    public void sendUpstream(final ChannelEvent e) {
        final DefaultChannelHandlerContext head = this.getActualUpstreamContext(this.head);
        if (head == null) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("The pipeline contains no upstream handlers; discarding: " + e);
            }
            return;
        }
        this.sendUpstream(head, e);
    }
    
    void sendUpstream(final DefaultChannelHandlerContext ctx, final ChannelEvent e) {
        try {
            ((ChannelUpstreamHandler)ctx.getHandler()).handleUpstream(ctx, e);
        }
        catch (Throwable t) {
            this.notifyHandlerException(e, t);
        }
    }
    
    public void sendDownstream(final ChannelEvent e) {
        final DefaultChannelHandlerContext tail = this.getActualDownstreamContext(this.tail);
        if (tail == null) {
            try {
                this.getSink().eventSunk(this, e);
                return;
            }
            catch (Throwable t) {
                this.notifyHandlerException(e, t);
                return;
            }
        }
        this.sendDownstream(tail, e);
    }
    
    void sendDownstream(final DefaultChannelHandlerContext ctx, final ChannelEvent e) {
        if (e instanceof UpstreamMessageEvent) {
            throw new IllegalArgumentException("cannot send an upstream event to downstream");
        }
        try {
            ((ChannelDownstreamHandler)ctx.getHandler()).handleDownstream(ctx, e);
        }
        catch (Throwable t) {
            e.getFuture().setFailure(t);
            this.notifyHandlerException(e, t);
        }
    }
    
    private DefaultChannelHandlerContext getActualUpstreamContext(final DefaultChannelHandlerContext ctx) {
        if (ctx == null) {
            return null;
        }
        DefaultChannelHandlerContext realCtx = ctx;
        while (!realCtx.canHandleUpstream()) {
            realCtx = realCtx.next;
            if (realCtx == null) {
                return null;
            }
        }
        return realCtx;
    }
    
    private DefaultChannelHandlerContext getActualDownstreamContext(final DefaultChannelHandlerContext ctx) {
        if (ctx == null) {
            return null;
        }
        DefaultChannelHandlerContext realCtx = ctx;
        while (!realCtx.canHandleDownstream()) {
            realCtx = realCtx.prev;
            if (realCtx == null) {
                return null;
            }
        }
        return realCtx;
    }
    
    public ChannelFuture execute(final Runnable task) {
        return this.getSink().execute(this, task);
    }
    
    protected void notifyHandlerException(final ChannelEvent e, final Throwable t) {
        if (e instanceof ExceptionEvent) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("An exception was thrown by a user handler while handling an exception event (" + e + ')', t);
            }
            return;
        }
        ChannelPipelineException pe;
        if (t instanceof ChannelPipelineException) {
            pe = (ChannelPipelineException)t;
        }
        else {
            pe = new ChannelPipelineException(t);
        }
        try {
            this.sink.exceptionCaught(this, e, pe);
        }
        catch (Exception e2) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("An exception was thrown by an exception handler.", e2);
            }
        }
    }
    
    private void init(final String name, final ChannelHandler handler) {
        final DefaultChannelHandlerContext ctx = new DefaultChannelHandlerContext(null, null, name, handler);
        callBeforeAdd(ctx);
        final DefaultChannelHandlerContext defaultChannelHandlerContext = ctx;
        this.tail = defaultChannelHandlerContext;
        this.head = defaultChannelHandlerContext;
        this.name2ctx.clear();
        this.name2ctx.put(name, ctx);
        this.callAfterAdd(ctx);
    }
    
    private void checkDuplicateName(final String name) {
        if (this.name2ctx.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate handler name: " + name);
        }
    }
    
    private DefaultChannelHandlerContext getContextOrDie(final String name) {
        final DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext)this.getContext(name);
        if (ctx == null) {
            throw new NoSuchElementException(name);
        }
        return ctx;
    }
    
    private DefaultChannelHandlerContext getContextOrDie(final ChannelHandler handler) {
        final DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext)this.getContext(handler);
        if (ctx == null) {
            throw new NoSuchElementException(handler.getClass().getName());
        }
        return ctx;
    }
    
    private DefaultChannelHandlerContext getContextOrDie(final Class<? extends ChannelHandler> handlerType) {
        final DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext)this.getContext(handlerType);
        if (ctx == null) {
            throw new NoSuchElementException(handlerType.getName());
        }
        return ctx;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
        discardingSink = new DiscardingChannelSink();
    }
    
    private final class DefaultChannelHandlerContext implements ChannelHandlerContext
    {
        volatile DefaultChannelHandlerContext next;
        volatile DefaultChannelHandlerContext prev;
        private final String name;
        private final ChannelHandler handler;
        private final boolean canHandleUpstream;
        private final boolean canHandleDownstream;
        private volatile Object attachment;
        
        DefaultChannelHandlerContext(final DefaultChannelHandlerContext prev, final DefaultChannelHandlerContext next, final String name, final ChannelHandler handler) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            if (handler == null) {
                throw new NullPointerException("handler");
            }
            this.canHandleUpstream = (handler instanceof ChannelUpstreamHandler);
            this.canHandleDownstream = (handler instanceof ChannelDownstreamHandler);
            if (!this.canHandleUpstream && !this.canHandleDownstream) {
                throw new IllegalArgumentException("handler must be either " + ChannelUpstreamHandler.class.getName() + " or " + ChannelDownstreamHandler.class.getName() + '.');
            }
            this.prev = prev;
            this.next = next;
            this.name = name;
            this.handler = handler;
        }
        
        public Channel getChannel() {
            return this.getPipeline().getChannel();
        }
        
        public ChannelPipeline getPipeline() {
            return DefaultChannelPipeline.this;
        }
        
        public boolean canHandleDownstream() {
            return this.canHandleDownstream;
        }
        
        public boolean canHandleUpstream() {
            return this.canHandleUpstream;
        }
        
        public ChannelHandler getHandler() {
            return this.handler;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Object getAttachment() {
            return this.attachment;
        }
        
        public void setAttachment(final Object attachment) {
            this.attachment = attachment;
        }
        
        public void sendDownstream(final ChannelEvent e) {
            final DefaultChannelHandlerContext prev = DefaultChannelPipeline.this.getActualDownstreamContext(this.prev);
            if (prev == null) {
                try {
                    DefaultChannelPipeline.this.getSink().eventSunk(DefaultChannelPipeline.this, e);
                }
                catch (Throwable t) {
                    DefaultChannelPipeline.this.notifyHandlerException(e, t);
                }
            }
            else {
                DefaultChannelPipeline.this.sendDownstream(prev, e);
            }
        }
        
        public void sendUpstream(final ChannelEvent e) {
            final DefaultChannelHandlerContext next = DefaultChannelPipeline.this.getActualUpstreamContext(this.next);
            if (next != null) {
                DefaultChannelPipeline.this.sendUpstream(next, e);
            }
        }
    }
    
    private static final class DiscardingChannelSink implements ChannelSink
    {
        DiscardingChannelSink() {
        }
        
        public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("Not attached yet; discarding: " + e);
            }
        }
        
        public void exceptionCaught(final ChannelPipeline pipeline, final ChannelEvent e, final ChannelPipelineException cause) throws Exception {
            throw cause;
        }
        
        public ChannelFuture execute(final ChannelPipeline pipeline, final Runnable task) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("Not attached yet; rejecting: " + task);
            }
            return Channels.failedFuture(pipeline.getChannel(), new RejectedExecutionException("Not attached yet"));
        }
    }
}
