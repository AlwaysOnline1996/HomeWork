package com.zengtong.Async.handler;

import com.zengtong.Async.EventHandler;
import com.zengtong.Async.EventModel;
import com.zengtong.Async.EventType;
import com.zengtong.DAO.UserDao;
import com.zengtong.Service.EmailService;
import com.zengtong.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component

public class EmailHandler implements EventHandler {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserDao userDao;

    @Override
    public void doHandler(EventModel model) {

        User user = userDao.selectById(model.getTo_id());

        emailService.registerSuc(user.getEmail(),user);

    }

    @Override
    public List<EventType> getSupportType() {
        return Arrays.asList(EventType.REGISTER);
    }
}
