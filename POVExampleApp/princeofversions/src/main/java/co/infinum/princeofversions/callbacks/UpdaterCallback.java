package co.infinum.princeofversions.callbacks;

/**
 * Created by stefano on 08/07/16.
 */
public interface UpdaterCallback {

    void onNewUpdate(String version, boolean isMandatory);

    void onNoUpdate();

    void onError(String error);
}
