package com.team10.matchup.chat;

import com.team10.matchup.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository repo;
    private final TeamRepository teamRepository;

    public ChatRoomService(ChatRoomRepository repo, TeamRepository teamRepository) {
        this.repo = repo;
        this.teamRepository = teamRepository;
    }

    public ChatRoom create(ChatRoomRequest req) {
        if (req.getTeamId() == null || !teamRepository.existsById(req.getTeamId())) {
            throw new IllegalArgumentException("teamId가 올바르지 않습니다.");
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("채팅방 이름을 입력하세요.");
        }

        ChatRoom room = new ChatRoom(req.getTeamId(), req.getName());
        return repo.save(room);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getRooms(Long teamId) {
        return repo.findByTeamId(teamId);
    }
}
