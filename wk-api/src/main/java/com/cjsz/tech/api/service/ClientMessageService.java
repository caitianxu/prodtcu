package com.cjsz.tech.api.service;

import com.cjsz.tech.api.beans.CJKJClientMessage;
import com.cjsz.tech.api.mapper.ClientMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClientMessageService {

    @Autowired
    private ClientMessageMapper clientMessageMapper;

    public void saveMessage(CJKJClientMessage message) {
        message.setCreate_time(new Date());
        clientMessageMapper.insert(message);
    }
}
