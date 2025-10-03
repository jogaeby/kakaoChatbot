package com.chatbot.base.domain.user.service.impl;

import com.chatbot.base.domain.user.dto.UserDto;
import com.chatbot.base.domain.user.entity.User;
import com.chatbot.base.domain.user.repository.UserRepository;
import com.chatbot.base.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto join(String channelName, String userKey, String name, String phone, String address, boolean addressDefault, boolean privacyAgreed) {
        Optional<UserDto> maybeUser = isUser(userKey);

        if (maybeUser.isPresent()) {
            throw new DuplicateKeyException("중복회원입니다.");
        }


        User user = User.create(channelName, userKey, name, phone, address, addressDefault,true);
        User save = userRepository.save(user);

        return save.toDto();
    }

    @Override
    public Optional<UserDto> isUser(String userKey) {
        return userRepository.findByUserKey(userKey)
                .map(User::toDto);
    }

    @Transactional
    @Override
    public UserDto modifyAddress(UserDto userDto, String address) {
        String userKey = userDto.getUserKey();

        User user = userRepository.findByUserKey(userKey).get();
        user.modifyDefaultAddress(address);

        return user.toDto();
    }
}
