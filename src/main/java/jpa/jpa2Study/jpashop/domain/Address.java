package jpa.jpa2Study.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 내장이 될 수 있다.
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() { // JPA의 리플랙션 기능 때문에 넣어 두기
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
