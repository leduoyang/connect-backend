package com.connect.data.repository.impl;

import com.connect.common.exception.ConnectDataException;
import com.connect.common.exception.ConnectErrorCode;
import com.connect.data.dao.IUserDao;
import com.connect.data.entity.Profile;
import com.connect.data.entity.User;
import com.connect.data.param.QueryUserParam;
import com.connect.data.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserRepositoryImpl implements IUserRepository {

    @Autowired
    IUserDao userDao;

    public UserRepositoryImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    public boolean authenticateRootUser(String userId, String password) {
        return userDao.authenticateRootUser(userId, password);
    }

    public User signIn(String userId) {
        User user = userDao.signIn(userId);

        if (user == null) {
            throw new ConnectDataException(
                    ConnectErrorCode.USER_NOT_EXISTED_EXCEPTION,
                    String.format("SignIn Failed as userId not exited.", userId)
            );
        }

        return user;
    }

    public void signUp(User user) {
        boolean existed = userDao.userExistingWithEmail(user.getUserId(), user.getEmail());
        if (existed) {
            throw new ConnectDataException(
                    ConnectErrorCode.USER_EXISTED_EXCEPTION,
                    String.format("UserId %s or email %s has exited.", user.getUserId(), user.getEmail())
            );
        }

        log.info("payload for creating user - " + user);
        int affected = userDao.signUp(user);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.USER_CREATE_EXCEPTION);
        }
    }

    public void editUser(String requesterId, User user) {
        User targetUser = userDao.internalQueryUserByUserId(requesterId);
        log.info("targetUser for updating - " + targetUser);
        if (targetUser == null) {
            throw new ConnectDataException(
                    ConnectErrorCode.USER_NOT_EXISTED_EXCEPTION,
                    String.format("User %s not exited.", requesterId)
            );
        }
        user.setId(targetUser.getId());
        log.info("payload for updating user - " + user);

        int affected = userDao.editUser(user);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.USER_EDIT_EXCEPTION);
        }
    }

    public void editUserProfile(String requesterId, Profile profile) {
        User targetUser = userDao.internalQueryUserByUserId(requesterId);
        log.info("targetUser for updating - " + targetUser);
        if (targetUser == null) {
            throw new ConnectDataException(
                    ConnectErrorCode.USER_NOT_EXISTED_EXCEPTION,
                    String.format("User %s not exited.", requesterId)
            );
        }
        profile.setId(targetUser.getId());
        log.info("payload for updating user - " + profile);

        int affected = userDao.editUserProfile(profile);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.USER_EDIT_EXCEPTION);
        }
    }

    public void incrementViews(String userId, int version) {
        int affected = userDao.incrementViews(userId, version);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.USER_EDIT_EXCEPTION, "update viewCounts failed");
        }
    }

    public void refreshFollowers(String userId, int version, int followers) {
        int affected = userDao.refreshFollowers(userId, version, followers);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.USER_EDIT_EXCEPTION, "update followers failed");
        }
    }

    public void refreshFollowings(String userId, int version, int followings) {
        int affected = userDao.refreshFollowings(userId, version, followings);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.USER_EDIT_EXCEPTION, "update followings failed");
        }
    }

    public void deleteUser(String userId) {
        boolean existed = userDao.userExisting(userId);
        if (!existed) {
            throw new ConnectDataException(
                    ConnectErrorCode.USER_NOT_EXISTED_EXCEPTION,
                    String.format("User %s not exited.", userId)
            );
        }
        int affected = userDao.deleteUser(userId);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.USER_DELETE_EXCEPTION);
        }
    }

    public List<User> queryUser(QueryUserParam param, String requesterId) {
        log.info(param.toString());
        return userDao.queryUser(param.getKeyword(), requesterId);
    }

    public User internalQueryUserByUserId(String userId) {
        User targetUser = userDao.internalQueryUserByUserId(userId);
        if (targetUser == null) {
            throw new ConnectDataException(
                    ConnectErrorCode.USER_NOT_EXISTED_EXCEPTION,
                    String.format("User %s not exited", userId)
            );
        }
        return targetUser;
    }

    public User queryUserByUserId(String userId, String requesterId) {
        User targetUser = userDao.queryUserByUserId(userId, requesterId);
        if (targetUser == null) {
            throw new ConnectDataException(
                    ConnectErrorCode.USER_NOT_EXISTED_EXCEPTION,
                    String.format("User %s not exited or not authorized to see it", userId)
            );
        }
        return targetUser;
    }
}
