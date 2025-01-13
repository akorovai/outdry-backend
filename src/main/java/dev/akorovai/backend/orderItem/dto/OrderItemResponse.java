package dev.akorovai.backend.orderItem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
	private Long id;
	private Integer quantity;
	private Double price;
	private Long productId;
	private String productName;
	private String size;
	private String color;
	private String imageLink;
}