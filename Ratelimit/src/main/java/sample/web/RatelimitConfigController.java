package sample.web;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sample.entity.User;
import sample.repo.UserRepo;

@Controller
@RequestMapping("/config")

public class RatelimitConfigController {

	private UserRepo repo;

	@Autowired
	public RatelimitConfigController(UserRepo repo) {
		this.repo = repo;
	}

	@ModelAttribute(name = "user")
	public User User() {
		return new User();
	}

	@ModelAttribute
	public void addUserToModel(Model model) {
		List<User> users = new ArrayList<>();
		repo.findAll().forEach(i -> users.add(i));

		model.addAttribute("users", users);

	}

	@GetMapping
	public String requestPage() {
		return "config";

	}

	@PostMapping
	public String changeConfig(@Valid User user) {

		repo.save(user);

		return "config";
	}

}
