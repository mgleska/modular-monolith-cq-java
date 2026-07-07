package com.mgleska.mmcqjava2.user.support;

import com.mgleska.mmcqjava2.shared.exception.AppAuthException;
import com.mgleska.mmcqjava2.user.model.User;
import com.mgleska.mmcqjava2.user.model.UserRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserTokenService {

    private final SecureRandom random = new SecureRandom();

    private final UserRepository userRepository;

    public UserTokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(int userId) {
        var bytes = new byte[32];
        random.nextBytes(bytes);

        return userId + "-" + Base64.getEncoder().encodeToString(bytes);
    }

    public @NonNull User validateToken(String token) {
        var parts = token.split("-");
        if (parts.length != 2) {
            throw new AppAuthException("User token is invalid.");
        }

        var user = userRepository.findById(NumberUtils.toInt(parts[0], -1)).orElse(null);
        if (user == null || ! user.getToken().equals(token)) {
            throw new AppAuthException("Invalid user token.");
        }

        return user;
    }
}
