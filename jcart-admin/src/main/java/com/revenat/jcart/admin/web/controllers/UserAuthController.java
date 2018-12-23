package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.admin.web.utils.WebUtils;
import com.revenat.jcart.core.common.services.EmailService;
import com.revenat.jcart.core.entities.User;
import com.revenat.jcart.core.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;

import static com.revenat.jcart.admin.web.utils.MessageCodes.*;

@Controller
public class UserAuthController extends JCartAdminBaseController {

    private static final String VIEW_PREFIX = "public/";
    private static final String EMAIL = "email";
    private static final String TOKEN = "token";
    private static final String MSG = "msg";
    private static final String PASS = "password";
    private static final String CONF_PASS = "confPassword";

    @Autowired
    protected SecurityService securityService;
    @Autowired
    protected EmailService emailService;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected TemplateEngine templateEngine;

    @Override
    protected String getHeaderTitle() {
        return "User";
    }

    @RequestMapping(value = "/forgotPwd", method = RequestMethod.GET)
    public String forgotPassword() {
        return VIEW_PREFIX + "forgotPwd";
    }

    @RequestMapping(value = "/forgotPwd", method = RequestMethod.POST)
    public String handleForgotPassword(@RequestParam("email") String email, HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        try {
            User user = securityService.findUserByEmail(email);
            String token = securityService.resetPassword(email);
            String resetPasswordURL =
                    WebUtils.getURLWithContextPath(request) + "/resetPwd?email=" + email + "&token=" + token;
            LOGGER.debug(resetPasswordURL);
            this.sendForgotPasswordEmail(email, user.getName(), resetPasswordURL);
            redirectAttributes.addFlashAttribute(MSG, getMessage(INFO_USER_PASS_RESET_LINK_SENT));
        }
        catch (JCartException e) {
            LOGGER.error("Exception occurred while handling reset password request.", e);
            redirectAttributes.addFlashAttribute(MSG, e.getMessage());
        }

        return "redirect:/forgotPwd";
    }

    @RequestMapping(value = "/resetPwd", method = RequestMethod.GET)
    public String resetPassword(@RequestParam("email") String email, @RequestParam("token") String token,
                                Model model, RedirectAttributes redirectAttributes) {

        try {
            boolean valid = securityService.verifyPasswordResetToken(email, token);
            if (valid) {
                model.addAttribute(EMAIL, email);
                model.addAttribute(TOKEN, token);
                return VIEW_PREFIX + "resetPwd";
            } else {
                redirectAttributes.addFlashAttribute(MSG, getMessage(ERROR_INVALID_USER_PASS_RESET_REQUEST));
            }
        } catch (JCartException e) {
            LOGGER.error("Exception occurred while resetting user password.", e);
            redirectAttributes.addFlashAttribute(MSG, e.getMessage());
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    public String handleResetPassword(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            String email = request.getParameter(EMAIL);
            String token = request.getParameter(TOKEN);
            String password = request.getParameter(PASS);
            String confirmPassword = request.getParameter(CONF_PASS);

            if (!password.equals(confirmPassword)) {
                model.addAttribute(EMAIL, email);
                model.addAttribute(TOKEN, token);
                model.addAttribute(MSG, getMessage(ERROR_USER_PASS_CONF_PASS_MISMATCH));

                return VIEW_PREFIX + "resetPwd";
            }
            String encodedPassword = passwordEncoder.encode(password);
            securityService.updatePassword(email, token, encodedPassword);

            redirectAttributes.addFlashAttribute(MSG, getMessage(INFO_USER_PASS_UPDATED_SUCCESS));
        } catch (JCartException e) {
            LOGGER.error("Error during updating user password.",e);
            redirectAttributes.addFlashAttribute(MSG, e.getMessage());
        }
        return "redirect:/login";
    }

    private void sendForgotPasswordEmail(String email, String username, String resetPasswordURL) {
        try {
            // Prepare the evaluation context
            final Context context = new Context();
            context.setVariable("resetPwdURL", resetPasswordURL);
            context.setVariable("username", username);

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.templateEngine.process("forgot-password-email", context);

            this.emailService.sendEmail(email, getMessage(LABEL_USER_PASS_RESET_EMAIL_SUBJECT), htmlContent);
        } catch (JCartException e) {
            LOGGER.debug("Error during sending forgot password email.", e);
        }
    }
}
