package dev.yuhanwang.relayrpc.serializer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SerializerFactoryTest {

    @Test
    void getInstance_withJdkKey_returnsJdkSerializer() {
        //ACT
        Serializer result = SerializerFactory.getInstance(SerializerKeys.JDK);

        //ASSERT
        assertInstanceOf(JdkSerializer.class,result);
    }

    @Test
    void getInstance_withJsonKey_returnsJsonSerializer() {
        //ACT
        Serializer result = SerializerFactory.getInstance(SerializerKeys.JSON);

        //ASSERT
        assertInstanceOf(JsonSerializer.class,result);
    }

    @Test
    void getInstance_withSameKey_returnsSameInstance() {
        Serializer first = SerializerFactory.getInstance(SerializerKeys.JSON);
        Serializer second = SerializerFactory.getInstance(SerializerKeys.JSON);

        //assert
        assertSame(first,second);
    }

    @Test
    void getInstance_withUnsupportedKey_throwsException() {
        String unsupportedKey = "unknown";
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->SerializerFactory.getInstance(unsupportedKey));

        assertTrue(exception.getMessage().contains(unsupportedKey));
        assertTrue(exception.getMessage().contains(Serializer.class.getName()));
    }
}
