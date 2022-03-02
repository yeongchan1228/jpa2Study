package jpa.jpa2Study.jpashop.domain;

import jpa.jpa2Study.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item", // ManyToMany일 때 중간 테이블을 설정해야 한다.
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();


    /* 셀프 부모 자식 설정하기 */
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "parent_id")
    private Category parent; // 제일 위의 카테고리

    @OneToMany(mappedBy = "parent") // 서브 카테고리
    private List<Category> child = new ArrayList<>();

    //연관관계 편의 메서드, 컨트롤 하는 쪽에 들고 있는게 좋다.

    public void addChildCategory(Category child){
        child.setParent(this);
        this.child.add(child);
    }
}
