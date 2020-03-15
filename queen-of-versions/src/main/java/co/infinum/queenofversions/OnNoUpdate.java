package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateInfo;
import java.util.Map;
import javax.annotation.Nullable;

public interface OnNoUpdate {

    void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo);
}
