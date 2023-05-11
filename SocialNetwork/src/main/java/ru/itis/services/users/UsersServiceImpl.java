package ru.itis.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itis.dto.group.GroupDto;
import ru.itis.dto.group.GroupsPage;
import ru.itis.dto.posts.PostDto;
import ru.itis.dto.user.*;
import ru.itis.exceptions.NotFoundException;
import ru.itis.mappers.groups.GroupCollectionsMapper;
import ru.itis.mappers.posts.PostsMapper;
import ru.itis.mappers.users.UsersMapper;
import ru.itis.models.Group;
import ru.itis.models.User;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.services.utils.UsersServiceUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final GroupCollectionsMapper groupCollectionsMapper;
    private final RequestParsingUtil requestParsingUtil;
    private final JwtUtil jwtUtil;
    private final UsersServiceUtils usersServiceUtils;
    private final PostsMapper postsMapper;
    private final String ADMIN = "SUPER_ADMIN";

    @Override
    public <T extends PublicUserDto> T getById(Long id, String token) {
        Map<String, String> data = requestParsingUtil.getDataFromToken(token);
        User user = getOrThrow(id);

        if (data.get("username").equals(user.getUsername()) || data.get("role").equals(ADMIN)) {
            return (T) usersMapper.toPrivateDto(user);
        }
        return (T) usersMapper.toPublicDto(user);
    }

    @Override
    public User findByUsername(String username) {
        return getOrThrow(username);
    }

    @Override
    public Set<PostDto> getPostsFromGroups(String token) {
        User user = usersServiceUtils.getUserFromToken(token);
        Set<Group> groups = user.getGroups();
        Set<PostDto> posts = new HashSet<>();
        groups.forEach(x ->
                x.getPosts()
                        .forEach(y -> posts.add(postsMapper.toDto(y)))
        );

        return posts;
    }

    @Override
    public User findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    public PrivateUserDto signUp(UserSignUpDto form) {
        User user = usersMapper.toUser(usersMapper.toDto(form));

        user.setRole(User.Role.AUTHORIZED);
        user.setState(User.State.ACTIVE);
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        return usersMapper.toPrivateDto(usersRepository.save(user));
    }

    @Override
    public void banUser(Long id) {
        User user = getOrThrow(id);
        user.setState(User.State.BANNED);

        usersRepository.save(user);
    }

    @Override
    public PrivateUserDto update(Long id, UserUpdateDto userDto) {
        User user = getOrThrow(id);

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setSurname(userDto.getSurname());
        user.setAge(userDto.getAge());
        user.setBio(userDto.getBio());
        user.setAvatarLink(userDto.getImageLink());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setName(userDto.getName());

        usersRepository.save(user);

        return usersMapper.toPrivateDto(user);
    }

    @Override
    public Set<GroupDto> getGroups(String token) {
        Set<Group> groups = usersServiceUtils.getUserFromToken(token).getGroups();
        System.out.println(groups);
        return groupCollectionsMapper.toGroupDtoSet(groups);
    }

    @Override
    public GroupsPage getGroups(Long userId) {
        Set<Group> groups = getOrThrow(userId).getGroups();

        return GroupsPage.builder()
                .groups(groupCollectionsMapper.toGroupDtoSet(groups))
                .totalCount(groups.size())
                .build();
    }

    private User getOrThrow(Long id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id <" + id + "> not found"));

        if (user.isBanned()) {
            throw new NotFoundException("User with id <" + id + "> is banned");
        }

        return user;
    }

    private User getOrThrow(String username) {
        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username <" + username + "> not found"));

        if (user.isBanned()) {
            throw new NotFoundException("User with username <" + username + "> is banned");
        }

        return user;
    }

}
