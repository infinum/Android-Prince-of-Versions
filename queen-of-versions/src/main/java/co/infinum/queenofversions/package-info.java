/**
 * Queen of Versions checks for updates using configuration from remote or local resource and Google In-App updates API.
 * <pre>
 *     PrinceOfVersions updater = new PrinceOfVersions.Builder().build(this);
 *     QueenOfVersions queen = new QueenOfVersions.Builder().build(this);
 *     updater.checkForUpdates("urlToConfigurationFile", queen.getPrinceOfVersionsCallback());
 * </pre>
 */
@NotNullByDefault
package co.infinum.queenofversions;