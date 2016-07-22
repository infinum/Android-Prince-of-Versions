package co.infinum.princeofversions.enums;

/**
 * Created by stefano on 19/07/16.
 */
public enum VersionStatus {

    INCORRECT_MIN_VERSION(0),
    NEW_UPDATES_AVAILABLE(1);

    private int value;

    VersionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
