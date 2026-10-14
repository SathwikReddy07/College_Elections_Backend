package com.election.controller;

import com.election.dto.VoteCastRequest;
import com.election.service.ElectionService;
import com.election.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final VoteService voteService;
    private final ElectionService electionService;

    public StudentController(VoteService voteService, ElectionService electionService) {
        this.voteService = voteService;
        this.electionService = electionService;
    }

    @GetMapping("/elections")
    public ResponseEntity<?> getEligibleElections() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return ResponseEntity.ok(voteService.getEligibleElections(userId));
    }

    @GetMapping("/elections/{id}")
    public ResponseEntity<?> getElection(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElection(id));
    }

    @PostMapping("/elections/{electionId}/vote")
    public ResponseEntity<?> castVotes(@PathVariable Long electionId, @RequestBody List<VoteCastRequest> votes) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        voteService.castVotes(userId, electionId, votes);
        return ResponseEntity.ok(Map.of("message", "Votes cast successfully"));
    }

    @GetMapping("/elections/{electionId}/status")
    public ResponseEntity<?> hasVoted(@PathVariable Long electionId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        boolean voted = voteService.hasVoted(userId, electionId);
        return ResponseEntity.ok(Map.of("hasVoted", voted));
    }
}
