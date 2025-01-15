package dev.akorovai.backend.color.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ColorResponse {
    private Long id;
    private String name;
    private String code;
    @Override
    public String toString() {
        return name;
    }
}