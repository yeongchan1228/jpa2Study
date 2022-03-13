package jpa.jpa2Study.jpashop.api;

import jpa.jpa2Study.jpashop.domain.*;
import jpa.jpa2Study.jpashop.repository.OrderRepository;
import jpa.jpa2Study.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * 엔티티를 직접 사용 -> 매우 안 좋다.
     * 지연 로딩 문제
     */
    @GetMapping("/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());

        for (Order order : all) {
            /*
                지연 로딩 문제 해결
             */

            order.getDelivery().getStatus();
            order.getMember().getName();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

    /**
     * 컬렉션을 Dto로
     */
    @GetMapping("/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        List<OrderDto> result = all.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 패치 조인으로 모든 테이블을 엮어서 실행하면 불필요한 데이터까지 다 넘어온다.
     */
    @GetMapping("/v3/orders")
    public List<Order> ordersV3(){
        List<Order> allWithItem = orderRepository.findAllWithItem();

        return allWithItem;
    }

    @GetMapping("/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithDeliveryMember(offset, limit);

        return orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());
    }

    @Data
    static class OrderDto{ // Dto 안에 엔티티가 존재해서는 안된다 -> 안의 엔티티를 Dto로 풀어야 한다.
        private Long id;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o) {
            id = o.getId();
            name = o.getMember().getName();
            orderDate = o.getOrderDate();
            status = o.getStatus();
            address = o.getDelivery().getAddress();
            orderItems = o.getOrderItems()
                .stream().map(orderItem -> new OrderItemDto(orderItem)).collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }
}
