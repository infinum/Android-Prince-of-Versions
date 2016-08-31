package co.infinum.princeofversions.util;

import java.io.InputStream;

/**
 * Utility methods for accessing resources bundled with test APK. Standard Android Resources don't seem to work for test APK
 * (unable to fetch R.java).
 * <p>
 * Resources should be placed under /resources/mockdata folder in androidTest flavour. Use {@link #readFromFile(String)} to read a text
 * file to String giving only a name of the file located in /resources folder.
 */
public class ResourceUtils {

    private ResourceUtils() {

    }

    /**
     * Converts InputStream to String.
     */
    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Reads a resource file to <code>String</code>.
     */
    public static String readFromFile(String filename) {
        InputStream is = ResourceUtils.class.getClassLoader().getResourceAsStream("mockdata/" + filename);
        return convertStreamToString(is);

    }

}