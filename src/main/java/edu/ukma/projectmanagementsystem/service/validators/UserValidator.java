package edu.ukma.projectmanagementsystem.service.validators;

import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.web.exception.EmailDuplicateException;
import edu.ukma.projectmanagementsystem.web.exception.UsernameDuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateForDuplicateEmail(Long id, String email) {
        userRepository.findByEmail(email).filter(user -> !user.getId().equals(id)).ifPresent(this::throwEmailDuplicateException);
    }

    public void validateForDuplicateUsername(Long id, String username) {
        userRepository.findByUsername(username).filter(user -> !user.getId().equals(id)).ifPresent(this::throwUsernameDuplicateException);
    }

    private void throwEmailDuplicateException(UserEntity user) {
        throw new EmailDuplicateException();
    }

    private void throwUsernameDuplicateException(UserEntity user) {
        throw new UsernameDuplicateException();
    }
}
