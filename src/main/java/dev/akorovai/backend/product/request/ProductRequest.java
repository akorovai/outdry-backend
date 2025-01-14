package dev.akorovai.backend.product.request;

import dev.akorovai.backend.color.response.ColorResponse;
import dev.akorovai.backend.product.Gender;
import dev.akorovai.backend.product.Size;
import dev.akorovai.backend.type.response.TypeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductRequest {

	private Long id;
	private String name;
	private String description;
	private List<String> links;
	private Integer amount;
	private Integer discount;
	private Size size;
	private TypeResponse type;
	private ColorResponse color;
	private Double price;
	private Gender gender;
}
