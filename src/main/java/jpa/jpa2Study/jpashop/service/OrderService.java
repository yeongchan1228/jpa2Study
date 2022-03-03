package jpa.jpa2Study.jpashop.service;

import jpa.jpa2Study.jpashop.domain.Delivery;
import jpa.jpa2Study.jpashop.domain.Member;
import jpa.jpa2Study.jpashop.domain.Order;
import jpa.jpa2Study.jpashop.domain.OrderItem;
import jpa.jpa2Study.jpashop.domain.item.Item;
import jpa.jpa2Study.jpashop.repository.ItemRepository;
import jpa.jpa2Study.jpashop.repository.MemberRepository;
import jpa.jpa2Study.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송 정보 생성
        Delivery delivery = new Delivery(); // 이렇게 new로 생성하면 디비에 넣어 주고 나서 order의 delivery에 넣어주어야 하는데
        // Order의 delivery에 cascade ALL이 적용되어 있어 order의 delivery에 값이 있으면 이도 자동으로 persist해 주기 때문에 안해도 된다.
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //Order 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        //주문 취소
        order.cancel();

        // JPA를 사용하지 않으면 주문 취소시 수량 변경, 주문 상태 변경에 해당되게 하나 하나 변경 쿼리를 날려야 하지만
        // JPA를 사용하면 JPA가 더티 체크를 해서 변경된 엔티티는 업데이트 쿼리를 자동으로 날려준다.
    }

    /**
     * 주문 검색
     */
/*    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }*/

}
