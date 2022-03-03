package jpa.jpa2Study.jpashop.repository;

import jpa.jpa2Study.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    private String memberName;
    private OrderStatus orderStatus; // 주문, 취소
}
