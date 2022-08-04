package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Result;
import com.quickpaas.shop.config.jwt.JwtTokenProvider;
import com.quickpaas.shop.config.jwt.JwtUserDetails;
import com.quickpaas.shop.config.jwt.JwtUserDetailsService;
import com.quickpaas.shop.config.jwt.JwtRequest;
import com.quickpaas.shop.config.jwt.JwtResponse;
import com.quickpaas.shop.system.domain.SysUser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/account")
@Api(value = "项目")
public class LoginController {


    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public LoginController() {
    }

    @PostMapping("login")
    public Result<JwtResponse> login(@RequestBody JwtRequest request) throws Exception {
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getUsername());
        final String token = jwtTokenProvider.generateToken(userDetails);

        return Result.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("logout")
    public Result logout() {
        Result result = Result.ok();
        return result;
    }

    @GetMapping("currentUser")
    public Result<SysUser> currentUser() {
        SysUser user = new SysUser();
        Object details  = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(details != null && details instanceof JwtUserDetails) {
            JwtUserDetails jwtDetails = (JwtUserDetails)details;
            user.setAccount(jwtDetails.getUsername());
            user.setName(jwtDetails.getName());
            return Result.one(user);
        }else {
            return Result.error();
        }

    }
}
