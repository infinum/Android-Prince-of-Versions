package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

// Interface for our test object
internal interface ValueProvider {
    fun getValue(): String
}

class LazyTest {

    @Test
    fun testLazyInitializationLifecycle() {
        var initializationCounter = 0
        val creator = Callable<ValueProvider> {
            initializationCounter++
            object : ValueProvider {
                override fun getValue(): String = "my-value"
            }
        }

        val lazyValue = Lazy.create(ValueProvider::class.java, creator)

        assertThat(initializationCounter).isEqualTo(0)

        assertThat(lazyValue.getValue()).isEqualTo("my-value")
        assertThat(initializationCounter).isEqualTo(1)

        assertThat(lazyValue.getValue()).isEqualTo("my-value")
        assertThat(initializationCounter).isEqualTo(1)
    }

    @Test
    fun testLazyInitializationIsThreadSafe() {
        val initializationCounter = AtomicInteger(0)
        val creator = Callable<ValueProvider> {
            Thread.sleep(100)
            initializationCounter.incrementAndGet()
            object : ValueProvider {
                override fun getValue(): String = "my-value"
            }
        }

        val lazyValue = Lazy.create(ValueProvider::class.java, creator)
        val threadCount = 10
        val executor = Executors.newFixedThreadPool(threadCount)

        for (i in 1..threadCount) {
            executor.submit {
                lazyValue.getValue()
            }
        }

        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)

        assertThat(initializationCounter.get()).isEqualTo(1)
    }
}
