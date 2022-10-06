package com.example.seckill.exception;

import com.example.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;

    public GlobalException(RespBeanEnum respBeanEnum){
        this.respBeanEnum = respBeanEnum;
    }
}
