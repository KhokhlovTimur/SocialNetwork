package ru.itis.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.annotations.TokenValid;
import ru.itis.controllers.rest.api.ApiController;
import ru.itis.controllers.rest.api.UsersGroupsApi;
import ru.itis.dto.group.GroupDto;
import ru.itis.services.UsersGroupsService;

@RestController
@RequiredArgsConstructor
public class UsersGroupRestController implements UsersGroupsApi {
    private final UsersGroupsService usersGroupsService;

    public ResponseEntity<?> deleteUserFromGroup(Long userId, Long groupId) {
        usersGroupsService.deleteUserFromGroup(userId, groupId);
        return ResponseEntity.accepted()
                .build();
    }

    @Override
    @TokenValid
    public ResponseEntity<GroupDto> addGroupToUser(Long userId, Long groupId, String rawToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usersGroupsService.addGroupToUser(userId, groupId));
    }

}
