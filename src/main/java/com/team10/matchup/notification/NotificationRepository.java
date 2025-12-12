package com.team10.matchup.notification;

import com.team10.matchup.match.MatchRequest;
import com.team10.matchup.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop5ByReceiverOrderByCreatedAtDesc(User receiver);

    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);

    List<Notification> findByRelatedMatchRequest(MatchRequest req);

}
