package com.chatbot.base.domain.user.service.impl;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.domain.product.dto.ProductDto;
import com.chatbot.base.domain.user.dto.UserDto;
import com.chatbot.base.domain.user.entity.User;
import com.chatbot.base.domain.user.repository.UserRepository;
import com.chatbot.base.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final GoogleSheetUtil googleSheetUtil;

    private final String SHEET_ID = "12LK-mODVa9b5b8KA_m68GUF50AojwdOK7_0cok3inFM";

    @Transactional
    @Override
    public UserDto join(String channelName, String channelId, String userKey, String name, String phone, String address, boolean addressDefault, boolean privacyAgreed) {
        Optional<UserDto> maybeUser = isUser(userKey);

        if (maybeUser.isPresent()) {
            throw new DuplicateKeyException("중복회원입니다.");
        }


        User user = User.create(channelName, channelId, userKey, name, phone, address, addressDefault,true);
        User save = userRepository.save(user);

        return save.toDto();
    }

    @Override
    public Optional<UserDto> isUser(String userKey) {
        return userRepository.findByUserKey(userKey)
                .map(User::toDto);
    }

    @Override
    public Optional<UserDto> isBlackUser(String userKey) {
        try {
            List<List<Object>> blackList = googleSheetUtil.readAllSheet(SHEET_ID, "블랙리스트");
            Optional<List<Object>> blackUser = blackList.stream()
                    .filter(row -> row.get(0).equals(userKey))
                    .findFirst();

            if (blackUser.isEmpty()) {
                return Optional.empty();
            }
            return userRepository.findByUserKey(userKey)
                    .map(User::toDto);
        }catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public UserDto modifyAddress(UserDto userDto, String address) {
        String userKey = userDto.getUserKey();

        User user = userRepository.findByUserKey(userKey).get();
        user.modifyDefaultAddress(address);

        return user.toDto();
    }

    @Transactional
    @Override
    public UserDto modifyName(UserDto userDto, String name) {
        String userKey = userDto.getUserKey();

        User user = userRepository.findByUserKey(userKey).get();
        user.modifyName(name);

        return user.toDto();
    }

    @Transactional
    @Override
    public UserDto modifyPhone(UserDto userDto, String phone) {
        String userKey = userDto.getUserKey();

        User user = userRepository.findByUserKey(userKey).get();
        user.modifyPhone(phone);

        return user.toDto();
    }

    @Transactional
    @Override
    public UserDto saveProductToCart(String userKey, ProductDto productDto) {
        User user = userRepository.findByUserKey(userKey).get();

        user.addProductToCart(productDto);

        return user.toDto();
    }
}
