package co.infinum.princeofversions;

import java.util.concurrent.Executor;

public interface PrinceOfVersionsCall extends PrinceOfVersionsCancelable {

    Result execute() throws Throwable;

    void enqueue(UpdaterCallback callback);

    void enqueue(Executor executor, UpdaterCallback callback);
}
