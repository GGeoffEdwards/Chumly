package controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import data.InterestDAO;
import data.LocationDAO;
import data.MessageDAO;
import data.UserDAO;
import entities.Interest;
import entities.Location;
import entities.Message;
import entities.Profile;
import entities.User;
import entities.User.Role;

@Controller
@SessionAttributes(names = { "sessionUser" })
public class UserController {

	@Autowired
	UserDAO udao;

	@Autowired
	MessageDAO mdao;

	@Autowired
	InterestDAO idao;

	@Autowired
	LocationDAO ldao;

	/*
	private static String VIEW_ADMIN_HOME     = "adminhome";
	private static String VIEW_CREATE_PROFILE = "createprofile";
	private static String VIEW_INDEX          = "index";
	private static String VIEW_MESSAGE        = "message";
	private static String VIEW_NEW_USER       = "newuser";
	private static String VIEW_OTHER_USER     = "otheruser";
	private static String VIEW_PROFILE        = "profile";
	private static String VIEW_RESULTS        = "results";
	private static String VIEW_UPDATE_PROFILE = "updateprofile";
	 */
	private static String VIEW_ADMIN_HOME     = "adminhome";
	private static String VIEW_CREATE_PROFILE = "createprofile";
	private static String VIEW_INDEX          = "index";
	private static String VIEW_MESSAGE        = "message";
	private static String VIEW_NEW_USER       = "newuser";
	private static String VIEW_OTHER_USER     = "otheruser";
	private static String VIEW_PROFILE        = "profile";
	private static String VIEW_RESULTS        = "results";
	private static String VIEW_UPDATE_PROFILE = "updateprofile";


	@ModelAttribute(name = "sessionUser")
	public User sessionUserFactory() {
		return new User();
	}

	@ModelAttribute(name = "user")
	public User defaultUserFactory() {
		return new User();
	}

	@ModelAttribute(name = "profile")
	public Profile defaultProfileFactory() {
		return new Profile();
	}

	@RequestMapping(path = "home.do", method = RequestMethod.GET)
	public String welcome(Model model) {

		if(model.containsAttribute("sessionUser")) {
			User u = (User) model.asMap().get("sessionUser");
			if(u != null && u.getUsername() != null) {
				return VIEW_PROFILE;
			}
		}
		return VIEW_INDEX;
	}

	@RequestMapping(method = RequestMethod.GET, path = "logout.do")
	public String logout(Model model) {
		model.asMap().remove("sessionUser");
		return VIEW_INDEX;
	}
	@RequestMapping(method = RequestMethod.GET, path = "admin.do")
	public String adminHome(Model model) {
		model.addAttribute("categories", idao.indexCategories());
		model.addAttribute("users", udao.index());
		
		return VIEW_ADMIN_HOME;
	}

