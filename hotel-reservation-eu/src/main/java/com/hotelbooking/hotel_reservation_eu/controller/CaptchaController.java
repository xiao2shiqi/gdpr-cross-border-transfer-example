package com.hotelbooking.hotel_reservation_eu.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 验证码控制器
 * 处理验证码图片生成和验证
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CaptchaController {

    private final DefaultKaptcha defaultKaptcha;

    /**
     * 生成验证码图片
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 生成验证码文本
        String captchaText = defaultKaptcha.createText();
        
        // 将验证码存储到session中
        request.getSession().setAttribute("captcha", captchaText);
        log.info("Generated captcha: {}", captchaText);

        // 生成验证码图片
        BufferedImage bufferedImage = defaultKaptcha.createImage(captchaText);
        
        // 输出图片到响应流
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }
} 