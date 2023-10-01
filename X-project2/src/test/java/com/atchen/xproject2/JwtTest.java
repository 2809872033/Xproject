package com.atchen.xproject2;

import com.atchen.common.utils.JwtUtil;
import com.atchen.sys.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTest {
    @Autowired
    private JwtUtil jwtUtil;
    @Test
    public void testCreateJwt(){
        User user = new User();
        user.setUsername("zhangsan");
        user.setPhone("124356789");
     String token =    jwtUtil.createToken(user);
        System.out.println(token);
    }
    @Test
    public void testParseJwt(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMmViZjg3MC1lNWMzLTRjMDQtYWIyMS04ZjcwMThmNzQ2N2QiLCJzdWIiOiJ7XCJwaG9uZVwiOlwiMTI0MzU2Nzg5XCIsXCJ1c2VybmFtZVwiOlwiemhhbmdzYW5cIn0iLCJpc3MiOiJzeXN0ZW0iLCJpYXQiOjE2OTUyMTcwNzMsImV4cCI6MTY5NTIxODg3M30.IZ54661k5gZEsIjq5Mjdkhgag6_5HZLNgosiQ5JqSmY";
        Claims claims = jwtUtil.parseToken(token);
        System.out.println(claims);
    }
}
