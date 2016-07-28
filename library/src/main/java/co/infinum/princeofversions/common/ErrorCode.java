package co.infinum.princeofversions.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Integer error codes for representing error occurred while checking for updates.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ErrorCode.WRONG_VERSION, ErrorCode.LOAD_ERROR, ErrorCode.UNKNOWN_ERROR})
public @interface ErrorCode {

    /**
     * Describes error appeared while parsing version string. Parse error while be thrown if version does not follow
     * <a href="http://semver.org/">semver</a> specification.
     *
     * @see <a href="http://semver.org/">http://semver.org</a>
     */
    int WRONG_VERSION = 0;

    /**
     * Describes error appeared while loading update configuration resource.
     */
    int LOAD_ERROR = 1;

    /**
     * Describes generic error not happened while parsing versions or loading resource.
     */
    int UNKNOWN_ERROR = 2;

}
