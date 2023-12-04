package com.sima.dms.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;

public class Pagination {

    public static OrderSpecifier[] getOrderSpecifiers(@NotNull Pageable pageable, @NotNull Class klass) {

        String className = klass.getSimpleName();
        final String orderVariable = String.valueOf(Character.toLowerCase(className.charAt(0))).concat(className.substring(1));

        return pageable.getSort().stream()
                .map(order -> new OrderSpecifier(
                        Order.valueOf(order.getDirection().toString()),
                        new PathBuilder(klass, orderVariable).get(order.getProperty())))
                .toArray(OrderSpecifier[]::new);
    }
}