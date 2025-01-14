package dev.akorovai.backend.type;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<Type, Long> {
	Optional<Type> findByName( String source );
}
