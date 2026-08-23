package dev.yuhanwang.relayrpc.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LocalRegistryTest {
    //mock a dummy serviceimpl class as argument
    private static class DummyServiceImpl {}
    private static class AnotherDummyServiceImpl{}

    //clear registry after
    @AfterEach
    void tearDown(){
        LocalRegistry.remove("dummyServiceImpl");
        LocalRegistry.remove("toRemoveServiceImpl");
    }

    @Test
    @DisplayName("Should register and retrieve the corresponding service implementation")
    void registerAndGet_returnsRegisteredClass(){
        LocalRegistry.register("dummyService",DummyServiceImpl.class);
        assertEquals(DummyServiceImpl.class,LocalRegistry.get("dummyService"));
    }

    @Test
    @DisplayName("Should return null when querying an unregistered service name")
    void getUnregisteredService_returnsNull(){
        assertNull(LocalRegistry.get("unregisteredService"));
    }

    @Test
    @DisplayName("Should remove service and return null on subsequent retrieval")
    void removeAndGetRemovedService_returnsNull(){
        LocalRegistry.register("toRemoveService",DummyServiceImpl.class);
        LocalRegistry.remove("toRemoveService");
        assertNull(LocalRegistry.get("toRemoveService"));
    }

    @Test
    @DisplayName("Should overwrite existing registration when registering with same service name")
    void registerExistingService_overwritesPreviousClass() {
        LocalRegistry.register("dummyService", DummyServiceImpl.class);
        LocalRegistry.register("dummyService", AnotherDummyServiceImpl.class);

        assertEquals(AnotherDummyServiceImpl.class, LocalRegistry.get("dummyService"));
    }
}
