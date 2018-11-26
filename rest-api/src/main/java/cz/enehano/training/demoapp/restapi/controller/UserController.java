package cz.enehano.training.demoapp.restapi.controller;

import cz.enehano.training.demoapp.restapi.dto.UserDto;
import cz.enehano.training.demoapp.restapi.mapper.UserMapper;
import cz.enehano.training.demoapp.restapi.model.User;
import cz.enehano.training.demoapp.restapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getLastName, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(UserMapper.INSTANCE::userToUserDto).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public @ResponseBody
    UserDto getUser(@PathVariable Long id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            return UserMapper.INSTANCE.userToUserDto(byId.get());
        }
        throw new RuntimeException("User not found!");
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    UserDto createUser(@RequestBody UserDto dto) {
        User user = UserMapper.INSTANCE.userDtoToUser(dto);
        User save = userRepository.save(user);
        return UserMapper.INSTANCE.userToUserDto(save);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
    public @ResponseBody
    UserDto updateUser(@RequestBody UserDto dto, @PathVariable Long id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            User user = UserMapper.INSTANCE.userDtoToUser(dto);
            user.setId(id);
            User save = userRepository.save(user);
            return UserMapper.INSTANCE.userToUserDto(save);
        }
        throw new RuntimeException("User not found!");
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            userRepository.delete(byId.get());
            return ResponseEntity.ok().build();
        } else {
            throw new RuntimeException("User not found!");
        }
    }
}
