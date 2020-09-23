package com.citi.olympus.permcomparator.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.citi.olympus.permcomparator.model.AccessToken;
import com.citi.olympus.permcomparator.model.PermissionResponse;
import com.citi.olympus.permcomparator.model.User;
import com.citi.olympus.permcomparator.repo.AccessTokenRepo;
import com.citi.olympus.permcomparator.service.PermissionSetService;
import com.citi.olympus.permcomparator.service.ProfilePermissionSet;
import com.citi.olympus.permcomparator.service.UserService;
import com.citi.olympus.permcomparator.utils.Utilities;

@Controller
public class ComparatorController {

	public static String COLL_HOME = "pages/home.html";
	public static String unauthorized = "pages/unauthorized.html";

	@Autowired
	AccessTokenRepo accessRepo;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	SecurityContextRepository securityContextRepository;

	@Autowired
	PermissionSetService permissionSetService;

	@Autowired
	ProfilePermissionSet profilePermissionSet; 
	
	@Value("${permissionSet.user.query}")
	private String userQuery;

	@Autowired
	private UserService userService;

	/*
	 * private static String code; private static String state;
	 */
	@GetMapping("/")
	// @PreAuthorize("hasAuthority('ADMIN')")
	public ModelAndView ping(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		ModelAndView view = new ModelAndView();
		view.setViewName(COLL_HOME);

		User toRet = userService.getUser(request, response, session);

		view.addObject("photo", toRet.getPhotos().getPicture());
		view.addObject("userName", toRet.getDisplay_name());
		view.addObject("userId", toRet.getUser_id());

		//String usersObj = permissionSetService.getUserProfiles(session, userAll);
		//String permissionObj = permissionSetService.getUserProfiles(session, permissionSetAll);
		//String profileObj = permissionSetService.getUserProfiles(session, profileAll);
		
		String usersObj = permissionSetService.query(toRet.getUser_id(), "query.userList",  "",null);
		String permissionObj = permissionSetService.query(toRet.getUser_id(), "query.permissionSetList",  "",null);
		String profileObj = permissionSetService.query(toRet.getUser_id(), "query.pofileList",  "",null);

		JSONObject users_obj = (JSONObject) Utilities.parseStringToObject(usersObj);
		JSONObject permission_obj = (JSONObject) Utilities.parseStringToObject(permissionObj);
		JSONObject profile_obj = (JSONObject) Utilities.parseStringToObject(profileObj);

		/*
		 * System.out.println("usersObj: " + usersObj);
		 * System.out.println("permissionObj: " + permissionObj);
		 * System.out.println("profileObj: " + profileObj);
		 */		
		
		view.addObject("usersObj", users_obj);
		view.addObject("permissionObj", permission_obj);
		view.addObject("profileObj", profile_obj);

		
		return view;
	}

	@RequestMapping(value = "/permcomparator/callback")
	public String registration(@RequestParam("code") String code, @RequestParam("state") String state,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		System.out.println("Inside permcomparator/callback");
		session.setAttribute("code", code);
		session.setAttribute("state", state);
		AccessToken token = userService.getAccessToekn(code, state, request, response, session);
		
		System.out.println("=================token====================");
		System.out.println(token);
		System.out.println("=================token====================");
		
		String auth_token = token.getToken_type() + " " + token.getAccess_token();
		session.setAttribute("auth_token", auth_token);
		session.setAttribute("AccessToken", token);
		String user_url = token.getId();
		String[] userArr = token.getId().split("/");
		String user_id = userArr[userArr.length - 1];
		session.setAttribute("user_id", user_id);

		System.out.println("before accessRepo.findById");
		Optional<AccessToken> tkn = accessRepo.findById(user_id);
		if (tkn.isPresent()) {
			AccessToken tkn1 = tkn.get();
			tkn1.setAccess_token(token.getAccess_token());
			tkn1.setInstance_url(token.getInstance_url());
			tkn1.setIssued_at(token.getIssued_at());
			tkn1.setScope(token.getScope());
			tkn1.setSignature(token.getSignature());
			tkn1.setToken_type(token.getToken_type());
			//tkn1.setId(user_id);
			tkn1.setId_token(user_url);
			accessRepo.save(tkn1);
			System.out.println("Inside if tkn.isPresent");
		} else {
			System.out.println("Inside else at begning tkn.isPresent");
			token.setId(user_id);
			token.setId_token(user_url);
			accessRepo.save(token);
			System.out.println("====================== All Accesstoken =======================");
			System.out.println(accessRepo.findAll());
			System.out.println("====================== AccessToken by Id =======================");
			
			System.out.println(accessRepo.findById(user_id));
			System.out.println("Inside else at last tkn.isPresent");
		}
		System.out.println("After if accessRepo.findById");
		Authentication result = this.authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user_id, ""));
		SecurityContextHolder.getContext().setAuthentication(result);
		this.securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
		return "redirect:/";
	}

	/*
	 * @GetMapping("/users") public ModelAndView profiles(HttpSession session) {
	 * 
	 * ModelAndView view = new ModelAndView(); view.setViewName("/pages/users");
	 * 
	 * String usersObj = permissionSetService.getUserProfiles(session, userAll);
	 * String permissionObj = permissionSetService.getUserProfiles(session,
	 * permissionSetAll); String profileObj =
	 * permissionSetService.getUserProfiles(session, profileAll);
	 * 
	 * JSONObject users_obj = (JSONObject) Utilities.parseStringToObject(usersObj);
	 * JSONObject permission_obj = (JSONObject)
	 * Utilities.parseStringToObject(permissionObj); JSONObject profile_obj =
	 * (JSONObject) Utilities.parseStringToObject(profileObj);
	 * 
	 * 
	 * 
	 * view.addObject("usersObj", users_obj); view.addObject("permissionObj",
	 * permission_obj); view.addObject("profileObj", profile_obj);
	 * 
	 * return view; }
	 */
	
	@GetMapping("/query/{userId}/{Id}/{type}")
	@ResponseBody
	public ResponseEntity<PermissionResponse> query(@PathVariable(name = "userId") String userId, @PathVariable(name = "Id") String Id,
			@PathVariable(name = "type") String type) throws ParseException {

		PermissionResponse toRet = new PermissionResponse();
		String type_objPerm = type.concat(".ObjectPerm");
		String type_userPerm = type.concat(".UserPerm");
		String type_setupPerm = type.concat(".SetupEntity");
		
		String objectPerm = permissionSetService.query(userId, type_objPerm, Id, null);
		String userPerm = permissionSetService.query(userId, type_userPerm, Id, null);
		String setupEntityPerm = permissionSetService.querySetupEntity(userId, type_setupPerm, Id);
		
	
		toRet.setObjectPermission(objectPerm);
		toRet.setUserPermission(userPerm);
		toRet.setSetupEntityPermission(setupEntityPerm);
		
		//String fields = profilePermissionSet.getPermissionFields(userId);
	
		return new ResponseEntity<PermissionResponse>(toRet, HttpStatus.OK);
	}
	
	@GetMapping("/login")
	public ModelAndView login() {
		ModelAndView view = new ModelAndView();
		view.setViewName("pages/login");
		return view;
	}
	
	@GetMapping("/logout")
	public ModelAndView logout() {
		System.out.println("Inside Logout");
		ModelAndView view = new ModelAndView();
		view.setViewName("pages/login");
		userService.revokeAccessToken("0052w000005MZbdAAG");
		return view;
	}
}
