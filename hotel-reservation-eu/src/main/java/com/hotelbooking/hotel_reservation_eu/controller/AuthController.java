package com.hotelbooking.hotel_reservation_eu.controller;

import com.hotelbooking.hotel_reservation_eu.dto.ForgotPasswordRequestDto;
import com.hotelbooking.hotel_reservation_eu.dto.RegisterRequestDto;
import com.hotelbooking.hotel_reservation_eu.dto.ResetPasswordRequestDto;
import com.hotelbooking.hotel_reservation_eu.service.UserService;
import com.hotelbooking.hotel_reservation_eu.service.TestUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * 认证控制器
 * 处理用户登录、注册、忘记密码等认证相关请求
 */
@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TestUserService testUserService;

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "登录失败，请检查用户名、密码和验证码");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "您已成功退出登录");
        }
        return "auth/login";
    }

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequestDto());
        return "auth/register";
    }

    /**
     * 处理用户注册
     */
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerRequest") RegisterRequestDto registerRequest,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        // 验证表单
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // 验证密码匹配
        if (!registerRequest.isPasswordMatching()) {
            model.addAttribute("errorMessage", "密码和确认密码不匹配");
            return "auth/register";
        }

        // 验证GDPR必需同意
        if (!registerRequest.isGdprProcessingAgreed()) {
            model.addAttribute("errorMessage", "您必须同意数据处理条款才能注册");
            return "auth/register";
        }

        try {
            userService.register(registerRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "注册成功！请使用您的用户名和密码登录。");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            log.error("Registration failed for user: {}", registerRequest.getUsername(), e);
            model.addAttribute("errorMessage", "注册失败，请稍后重试");
            return "auth/register";
        }
    }

    /**
     * 显示忘记密码页面
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequestDto());
        return "auth/forgot-password";
    }

    /**
     * 处理忘记密码请求
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequestDto forgotPasswordRequest,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }

        try {
            userService.initiatePasswordReset(forgotPasswordRequest.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", 
                "如果该邮箱地址存在于我们的系统中，您将收到一封包含重置密码链接的邮件。");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Password reset failed for email: {}", forgotPasswordRequest.getEmail(), e);
            model.addAttribute("errorMessage", "发送重置邮件失败，请稍后重试");
            return "auth/forgot-password";
        }
    }

    /**
     * 显示重置密码页面
     */
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model,
                                       RedirectAttributes redirectAttributes) {
        if (!userService.isValidResetToken(token)) {
            redirectAttributes.addFlashAttribute("errorMessage", "重置链接无效或已过期");
            return "redirect:/auth/login";
        }

        ResetPasswordRequestDto resetPasswordRequest = new ResetPasswordRequestDto();
        resetPasswordRequest.setToken(token);
        model.addAttribute("resetPasswordRequest", resetPasswordRequest);
        return "auth/reset-password";
    }

    /**
     * 处理重置密码请求
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequestDto resetPasswordRequest,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }

        // 验证密码匹配
        if (!resetPasswordRequest.isPasswordMatching()) {
            model.addAttribute("errorMessage", "新密码和确认密码不匹配");
            return "auth/reset-password";
        }

        try {
            userService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage", "密码重置成功！请使用新密码登录。");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Password reset failed for token: {}", resetPasswordRequest.getToken(), e);
            model.addAttribute("errorMessage", "密码重置失败，请稍后重试");
            return "auth/reset-password";
        }
    }


    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsernameAvailability(@RequestParam String username) {
        return !userService.isUsernameExists(username);
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailAvailability(@RequestParam String email) {
        return !userService.isEmailExists(email);
    }

    /**
     * 获取随机测试用户信息
     */
    @GetMapping("/get-random-test-user")
    @ResponseBody
    public RegisterRequestDto getRandomTestUser() {
        RegisterRequestDto testUser = testUserService.getRandomTestUser();
        if (testUser == null) {
            throw new RuntimeException("没有可用的测试用户");
        }
        return testUser;
    }
} 