package com.example.tama_gargoyles.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PongResultDTO {
    private Long gargoyleId;
    private int survivalTime;
}