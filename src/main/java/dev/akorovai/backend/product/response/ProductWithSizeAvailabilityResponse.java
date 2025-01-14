package dev.akorovai.backend.product.response;

import dev.akorovai.backend.color.response.ColorResponse;
import dev.akorovai.backend.product.Gender;
import dev.akorovai.backend.product.Size;
import dev.akorovai.backend.type.response.TypeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ProductWithSizeAvailabilityResponse {
	private Long id;
	private String name;
	private String description;
	private Double price;
	private ColorResponse color;
	private TypeResponse type;
	private List<String> links;
	private Gender gender;
	private Integer amount;
	private Integer discount;
	private Size size;
	private Map<ColorResponse, Set<Size>> sizeAvailabilityByColor;
}