package com.um.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.um.model.AppUser;
import com.um.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String home() {
		return "redirect:/login";
	}
	
	@PostMapping("/register")
	public String registerForm(@ModelAttribute AppUser appUser, RedirectAttributes redirectAttributes) {
		appUser.setStatus("PENDING");
		userService.registerUser(appUser);
		redirectAttributes.addFlashAttribute("success",
				"Registration successful! you can access the application once approved");
		return "redirect:/login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("user", new AppUser());
		return "register";
	}

	@GetMapping("/login")
	public String loginForm(Model model) {
		model.addAttribute("user", new AppUser());
		return "login";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute AppUser user, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {
		AppUser alreadyUser = userService.findUserByUsername(user.getUsername());
		if (alreadyUser != null) {
			AppUser validUser = userService.authenticate(user.getUsername(), user.getPassword());
			System.out.println("inside");
			if (validUser != null) {
				session.setAttribute("loggedInUser", validUser);
				return "redirect:/items";
			} else {
				redirectAttributes.addFlashAttribute("error", "Invalid Credentials");
				return "redirect:/login";
			}
		} else {
			redirectAttributes.addFlashAttribute("error", "No user found");
			return "redirect:/login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	@GetMapping("/admin/users/pending")
	public String pendingUsers(HttpSession session, Model model) {
		AppUser loggedUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
			return "redirect:/login"; // or return an error page
		}
		List<AppUser> pendingUsers = userService.findUsersByStatus("PENDING");
		model.addAttribute("pendingUsers", pendingUsers);
		return "admin-pending-users";
	}

	@PostMapping("/admin/users/{id}/approve")
	public String approveUser(@PathVariable Long id, HttpSession session) {
		AppUser loggedUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
			return "redirect:/login";
		}
		userService.updateStatus(id, "APPROVED");
		return "redirect:/admin/users/pending";
	}

	@PostMapping("/admin/users/{id}/reject")
	public String rejectUser(@PathVariable Long id, HttpSession session) {
		AppUser loggedUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
			return "redirect:/login";
		}
		userService.updateStatus(id, "REJECTED");
		return "redirect:/admin/users/pending";
	}

}
