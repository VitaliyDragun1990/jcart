package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.core.exceptions.NotFoundException;
import com.revenat.jcart.site.web.models.Cart;
import com.revenat.jcart.site.web.models.LineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController extends JCartSiteBaseController {

    @Autowired
    private CatalogService catalogService;

    @Override
    protected String getHeaderTitle() {
        return "Cart";
    }

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String showCart(HttpServletRequest request, Model model) {
        Cart cart = getOrCreateCart(request);
        model.addAttribute("cart", cart);
        return "public/cart";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/cart/items/count", method = RequestMethod.GET,
            consumes = {"application/json", "application/json;charset=UTF-8"})
    public Map<String, Object> getCartItemCount(HttpServletRequest request) {
        Cart cart = getOrCreateCart(request);
        int itemCount = cart.getItemCount();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("count", itemCount);
        return responseBody;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/cart/items", method = RequestMethod.POST,
            consumes = {"application/json", "application/json;charset=UTF-8"})
    public void addToCart(@RequestBody Product product, HttpServletRequest request) {
        Cart cart = getOrCreateCart(request);
        Product productToAdd = catalogService.getProductBySku(product.getSku());
        if (productToAdd == null) {
            throw new NotFoundException("Product with SKU: " + product.getSku() + " can not be found.");
        }
        cart.addItem(productToAdd);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/cart/items", method = RequestMethod.PUT,
            consumes = {"application/json", "application/json;charset=UTF-8"})
    public void updateCartItem(@RequestBody LineItem item, HttpServletRequest request) {
        Cart cart = getOrCreateCart(request);
        if (item.getQuantity() <= 0) {
            cart.removeItem(item.getProduct());
        } else {
            cart.updateItemQuantity(item.getProduct(), item.getQuantity());
        }
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/cart/items/{sku}", method = RequestMethod.DELETE,
            consumes = {"application/json", "application/json;charset=UTF-8"})
    public void removeCartItem(@PathVariable("sku") String sku, HttpServletRequest request) {
        Cart cart = getOrCreateCart(request);
        cart.removeItem(sku);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/cart", method = RequestMethod.DELETE,
            consumes = {"application/json", "application/json;charset=UTF-8"})
    public void clearCart(HttpServletRequest request) {
        Cart cart = getOrCreateCart(request);
        cart.clearItems();
    }
}