	@RequestMapping(method = RequestMethod.POST, path = "login.do")
	public String login(@Valid User user, Errors errors, Model model) {
		if (errors.hasErrors()) {
			return VIEW_INDEX;
		}

		User u = udao.getUserByUsername(user.getUsername());
		if (u != null) {
			if (u.getPassword().equals(user.getPassword())) {
				if (u.getRole() == Role.ADMIN) {
					model.addAttribute("sessionUser", u);
					model.addAttribute("categories", idao.indexCategories());
					model.addAttribute("users", udao.index());
					return VIEW_ADMIN_HOME;
				}
				model.addAttribute("sessionUser", u);
				return VIEW_PROFILE;
			} else {
				errors.rejectValue("password", "error.password",
						"Invalid password");
				// bad password
				return VIEW_INDEX;
			}
		}
		else {
			// bad username
			errors.rejectValue("username", "error.username",
					"The username you entered is not associated with an account, please try another");
			return VIEW_INDEX;
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "getUpdateProfile.do")
	public String getUpdateProfile(Model model) {
		List<Interest> interests = idao.index();
		model.addAttribute("interests", interests);
		return VIEW_UPDATE_PROFILE;
	}

	@RequestMapping(method = RequestMethod.GET, path = "getUsersByInterest.do")
	public String searchByInterest(String interest, Model model) {

		List<User> users = udao.indexByInterest(interest);

		model.addAttribute("interest", interest);
		model.addAttribute("users", users);

		return VIEW_RESULTS;
	}

	@RequestMapping(method = RequestMethod.POST, path = "deleteProfile.do")
	public String deleteProfile(Model model) {
		User sessionUser = (User) model.asMap().get("sessionUser");
		udao.destroy(sessionUser.getId());
		return VIEW_INDEX;
	}

	@RequestMapping(method = RequestMethod.GET, path = "getOtherUserProfileInformation.do")
	public String getUpdateProfile(Integer id, Model model) {
		User user = udao.show(id);
		model.addAttribute("user", user);
		return VIEW_OTHER_USER;
	}

	@RequestMapping(method = RequestMethod.GET, path = "messageUser.do")
	public String getUserMessage(Integer id, Model model) {

		User recipient = udao.show(id);
		User sessionUser = (User) model.asMap().get("sessionUser");

		List<Message> messages = null;
		messages = mdao.indexByConversation(recipient, sessionUser);

		model.addAttribute("sender", sessionUser);
		model.addAttribute("recipient", recipient);
		model.addAttribute("messages", messages);

		return VIEW_MESSAGE;
	}

	@RequestMapping(method = RequestMethod.POST, path = "updateProfileDescription.do")
	public String updateProfileDescription(String description, Integer id, Model model) {
		User sessionUser = udao.updateUserProfileDescription(description, id);
		model.addAttribute("sessionUser", sessionUser);

		return VIEW_PROFILE;
	}

	@RequestMapping(method = RequestMethod.POST, path = "addMessage.do")
	public String addMessage(Integer sessionId, Integer recipientId, String message, Model model) {
		User sessionUser = udao.show(sessionId);
		User recipient = udao.show(recipientId);

		Message newMessage = new Message();

		if (newMessage.getRecipients() == null) {
			List<User> recipients = new ArrayList<>();
			newMessage.setRecipients(recipients);
		}

		newMessage.getRecipients().add(recipient);
		newMessage.setSender(sessionUser);
		newMessage.setText(message);
		newMessage.setTimeStamp(new Date(new java.util.Date().toInstant().toEpochMilli()));

		newMessage = mdao.create(newMessage);

		List<Message> messages = mdao.indexByConversation(recipient, sessionUser);

		model.addAttribute("sender", sessionUser);
		model.addAttribute("recipient", recipient);
		model.addAttribute("messages", messages);

		return VIEW_MESSAGE;
	}

	@RequestMapping(method = RequestMethod.GET, path = "connectToUser.do")
	public String addInterest(Integer userId, Integer sessionId, Model model) {
		User sessionUser = udao.show(sessionId);
		User friend = udao.show(userId);
		List<User> connections = null;

		if (sessionUser.getConnections().isEmpty()) {
			connections = new ArrayList<>();
			sessionUser.setConnections(connections);
		}

		sessionUser.getConnections().add(friend);
		friend.getConnections().add(sessionUser);
		udao.updateConnection(sessionId, sessionUser);
		udao.updateConnection(userId, friend);

		model.addAttribute("user", friend);

		return VIEW_OTHER_USER;
	}

	@RequestMapping(method = RequestMethod.GET, path = "createUser.do")
	public String createUser(Model model) {
		return VIEW_NEW_USER;
	}

	@RequestMapping(method = RequestMethod.POST, path = "makeUser.do")
	public String makeUser(User user, Model model) {
		Profile profile = new Profile();
		User sessionUser = udao.create(user);
		sessionUser.setProfile(profile);
		
		model.addAttribute("locations", ldao.index());
		model.addAttribute("location", ldao.mapByState());
		model.addAttribute("sessionUser", sessionUser);

		return VIEW_CREATE_PROFILE;
	}

	@RequestMapping(method = RequestMethod.POST, path = "updateProfile.do")
	public String updateProfile(Integer id, Profile profile, Model model, Integer locationId) {
		System.out.println("locationId: " + locationId);
		Location location = ldao.show(locationId);	
		profile.setLocation(location);
		User sessionUser = udao.show(id);
		sessionUser.setProfile(profile);
		sessionUser = udao.updateUserProfile(sessionUser.getId(), sessionUser);

		model.addAttribute("sessionUser", sessionUser);

		return VIEW_PROFILE;
	}

	@RequestMapping(method = RequestMethod.GET, path = "searchInterest.do")
	public String searchInterest(String name, Model model) {

		List<Interest> interests = idao.indexByContainsText(name);
		model.addAttribute("interests", interests);

		return VIEW_PROFILE;
	}

	@RequestMapping(method = RequestMethod.POST, path = "addInterest.do")
	public String addInterest(Integer id, Model model, Integer userId) {
		User sessionUser = udao.show(userId);
		Interest interest = idao.show(id);
		sessionUser.getInterests().add(interest);
		sessionUser = udao.updateInterest(userId, sessionUser);
		model.addAttribute("sessionUser", sessionUser);

		return VIEW_PROFILE;
	}

	@RequestMapping(method = RequestMethod.POST, path = "deleteUser.do")
	public String deleteUser(Integer id, Integer sessionId, Model model) {
		udao.destroy(id);
		if (sessionId != null) {
			model.addAttribute("categories", idao.indexCategories());
			model.addAttribute("users", udao.index());
			return "adminhome";
		}
		return VIEW_INDEX;
	}

	@RequestMapping(method = RequestMethod.POST, path = "createInterest.do")
	public String createInterest(Integer id, String interest, Model model) {
		idao.create(id, interest);

		model.addAttribute("categories", idao.indexCategories());
		model.addAttribute("users", udao.index());
		return VIEW_ADMIN_HOME;
	}
}
