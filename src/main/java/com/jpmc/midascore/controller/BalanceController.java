package com.jpmc.midascore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

@RestController
@RequestMapping("/balance")
public class BalanceController {

	private final UserRepository userRepository;

	@Autowired
	public BalanceController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping
	public Balance getBalance(@RequestParam Long userId) {
        UserRecord user = userRepository.findById(userId).orElse(null);
        float amount = (user != null) ? user.getBalance() : 0f;
        return new Balance(amount);
    }
}
