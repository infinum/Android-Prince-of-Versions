package co.infinum.princeofversions;

public class StorageMigration {

    public static void migrateStorage(Storage oldStorage, Storage newStorage) {
        if (newStorage.lastNotifiedVersion(null) == null) {
            String oldValue = oldStorage.lastNotifiedVersion(null);
            if (oldValue != null) {
                newStorage.rememberLastNotifiedVersion(oldValue);
                oldStorage.rememberLastNotifiedVersion(null);
            }
        }
    }

}
