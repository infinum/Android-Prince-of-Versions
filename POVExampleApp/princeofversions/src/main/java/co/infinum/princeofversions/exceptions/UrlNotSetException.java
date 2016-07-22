package co.infinum.princeofversions.exceptions;

import co.infinum.princeofversions.UpdateConfigLoader;

/**
 * Created by stefano on 08/07/16.
 */
public class UrlNotSetException extends UpdateConfigLoader.ValidationException {

    public UrlNotSetException(String message) {
        super(message);
    }
}
