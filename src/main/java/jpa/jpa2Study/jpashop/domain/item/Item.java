package jpa.jpa2Study.jpashop.domain.item;


import jpa.jpa2Study.jpashop.domain.Category;
import jpa.jpa2Study.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // JOINED -> 가장 정규화된 스타일, SINGLE_TABLE ->  한 테이블에 전부 다, TABLE_PER_CLASS -> 클래스 당 테이블
@DiscriminatorColumn(name = "dtype") // 구분자 -> Singletable일 때 구분할 수 있도록 해주는 컬럼 지정
@Getter
//@Setter 세터로 값을 변경하는 것이 아닌 비지니스 로직을 가지고 값을 변경해야 한다.
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
    
    //비즈니스 로직 -> 응집력을 위해

    /**
     * stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0)
            throw new NotEnoughStockException("need more stock");
        this.stockQuantity = restStock;
    }
}
