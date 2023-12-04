package com.sima.dms.domain.dto;


import lombok.Data;

@Data
public class DocumentTypeDto {

    private Long id;
    private String numbers;
    private String title;
    private String similarTitle;
    private Integer score;
}
