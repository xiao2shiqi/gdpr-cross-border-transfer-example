package com.hotelbooking.hotel_reservation_eu.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置类
 * 配置Kaptcha验证码生成器的各项参数
 */
@Configuration
public class CaptchaConfig {

    /**
     * 配置验证码生成器
     */
    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // === 边框设置 ===
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "black");
        properties.setProperty("kaptcha.border.thickness", "1");
        
        // === 图片尺寸（增加分辨率提高清晰度） ===
        properties.setProperty("kaptcha.image.width", "200");   // 增加宽度
        properties.setProperty("kaptcha.image.height", "80");   // 增加高度
        
        // === 字体设置（优化清晰度） ===
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.textproducer.font.size", "45");           // 增大字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial");       // 使用清晰字体
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.char.space", "6");           // 增加字符间距
        
        // === 背景设置（纯白背景更清晰） ===
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");
        
        // === 会话配置 ===
        properties.setProperty("kaptcha.session.key", "captcha");
        
        // === 移除所有干扰效果（最大化清晰度） ===
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");           // 无噪点
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
} 