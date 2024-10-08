package com.maxzamota.spring_sandbox.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BrandDto {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private Timestamp foundationDate;
    @NonNull
    private String countryOfOrigin;
    @NonNull
    private String description;
    @NonNull
    private String history;
}
