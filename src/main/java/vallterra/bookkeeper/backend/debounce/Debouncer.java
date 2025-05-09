package vallterra.bookkeeper.backend.debounce;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * General-purpose debouncer.
 * First call runs immediately.  While {@code delay} is passing, every new
 * call replaces the pending one.  When the delay expires the last call is
 * executed.
 * <p>
 * Thread-safe – can be shared by several components.
 * </p>
 */
public final class Debouncer {

    private static final ScheduledExecutorService EXEC =
            Executors.newScheduledThreadPool(1, Thread.ofVirtual().name("debounce-%d").factory());

    private final Duration delay;
    private final AtomicReference<ScheduledFuture<?>> pending = new AtomicReference<>();

    public Debouncer(@NotNull Duration delay) {
        this.delay = Objects.requireNonNull(delay);
    }

    public void call(@NotNull Runnable task) {
        Objects.requireNonNull(task);

        if (pending.get() == null) {
            task.run();
        }

        ScheduledFuture<?> next = EXEC.schedule(() -> {
            pending.set(null);
            task.run();
        }, delay.toMillis(), TimeUnit.MILLISECONDS);

        ScheduledFuture<?> prev = pending.getAndSet(next);
        if (prev != null) {
            prev.cancel(false);
        }
    }

    public void cancel() {
        ScheduledFuture<?> f = pending.getAndSet(null);
        if (f != null) f.cancel(false);
    }

}
