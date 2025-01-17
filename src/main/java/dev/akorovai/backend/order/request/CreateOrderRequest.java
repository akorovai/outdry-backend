package dev.akorovai.backend.order.request;

import dev.akorovai.backend.address_info.response.AddressInfoResponse;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Address info is required")
    private AddressInfoResponse addressInfo;

    @NotNull(message = "Shipping price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Shipping price must be greater than 0")
    private Double shippingPrice;

    @NotBlank(message = "Shipping time is required")
    private String shippingTime;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}