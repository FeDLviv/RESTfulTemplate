package net.omisoft.rest.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.omisoft.rest.service.interkassa.InterkassaLog;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter(autoApply = true)
public class JpaConverterJson implements AttributeConverter<InterkassaLog, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(InterkassaLog meta) {
        if (meta != null) {
            try {
                return objectMapper.writeValueAsString(meta);
            } catch (JsonProcessingException ex) {
                return null;
            }
        }
        return null;
    }

    @Override
    public InterkassaLog convertToEntityAttribute(String data) {
        if (data != null) {
            try {
                return objectMapper.readValue(data, InterkassaLog.class);
            } catch (IOException ex) {
                return null;
            }
        }
        return null;
    }

}