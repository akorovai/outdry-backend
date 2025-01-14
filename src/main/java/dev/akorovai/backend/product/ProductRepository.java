package dev.akorovai.backend.product;

import dev.akorovai.backend.color.Color;
import dev.akorovai.backend.type.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByDiscountGreaterThan( int value );

	List<Product> findByTypeAndGenderAndColor( Type type, Gender gender, Color color);


	@Query("SELECT p FROM Product p WHERE " +
			       "(p.type = :type AND p.gender = :gender) OR " +
			       "(p.type = :type AND p.color = :color) OR " +
			       "(p.gender = :gender AND p.color = :color)")
	List<Product> findByAnyTwoAttributes(
			@Param("type") Type type,
			@Param("gender") Gender gender,
			@Param("color") Color color
	);


	@Query("SELECT p FROM Product p WHERE " +
			       "p.type = :type OR p.gender = :gender OR p.color = :color")
	List<Product> findByAnyOneAttribute(
			@Param("type") Type type,
			@Param("gender") Gender gender,
			@Param("color") Color color
	);

	List<Product> findByGender( Gender gender );

	List<Product> findByType( Type type );


	List<Product> findByCreatedDateAfter( LocalDateTime cutoffDate );

	List<Product> findByName( String name );

}
