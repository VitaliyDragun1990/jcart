package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.entities.Permission;
import com.revenat.jcart.entities.Role;
import com.revenat.jcart.security.SecurityService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_ROLE_CREATED_SUCCESSFULLY;
import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_ROLE_UPDATED_SUCCESSFULLY;

@Controller
@Secured(SecurityUtil.MANAGE_ROLES)
public class RoleController extends JCartAdminBaseController {

    private static final String VIEW_PREFIX = "roles/";

    @Autowired
    private ConversionService conversionService;
    @Autowired
    protected SecurityService securityService;
    @Autowired
    private Validator roleValidator;

    @Override
    protected String getHeaderTitle() {
        return "Manage Roles";
    }

    @ModelAttribute("permissionsList")
    public List<Permission> permissionsList() {
        return securityService.getAllPermissions();
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public String listRoles(Model model) {
        List<Role> roles = securityService.getAllRoles();
        model.addAttribute("roles", roles);
        return VIEW_PREFIX + "roles";
    }

    @RequestMapping(value = "/roles/new", method = RequestMethod.GET)
    public String createRoleForm(Model model) {
        RoleCommand role = new RoleCommand();
        model.addAttribute("role", role);

        return VIEW_PREFIX + "create_role";
    }

    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    public String createRole(@Valid @ModelAttribute("role") RoleCommand role, BindingResult result,
                             RedirectAttributes redirectAttributes) {

      roleValidator.validate(role, result);
      if (result.hasErrors()) {
          return VIEW_PREFIX + "create_role";
      }

      Role persistedRole = securityService.createRole(conversionService.convert(role, Role.class));
      LOGGER.debug("Created new role with id : {} and name : {}", persistedRole.getId(), persistedRole.getName());
      redirectAttributes.addFlashAttribute("info", getMessage(INFO_ROLE_CREATED_SUCCESSFULLY));
      return "redirect:/roles";
    }

    @RequestMapping(value = "/roles/{id}", method = RequestMethod.GET)
    public String editRoleForm(@PathVariable("id") Integer id, Model model) {
        Role role = securityService.getRoleById(id);

        List<Permission> assignedPermissions = getAssignedPermissions(role);

        role.setPermissions(assignedPermissions);

        RoleCommand command = conversionService.convert(role, RoleCommand.class);
        model.addAttribute("role", command);

        return VIEW_PREFIX + "edit_role";
    }

    @RequestMapping(value = "/roles/{id}", method = RequestMethod.POST)
    public String updateRole(@Valid @ModelAttribute("role") RoleCommand role, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return VIEW_PREFIX + "edit_role";
        }

        Role persistedRole = securityService.updateRole(conversionService.convert(role, Role.class));
        LOGGER.debug("Updated role with id: {} and name: {}", persistedRole.getId(), persistedRole.getName());

        redirectAttributes.addFlashAttribute("info", getMessage(INFO_ROLE_UPDATED_SUCCESSFULLY));
        return "redirect:/roles";
    }

    /**
     * This method is needed in order to get properly indexed list of role's permissions according to all permissions
     * we have in the system to correctly display them in the Thymeleaf view as checkboxes initially selected only those
     * permissions which given role have. This is due to usage of indexed property binding {@code *{permissions[__${rowStat.index}__].id}}
     * the role permission objects index should match with the all List objects.
     * */
    private List<Permission> getAssignedPermissions(Role role) {
        Map<Integer, Permission> assignedPermissionsMap = new HashMap<>();

        List<Permission> permissions = role.getPermissions();
        for (Permission permission : permissions) {
            assignedPermissionsMap.put(permission.getId(), permission);
        }

        List<Permission> rolePermissions = new ArrayList<>();
        List<Permission> allPermissions = securityService.getAllPermissions();
        for (Permission permission : allPermissions) {
            if (assignedPermissionsMap.containsKey(permission.getId())) {
                rolePermissions.add(permission);
            } else {rolePermissions.add(null);
            }
        }

        return rolePermissions;
    }
}
