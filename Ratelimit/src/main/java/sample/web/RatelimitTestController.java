package sample.web;

import java.text.DecimalFormat;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ratelimit.api.RateLimiter;
import ratelimit.api.RateLimiter.ExceedsLimitException;
import sample.entity.User;
import sample.repo.UserRepo;

@Controller
@RequestMapping("/test")

public class RatelimitTestController {

	private UserRepo repo;
	DecimalFormat df = new DecimalFormat("0.00");
	private RateLimiter rateLimiter;
	private long startTime = System.currentTimeMillis();

	@Autowired
	public RatelimitTestController(UserRepo repo) {
		this.repo = repo;

	}

	@ModelAttribute(name = "user")
	public User User() {
		return new User();
	}

	@GetMapping("/anonymous")
	public String anonymous(Model model) {

		return requestPage(model, "anonymous");
	}

	@GetMapping("/regular")
	public String regular(Model model) {
		return requestPage(model, "regular");
	}

	@GetMapping("/vip")
	public String vip(Model model) {
		return requestPage(model, "vip");
	}

	public String requestPage(Model model, String userId) {

		User user = repo.findById(userId).get();
		model.addAttribute("userId", userId);
		rateLimiter = RateLimiter.getRateLimiter(user.getId(), user.getRequest(), user.getRequest(), false);

		model.addAttribute("license", user.getId());
		model.addAttribute("request", user.getRequest());
		model.addAttribute("seconds", user.getSeconds());

		model.addAttribute("alive", rateLimiter.requestAlive());
		model.addAttribute("targetTPS", df.format(user.getRequest() / (float) user.getSeconds()));
		model.addAttribute("realTPS", df.format(rateLimiter.requestAlive() / (float) user.getSeconds()));
		model.addAttribute("overallTPS",
				df.format(rateLimiter.requestTotal() * 1000 / (float) (System.currentTimeMillis() - startTime)));
		try {
			rateLimiter.processRequest(() -> {
				// TODO: Add actual code to process the request with the RateLimiter

			});
		} catch (ExceedsLimitException e) {
			model.addAttribute("exceeded", true);
		}

		return "test";
	}

	@PostMapping("/restart")
	public String restart(@Valid User u) {

		User user = repo.findById(u.getId()).get();
		startTime = System.currentTimeMillis();
		rateLimiter = RateLimiter.getRateLimiter(user.getId(), user.getRequest(), user.getRequest(), true);
		return "redirect:/test/" + u.getId();

	}

}
