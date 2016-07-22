package co.infinum.princeofversions.interfaces;

public interface VersionRepository {

    String getLastVersionName();

    String getLastVersionName(String defaultValue);

    void setLastVersionName(String version);

}
