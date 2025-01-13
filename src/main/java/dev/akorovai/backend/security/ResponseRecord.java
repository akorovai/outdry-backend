package dev.akorovai.backend.security;

import lombok.Builder;

@Builder
public record ResponseRecord (int code, Object message) {
}
