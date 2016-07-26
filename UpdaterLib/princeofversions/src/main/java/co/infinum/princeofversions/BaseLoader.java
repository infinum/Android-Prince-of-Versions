package co.infinum.princeofversions;

public abstract class BaseLoader implements UpdateConfigLoader {

    protected volatile boolean cancelled = false;

    /**
     * Checks if loading is cancelled and throwing interrupt if it is.
     * @throws InterruptedException if loading is cancelled.
     */
    protected void ifTaskIsCancelledThrowInterrupt() throws InterruptedException {
        if (cancelled) {
            throw new InterruptedException();
        }
    }


    @Override
    public void cancel() {
        this.cancelled = true;
    }

}
