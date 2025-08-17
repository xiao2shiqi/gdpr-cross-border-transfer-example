package com.hotelbooking.hotel_reservation_eu.mapper;

import com.hotelbooking.hotel_reservation_eu.model.AdminUser;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 运营人员数据访问映射器
 */
@Mapper
public interface AdminUserMapper {

    /**
     * 根据用户名查找运营人员
     */
    @Select("SELECT * FROM admin_user WHERE username = #{username} AND status = 1")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "displayName", column = "display_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "role", column = "role", typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "department", column = "department"),
        @Result(property = "status", column = "status"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "failedLoginAttempts", column = "failed_login_attempts"),
        @Result(property = "accountLockedUntil", column = "account_locked_until"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by")
    })
    AdminUser findByUsername(@Param("username") String username);

    /**
     * 根据ID查找运营人员
     */
    @Select("SELECT * FROM admin_user WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "displayName", column = "display_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "role", column = "role", typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "department", column = "department"),
        @Result(property = "status", column = "status"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "failedLoginAttempts", column = "failed_login_attempts"),
        @Result(property = "accountLockedUntil", column = "account_locked_until"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by")
    })
    AdminUser findById(@Param("id") Long id);

    /**
     * 查找所有启用的运营人员
     */
    @Select("SELECT * FROM admin_user WHERE status = 1 ORDER BY created_at DESC")
    @ResultMap("adminUserResultMap") 
    List<AdminUser> findAllEnabled();

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE admin_user SET last_login_time = #{loginTime}, failed_login_attempts = 0 WHERE id = #{id}")
    void updateLastLoginTime(@Param("id") Long id, @Param("loginTime") LocalDateTime loginTime);

    /**
     * 增加失败登录次数
     */
    @Update("UPDATE admin_user SET failed_login_attempts = failed_login_attempts + 1 WHERE id = #{id}")
    void incrementFailedLoginAttempts(@Param("id") Long id);

    /**
     * 锁定账户
     */
    @Update("UPDATE admin_user SET account_locked_until = #{lockedUntil} WHERE id = #{id}")
    void lockAccount(@Param("id") Long id, @Param("lockedUntil") LocalDateTime lockedUntil);

    /**
     * 解锁账户
     */
    @Update("UPDATE admin_user SET account_locked_until = NULL, failed_login_attempts = 0 WHERE id = #{id}")
    void unlockAccount(@Param("id") Long id);

    /**
     * 创建新的运营人员
     */
    @Insert("INSERT INTO admin_user (username, password, display_name, email, role, department, status, created_by) " +
            "VALUES (#{username}, #{password}, #{displayName}, #{email}, #{role}, #{department}, #{status}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAdminUser(AdminUser adminUser);

    /**
     * 更新运营人员信息
     */
    @Update("UPDATE admin_user SET display_name = #{displayName}, email = #{email}, role = #{role}, " +
            "department = #{department}, status = #{status}, updated_by = #{updatedBy}, updated_at = NOW() " +
            "WHERE id = #{id}")
    void updateAdminUser(AdminUser adminUser);

    /**
     * 更改密码
     */
    @Update("UPDATE admin_user SET password = #{newPassword}, updated_by = #{updatedBy}, updated_at = NOW() " +
            "WHERE id = #{id}")
    void changePassword(@Param("id") Long id, @Param("newPassword") String newPassword, @Param("updatedBy") Long updatedBy);

    /**
     * 启用/禁用账户
     */
    @Update("UPDATE admin_user SET status = #{status}, updated_by = #{updatedBy}, updated_at = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updatedBy") Long updatedBy);

    // ResultMap定义，复用上面的Results配置
    @Select("SELECT 1") // 占位查询，实际不会执行
    @Results(id = "adminUserResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "displayName", column = "display_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "role", column = "role", typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
        @Result(property = "department", column = "department"),
        @Result(property = "status", column = "status"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "failedLoginAttempts", column = "failed_login_attempts"),
        @Result(property = "accountLockedUntil", column = "account_locked_until"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by")
    })
    void defineResultMap();
} 