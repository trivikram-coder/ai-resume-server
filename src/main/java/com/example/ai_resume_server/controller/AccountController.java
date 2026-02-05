package com.example.ai_resume_server.controller;

import com.example.ai_resume_server.models.Account;
import com.example.ai_resume_server.repo.AccountRepo;
import com.example.ai_resume_server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AccountController {
    @Autowired
    private AccountRepo repo;
   @Autowired
    private AccountService service;
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Account account){
        if(repo.existsByEmail(account.getEmail())){
            return ResponseEntity.badRequest().body(Map.of("status",false,"message","Email already existed"));

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status",true,"message","Registered successfully","account",service.register(account)));
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Account account){
        if(!repo.existsByEmail(account.getEmail())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status",false,"message","Email id not exists"));
        }
        if(account.getEmail().isEmpty()||account.getPassword().isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("status",false,"message","Please provide email and password"));
        }
        boolean isValid= service.login(account);
        if(!isValid){
            return ResponseEntity.badRequest().body(Map.of("status",false,"message","Invalid credentials"));
        }
        return ResponseEntity.ok().body(Map.of("status",true,"message","Login successfully"));
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam("email") String email){
        try{
            if(!repo.existsByEmail(email)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status",false,"message","Email id not exists"));
            }
            if(email.isEmpty()){
                return ResponseEntity.badRequest().body(Map.of("status",false,"message","Email id is required"));
            }
            Account user=service.getUserByEmail(email);
            return ResponseEntity.ok(Map.of("status",true,"message","User fetched successfully","user",user));
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(Map.of("status",false,"message",e.getMessage()==null?"Server is not responding":""));
        }

    }
    @PutMapping("/edit")
    public ResponseEntity<?> updateUser(@RequestParam("email") String email,@RequestBody Account updatedAccount){
        if(!repo.existsByEmail(email)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status",false,"message","Email id not exists"));
        }
        Account account=service.updateUser(email,updatedAccount);
        return ResponseEntity.ok(Map.of("status",true,"message","User updated","updatedAccount",account));
    }
    @DeleteMapping("/remove")
    public ResponseEntity<?> deleteUser(@RequestParam("email") String email){
        if(!repo.existsByEmail(email)){
            return ResponseEntity.badRequest().body(Map.of("status",false,"message","User not exists"));
        }
        boolean deleted= service.deleteUser(email);
        if(!deleted){
            return ResponseEntity.badRequest().body(Map.of("status",false,"message","Unable to delete user"));
        }
        return ResponseEntity.ok().body(Map.of("status",true,"message","User deleted successfully"));
    }
}
