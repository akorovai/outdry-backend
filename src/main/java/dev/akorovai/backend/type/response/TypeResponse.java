
package dev.akorovai.backend.type.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TypeResponse {
    private Long id;
    private String name;
}