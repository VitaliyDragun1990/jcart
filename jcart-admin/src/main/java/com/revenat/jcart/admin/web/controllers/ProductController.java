package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.admin.web.models.ProductForm;
import com.revenat.jcart.admin.web.validators.ProductFormValidator;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.common.services.ImageService;
import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.exceptions.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_PRODUCT_CREATED_SUCCESSFULLY;
import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_PRODUCT_UPDATED_SUCCESSFULLY;

@Controller
@Secured(SecurityUtil.MANAGE_PRODUCTS)
public class ProductController extends JCartAdminBaseController {

    private static final String VIEW_PREFIX = "products/";

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ProductFormValidator validator;
    @Autowired
    private ImageService imageService;

    @Override
    protected String getHeaderTitle() {
        return "Manage Products";
    }

    @ModelAttribute("categoriesList")
    public List<Category> categoriesList() {
        return catalogService.getAllCategories();
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String listProducts(Model model) {
        model.addAttribute("products", catalogService.getAllProducts());
        return VIEW_PREFIX + "products";
    }

    @RequestMapping(value = "/products/new", method = RequestMethod.GET)
    public String createProductForm(Model model) {
        ProductForm form = new ProductForm();
        model.addAttribute("product", form);

        return VIEW_PREFIX + "create_product";
    }

    @RequestMapping(value = "products", method = RequestMethod.POST)
    public String createProduct(@Valid @ModelAttribute("product") ProductForm form, BindingResult result,
                                RedirectAttributes redirectAttributes) {
        validator.validate(form, result);
        if (result.hasErrors()) {
            return VIEW_PREFIX + "create_product";
        }

        Product persistedProduct = catalogService.createProduct(form.toProduct());

        form.setId(persistedProduct.getId());
        saveProductImageToDisk(form);

        LOGGER.debug("Created new product with id: {} and name: {}", persistedProduct.getId(), persistedProduct.getName());
        redirectAttributes.addFlashAttribute("info", getMessage(INFO_PRODUCT_CREATED_SUCCESSFULLY));

        return "redirect:/products";
    }

    @RequestMapping(value = "products/{id}", method = RequestMethod.GET)
    public String editProductForm(@PathVariable("id") Integer id, Model model) {
        Product product = catalogService.getProductById(id);
        if (product == null) {
            throw new NotFoundException(Product.class, id);
        }
        model.addAttribute("product", ProductForm.fromProduct(product));

        return VIEW_PREFIX + "edit_product";
    }

    @RequestMapping(value = "/products/images/{productId}", method = RequestMethod.GET)
    public void showProductImage(@PathVariable("productId") String productId, HttpServletResponse response) {
        try {
            byte[] image = imageService.loadImage(productId + ".jpg");
            IOUtils.write(image, response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("Error during processing image request: ", e);
        }
    }

    @RequestMapping(value = "/products/{id}", method = RequestMethod.POST)
    public String updateProduct(@Valid @ModelAttribute("product") ProductForm form, BindingResult result,
                                RedirectAttributes redirectAttributes) {
        validator.validate(form, result);
        if (result.hasErrors()) {
            return VIEW_PREFIX + "edit_product";
        }

        Product persistedProduct = catalogService.updateProduct(form.toProduct());
        saveProductImageToDisk(form);

        LOGGER.debug("Updated product with id: {} and name: {}", persistedProduct.getId(), persistedProduct.getName());
        redirectAttributes.addFlashAttribute("info", getMessage(INFO_PRODUCT_UPDATED_SUCCESSFULLY) );

        return "redirect:/products";
    }

    private void saveProductImageToDisk(ProductForm form) {
        MultipartFile file = form.getImage();
        if (file != null && !file.isEmpty()) {
            String imageName = form.getId() + ".jpg";
            try {
                byte[] bytes = file.getBytes();
                imageService.saveImage(imageName, bytes);
            } catch (Exception e) {
                LOGGER.error("Error during saving product image", e);
                throw new JCartException(e);
            }
        }
    }
}
