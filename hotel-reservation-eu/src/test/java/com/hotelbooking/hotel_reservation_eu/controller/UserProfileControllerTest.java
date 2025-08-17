package com.hotelbooking.hotel_reservation_eu.controller;

import com.hotelbooking.hotel_reservation_eu.model.User;
import com.hotelbooking.hotel_reservation_eu.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserProfileController测试类
 * 测试用户个人资料相关功能，包括GDPR数据导出
 */
@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserProfileController userProfileController;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .address("123 Test Street")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .nationality("中国")
                .role("USER")
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .gdprProcessingConsent(true)
                .gdprMarketingConsent(false)
                .gdprAnalyticsConsent(true)
                .gdprConsentDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 设置SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testExportUserData_Success() {
        // 准备测试数据
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // 执行测试
        ResponseEntity<String> response = userProfileController.exportUserData(authentication);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证响应头
        assertTrue(response.getHeaders().containsKey("Content-Disposition"));
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("attachment"));
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("user_data_testuser"));
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains(".json"));
        
        // 验证Content-Type
        assertEquals("application/json", response.getHeaders().getFirst("Content-Type"));
        
        // 验证JSON内容包含必要字段
        String jsonContent = response.getBody();
        assertTrue(jsonContent.contains("基本信息"));
        assertTrue(jsonContent.contains("GDPR同意设置"));
        assertTrue(jsonContent.contains("账户安全信息"));
        assertTrue(jsonContent.contains("导出元数据"));
        assertTrue(jsonContent.contains("testuser"));
        assertTrue(jsonContent.contains("test@example.com"));
        
        // 验证服务方法被调用
        verify(userService, times(1)).findByUsername("testuser");
    }

    @Test
    void testExportUserData_NoAuthentication() {
        // 执行测试
        ResponseEntity<String> response = userProfileController.exportUserData(null);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("未授权访问", response.getBody());
        
        // 验证服务方法没有被调用
        verify(userService, never()).findByUsername(any());
    }

    @Test
    void testExportUserData_UserNotFound() {
        // 准备测试数据
        when(authentication.getName()).thenReturn("nonexistentuser");
        when(userService.findByUsername("nonexistentuser")).thenReturn(null);

        // 执行测试
        ResponseEntity<String> response = userProfileController.exportUserData(authentication);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("用户不存在", response.getBody());
        
        // 验证服务方法被调用
        verify(userService, times(1)).findByUsername("nonexistentuser");
    }

    @Test
    void testExportUserData_WithNullFields() {
        // 创建包含null字段的测试用户
        User userWithNulls = User.builder()
                .id(2L)
                .username("testuser2")
                .firstName("Test")
                .lastName("User")
                .email("test2@example.com")
                .phoneNumber(null)
                .address(null)
                .dateOfBirth(null)
                .nationality(null)
                .role("USER")
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .gdprProcessingConsent(true)
                .gdprMarketingConsent(null)
                .gdprAnalyticsConsent(null)
                .gdprConsentDate(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 准备测试数据
        when(authentication.getName()).thenReturn("testuser2");
        when(userService.findByUsername("testuser2")).thenReturn(userWithNulls);

        // 执行测试
        ResponseEntity<String> response = userProfileController.exportUserData(authentication);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证JSON内容正确处理null值
        String jsonContent = response.getBody();
        assertTrue(jsonContent.contains("null")); // 应该包含null值
        assertTrue(jsonContent.contains("testuser2"));
        assertTrue(jsonContent.contains("test2@example.com"));
    }

    @Test
    void testDeleteUserAccount_Success() {
        // 准备测试数据
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        doNothing().when(userService).deleteUserAccount(testUser.getId());

        // 执行测试
        ResponseEntity<Map<String, Object>> response = userProfileController.deleteUserAccount(authentication, true);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("账户已成功删除，您的个人数据已被匿名化处理", responseBody.get("message"));
        assertEquals("/auth/logout", responseBody.get("redirectUrl"));
        
        // 验证服务方法被调用
        verify(userService, times(1)).findByUsername("testuser");
        verify(userService, times(1)).deleteUserAccount(testUser.getId());
    }

    @Test
    void testDeleteUserAccount_NoAuthentication() {
        // 执行测试
        ResponseEntity<Map<String, Object>> response = userProfileController.deleteUserAccount(null, true);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("未授权访问", responseBody.get("message"));
        
        // 验证服务方法没有被调用
        verify(userService, never()).findByUsername(any());
        verify(userService, never()).deleteUserAccount(any());
    }

    @Test
    void testDeleteUserAccount_UserNotFound() {
        // 准备测试数据
        when(authentication.getName()).thenReturn("nonexistentuser");
        when(userService.findByUsername("nonexistentuser")).thenReturn(null);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = userProfileController.deleteUserAccount(authentication, true);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("用户不存在", responseBody.get("message"));
        
        // 验证服务方法被调用
        verify(userService, times(1)).findByUsername("nonexistentuser");
        verify(userService, never()).deleteUserAccount(any());
    }

    @Test
    void testDeleteUserAccount_NoConfirmation() {
        // 准备测试数据
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // 执行测试（未确认删除）
        ResponseEntity<Map<String, Object>> response = userProfileController.deleteUserAccount(authentication, false);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("请确认删除操作", responseBody.get("message"));
        
        // 验证服务方法没有被调用
        verify(userService, times(1)).findByUsername("testuser");
        verify(userService, never()).deleteUserAccount(any());
    }

    @Test
    void testDeleteUserAccount_ServiceException() {
        // 准备测试数据
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        doThrow(new RuntimeException("删除失败")).when(userService).deleteUserAccount(testUser.getId());

        // 执行测试
        ResponseEntity<Map<String, Object>> response = userProfileController.deleteUserAccount(authentication, true);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("删除失败，请稍后重试", responseBody.get("message"));
        
        // 验证服务方法被调用
        verify(userService, times(1)).findByUsername("testuser");
        verify(userService, times(1)).deleteUserAccount(testUser.getId());
    }
} 