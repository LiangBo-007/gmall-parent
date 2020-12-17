package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.atguigu.gmall.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public UserInfo verify(String token) {

        UserInfo userInfo = (UserInfo)redisTemplate.opsForValue().get("user:login:" + token);

        return userInfo;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {

        // 查询mysql进行用户验证
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name",userInfo.getLoginName());
        queryWrapper.eq("passwd", MD5.encrypt(userInfo.getPasswd()));
        userInfo = userInfoMapper.selectOne(queryWrapper);

        if(null==userInfo){
            return null;
        }else {
            // 如果验证通过根据规则，生成token
            String token = UUID.randomUUID().toString();
            userInfo.setToken(token);

            // 将用户登录token和用户信息放入缓存
            redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + token, userInfo);
        }

        return userInfo;
    }
}
