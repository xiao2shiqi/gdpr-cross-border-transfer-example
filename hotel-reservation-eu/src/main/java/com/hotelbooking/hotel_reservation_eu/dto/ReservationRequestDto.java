package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预订请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @NotNull(message = "房型ID不能为空")
    private Long roomTypeId;
    
    @NotNull(message = "分店ID不能为空")
    private Long branchId;
    
    @NotNull(message = "入住日期不能为空")
    @Future(message = "入住日期必须是未来日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkinDate;
    
    @NotNull(message = "退房日期不能为空")
    @Future(message = "退房日期必须是未来日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkoutDate;
    
    @NotNull(message = "入住人数不能为空")
    @Min(value = 1, message = "入住人数至少为1人")
    @Max(value = 10, message = "入住人数不能超过10人")
    private Integer guests;
    
    @NotNull(message = "房间数量不能为空")
    @Min(value = 1, message = "房间数量至少为1间")
    @Max(value = 5, message = "房间数量不能超过5间")
    private Integer rooms;
    
    @NotNull(message = "每晚价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal pricePerNight;
    
    @Size(max = 500, message = "特殊要求不能超过500字符")
    private String specialRequests;
    
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名不能超过50字符")
    private String contactName;
    
    @NotBlank(message = "联系人电话不能为空")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "电话号码格式不正确")
    private String contactPhone;
    
    @NotBlank(message = "联系人邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String contactEmail;
    
    /**
     * 验证入住和退房日期
     */
    public boolean isValidDateRange() {
        return checkinDate != null && checkoutDate != null && 
               checkoutDate.isAfter(checkinDate);
    }
    
    /**
     * 计算总价格
     */
    public BigDecimal calculateTotalPrice() {
        if (pricePerNight == null || rooms == null) {
            return BigDecimal.ZERO;
        }
        
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        return pricePerNight.multiply(BigDecimal.valueOf(rooms)).multiply(BigDecimal.valueOf(nights));
    }
}
