package br.com.productservice.domain.products;

import br.com.productservice.core.validations.CreateValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.math.BigDecimal;

public record ProductDTO(
        @Null(groups = CreateValidation.class)
        Long id,
        @NotBlank(groups = CreateValidation.class)
        String name,
        @NotBlank(groups = CreateValidation.class)
        String description,
        @NotNull(groups = CreateValidation.class)
        BigDecimal price
) { }
