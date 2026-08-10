package dev.yuhanwang.relayrpc.registry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LocalRegistryTest {
    //mock a dummy serviceimpl class as argument
    private static class DummyServiceImpl {}

    @Test
    void registerAndGet_returnsRegisteredClass(){
        LocalRegistry.register("dummyService",DummyServiceImpl.class);
        assertEquals(DummyServiceImpl.class,LocalRegistry.get("dummyService"));
    }

    @Test
    void getUnregisteredService_returnsNull(){
        assertNull(LocalRegistry.get("notRegisterdService"));
    }

    @Test
    void removeAndGetRemovedService_returnsNull(){
        LocalRegistry.register("toRemoveService",DummyServiceImpl.class);
        LocalRegistry.remove("toRemoveService");
        assertNull(LocalRegistry.get("toRemoveService"));
    }
}
