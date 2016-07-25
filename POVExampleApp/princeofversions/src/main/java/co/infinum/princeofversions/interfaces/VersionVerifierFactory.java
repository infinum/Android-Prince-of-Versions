package co.infinum.princeofversions.interfaces;

/**
 * Factory interface for creating new instance of specific VersionVerifier.
 * <p><b>Always new instance should be provided.</b></p>
 */
public interface VersionVerifierFactory {

    /**
     * Method provides <b>new</b> instance of specific VersionVerifier.
     * @return <b>New</b> instance of specific VersionVerifier.
     */
    VersionVerifier newInstance();

}
