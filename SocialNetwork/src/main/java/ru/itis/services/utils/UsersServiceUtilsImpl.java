package ru.itis.services.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.itis.exceptions.NotFoundException;
import ru.itis.models.User;
import ru.itis.repositories.UsersRepository;
import ru.itis.security.details.UserDetailsImpl;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.security.utils.RequestParsingUtilImpl;

@RequiredArgsConstructor
@Service
public class UsersServiceUtilsImpl implements UsersServiceUtils {
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final RequestParsingUtil requestParsingUtil;

    public User getUserFromContext() {
        String username = ((UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUsername();

        return usersRepository.findByUsername(username).orElseThrow();
    }


    @Override
    public User getUserFromToken(String token) {
        String username;
        if (token.startsWith(RequestParsingUtilImpl.BEARER)) {
            username = requestParsingUtil.getDataFromToken(token).get("username");
        } else {
            username = jwtUtil.parse(token).get("username");
        }

        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username \"" + username + "\" not found"));

        return user;
    }
}
