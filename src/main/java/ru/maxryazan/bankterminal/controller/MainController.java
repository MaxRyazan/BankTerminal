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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ClientService clientService;

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
    public String getWithdrawMoneyPage() {
        return "personal_withdraw_money";
    }

    @PostMapping("/personal/withdraw-money")
    public String postWithdrawMoneyPage(@RequestParam int sum) {
        sum = -sum;
        Client client = clientService.findByAuthentication();
        clientService.changeBalance(sum, client);
        return "redirect:/personal";
    }

    @GetMapping("/personal/balance")
    public String getBalancePage(Model model) {
        Client client = clientService.findByAuthentication();
        model.addAttribute("balance", client.getBalance());
        return "personal_balance";
    }

    @GetMapping("/personal/operations")
    public String getOperationsPage() {
        return "personal_operations";
    }

    @PostMapping("/personal/operations")
    public String postOperationsPage(@RequestParam int sum,
                                     @RequestParam String phone) {
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
        return "personal_credit";
    }

    @PostMapping("/personal/credit")
    public String postCreditPage(@RequestParam String creditID, @RequestParam double sum) {
        clientService.getPayForCredit(creditID, sum);
        return "redirect:/personal";
    }
}
