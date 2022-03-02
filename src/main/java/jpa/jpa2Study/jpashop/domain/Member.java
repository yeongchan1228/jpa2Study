package jpa.jpa2Study.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 타입을 포함했다.
    private Address address;

    @OneToMany(mappedBy = "member") // Order의 member 변수에 의해 매핑될 것이다. 거울이다.
    private List<Order> orders = new ArrayList<>(); // null 포인터 오류 방지ㅁ



}
