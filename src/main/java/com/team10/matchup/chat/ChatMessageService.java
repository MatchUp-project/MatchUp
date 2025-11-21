package com.team10.matchup.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository repo;
    private final ChatRoomRepository roomRepository;
    private final UsersRepository usersRepository;

    public ChatMessageService(ChatMessageRepository repo,
                              ChatRoomRepository roomRepository,
                              UsersRepository usersRepository) {
        this.repo = repo;
        this.roomRepository = roomRepository;
        this.usersRepository = usersRepository;
    }

    public ChatMessage create(ChatMessageRequest req) {
        if (!roomRepository.existsById(req.getRoomId())) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }
        if (!usersRepository.existsById(req.getSenderId())) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        if (req.getMessage() == null || req.getMessage().isBlank()) {
            throw new IllegalArgumentException("메시지를 입력하세요.");
        }

        ChatMessage msg = new ChatMessage(req.getRoomId(), req.getSenderId(), req.getMessage());
        return repo.save(msg);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Long roomId) {
        return repo.findByRoomIdOrderBySentAtAsc(roomId);
    }
}
