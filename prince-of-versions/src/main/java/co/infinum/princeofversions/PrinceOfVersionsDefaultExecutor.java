package co.infinum.princeofversions;

public class PrinceOfVersionsDefaultExecutor implements Executor {

    @Override
    public void execute(Runnable runnable) {
        Thread t = new Thread(runnable, "PrinceOfVersions Thread");
        t.setPriority(Thread.NORM_PRIORITY);
        t.setDaemon(true);
        t.start();
    }
}
