package jpa.jpa2Study.jpashop.domain.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("B")
public class Book extends Item {

    private String author;
    private String isbn;

    public static Book createBook(String name, int price, int stockQuantity, String author, String isbn){
        Book book = new Book();

        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        book.author = author;
        book.isbn = isbn;

        return book;
    }
}

