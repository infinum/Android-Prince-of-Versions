package co.infinum.princeofversions.helpers.parsers;

import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.exceptions.ParseException;

/**
 * This interface represents parser for parsing loaded update configuration and creating VersionContext holder.
 * If there is some error while parsing resource ParseException will be thrown.
 */
public interface VersionConfigParser {

    /**
     * Method parse update configuration provided as string.
     *
     * @param content Update configuration representation as string.
     * @return Holder of parsed and loaded version data.
     * @throws ParseException if some error occurred while parsing.
     */
    VersionContext parse(String content) throws ParseException;

}
