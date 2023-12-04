package com.sima.dms.validation;

import com.sima.dms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sima.dms.utils.Responses.badRequest;

@Component
public class UsernameValidations {

    private static UserRepository repository;

    @Autowired
    public UsernameValidations(UserRepository repository) {
        UsernameValidations.repository = repository;
    }

    public static void validateUsernameUniqueness(String username) {
        if (repository.existsByPersonelUserName(username.toLowerCase())) {
            if (!repository.findOneByPersonelUserName(username).get().isActive())
                throw badRequest("");
            throw badRequest("");
        }
    }

    public static void validateUsernameActive(String username) {
        if (repository.existsByPersonelUserName(username.toLowerCase())) {
            if (!repository.findOneByPersonelUserName(username).get().isActive())
                throw badRequest("");
        }
    }

    public static void validateEmailUniquenessOnModify(String newEntity, String actualEntity) {
        String newEmail = newEntity;
        String actualEmail = actualEntity;

        Boolean changedEmail = !actualEmail.equals(newEmail);
        Boolean emailAlreadyUsed = repository.existsByPersonelUserName(newEmail);

        if (changedEmail && emailAlreadyUsed) {
            throw badRequest("");
        }
    }

//    public static void validateEmailUniqueness(String email) {
//        if (repository.existsByEmail(email.toLowerCase())) {
//            if (!repository.findOneByEmail(email).get().getActive())
//                throw new EmailValidationException();
//            throw new EmailValidationException();
//        }
//    }
//
//    public static void validateEmailActive(String email) {
//        if (repository.existsByEmail(email.toLowerCase())) {
//            if (!repository.findOneByEmail(email).get().getActive())
//                throw new EmailValidationException();
//        }
//    }

}
