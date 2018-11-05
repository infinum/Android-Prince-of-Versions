package co.infinum.princeofversions;

/**
 * Provides information about specific version.
 */
public class Version {

    protected com.github.zafarkhaja.semver.Version version;

    public Version(com.github.zafarkhaja.semver.Version version) {
        this.version = version;
    }

    public boolean isGreaterThan(Version version) {
        return this.version.greaterThan(version.version);
    }

    public boolean isGreaterThanOrEqualsTo(Version version) {
        return this.version.greaterThanOrEqualTo(version.version);
    }

    public boolean isEqualsTo(Version version) {
        return this.version.equals(version.version);
    }

    public boolean isLessThan(Version version) {
        return this.version.lessThan(version.version);
    }

    public boolean isLessThanOrEqualsTo(Version version) {
        return this.version.lessThanOrEqualTo(version.version);
    }

    public String value() {
        return version.toString();
    }

}
