package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateInfo;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Called in case update check has finished with resolution of no update.
 */
public interface OnNoUpdate {

    /**
     * Called in case update check has finished with resolution of no update.
     *
     * Parameters of this method are not null in case {@link co.infinum.princeofversions.PrinceOfVersions} check has finished successfully.
     * @param metadata metadata received from {@link co.infinum.princeofversions.PrinceOfVersions} update check.
     * @param updateInfo information received from {@link co.infinum.princeofversions.PrinceOfVersions} update check.
     */
    void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo);
}
