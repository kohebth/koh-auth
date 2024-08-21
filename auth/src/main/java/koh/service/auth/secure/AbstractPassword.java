package koh.service.auth.secure;

abstract class AbstractPassword {
    private final String securedDigest;

    protected AbstractPassword(String raw) {
        securedDigest = secure(raw);
    }

    protected abstract String secure(String rawPassword);

    public final String getSecuredDigest() {
        return securedDigest;
    }

    /**
     * Prevent from printing out the {@link AbstractPassword#securedDigest}
     *
     * @return Hidden string "*****"
     */
    @Override
    public final String toString() {
        return "*****";
    }
}
