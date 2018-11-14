package co.infinum.princeofversions;

import javax.annotation.Nullable;

final class MigrationStorage implements Storage {

    private final Storage migrateFrom;
    private final Storage migrateTo;

    private boolean hasBeenMigrated;

    MigrationStorage(final Storage migrateFrom, final Storage migrateTo) {
        this.migrateFrom = migrateFrom;
        this.migrateTo = migrateTo;
    }

    @Nullable
    @Override
    public String lastNotifiedVersion(@Nullable final String defaultValue) {
        migrateIfNot();
        return migrateTo.lastNotifiedVersion(defaultValue);
    }

    @Override
    public void rememberLastNotifiedVersion(@Nullable final String version) {
        migrateIfNot();
        migrateTo.rememberLastNotifiedVersion(version);
    }

    private void migrateIfNot() {
        if (!hasBeenMigrated) {
            synchronized (this) {
                if (!hasBeenMigrated) {
                    hasBeenMigrated = true;
                    StorageMigration.migrateStorage(migrateFrom, migrateTo);
                }
            }
        }
    }
}
