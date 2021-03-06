package com.poppo.dallab.cafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDayUpdateRequestDto {

    private Long workDayId;

    private Long menuId;

    private Double pos;
}
