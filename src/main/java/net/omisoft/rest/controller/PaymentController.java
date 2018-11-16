package net.omisoft.rest.controller;

import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.configuration.annotation.CurrentUser;
import net.omisoft.rest.configuration.annotation.RemoteIp;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.service.interkassa.InterkassaUrl;
import net.omisoft.rest.service.payment.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping(value = "payment")
@Validated
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MessageSourceConfiguration message;

    @GetMapping(value = {"", "/", "/checkout"})
    @PreAuthorize("isAuthenticated()")
    public String checkout(@RequestParam(value = "amount") @Positive @Digits(integer = 5, fraction = 2) BigDecimal amount,
                           @RequestParam(value = "email", required = false) @Email String email,
                           @CurrentUser UserEntity currentUser,
                           Model model) {
        model.addAttribute("data", paymentService.preparedPayment(amount, email, null, currentUser));
        model.addAttribute("symbol", paymentService.getCurrencySymbol());
        return "checkout";
    }

    @PostMapping(value = {"/success/{uuid}"})
    public ModelAndView success(@PathVariable("uuid") @NotBlank String uuid,
                                ModelMap model) {
        paymentService.updatePaymentInfo(uuid, InterkassaUrl.success);
        model.addAttribute("result", message.getMessage("payment.success"));
        return new ModelAndView("result", model);
    }

    @PostMapping(value = {"/fail/{uuid}"})
    public ModelAndView fail(@PathVariable("uuid") @NotBlank String uuid,
                             ModelMap model) {
        paymentService.updatePaymentInfo(uuid, InterkassaUrl.fail);
        model.addAttribute("result", message.getMessage("payment.fail"));
        return new ModelAndView("result", model);
    }

    @PostMapping(value = {"/pending/{uuid}"})
    public ModelAndView pending(@PathVariable("uuid") @NotBlank String uuid,
                                ModelMap model) {
        paymentService.updatePaymentInfo(uuid, InterkassaUrl.pending);
        model.addAttribute("result", message.getMessage("payment.pending"));
        return new ModelAndView("result", model);
    }

    @PostMapping(value = {"/interaction/{uuid}"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void interaction(@PathVariable("uuid") @NotBlank String uuid,
                            @RequestParam Map<String, String> data,
                            @RemoteIp String ip) {
        paymentService.updatePaymentState(ip, uuid, data);
    }

}