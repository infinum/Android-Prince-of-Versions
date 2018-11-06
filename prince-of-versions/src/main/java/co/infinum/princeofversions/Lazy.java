package co.infinum.princeofversions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

final class Lazy<T> {

    private Callable<T> creator;

    @Nullable
    private volatile T instance;
    private AtomicBoolean hasBeenInitialized = new AtomicBoolean(false);

    Lazy(final Callable<T> creator) {
        this.creator = creator;
    }

    @SuppressWarnings("unchecked")
    static <T> T create(Class<T> clazz, Callable<T> creator) {
        final Lazy<T> lazy = new Lazy<>(creator);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                return method.invoke(lazy.get(), args);
            }

            @Override
            public String toString() {
                return lazy.toString();
            }
        });
    }

    @Nullable
    private T get() {
        if (!hasBeenInitialized.get()) {
            synchronized (this) {
                if (!hasBeenInitialized.getAndSet(true)) {
                    try {
                        instance = creator.call();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    } finally {
                        clearCreator();
                    }
                }
            }
        }
        return instance;
    }

    private boolean isInitialized() {
        return hasBeenInitialized.get();
    }

    @SuppressFBWarnings(
        value = "NP_STORE_INTO_NONNULL_FIELD",
        justification = "We don't need creator instance anymore after instantiation. We clear it so it can be garbage collected."
    )
    private void clearCreator() {
        creator = null;
    }

    @Override
    public String toString() {
        return isInitialized() ? String.valueOf(get()) : "Lazy value not initialized yet.";
    }
}
