package jpa.jpa2Study.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery",
    fetch = FetchType.LAZY) // 자주 안써서 이쪽을 거울로 order쪽을 연관관계 주인으로 설정
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // READY, COMP

}
