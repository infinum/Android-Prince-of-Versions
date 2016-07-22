package co.infinum.princeofversions.mvp.view;

/**
 * Created by stefano on 08/07/16.
 */
public interface POVView {

    void notifyMandatoryUpdate(String version);

    void notifyOptionalUpdate(String version);

    void notifyNoUpdate();

    void notifyError(String error);
}
