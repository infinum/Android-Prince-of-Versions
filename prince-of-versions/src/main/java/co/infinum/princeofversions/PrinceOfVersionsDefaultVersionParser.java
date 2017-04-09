package co.infinum.princeofversions;

public class PrinceOfVersionsDefaultVersionParser implements VersionParser {

    @Override
    public Version parse(String value) {
        return new Version(com.github.zafarkhaja.semver.Version.valueOf(value));
    }
}
