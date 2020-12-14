package com.kb.leggid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubLeggidDto {
    private Long id;
    private String name;
    private String description;
    private Integer numberOfPosts;
}