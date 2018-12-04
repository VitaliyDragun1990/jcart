package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

import static com.revenat.jcart.admin.web.utils.MessageCodes.*;

@Controller
@Secured(SecurityUtil.MANAGE_CATEGORIES)
public class CategoryController extends JCartAdminBaseController {

    private static final String VIEW_PREFIX = "categories/";

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private Validator categoryValidator;
    @Autowired
    private ConversionService conversionService;

    @Override
    protected String getHeaderTitle() {
        return "Manage Categories";
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public String listCategories(Model model) {
        List<Category> categories = catalogService.getAllCategories();
        model.addAttribute("categories", categories);

        return VIEW_PREFIX + "categories";
    }

    @RequestMapping(value = "/categories/new", method = RequestMethod.GET)
    public String createCategoryForm(Model model) {
        CategoryCommand category = new CategoryCommand();
        model.addAttribute("category", category);

        return VIEW_PREFIX + "create_category";
    }

    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public String createCategory(@Valid @ModelAttribute("category") CategoryCommand category, BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        categoryValidator.validate(category, result);
        if (result.hasErrors()) {
            return VIEW_PREFIX + "create_category";
        }

        Category persistedCategory = catalogService.createCategory(conversionService.convert(category, Category.class));
        LOGGER.debug("Created new category with id: {} and name: {}", persistedCategory.getId(), persistedCategory.getName());
        redirectAttributes.addFlashAttribute("info", getMessage(INFO_CATEGORY_CREATED_SUCCESSFULLY));

        return "redirect:/categories";
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public String editCategoryForm(@PathVariable("id") Integer id, Model model) {
        Category category = catalogService.getCategoryById(id);
        if (category == null) {
            throw new NotFoundException(Category.class, id);
        }
        CategoryCommand command = conversionService.convert(category, CategoryCommand.class);
        model.addAttribute("category", command);

        return VIEW_PREFIX + "edit_category";
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.POST)
    public String updateCategory(CategoryCommand category, RedirectAttributes redirectAttributes) {
        Category persistedCategory = catalogService.updateCategory(conversionService.convert(category, Category.class));
        LOGGER.debug("Updated category with id: {} and name: {}", persistedCategory.getId(), persistedCategory.getName());
        redirectAttributes.addFlashAttribute("info", getMessage(INFO_CATEGORY_UPDATED_SUCCESSFULLY));

        return "redirect:/categories";
    }
}
