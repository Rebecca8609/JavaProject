package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.model.UserInfo;
import com.example.demo.model.UserInfoRepository;

import java.util.Optional;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    // 根據ID查找用戶
    public Optional<UserInfo> findById(Integer userId) {
        return userInfoRepository.findById(userId);
    }

    // 保存用戶信息
    public UserInfo saveUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    // 更新用戶信息
    public UserInfo updateUserInfo(Integer userId, UserInfo updatedUserInfo) {
        return userInfoRepository.findById(userId)
                .map(userInfo -> {
                    userInfo.setUserName(updatedUserInfo.getUserName());
                    userInfo.setUserEmail(updatedUserInfo.getUserEmail());
                    userInfo.setUserTel(updatedUserInfo.getUserTel());
                    userInfo.setUserBirthday(updatedUserInfo.getUserBirthday());
                    userInfo.setUserIdNumber(updatedUserInfo.getUserIdNumber());
                    userInfo.setUserStatus(updatedUserInfo.getUserStatus());
                    return userInfoRepository.save(userInfo);
                })
                .orElseThrow(() -> new IllegalArgumentException("無效的用戶ID"));
    }

    // 刪除用戶
    public void deleteUserById(Integer userId) {
        if (userInfoRepository.existsById(userId)) {
            userInfoRepository.deleteById(userId);
        } else {
            throw new IllegalArgumentException("無效的用戶ID");
        }
    }
}
