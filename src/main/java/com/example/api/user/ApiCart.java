package com.example.api.user;

import com.example.exception.CustomException;
import com.example.request.CartRequest;
import com.example.response.CartItemResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("cartOfUser")
@RequestMapping("/api/user")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
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
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/cart")
    public ResponseEntity<?> updateCartItem(@RequestParam("id") Long id,
                                            @RequestBody CartRequest cartRequest) throws CustomException {
        CartItemResponse cart = cartService.updateCartItem(cartRequest, id);

        ResponseData<CartItemResponse> responseData = new ResponseData<>();
        responseData.setResults(cart);
        responseData.setMessage("Update cart item success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/cart")
    public ResponseEntity<?> deleteCartItem(@RequestParam("id") Long id) throws CustomException {
        String message = cartService.deleteCartItem(id);

        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/carts/{idProducts}")
    public ResponseEntity<?> deleteMultiCartItems(@PathVariable List<Long> idProducts) throws CustomException {
        String delete = cartService.deleteMultiCartItem(idProducts);

        Response response = new Response();
        response.setMessage(delete);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/cart/detail")
    public ResponseEntity<?> getCartDetailOfUser() throws CustomException {
        return new ResponseEntity<>(cartService.getCartDetails(), HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/cart")
    public ResponseEntity<?> countCartItem() throws CustomException {
        int total = cartService.countCartItem();

        ResponseData<Integer> responseData = new ResponseData<>();
        responseData.setResults(total);
        responseData.setMessage("Count total cart item success !!!");
        responseData.setSuccess(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
