package dev.akorovai.backend.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductCartResponse {

	private Long id;
	private String name;
	private String color;
	private Double price;
	private Integer discount;
	private Integer amount;
	private String imageUrl;
	private Integer warehouseAmount;
}
