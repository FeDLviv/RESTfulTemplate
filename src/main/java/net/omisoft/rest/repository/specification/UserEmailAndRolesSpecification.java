package net.omisoft.rest.repository.specification;

import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import net.omisoft.rest.model.UserEntity;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "email", spec = Like.class),
        @Spec(path = "role", spec = In.class)
})
public interface UserEmailAndRolesSpecification extends Specification<UserEntity> {

}
