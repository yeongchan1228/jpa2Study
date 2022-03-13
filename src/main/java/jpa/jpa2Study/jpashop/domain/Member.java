package jpa.jpa2Study.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
//    @JsonIgnore  // api 응답시 JSON에서 빠진다. 양방향 연관 관계에서는 한 쪽을 JSONIGNORE해주어야 한다. -> 무한 루프에 빠짐
    private List<Order> orders = new ArrayList<>(); // null 포인터 오류 방지ㅁ



}
