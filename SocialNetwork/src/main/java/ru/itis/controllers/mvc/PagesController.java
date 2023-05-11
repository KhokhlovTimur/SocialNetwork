package ru.itis.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itis.services.groups.GroupsService;
import ru.itis.services.posts.PostsService;
import ru.itis.services.users.FriendsService;
import ru.itis.services.users.UsersService;

import static ru.itis.security.utils.RequestParsingUtilImpl.AUTHORIZATION_COOKIE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/app")
public class PagesController {
    private final UsersService usersService;
    private final FriendsService friendsService;

    @GetMapping("/login")
    public String getAuthPage() {
        return "authenticationPage";
    }

    @GetMapping("/feeds")
    public String getFeedsPage(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        model.addAttribute("posts", usersService.getPostsFromGroups(token));
        return "feeds";
    }

    @GetMapping("/groups")
    public String getGroupsPage(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        model.addAttribute("groups", usersService.getGroups(token));
        return "groups";
    }

    @GetMapping("/friends")
    public String getFriendsPage(Model model, @CookieValue(AUTHORIZATION_COOKIE) String token) {
        model.addAttribute("friends", friendsService.getFriends(token));
        return "friends";
    }
}
