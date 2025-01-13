package dev.akorovai.backend.address_info;

import dev.akorovai.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AddressInfoRepository extends JpaRepository<AddressInfo, Integer> {
	List<AddressInfo> findByUser( User authenticatedUser );

	Optional<AddressInfo> findByIdAndUser( Long addressId, User authenticatedUser );
}
