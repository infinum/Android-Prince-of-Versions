package co.infinum.princeofversions;

public interface Interactor {

    CheckResult check(Loader loader, ApplicationConfiguration appConfig) throws Throwable;

}
