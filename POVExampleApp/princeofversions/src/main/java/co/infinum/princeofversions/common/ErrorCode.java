package co.infinum.princeofversions.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ErrorCode.WRONG_VERSION, ErrorCode.LOAD_ERROR, ErrorCode.UNKNOWN_ERROR})
public @interface ErrorCode {

    public static final int WRONG_VERSION = 0;
    public static final int LOAD_ERROR = 1;
    public static final int UNKNOWN_ERROR = 2;

}
