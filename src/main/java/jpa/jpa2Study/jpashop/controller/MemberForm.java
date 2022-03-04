package jpa.jpa2Study.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다.") // null 허용 X
    private String name;

    private String city;
    private String street;
    private String zipcode;

}
