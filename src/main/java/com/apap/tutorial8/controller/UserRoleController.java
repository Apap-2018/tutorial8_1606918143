package com.apap.tutorial8.controller;

import java.util.regex.Pattern;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.service.UserRoleService;
import com.apap.tutorial8.model.PasswordModel;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	private String addUserSubmit(@ModelAttribute @Valid UserRoleModel user, Model model) {
		String msg = "";
		if (validatePassword(user.getPassword())) {
			userService.addUser(user);
			msg = "user berhasil ditambahkan";
		}
		else {
			msg = "password baru anda belum sesuai ketentuan: lebih dari 8 karakter, mengandung minimal 1 huruf dan 1 angka";
		}
		model.addAttribute("msg", msg);
		model.addAttribute("user", user);
		
		return "home";
	}
	
	@RequestMapping("/passwordUpdate")
	public String updatepw () {
		return "update-password";
	}
	
	public boolean validatePassword(String password) {
		if (password.length() >= 8 && Pattern.compile("[0-9]").matcher(password).find() && Pattern.compile("[a-zA-Z]").matcher(password).find()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@RequestMapping(value="/passwordSubmit", method=RequestMethod.POST)
	public ModelAndView updatePasswordSubmit(@ModelAttribute PasswordModel pass, Model model, RedirectAttributes redir) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String msg = "";
		
		if (pass.getPasswordConfirm().equals(pass.getPasswordBaru())) {
			if (passwordEncoder.matches(pass.getPasswordLama(), user.getPassword())) {
				if (validatePassword(pass.getPasswordBaru())) {
					userService.updatePassword(user, pass.getPasswordBaru());
					msg = "password berhasil diubah";
				}
				else {
					msg = "password baru anda belum sesuai ketentuan: lebih dari 8 karakter, mengandung minimal 1 huruf dan 1 angka";
				}
			}
			else {
				msg = "password lama anda tidak tepat";
			}
			
		}
		else {
			msg = "password tidak sesuai";
		}
		ModelAndView modelAndView = new ModelAndView("redirect:/user/passwordUpdate");
		redir.addFlashAttribute("msg", msg);
		return modelAndView;
	}
}
