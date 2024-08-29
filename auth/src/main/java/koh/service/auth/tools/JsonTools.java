package koh.service.auth.tools;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;

import java.io.BufferedReader;
import java.io.IOException;

public class JsonTools {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        OBJECT_MAPPER.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        OBJECT_MAPPER.setConstructorDetector(ConstructorDetector.USE_DELEGATING);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static <T> T fromJson(String json, Class<T> cls)
            throws IOException {
        return OBJECT_MAPPER.readValue(json, cls);
    }

    public static <T> T fromJson(byte[] json, Class<T> cls)
            throws IOException {
        return OBJECT_MAPPER.readValue(json, cls);
    }

    public static <T> T fromJson(BufferedReader reader, Class<T> cls)
            throws IOException {
        return OBJECT_MAPPER.readValue(reader, cls);
    }

    public static String toJson(Object object)
            throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static byte[] toJsonBytes(Object object)
            throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }


    public static <T> T fromJson(Object object)
            throws IOException {
        return OBJECT_MAPPER.convertValue(object, new TypeReference<T>() {
        });
    }
}
