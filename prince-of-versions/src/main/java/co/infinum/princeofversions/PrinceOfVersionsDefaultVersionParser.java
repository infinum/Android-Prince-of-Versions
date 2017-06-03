package co.infinum.princeofversions;

/**
 * Implementation of {@link VersionParser} which parses version string by SemanticVersioning standard.
 *
 * @see <a href="http://semver.org/">http://semver.org/</a>
 */
public class PrinceOfVersionsDefaultVersionParser implements VersionParser {

    @Override
    public Version parse(String value) {
        return new Version(com.github.zafarkhaja.semver.Version.valueOf(value));
    }
}
