package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.common.services.ImageService;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.core.exceptions.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class ProductController extends JCartSiteBaseController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private CatalogService catalogService;

    @Override
    protected String getHeaderTitle() {
        return "Product";
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String searchProducts(@RequestParam(name = "q", defaultValue = "") String query, Model model) {
        List<Product> products = catalogService.searchProduct(query);
        model.addAttribute("products", products);
        return "public/products";
    }

    @RequestMapping(value = "/products/{sku}", method = RequestMethod.GET)
    public String getProductPage(@PathVariable("sku") String sku, Model model) {
        Product product = catalogService.getProductBySku(sku);
        if (product == null) {
            throw new NotFoundException("Product with SKU: " + sku + " can not be found.");
        }
        model.addAttribute("product", product);
        return "public/product";
    }

    @RequestMapping(value = "/products/images/{productId}", method = RequestMethod.GET)
    public void showProductImage(@PathVariable String productId, HttpServletResponse response) {
        try {
            byte[] image = imageService.loadImage(productId + ".jpg");
            IOUtils.write(image, response.getOutputStream());
        } catch (IOException e) {
            logger.error("Error during processing image request: ", e);
        }
    }
}
