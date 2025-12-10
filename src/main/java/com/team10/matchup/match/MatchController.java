package com.team10.matchup.match;

import com.team10.matchup.common.CurrentUserService;
import com.team10.matchup.team.Team;
import com.team10.matchup.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;
    private final CurrentUserService currentUserService;

    @GetMapping("/apply")
    public String matchApplyPage(
            @RequestParam(name = "filter", defaultValue = "available") String filter,
            Model model) {

        Team team = currentUserService.getCurrentUserTeamOrNull();
        if (team == null) {
            model.addAttribute("noTeam", true);
            model.addAttribute("filter", filter);
            return "match_apply";
        }

        User currentUser = currentUserService.getCurrentUser();
        Map<Long, String> requestStatusMap = matchService.getMyRequestStatusMap();
        List<MatchPost> allPosts = matchService.getAllMatchPosts();

        // ✅ 서버에서 '가능/불가능' 필터링
        List<MatchPost> filteredPosts = allPosts.stream()
                .filter(post -> {
                    String status = post.getStatus();
                    Long postId = post.getId();
                    String myReqStatus = requestStatusMap.get(postId);

                    boolean isMine = post.getCreatedBy() != null
                            && post.getCreatedBy().getId().equals(currentUser.getId());

                    boolean available = "OPEN".equals(status)
                            && !isMine
                            && myReqStatus == null;

                    if ("available".equals(filter)) {
                        return available;            // 신청 가능만
                    } else { // "unavailable"
                        return !available;           // 그 외는 전부 불가능
                    }
                })
                .collect(Collectors.toList());

        model.addAttribute("noTeam", false);
        model.addAttribute("team", team);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("matchCreateForm", new MatchCreateForm());
        model.addAttribute("matchPosts", filteredPosts);
        model.addAttribute("requestStatusMap", requestStatusMap);
        model.addAttribute("filter", filter);

        return "match_apply";
    }

    @PostMapping("/create")
    public String createMatch(@ModelAttribute MatchCreateForm form) {
        matchService.createMatchPost(
                form.getPlayerCount(),
                form.getLocation(),
                form.getDate(),
                form.getTime()
        );
        return "redirect:/match/apply";
    }

    @PostMapping("/{matchId}/request")
    public String requestMatch(@PathVariable Long matchId) {
        matchService.requestMatch(matchId);
        return "redirect:/match/apply?filter=available";
    }

    @PostMapping("/{matchId}/delete")
    public String deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        // 보통 내가 올린 매치는 '불가능' 탭에서 보니까 거기로 돌아가게
        return "redirect:/match/apply?filter=unavailable";
    }

    // 전체 예정된 매치 목록 페이지
    @GetMapping("/upcoming")
    public String upcomingMatches(Model model) {

        model.addAttribute("matches", matchService.getMyTeamUpcomingMatches());

        return "match_upcoming";
    }


}