package ru.maxryazan.bankterminal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.maxryazan.bankterminal.model.Client;
import ru.maxryazan.bankterminal.service.ClientService;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ClientService clientService;

    @GetMapping("/")
    public String getMain() {
        return "main_page";
    }

    @GetMapping("/personal")
    public String getPersonal() {
        return "personal_page";
    }

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/personal/credit")
    public String getCredits() {
        return "personal_credit";
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
}
