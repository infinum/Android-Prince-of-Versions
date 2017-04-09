package co.infinum.princeofversions;

public interface Presenter {

    Result check(Loader loader) throws Throwable;

    PrinceOfVersionsCall check(Loader loader, Executor executor, UpdaterCallback callback);

}
