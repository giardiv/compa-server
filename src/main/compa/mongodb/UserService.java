package main.compa.mongodb;

import main.compa.Model.User;

import java.util.List;

public interface UserService {

    User saveUser(String login, String password);

    User findUserByLogin(String login);

    List<User> findAllUser();

    void removeUserByLogin(String login);

}
