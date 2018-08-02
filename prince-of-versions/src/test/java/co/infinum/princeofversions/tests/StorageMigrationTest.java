package co.infinum.princeofversions.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.StorageMigration;
import co.infinum.princeofversions.mocks.MockStorage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class StorageMigrationTest {

    @Test
    public void testNoData(){
        Storage oldStorage = new MockStorage(null);
        Storage newStorage = new MockStorage(null);
        StorageMigration.migrateStorage(oldStorage, newStorage);
        assertNull(oldStorage.lastNotifiedVersion(null));
        assertNull(newStorage.lastNotifiedVersion(null));
    }

    @Test
    public void testDataInPreviousBlankNewStorage(){
        Storage oldStorage = new MockStorage("1.0.0");
        Storage newStorage = new MockStorage(null);
        StorageMigration.migrateStorage(oldStorage, newStorage);
        assertNull(oldStorage.lastNotifiedVersion(null));
        assertEquals("1.0.0", newStorage.lastNotifiedVersion(null));
    }

    @Test
    public void testDataPresentInOldAndNewStorage(){
        Storage oldStorage = new MockStorage("1.0.0");
        Storage newStorage = new MockStorage("2.0.0");
        StorageMigration.migrateStorage(oldStorage,newStorage);
        assertEquals("1.0.0", oldStorage.lastNotifiedVersion(null));
        assertEquals("2.0.0", newStorage.lastNotifiedVersion(null));
    }

    @Test
    public void testBlankOldDataPresentInNewStorage(){
        Storage oldStorage = new MockStorage(null);
        Storage newStorage = new MockStorage("2.0.0");
        StorageMigration.migrateStorage(oldStorage,newStorage);
        assertEquals(null, oldStorage.lastNotifiedVersion(null));
        assertEquals("2.0.0", newStorage.lastNotifiedVersion(null));
    }

}