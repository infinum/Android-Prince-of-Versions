package co.infinum.princeofversions;

class RequirementsNotSatisfiedException extends IllegalStateException {

    RequirementsNotSatisfiedException(String msg) {
        super(msg);
    }
}
