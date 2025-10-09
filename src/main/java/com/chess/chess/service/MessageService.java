package com.chess.chess.service;

import com.chess.chess.model.*;
import com.chess.chess.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MessageService {
    private final MessageRepository messageRepo;

    public MessageService(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    public Message sendMessage(Message message) {
        return messageRepo.save(message);
    }

    public List<Message> getMessagesByMatch(Match match) {
        return messageRepo.findByMatchOrderByTimestampAsc(match);
    }
}
