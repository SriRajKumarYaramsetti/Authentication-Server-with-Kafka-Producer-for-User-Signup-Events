package com.SriRaj.UserService.Services;


import com.SriRaj.UserService.Dtos.UserResponseDto;
import com.SriRaj.UserService.Dtos.ValidateResponseDto;
import com.SriRaj.UserService.Exceptions.InvalidPasswordException;
import com.SriRaj.UserService.Exceptions.UserNotFoundException;
import com.SriRaj.UserService.Models.Role;
import com.SriRaj.UserService.Models.Session;
import com.SriRaj.UserService.Models.SessionStatus;
import com.SriRaj.UserService.Models.User;
import com.SriRaj.UserService.Repository.SessionRepository;
import com.SriRaj.UserService.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserServiceImplementation implements  UserService{
    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    @Value("${jwt.secret}")
    private String secretKey;


    @Autowired
    public UserServiceImplementation(UserRepository userRepository,SessionRepository sessionRepository){
        this.userRepository=userRepository;
        this.sessionRepository=sessionRepository;

    }
    @Override
    public UserResponseDto createUser(String name, String email, String password, List<Role> roles) {

        User user=new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(roles);
        User savedUser=userRepository.save(user);

        return convertToUserResponseDto(savedUser);
    }

    @Override
    public ResponseEntity<UserResponseDto> userLogin(String email, String password) {
        Optional<User> userOptional= userRepository.findByEmail(email);
        if (userOptional.isEmpty()){
            throw new UserNotFoundException(email);
        }

        User user=userOptional.get();
        if (!password.equals(user.getPassword())){
            throw new InvalidPasswordException("Provided password is invalid");
        }
        SecretKey key= Keys.hmacShaKeyFor(secretKey.getBytes());
        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));
        jsonForJwt.put("userId",user.getId());

        String token = Jwts.builder()
                .setClaims(jsonForJwt)
                .signWith(key)
                .compact();

        Session session=new Session();
        session.setUser(user);
        session.setExpiringAt(new Date(LocalDate.now().plusDays(3).toEpochDay()));
        session.setToken(token);

        sessionRepository.save(session);
        UserResponseDto responseDto  = convertToUserResponseDto(user);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token=" + token);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");

        return new ResponseEntity<>(responseDto, headers, org.springframework.http.HttpStatus.OK);
    }

    public UserResponseDto convertToUserResponseDto(User user){
        UserResponseDto responseDto=new UserResponseDto();
        responseDto.setName(user.getName());
        responseDto.setEmail(user.getEmail());
        return responseDto;

    }

    @Override
    public ResponseEntity<Void> userLogOut(String Token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(Token);

            Long userId = claimsJws.getBody().get("userId", Long.class);
            Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(Token, userId);

            if (sessionOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Session session = sessionOptional.get();
            session.setSessionStatus(SessionStatus.ENDED);

            sessionRepository.save(session);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            // Handle token parsing or validation exceptions
            return ResponseEntity.badRequest().build();
        }


    }


    @Override
    public ResponseEntity<ValidateResponseDto> validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = claimsJws.getBody().get("userId", Long.class);
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Session session = sessionOptional.get();
        if (session.getSessionStatus() != SessionStatus.ACTIVE ) {    //|| session.getExpiringAt().before(new Date())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = session.getUser();
        ValidateResponseDto responseDto = new ValidateResponseDto();
        responseDto.setEmail(user.getEmail());
        responseDto.setUser_id(user.getId());
        responseDto.setRoles(user.getRoles());
        responseDto.setName(user.getName());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
