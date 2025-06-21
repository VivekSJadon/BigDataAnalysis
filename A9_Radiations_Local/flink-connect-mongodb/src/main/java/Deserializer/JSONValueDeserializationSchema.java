package Deserializer;

import Dto.Radiation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class JSONValueDeserializationSchema implements DeserializationSchema<Radiation> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void open(InitializationContext context) throws Exception {
        DeserializationSchema.super.open(context);
    }

    @Override
    public Radiation deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, Radiation.class);
    }

    @Override
    public boolean isEndOfStream(Radiation radiation) {
        return false;
    }

    @Override
    public TypeInformation<Radiation> getProducedType() {
        return TypeInformation.of(Radiation.class);
    }
}
