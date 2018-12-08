package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController extends JCartSiteBaseController {

    @Value("${home.numberOfProdToDisplay:4}")
    private int numberOfProdToDisplay;

    @Autowired
    private CatalogService catalogService;

    @Override
    protected String getHeaderTitle() {
        return "Home";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Model model) {
        List<Category> previewCategories = preparePreviewCategories();

        model.addAttribute("categories", previewCategories);
        return "public/home";
    }

    @RequestMapping(value = "/categories/{name}", method = RequestMethod.GET)
    public String category(@PathVariable("name") String name, Model model) {
        Category category = catalogService.getCategoryByName(name);
        model.addAttribute("category", category);

        return "public/category";
    }

    private List<Category> preparePreviewCategories() {
        List<Category> previewCategories = new ArrayList<>();
        List<Category> allCategories = catalogService.getAllCategories();

        for (Category category : allCategories) {
            Set<Product> categoryProducts = category.getProducts();
            Set<Product> previewProducts = getProductsPreviewFor(categoryProducts);
            category.setProducts(previewProducts);
            previewCategories.add(category);
        }
        return previewCategories;
    }

    private Set<Product> getProductsPreviewFor(Set<Product> categoryProducts) {
        Set<Product> previewProducts;

        if (categoryProducts.size() > numberOfProdToDisplay) {
            previewProducts = categoryProducts.stream().limit(numberOfProdToDisplay).collect(Collectors.toSet());
        } else {
            previewProducts = new HashSet<>(categoryProducts);
        }

        return previewProducts;
    }
}
