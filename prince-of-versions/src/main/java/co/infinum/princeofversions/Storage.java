package co.infinum.princeofversions;

public interface Storage {

    String lastNotifiedVersion(String defaultValue);

    void rememberLastNotifiedVersion(String version);

}
