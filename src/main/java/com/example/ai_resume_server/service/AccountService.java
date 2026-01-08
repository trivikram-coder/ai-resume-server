package com.example.ai_resume_server.service;

import com.example.ai_resume_server.config.SecurityConfig;
import com.example.ai_resume_server.models.Account;
import com.example.ai_resume_server.repo.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepo repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account register(Account account){
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return repo.save(account);
    }
    public boolean login(Account account){
        Account details=repo.findByEmail(account.getEmail());
        boolean validEmail=details.getEmail().equals(account.getEmail());
        String hashedPass=details.getPassword();
        boolean validPass=passwordEncoder.matches(account.getPassword(),hashedPass);
        return validEmail&&validPass;
    }
    public Account getUserByEmail(String email){
        return repo.findByEmail(email);
    }
    public Account updateUser(String emai,Account updatedAccount){
        Account acc=repo.findByEmail(emai);
        if(updatedAccount.getEmail()!=null){

        acc.setEmail(updatedAccount.getEmail());
        }
        if(updatedAccount.getUserName()!=null){

        acc.setUserName(updatedAccount.getUserName());
        }
        if(updatedAccount.getPassword()!=null){
            acc.setPassword(updatedAccount.getPassword());
        }
        repo.save(acc);
        return acc;
    }
    public boolean deleteUser(String email){
        return repo.deleteByEmail(email);
    }
}
