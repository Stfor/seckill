package com.example.seckill.mapper;

import com.example.seckill.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
