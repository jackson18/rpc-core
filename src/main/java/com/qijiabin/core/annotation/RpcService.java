package com.qijiabin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * ========================================================
 * 日 期：2016年6月14日 下午12:08:34
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：RPC 请求注解（标注在服务实现类上）
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    Class<?> value();
}
