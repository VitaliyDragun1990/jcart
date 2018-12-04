package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.admin.web.validators.UserValidator;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;
import com.revenat.jcart.core.exceptions.NotFoundException;
import com.revenat.jcart.core.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_USER_CREATED_SUCCESSFULLY;
import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_USER_UPDATED_SUCCESSFULLY;

@Controller
@Secured(SecurityUtil.MANAGE_USERS)
public class UserController extends JCartAdminBaseController {

    private static final String VIEW_PREFIX = "users/";

    @Autowired
    private ConversionService conversionService;
    @Autowired
    protected SecurityService securityService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Override
    protected String getHeaderTitle() {
        return "Manage Users";
    }

    @ModelAttribute("rolesList")
    public List<Role> rolesList() {
        return securityService.getAllRoles();
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String listUsers(Model model) {
        List<User> users = securityService.getAllUsers();
        model.addAttribute("users", users);

        return VIEW_PREFIX + "users";
    }

    @RequestMapping(value = "/users/new", method = RequestMethod.GET)
    public String createUserForm(Model model) {
        UserCommand user = new UserCommand();
        model.addAttribute("user", user);

        return VIEW_PREFIX + "create_user";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(@Valid @ModelAttribute("user") UserCommand command, BindingResult result, RedirectAttributes redirectAttributes) {
        userValidator.validate(command, result);
        if (result.hasErrors()) {
            return VIEW_PREFIX + "create_user";
        }

        String password = command.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        User user = conversionService.convert(command, User.class);
        user.setPassword(encodedPassword);

        User persistedUser = securityService.createUser(user);
        LOGGER.debug("Created new user with id: {} and name: {}", persistedUser.getId(), persistedUser.getName());
        redirectAttributes.addFlashAttribute("info", INFO_USER_CREATED_SUCCESSFULLY);

        return "redirect:/users";
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public String editUserForm(@PathVariable("id") Integer id, Model model) {
        User user = securityService.getUserById(id);
        if (user == null) {
            throw new NotFoundException(User.class, id);
        }

        List<Role> assignedRoles = getAssignedRoles(user);

        user.setRoles(assignedRoles);

        model.addAttribute("user", conversionService.convert(user, UserCommand.class));

        return VIEW_PREFIX + "edit_user";
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.POST)
    public String updateUser(@Valid @ModelAttribute("user") UserCommand command, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return VIEW_PREFIX + "edit_user";
        }

        User persistedUser = securityService.updateUser(conversionService.convert(command, User.class));
        LOGGER.debug("Updated user with id: {} and name: {}", persistedUser.getId(), persistedUser.getName());
        redirectAttributes.addFlashAttribute("info", INFO_USER_UPDATED_SUCCESSFULLY);

        return "redirect:/users";
    }

    /**
     * This method is needed in order to get properly indexed list of user's roles according to all roles
     * we have in the system to correctly display them in the Thymeleaf view as checkboxes initially selected only those
     * roles which given user have. This is due to usage of indexed property binding {@code *{roles[__${rowStat.index}__].id}}
     * the user role objects index should match with the all List objects.
     * */
    private List<Role> getAssignedRoles(User user) {
        Map<Integer, Role> assignedRoleMap = new HashMap<>();
        List<Role> roles = user.getRoles();
        for (Role role : roles) {
            assignedRoleMap.put(role.getId(), role);
        }

        List<Role> userRoles = new ArrayList<>();
        List<Role> allRoles = securityService.getAllRoles();
        for (Role role : allRoles) {
            if (assignedRoleMap.containsKey(role.getId())) {
                userRoles.add(role);
            } else {
                userRoles.add(null);
            }
        }
        return userRoles;
    }
}
