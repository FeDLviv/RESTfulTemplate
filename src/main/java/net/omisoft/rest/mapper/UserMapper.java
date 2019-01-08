package net.omisoft.rest.mapper;

import net.omisoft.rest.dto.user.UserCreateDto;
import net.omisoft.rest.mapper.base.BaseMapper;
import net.omisoft.rest.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = BaseMapper.class)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "role", expression = "java( net.omisoft.rest.model.base.UserRole.ROLE_CLIENT )"),
            @Mapping(target = "password", expression = "java( new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(data.getPassword()) )"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "created", ignore = true),
            @Mapping(target = "updated", ignore = true),
    })
    UserEntity convert(UserCreateDto data);

}