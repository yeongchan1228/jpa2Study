package jpa.jpa2Study.jpashop.controller;

import jpa.jpa2Study.jpashop.domain.item.Book;
import jpa.jpa2Study.jpashop.domain.item.Item;
import jpa.jpa2Study.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model){
        BookForm bookForm = new BookForm();

        model.addAttribute("form", bookForm);

        return "items/createItemForm";
    }

    @PostMapping("/new")
    public String create(BookForm form){

        Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/items";
    }

    @GetMapping("")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    @GetMapping("/{itemId}/edit")
    public String updateItemForm(@PathVariable Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId);

        BookForm bookForm = new BookForm();
        bookForm.setId(item.getId());
        bookForm.setAuthor(item.getAuthor());
        bookForm.setIsbn(item.getIsbn());
        bookForm.setPrice(item.getPrice());
        bookForm.setName(item.getName());
        bookForm.setStockQuantity(item.getStockQuantity());

        model.addAttribute("form", bookForm);
        return "items/updateItemform";
    }

    @PostMapping("/{itemId}/edit")
    public String updateItem(@PathVariable Long id,@ModelAttribute("form") BookForm bookForm){ // form이라는 이름으로 넘어오기 때문에 매핑한다.
        /*Book book = Book.createBook(bookForm.getName(), bookForm.getPrice(),
                bookForm.getStockQuantity(), bookForm.getAuthor(), bookForm.getIsbn());

        book.setId(bookForm.getId());

        itemService.saveItem(book); // -> merge가 이루어진다. -> 영속 컨텍스트에 book 내용을 전부 PUT한다.*/

        /*Item item = itemService.findOne(bookForm.getId());

        item.set... -> 변경 감지(더티 체킹)으로 변경된 부분을 알아서 업데이트 쿼리를 날려준다.*/

        /// merge는 모든 것을 다 PUT 하기 때문에 null 위험이 발생할 수 있어 변경 감지를 사용하는 것이 좋다.

        itemService.updateItem(id, bookForm.getName(), bookForm.getPrice(), bookForm.getStockQuantity()); // 변경 감지 사용

        return "redirect:/items";
    }
}
