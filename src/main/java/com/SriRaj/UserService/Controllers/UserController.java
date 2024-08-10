package com.SriRaj.UserService.Controllers;


import com.SriRaj.UserService.Dtos.LoginRequestDto;
import com.SriRaj.UserService.Dtos.SignUpRequestDto;
import com.SriRaj.UserService.Dtos.UserResponseDto;
import com.SriRaj.UserService.Dtos.ValidateResponseDto;
import com.SriRaj.UserService.Services.UserServiceImplementation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserServiceImplementation userService;


    @Autowired
    public UserController(UserServiceImplementation userService){
        this.userService=userService;

    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody SignUpRequestDto request){
        UserResponseDto userResponse=userService.createUser(request.getName(), request.getEmail(), request.getPassword(), request.getRoles());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }


    @PostMapping("/login")
    public  ResponseEntity<UserResponseDto> userLogin(@RequestBody LoginRequestDto request){
        ResponseEntity< UserResponseDto> LoginresponseDto =userService.userLogin(request.getEmail(), request.getPassword());
        return LoginresponseDto;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> userLogout(HttpServletRequest request){
        String token=null;
        //check the cookies for auth_token

        if(request.getCookies()!=null){
            for(Cookie cookie: request.getCookies()){
                if ("auth_token".equals(cookie.getName())){
                    token=cookie.getValue();
                    break;
                }
            }
        }

        if(token==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return userService.userLogOut(token);
    }


    @PostMapping("/validate")

    public ResponseEntity<ValidateResponseDto> ValidateToken(HttpServletRequest request) {
        String token = null;
        //check the cookies for auth_token

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  userService.validateToken(token);

    }

}
