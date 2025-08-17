package com.hotelbooking.hotel_reservation_eu.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * RoomTypeBranchMappingDto 价格逻辑测试类
 */
@DisplayName("房型分店关联DTO价格逻辑测试")
class RoomTypeBranchMappingDtoTest {

    @Test
    @DisplayName("测试优先使用分店特定价格")
    void testPriorityBranchSpecificPrice() {
        // 准备测试数据
        RoomTypeBranchMappingDto dto = RoomTypeBranchMappingDto.builder()
                .branchSpecificPrice(new BigDecimal("150.00"))
                .roomTypeBasePrice(new BigDecimal("120.00"))
                .build();

        // 执行测试
        BigDecimal effectivePrice = dto.getEffectivePrice();

        // 验证结果：应该优先使用分店特定价格
        assertEquals(new BigDecimal("150.00"), effectivePrice);
    }

    @Test
    @DisplayName("测试分店特定价格为空时使用房型基础价格")
    void testFallbackToBasePrice() {
        // 准备测试数据：分店特定价格为空
        RoomTypeBranchMappingDto dto = RoomTypeBranchMappingDto.builder()
                .branchSpecificPrice(null)
                .roomTypeBasePrice(new BigDecimal("120.00"))
                .build();

        // 执行测试
        BigDecimal effectivePrice = dto.getEffectivePrice();

        // 验证结果：应该使用房型基础价格
        assertEquals(new BigDecimal("120.00"), effectivePrice);
    }

    @Test
    @DisplayName("测试分店特定价格为0时使用房型基础价格")
    void testZeroBranchSpecificPriceUsesBasePrice() {
        // 准备测试数据：分店特定价格为0
        RoomTypeBranchMappingDto dto = RoomTypeBranchMappingDto.builder()
                .branchSpecificPrice(BigDecimal.ZERO)
                .roomTypeBasePrice(new BigDecimal("120.00"))
                .build();

        // 执行测试
        BigDecimal effectivePrice = dto.getEffectivePrice();

        // 验证结果：应该使用房型基础价格
        assertEquals(new BigDecimal("120.00"), effectivePrice);
    }

    @Test
    @DisplayName("测试分店特定价格为负数时使用房型基础价格")
    void testNegativeBranchSpecificPriceUsesBasePrice() {
        // 准备测试数据：分店特定价格为负数
        RoomTypeBranchMappingDto dto = RoomTypeBranchMappingDto.builder()
                .branchSpecificPrice(new BigDecimal("-50.00"))
                .roomTypeBasePrice(new BigDecimal("120.00"))
                .build();

        // 执行测试
        BigDecimal effectivePrice = dto.getEffectivePrice();

        // 验证结果：应该使用房型基础价格
        assertEquals(new BigDecimal("120.00"), effectivePrice);
    }

    @Test
    @DisplayName("测试所有价格都为空时返回0")
    void testAllPricesNullReturnsZero() {
        // 准备测试数据：所有价格都为空
        RoomTypeBranchMappingDto dto = RoomTypeBranchMappingDto.builder()
                .branchSpecificPrice(null)
                .roomTypeBasePrice(null)
                .build();

        // 执行测试
        BigDecimal effectivePrice = dto.getEffectivePrice();

        // 验证结果：应该返回0
        assertEquals(BigDecimal.ZERO, effectivePrice);
    }

    @Test
    @DisplayName("测试价格精度保持")
    void testPricePrecisionPreserved() {
        // 准备测试数据：带小数的价格
        RoomTypeBranchMappingDto dto = RoomTypeBranchMappingDto.builder()
                .branchSpecificPrice(new BigDecimal("149.99"))
                .roomTypeBasePrice(new BigDecimal("120.50"))
                .build();

        // 执行测试
        BigDecimal effectivePrice = dto.getEffectivePrice();

        // 验证结果：应该保持精度
        assertEquals(new BigDecimal("149.99"), effectivePrice);
    }
} 