package com.um.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.um.model.AppUser;
import com.um.model.Item;
import com.um.repository.ItemRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemRepository itemRepo;

    @GetMapping
    public String listItems(Model model, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("items", itemRepo.findAll());
        model.addAttribute("userRole", user.getRole());
        return "items";
    }

    @GetMapping("/add")
    public String addForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/items";
        model.addAttribute("item", new Item());
        return "item-form";
    }

    @PostMapping("/save")
    public String saveItem(@ModelAttribute Item item, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/items";
        itemRepo.save(item);
        return "redirect:/items";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/items";
        model.addAttribute("item", itemRepo.findById(id).orElse(null));
        return "item-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/items";
        itemRepo.deleteById(id);
        return "redirect:/items";
    }

    private boolean isAdmin(HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }
}
