package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 分店管理数据传输对象（移除分店代码字段）
 * 用于前端表单数据传输和验证
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelBranchDto {

    private Long id;

    /**
     * 分店名称
     */
    @NotBlank(message = "分店名称不能为空")
    @Size(min = 2, max = 100, message = "分店名称长度必须在2-100个字符之间")
    private String branchName;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    @Size(min = 2, max = 50, message = "城市名称长度必须在2-50个字符之间")
    private String city;

    /**
     * 国家
     */
    @NotBlank(message = "国家不能为空")
    @Size(min = 2, max = 50, message = "国家名称长度必须在2-50个字符之间")
    private String country;

    /**
     * 状态
     */
    private Integer status;
} 