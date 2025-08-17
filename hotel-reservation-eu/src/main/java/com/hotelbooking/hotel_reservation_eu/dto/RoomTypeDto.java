package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 房型数据传输对象
 * 用于前端表单数据传输
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeDto {

    private Long id;

    @NotBlank(message = "房型名称不能为空")
    private String typeName;

    private String description;

    @NotNull(message = "房间面积不能为空")
    @Positive(message = "房间面积必须大于0")
    private Integer size;

    @NotBlank(message = "床型不能为空")
    private String bedType;

    @NotNull(message = "最大入住人数不能为空")
    @Positive(message = "最大入住人数必须大于0")
    private Integer maxGuests;

    @NotNull(message = "基础价格不能为空")
    @Positive(message = "基础价格必须大于0")
    private BigDecimal basePrice;

    private String imageUrl;

    private String amenities; // JSON字符串
} 