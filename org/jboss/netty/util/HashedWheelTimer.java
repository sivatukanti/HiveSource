// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

import org.jboss.netty.util.internal.DetectionUtil;
import java.util.HashSet;
import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.jboss.netty.util.internal.SharedResourceMisuseDetector;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.logging.InternalLogger;

public class HashedWheelTimer implements Timer
{
    static final InternalLogger logger;
    private static final AtomicInteger id;
    private static final SharedResourceMisuseDetector misuseDetector;
    private static final AtomicIntegerFieldUpdater<HashedWheelTimer> WORKER_STATE_UPDATER;
    private final Worker worker;
    private final Thread workerThread;
    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    private volatile int workerState;
    private final long tickDuration;
    private final HashedWheelBucket[] wheel;
    private final int mask;
    private final CountDownLatch startTimeInitialized;
    private final Queue<HashedWheelTimeout> timeouts;
    private volatile long startTime;
    
    public HashedWheelTimer() {
        this(Executors.defaultThreadFactory());
    }
    
    public HashedWheelTimer(final long tickDuration, final TimeUnit unit) {
        this(Executors.defaultThreadFactory(), tickDuration, unit);
    }
    
    public HashedWheelTimer(final long tickDuration, final TimeUnit unit, final int ticksPerWheel) {
        this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
    }
    
    public HashedWheelTimer(final ThreadFactory threadFactory) {
        this(threadFactory, 100L, TimeUnit.MILLISECONDS);
    }
    
    public HashedWheelTimer(final ThreadFactory threadFactory, final long tickDuration, final TimeUnit unit) {
        this(threadFactory, tickDuration, unit, 512);
    }
    
    public HashedWheelTimer(final ThreadFactory threadFactory, final long tickDuration, final TimeUnit unit, final int ticksPerWheel) {
        this(threadFactory, null, tickDuration, unit, ticksPerWheel);
    }
    
    public HashedWheelTimer(final ThreadFactory threadFactory, final ThreadNameDeterminer determiner, final long tickDuration, final TimeUnit unit, final int ticksPerWheel) {
        this.worker = new Worker();
        this.workerState = 0;
        this.startTimeInitialized = new CountDownLatch(1);
        this.timeouts = new ConcurrentLinkedQueue<HashedWheelTimeout>();
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (tickDuration <= 0L) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
        this.wheel = createWheel(ticksPerWheel);
        this.mask = this.wheel.length - 1;
        this.tickDuration = unit.toNanos(tickDuration);
        if (this.tickDuration >= Long.MAX_VALUE / this.wheel.length) {
            throw new IllegalArgumentException(String.format("tickDuration: %d (expected: 0 < tickDuration in nanos < %d", tickDuration, Long.MAX_VALUE / this.wheel.length));
        }
        this.workerThread = threadFactory.newThread(new ThreadRenamingRunnable(this.worker, "Hashed wheel timer #" + HashedWheelTimer.id.incrementAndGet(), determiner));
        HashedWheelTimer.misuseDetector.increase();
    }
    
