package co.infinum.princeofversions.mvp.view;

import java.util.Map;

/**
 * Created by stefano on 08/07/16.
 */
public interface PovView {

    void notifyMandatoryUpdate(String version, Map<String, String> metadata);

    void notifyOptionalUpdate(String version, Map<String, String> metadata);

    void notifyNoUpdate(Map<String, String> metadata);

    void notifyError(Throwable throwable);
}
