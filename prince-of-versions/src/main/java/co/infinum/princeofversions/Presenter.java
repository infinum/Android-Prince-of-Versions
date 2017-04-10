package co.infinum.princeofversions;

public interface Presenter {

    Result check(Loader loader, ApplicationConfiguration appConfig) throws Throwable;

    PrinceOfVersionsCall check(Loader loader, Executor executor, UpdaterCallback callback, ApplicationConfiguration appConfig);

}
