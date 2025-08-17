package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 忘记密码请求DTO
 * 用于接收用户忘记密码表单数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDto {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
} 