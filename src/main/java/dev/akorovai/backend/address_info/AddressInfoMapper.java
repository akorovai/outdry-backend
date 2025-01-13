package dev.akorovai.backend.address_info;

import dev.akorovai.backend.address_info.response.AddressInfoResponse;
import dev.akorovai.backend.user.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserService.class})
public interface AddressInfoMapper {

	AddressInfoMapper INSTANCE = Mappers.getMapper(AddressInfoMapper.class);


	@Mapping(source = "user.id", target = "userId")
	AddressInfoResponse toResponse(AddressInfo addressInfo);


	@Mapping(source = "userId", target = "user.id")
	AddressInfo toEntity(AddressInfoResponse addressInfoResponse);


	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromDTO(AddressInfoResponse addressInfoResponse, @MappingTarget AddressInfo addressInfo);
}