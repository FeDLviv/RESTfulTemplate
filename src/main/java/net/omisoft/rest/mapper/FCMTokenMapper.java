package net.omisoft.rest.mapper;

import net.omisoft.rest.dto.fcm.FCMTokenCreateDto;
import net.omisoft.rest.mapper.base.BaseMapper;
import net.omisoft.rest.model.FCMTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(config = BaseMapper.class)
public interface FCMTokenMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "created", ignore = true),
            @Mapping(target = "updated", ignore = true)
    })
    FCMTokenEntity convert(FCMTokenCreateDto token);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "created", ignore = true),
            @Mapping(target = "updated", ignore = true)
    })
    void update(FCMTokenCreateDto dto, @MappingTarget FCMTokenEntity token);

}