package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.entities.Permission;
import com.revenat.jcart.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@Secured(SecurityUtil.MANAGE_PERMISSIONS)
public class PermissionController extends JCartAdminBaseController {

    @Autowired
    protected SecurityService securityService;

    @Override
    protected String getHeaderTitle() {
        return "Manage Permissions";
    }

    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    public String listPermissions(Model model) {
        List<Permission> permissions = securityService.getAllPermissions();
        model.addAttribute("permissions", permissions);
        return "permissions/permissions";
    }
}
