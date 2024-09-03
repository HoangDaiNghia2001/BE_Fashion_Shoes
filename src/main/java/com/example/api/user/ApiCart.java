package com.example.api.user;

import com.example.exception.CustomException;
import com.example.request.CartRequest;
import com.example.response.*;
import com.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("cartOfUser")
@RequestMapping("/api/user")
public class ApiCart {
    @Autowired
    private CartService cartService;

    // CALL SUCCESS
    @PostMapping("/cart")
    public ResponseEntity<?> addCartItem(@RequestBody CartRequest cartRequest) throws CustomException {

        CartItemResponse cart = cartService.addToCart(cartRequest);

        ResponseData<CartItemResponse> responseData = new ResponseData<>();
        responseData.setResults(cart);
        responseData.setMessage("Add cart item success !!!");
        responseData.setStatus(HttpStatus.CREATED.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/cart")
    public ResponseEntity<?> updateCartItem(@RequestParam("id") Long id,
                                            @RequestBody CartRequest cartRequest) throws CustomException {
        CartItemResponse cart = cartService.updateCartItem(id, cartRequest);

        ResponseData<CartItemResponse> responseData = new ResponseData<>();
        responseData.setResults(cart);
        responseData.setMessage("Update cart item success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/cart")
    public ResponseEntity<?> deleteCartItem(@RequestParam("id") Long id) throws CustomException {
        Response response = cartService.deleteCartItem(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/carts/{idCarts}")
    public ResponseEntity<?> deleteMultiCartItems(@PathVariable List<Long> idCarts) throws CustomException {
        Response response = cartService.deleteMultiCartItem(idCarts);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/cart/detail")
    public ResponseEntity<?> getCartDetailOfUser() throws CustomException {
        CartResponse cartResponse = cartService.getCartDetails();

        ResponseData<CartResponse> responseData = new ResponseData<>();
        responseData.setMessage("Get cart detail of user success !!!");
        responseData.setResults(cartResponse);
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/cart")
    public ResponseEntity<?> countCartItem() throws CustomException {
        int total = cartService.countCartItem();

        ResponseData<Integer> responseData = new ResponseData<>();
        responseData.setResults(total);
        responseData.setMessage("Count total cart item success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
