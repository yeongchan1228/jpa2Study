package jpa.jpa2Study.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // cascade = ALL -> orderItemA, orderItemB, orderItemC 를 
    // 각각 persist한 후 order를 persist 해야 하는데
    // 이를 persist(order)만 해도 전파되게 함
    // cascade는 자신 엔티티가 완전히 자신만 참조하는 개인 소유 엔티티일 경우에만 사용한다.
    // 여기서 orderItem은 Order의 완전 소유물이므로 사용해도 된다.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // order를 persist하거나 delete할 때 deilvery가 변경되면 이도 같이 persist 해준다.
    @JoinColumn(name = "delivery_id") // onetoone일때 보통 자주 쓰는 곳에 외래키를 넣는다.
    private Delivery delivery;

    /* cascade 예제 ALL을 써야하는 이유

    Member member = new Member();
        member.setName("andrew");
        member.setAge(32);


    Locker locker = new Locker();
        locker.setName("1번 사물함");

        member.setLocker(locker);

        memberRepository.save(member);
        lockerRepository.save(locker);

        이는 오류가 발생 -> DB의 Locker 테이블에 없는 존재를 생성하여 member에 넣어주고 member를 save하고 새로 생성한 locker도 save하면
        논리 흐름상 맞지 않는다
        이를 고치기 위해 lockerRepository.save(locker); 이 후 memberRepository.save(member);를 해줘야한다.
        cascade = ALL을 사용하면 memberRepository.save(member);만 해도 locker까지 알아서 persist해준다.
        */

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태, <Order, Cancel>

    //연관관계 편의 메서드, 컨트롤 하는 쪽에 들고 있는게 좋다.

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
