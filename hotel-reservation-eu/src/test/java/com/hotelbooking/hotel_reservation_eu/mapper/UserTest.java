package com.hotelbooking.hotel_reservation_eu.mapper;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.hotelbooking.hotel_reservation_eu.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
//        Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }
}
