package dev.yuhanwang.relayrpc.spi;

import dev.yuhanwang.relayrpc.serializer.JdkSerializer;
import dev.yuhanwang.relayrpc.serializer.JsonSerializer;
import dev.yuhanwang.relayrpc.serializer.Serializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SpiLoaderTest {
    @Test
    @DisplayName("Should load built-in serializer implementations")
    void load_withSerializerService_registersBuiltInImplementations() {
        // Arrange
        // SPI registrations are located in src/main/resources/META-INF.
        // Act
        Map<String, Class<?>> result = SpiLoader.load(Serializer.class);

        // Assert
        assertEquals(JdkSerializer.class, result.get("jdk"));

        assertEquals(JsonSerializer.class, result.get("json"));

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return the serializer registered for the given key")
    void getInstance_withJsonKey_returnsJsonSerializer() {
        //arrange
        SpiLoader.load(Serializer.class);
        //ACT
        Serializer result = SpiLoader.getInstance(Serializer.class,"json");
        //assert
        assertInstanceOf(JsonSerializer.class,result);
    }

    @Test
    @DisplayName("Should reuse the same serializer instance")
    void getInstance_calledTwice_returnsSameInstance() {
        //arrange
        SpiLoader.load(Serializer.class);
        //act
        Serializer first = SpiLoader.getInstance(Serializer.class,"json");
        Serializer second = SpiLoader.getInstance(Serializer.class,"json");
        //assert
        assertSame(first,second);
    }

    @Test
    @DisplayName("Should reject an unregistered serializer key")
    void getInstance_withUnknownKey_throwsException() {
        //arrange
        SpiLoader.load(Serializer.class);
        //act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->SpiLoader.getInstance(Serializer.class,"unknown"));
        //assert
        assertTrue(exception.getMessage().contains("unknown"));
        assertTrue(exception.getMessage().contains("json"));
        assertTrue(exception.getMessage().contains("jdk"));

    }

    @Test
    @DisplayName("Should reject a blank serializer key")
    void getInstance_withBlankKey_throwsException() {
        //arrange
        SpiLoader.load(Serializer.class);
        //act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->SpiLoader.getInstance(Serializer.class," "));
        //assert
        assertTrue(exception.getMessage().contains("must not be blank"));
    }
}
