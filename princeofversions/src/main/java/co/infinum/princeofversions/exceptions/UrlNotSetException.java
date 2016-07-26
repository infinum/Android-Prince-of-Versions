package co.infinum.princeofversions.exceptions;

import co.infinum.princeofversions.LoaderValidationException;

/**
 * Created by stefano on 08/07/16.
 */
public class UrlNotSetException extends LoaderValidationException {

    public UrlNotSetException(String message) {
        super(message);
    }
}
