package co.infinum.princeofversions;

class MockApplicationVersionProvider implements PrinceOfVersionsDefaultRequirementsChecker.ApplicationVersionProvider {

    private final int version;

    MockApplicationVersionProvider(final int version) {
        this.version = version;
    }

    @Override
    public int provide() {
        return version;
    }
}
