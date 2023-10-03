package com.neoastra.webscraper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmazonResponseDTO {
    
    String productName;
    String currency;
    String productPrice;
}
