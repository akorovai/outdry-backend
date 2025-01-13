package dev.akorovai.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByNicknameOrEmail(String username, String email);
	@Query("select u from User u where u.nickname = :userData or u.email = :userData")
	Optional<User> loadByNicknameOrEmail(@Param("userData") String userData);

	Optional<User> findByEmail( String name );
}
