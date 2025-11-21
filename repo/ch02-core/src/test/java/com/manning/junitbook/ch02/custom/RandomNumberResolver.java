package com.manning.junitbook.ch02.custom;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Random;


public class RandomNumberResolver implements ParameterResolver {

    private final Random random = new Random();

    @Override
    public boolean supportsParameter(
            ParameterContext paramCtx,
            ExtensionContext extCtx
    ) throws ParameterResolutionException {
        // 只支持用 @RandomAnnotation 注解的 int 或 long 类型参数
        return paramCtx.isAnnotated(RandomAnnotation.class) &&
                (paramCtx.getParameter().getType() == int.class ||
                        paramCtx.getParameter().getType() == long.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext paramCtx,
            ExtensionContext extCtx
    ) throws ParameterResolutionException {
        // 提供随机数
        Class<?> type = paramCtx.getParameter().getType();
        if (type == int.class) {
            return random.nextInt(100); // 生成 0-99 的随机整数
        } else if (type == long.class) {
            return random.nextLong();
        } else {
            throw new IllegalArgumentException("Unsupported parameter type: " + type);
        }
    }
}
