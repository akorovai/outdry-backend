package dev.akorovai.backend.role;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Builder
@Table(name = "role", indexes = {@Index(name = "idx_role_name", columnList = "name", unique = true)})
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 16, unique = true)
	private String name;


}