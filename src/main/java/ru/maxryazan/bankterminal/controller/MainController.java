package ru.maxryazan.bankterminal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.maxryazan.bankterminal.model.Client;
import ru.maxryazan.bankterminal.service.ClientService;
import ru.maxryazan.bankterminal.service.CreditService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ClientService clientService;
    private final CreditService creditService;

    @GetMapping("/")
    public String getMain() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "main_page";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login";
    }

    @GetMapping("/personal")
    public String getPersonal() {
        return "personal_page";
    }

    @GetMapping("/personal/add-money")
    public String getAddMoneyPage() {
        return "personal_add_money";
    }

    @PostMapping("/personal/add-money")
    public String postAddMoneyPage(@RequestParam int sum) {
        Client client = clientService.findByAuthentication();
        clientService.changeBalance(sum, client);
        return "redirect:/personal";
    }

    @GetMapping("/personal/withdraw-money")
    public String getWithdrawMoneyPage(Model model) {
        Client client = clientService.findByAuthentication();
        model.addAttribute("balance", client.getBalance());
        return "personal_withdraw_money";
    }

    @PostMapping("/personal/withdraw-money")
    public String postWithdrawMoneyPage(@RequestParam int sum, Model model) {
        if (clientService.validateSum(sum, model)) {
            return "personal_withdraw_money";
        }
        sum = -sum;
        Client client = clientService.findByAuthentication();
        clientService.changeBalance(sum, client);
        return "redirect:/personal";
    }


    @GetMapping("/personal/balance")
    public String getBalancePage(Model model) {
        Client client = clientService.findByAuthentication();
        model.addAttribute("balance",client.getBalance());
        return "personal_balance";
    }

    @GetMapping("/personal/operations")
    public String getOperationsPage(Model model) {
        Client client = clientService.findByAuthentication();
        model.addAttribute("balance", client.getBalance());
        return "personal_operations";
    }

    @PostMapping("/personal/operations")
    public String postOperationsPage(@RequestParam int sum,
                                     @RequestParam String phone, Model model) {
        if(!clientService.existsByPhone(phone)){
            model.addAttribute("error", "Номер телефона не существует!");
            model.addAttribute("balance", clientService.findByAuthentication().getBalance());
            return "personal_operations";
        }
        if (clientService.validateSum(sum, model)) {
            model.addAttribute("balance", clientService.findByAuthentication().getBalance());
            return "personal_operations";
        }
        clientService.doTransaction(sum, phone);
        return "redirect:/personal";
    }

    @GetMapping("/personal/history")
    public String getHistoryPage(Model model) {
        model.addAttribute("transactionsForLastWeek", clientService.transactionsForLastWeek());
        model.addAttribute("paysForLastWeek", clientService.paysForLastWeek());
        return "personal_history";
    }

    @GetMapping("/personal/credit")
    public String getCreditPage(Model model) {
        model.addAttribute("credits", clientService.showCredits());
        model.addAttribute("balance", clientService.findByAuthentication().getBalance());
        return "personal_credit";
    }

    @PostMapping("/personal/credit")
    public String postCreditPage(@RequestParam String creditID, @RequestParam double sum, Model model) {
       if(clientService.validateSum((int)sum, model)) {
           model.addAttribute("credits", clientService.showCredits());
            return "personal_credit";
       }
       if(creditService.validateCredit(creditID, sum, model, clientService.findByAuthentication())){
           model.addAttribute("credits", clientService.showCredits());
           return "personal_credit";
       }
        clientService.getPayForCredit(creditID, sum, model);
        return "redirect:/personal";
    }
}
