package dev.akorovai.backend.security;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ResponseRecord (@Getter int code, @Getter Object message) {
}
