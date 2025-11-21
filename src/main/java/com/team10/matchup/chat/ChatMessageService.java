package com.team10.matchup.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.team10.matchup.user.UserRepository;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository repo;
    private final ChatRoomRepository roomRepository;
    private final UserRepository userRepository; // ← 이름 수정

    public ChatMessageService(ChatMessageRepository repo,
                              ChatRoomRepository roomRepository,
                              UserRepository userRepository) { // ← 이름 수정
        this.repo = repo;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public ChatMessage create(ChatMessageRequest req) {
        if (!roomRepository.existsById(req.getRoomId())) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }
        if (!userRepository.existsById(req.getSenderId())) { // ← 이름 수정
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
