package dev.akorovai.backend.handler.shopping_cart;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UnauthorizedItemModificationException extends RuntimeException {
    private final String message;

}