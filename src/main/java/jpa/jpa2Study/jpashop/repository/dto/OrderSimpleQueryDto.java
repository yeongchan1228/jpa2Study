package jpa.jpa2Study.jpashop.repository.dto;

import jpa.jpa2Study.jpashop.domain.Address;
import jpa.jpa2Study.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {

    private Long id;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Address address;

    public OrderSimpleQueryDto(Long id, String name, LocalDateTime orderDate, OrderStatus status, Address address) {
        this.id = id;
        this.name = name;
        this.orderDate = orderDate;
        this.status = status;
        this.address = address;
    }
}
