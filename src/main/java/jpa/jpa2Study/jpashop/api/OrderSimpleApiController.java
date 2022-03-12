package jpa.jpa2Study.jpashop.api;

import jpa.jpa2Study.jpashop.domain.Address;
import jpa.jpa2Study.jpashop.domain.Order;
import jpa.jpa2Study.jpashop.domain.OrderStatus;
import jpa.jpa2Study.jpashop.repository.OrderRepository;
import jpa.jpa2Study.jpashop.repository.OrderSearch;
import jpa.jpa2Study.jpashop.repository.OrderSimpleQueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XToOne 성능 최적화 하기
 * Order 조회
 * 연관 :
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;


    /**
     * 엔티티 직접 반횐
     * 무한 루프, 지연 로딩 문제 발생
     */
    @GetMapping("/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        return all;
    }

    /**
     * 엔티티를 DTO 변환
     * Lazy -> 지연 로딩으로 인한 수 많은 쿼리가 발생
     * Order 조회 1 + Member 조회 (1 + 1) + Delivery 조회 (1 + 1) -> 1 + N + N
     */
    @GetMapping("/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> all = orderRepository.findAll(new OrderSearch());

        List<SimpleOrderDto> collect = all.stream().map(o -> new SimpleOrderDto(o.getId(), o.getMember().getName()
                        , o.getOrderDate(), o.getStatus(), o.getMember().getAddress()))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * 엔티티 Dto 변환2
     * fetch join을 사용하여 쿼리 최적화
     * Order + Member + Delivery에 포함된 모든 필드 정보를 가져온다. -> 불필요한 정보까지 가져온다.
     */
    @GetMapping("/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> all = orderRepository.findAllWithDeliveryMember();

        return all.stream().map(m -> new SimpleOrderDto(m.getId(), m.getMember().getName(), m.getOrderDate(), m.getStatus()
                , m.getMember().getAddress())).collect(Collectors.toList());
    }

    /**
     * Repository 내에서 Dto를 사용하여 가져온다.
     * Join을 사용하여 내가 필요한 필드들만 Dto로 만들어 뿌린다.
     */
    @GetMapping("/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){

        return orderRepository.findOrderDtos();
    }

    @Data
    @AllArgsConstructor
    static class SimpleOrderDto{
        private Long id;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Address address;
    }
}
