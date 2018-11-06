package co.infinum.princeofversions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

final class Lazy<T> {

    private Callable<T> creator;

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

    private T get() {
        if (!hasBeenInitialized.get()) {
            synchronized (this) {
                if (!hasBeenInitialized.getAndSet(true)) {
                    try {
                        instance = creator.call();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    } finally {
                        creator = null;
                    }
                }
            }
        }
        return instance;
    }

    private boolean isInitialized() {
        return hasBeenInitialized.get();
    }

    @Override
    public String toString() {
        return isInitialized() ? get().toString() : "Lazy value not initialized yet.";
    }
}