    private static HashedWheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
        if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException("ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
        }
        ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
        final HashedWheelBucket[] wheel = new HashedWheelBucket[ticksPerWheel];
        for (int i = 0; i < wheel.length; ++i) {
            wheel[i] = new HashedWheelBucket();
        }
        return wheel;
    }
    
    private static int normalizeTicksPerWheel(final int ticksPerWheel) {
        int normalizedTicksPerWheel;
        for (normalizedTicksPerWheel = 1; normalizedTicksPerWheel < ticksPerWheel; normalizedTicksPerWheel <<= 1) {}
        return normalizedTicksPerWheel;
    }
    
    public void start() {
        switch (HashedWheelTimer.WORKER_STATE_UPDATER.get(this)) {
            case 0: {
                if (HashedWheelTimer.WORKER_STATE_UPDATER.compareAndSet(this, 0, 1)) {
                    this.workerThread.start();
                    break;
                }
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                throw new IllegalStateException("cannot be started once stopped");
            }
            default: {
                throw new Error("Invalid WorkerState");
            }
        }
        while (this.startTime == 0L) {
            try {
                this.startTimeInitialized.await();
            }
            catch (InterruptedException ignore) {}
        }
    }
    
    public Set<Timeout> stop() {
        if (Thread.currentThread() == this.workerThread) {
            throw new IllegalStateException(HashedWheelTimer.class.getSimpleName() + ".stop() cannot be called from " + TimerTask.class.getSimpleName());
        }
        if (!HashedWheelTimer.WORKER_STATE_UPDATER.compareAndSet(this, 1, 2)) {
            HashedWheelTimer.WORKER_STATE_UPDATER.set(this, 2);
            HashedWheelTimer.misuseDetector.decrease();
            return Collections.emptySet();
        }
        boolean interrupted = false;
        while (this.workerThread.isAlive()) {
            this.workerThread.interrupt();
            try {
                this.workerThread.join(100L);
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        HashedWheelTimer.misuseDetector.decrease();
        return this.worker.unprocessedTimeouts();
    }
    
    public Timeout newTimeout(final TimerTask task, final long delay, final TimeUnit unit) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        this.start();
        final long deadline = System.nanoTime() + unit.toNanos(delay) - this.startTime;
        final HashedWheelTimeout timeout = new HashedWheelTimeout(this, task, deadline);
        this.timeouts.add(timeout);
        return timeout;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(HashedWheelTimer.class);
        id = new AtomicInteger();
        misuseDetector = new SharedResourceMisuseDetector(HashedWheelTimer.class);
        WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimer.class, "workerState");
    }
    
    private final class Worker implements Runnable
    {
        private final Set<Timeout> unprocessedTimeouts;
        private long tick;
        
        private Worker() {
            this.unprocessedTimeouts = new HashSet<Timeout>();
        }
        
        public void run() {
            HashedWheelTimer.this.startTime = System.nanoTime();
            if (HashedWheelTimer.this.startTime == 0L) {
                HashedWheelTimer.this.startTime = 1L;
            }
            HashedWheelTimer.this.startTimeInitialized.countDown();
            do {
                final long deadline = this.waitForNextTick();
                if (deadline > 0L) {
                    this.transferTimeoutsToBuckets();
                    final HashedWheelBucket bucket = HashedWheelTimer.this.wheel[(int)(this.tick & (long)HashedWheelTimer.this.mask)];
                    bucket.expireTimeouts(deadline);
                    ++this.tick;
                }
            } while (HashedWheelTimer.WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == 1);
            for (final HashedWheelBucket bucket2 : HashedWheelTimer.this.wheel) {
                bucket2.clearTimeouts(this.unprocessedTimeouts);
            }
            while (true) {
                final HashedWheelTimeout timeout = HashedWheelTimer.this.timeouts.poll();
                if (timeout == null) {
                    break;
                }
                this.unprocessedTimeouts.add(timeout);
            }
        }
        
        private void transferTimeoutsToBuckets() {
            for (int i = 0; i < 100000; ++i) {
                final HashedWheelTimeout timeout = HashedWheelTimer.this.timeouts.poll();
                if (timeout == null) {
                    break;
                }
                if (timeout.state() == 2 || !timeout.compareAndSetState(0, 1)) {
                    timeout.remove();
                }
                else {
                    final long calculated = timeout.deadline / HashedWheelTimer.this.tickDuration;
                    final long remainingRounds = (calculated - this.tick) / HashedWheelTimer.this.wheel.length;
                    timeout.remainingRounds = remainingRounds;
                    final long ticks = Math.max(calculated, this.tick);
                    final int stopIndex = (int)(ticks & (long)HashedWheelTimer.this.mask);
                    final HashedWheelBucket bucket = HashedWheelTimer.this.wheel[stopIndex];
                    bucket.addTimeout(timeout);
                }
            }
        }
        
        private long waitForNextTick() {
            final long deadline = HashedWheelTimer.this.tickDuration * (this.tick + 1L);
            long currentTime;
            while (true) {
                currentTime = System.nanoTime() - HashedWheelTimer.this.startTime;
                long sleepTimeMs = (deadline - currentTime + 999999L) / 1000000L;
                if (sleepTimeMs <= 0L) {
                    break;
                }
                if (DetectionUtil.isWindows()) {
                    sleepTimeMs = sleepTimeMs / 10L * 10L;
                }
                try {
                    Thread.sleep(sleepTimeMs);
                }
                catch (InterruptedException e) {
                    if (HashedWheelTimer.WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == 2) {
                        return Long.MIN_VALUE;
                    }
                    continue;
                }
            }
            if (currentTime == Long.MIN_VALUE) {
                return -9223372036854775807L;
            }
            return currentTime;
        }
        
        public Set<Timeout> unprocessedTimeouts() {
            return Collections.unmodifiableSet((Set<? extends Timeout>)this.unprocessedTimeouts);
        }
    }
    
    private static final class HashedWheelTimeout implements Timeout
    {
        private static final int ST_INIT = 0;
        private static final int ST_IN_BUCKET = 1;
        private static final int ST_CANCELLED = 2;
        private static final int ST_EXPIRED = 3;
        private static final AtomicIntegerFieldUpdater<HashedWheelTimeout> STATE_UPDATER;
        private final HashedWheelTimer timer;
        private final TimerTask task;
        private final long deadline;
        private volatile int state;
        long remainingRounds;
        HashedWheelTimeout next;
        HashedWheelTimeout prev;
        HashedWheelBucket bucket;
        
        HashedWheelTimeout(final HashedWheelTimer timer, final TimerTask task, final long deadline) {
            this.state = 0;
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
        }
        
        public Timer getTimer() {
            return this.timer;
        }
        
        public TimerTask getTask() {
            return this.task;
        }
        
        public void cancel() {
            final int state = this.state();
            if (state >= 2) {
                return;
            }
            if (state != 1 && this.compareAndSetState(0, 2)) {
                return;
            }
            if (!this.compareAndSetState(1, 2)) {
                return;
            }
            this.timer.timeouts.add(this);
        }
        
        public void remove() {
            if (this.bucket != null) {
                this.bucket.remove(this);
            }
        }
        
        public boolean compareAndSetState(final int expected, final int state) {
            return HashedWheelTimeout.STATE_UPDATER.compareAndSet(this, expected, state);
        }
        
        public int state() {
            return this.state;
        }
        
        public boolean isCancelled() {
            return this.state == 2;
        }
        
        public boolean isExpired() {
            return this.state > 1;
        }
        
        public HashedWheelTimeout value() {
            return this;
        }
        
        public void expire() {
            if (this.compareAndSetState(1, 3)) {
                try {
                    this.task.run(this);
                }
                catch (Throwable t) {
                    if (HashedWheelTimer.logger.isWarnEnabled()) {
                        HashedWheelTimer.logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
                    }
                }
                return;
            }
            assert this.state() != 0;
        }
        
        @Override
        public String toString() {
            final long currentTime = System.nanoTime();
            final long remaining = this.deadline - currentTime + this.timer.startTime;
            final StringBuilder buf = new StringBuilder(192);
            buf.append(this.getClass().getSimpleName());
            buf.append('(');
            buf.append("deadline: ");
            if (remaining > 0L) {
                buf.append(remaining);
                buf.append(" ns later");
            }
            else if (remaining < 0L) {
                buf.append(-remaining);
                buf.append(" ns ago");
            }
            else {
                buf.append("now");
            }
            if (this.isCancelled()) {
                buf.append(", cancelled");
            }
            buf.append(", task: ");
            buf.append(this.getTask());
            return buf.append(')').toString();
        }
        
        static {
            STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimeout.class, "state");
        }
    }
    
    private static final class HashedWheelBucket
    {
        private HashedWheelTimeout head;
        private HashedWheelTimeout tail;
        
        public void addTimeout(final HashedWheelTimeout timeout) {
            assert timeout.bucket == null;
            timeout.bucket = this;
            if (this.head == null) {
                this.tail = timeout;
                this.head = timeout;
            }
            else {
                this.tail.next = timeout;
                timeout.prev = this.tail;
                this.tail = timeout;
            }
        }
        
        public void expireTimeouts(final long deadline) {
            HashedWheelTimeout next;
            for (HashedWheelTimeout timeout = this.head; timeout != null; timeout = next) {
                boolean remove = false;
                if (timeout.remainingRounds <= 0L) {
                    if (timeout.deadline > deadline) {
                        throw new IllegalStateException(String.format("timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                    timeout.expire();
                    remove = true;
                }
                else if (timeout.isCancelled()) {
                    remove = true;
                }
                else {
                    final HashedWheelTimeout hashedWheelTimeout = timeout;
                    --hashedWheelTimeout.remainingRounds;
                }
                next = timeout.next;
                if (remove) {
                    this.remove(timeout);
                }
            }
        }
        
        public void remove(final HashedWheelTimeout timeout) {
            final HashedWheelTimeout next = timeout.next;
            if (timeout.prev != null) {
                timeout.prev.next = next;
            }
            if (timeout.next != null) {
                timeout.next.prev = timeout.prev;
            }
            if (timeout == this.head) {
                if (timeout == this.tail) {
                    this.tail = null;
                    this.head = null;
                }
                else {
                    this.head = next;
                }
            }
            else if (timeout == this.tail) {
                this.tail = timeout.prev;
            }
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
        }
        
        public void clearTimeouts(final Set<Timeout> set) {
            while (true) {
                final HashedWheelTimeout timeout = this.pollTimeout();
                if (timeout == null) {
                    break;
                }
                if (timeout.isExpired()) {
                    continue;
                }
                if (timeout.isCancelled()) {
                    continue;
                }
                set.add(timeout);
            }
        }
        
        private HashedWheelTimeout pollTimeout() {
            final HashedWheelTimeout head = this.head;
            if (head == null) {
                return null;
            }
            final HashedWheelTimeout next = head.next;
            if (next == null) {
                final HashedWheelTimeout hashedWheelTimeout = null;
                this.head = hashedWheelTimeout;
                this.tail = hashedWheelTimeout;
            }
            else {
                this.head = next;
                next.prev = null;
            }
            head.next = null;
            head.prev = null;
            return head;
        }
    }
}
