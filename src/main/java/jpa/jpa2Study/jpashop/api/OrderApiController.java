package jpa.jpa2Study.jpashop.api;

import jpa.jpa2Study.jpashop.domain.*;
import jpa.jpa2Study.jpashop.repository.OrderRepository;
import jpa.jpa2Study.jpashop.repository.OrderSearch;
import jpa.jpa2Study.jpashop.repository.api.OrderApiRepository;
import jpa.jpa2Study.jpashop.repository.dto.OrderFlatDto;
import jpa.jpa2Study.jpashop.repository.dto.OrderItemQueryDto;
import jpa.jpa2Study.jpashop.repository.dto.OrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderApiRepository orderApiRepository;

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
                .collect(toList());

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
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithDeliveryMember(offset, limit);

        return orders.stream().map(o -> new OrderDto(o))
                .collect(toList());
    }

    /**
     * Jpa에서 Dto로 직접 조회
     * 1+N 문제가 발생
     * @ToOne 조인 쿼리 1번
     * item 조회 쿼리 N번
     */
    @GetMapping("/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderApiRepository.findOrderQueryDtos();
    }

    /**
     * 위 문제 최적화
     * 쿼리 2번 나감
     * Order 가져올 때 1번
     * item 가져올 때 1번 -> 인 쿼리 사용 -> 이 후 메모리에서 Map으로 세팅
     */
    @GetMapping("/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderApiRepository.findAllByDto_optimization();
    }

    @GetMapping("/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flats = orderApiRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
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
                .stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());
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
