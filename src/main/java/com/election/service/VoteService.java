package com.election.service;

import com.election.dto.VoteCastRequest;
import com.election.entity.Election;
import com.election.entity.EligibilityRule;
import com.election.entity.User;
import com.election.entity.Vote;
import com.election.entity.VoteRecord;
import com.election.repository.ElectionRepository;
import com.election.repository.EligibilityRuleRepository;
import com.election.repository.PositionRepository;
import com.election.repository.UserRepository;
import com.election.repository.VoteRecordRepository;
import com.election.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {
    private final ElectionRepository electionRepository;
    private final EligibilityRuleRepository eligibilityRuleRepository;
    private final VoteRepository voteRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository;

    public VoteService(ElectionRepository electionRepository,
                       EligibilityRuleRepository eligibilityRuleRepository,
                       VoteRepository voteRepository,
                       VoteRecordRepository voteRecordRepository,
                       UserRepository userRepository) {
        this.electionRepository = electionRepository;
        this.eligibilityRuleRepository = eligibilityRuleRepository;
        this.voteRepository = voteRepository;
        this.voteRecordRepository = voteRecordRepository;
        this.userRepository = userRepository;
    }

    public List<Election> getEligibleElections(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Election> allElections = electionRepository.findAll();
        List<Election> eligible = new ArrayList<>();

        for (Election election : allElections) {
            if (!election.getStatus().equals("ACTIVE")) continue;

            List<EligibilityRule> rules = eligibilityRuleRepository.findByElectionId(election.getId());
            if (rules.isEmpty()) {
                eligible.add(election);
                continue;
            }

            for (EligibilityRule rule : rules) {
                boolean matches = true;
                if (rule.getBranch() != null && !rule.getBranch().equals(user.getBranch())) matches = false;
                if (rule.getBatch() != null && !rule.getBatch().equals(user.getBatch())) matches = false;
                if (rule.getStudentType() != null && !rule.getStudentType().equals(user.getStudentType())) matches = false;
                if (matches) {
                    eligible.add(election);
                    break;
                }
            }
        }
        return eligible;
    }

    @Transactional
    public void castVotes(Long userId, Long electionId, List<VoteCastRequest> votes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Election election = electionRepository.findById(electionId)
            .orElseThrow(() -> new RuntimeException("Election not found"));

        if (!election.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Election is not active");
        }

        List<EligibilityRule> rules = eligibilityRuleRepository.findByElectionId(electionId);
        if (!rules.isEmpty()) {
            boolean eligible = false;
            for (EligibilityRule rule : rules) {
                boolean matches = true;
                if (rule.getBranch() != null && !rule.getBranch().equals(user.getBranch())) matches = false;
                if (rule.getBatch() != null && !rule.getBatch().equals(user.getBatch())) matches = false;
                if (rule.getStudentType() != null && !rule.getStudentType().equals(user.getStudentType())) matches = false;
                if (matches) { eligible = true; break; }
            }
            if (!eligible) throw new RuntimeException("You are not eligible for this election");
        }

        for (VoteCastRequest vote : votes) {
            if (voteRecordRepository.existsByElectionIdAndUserIdAndPositionId(electionId, userId, vote.positionId())) {
                throw new RuntimeException("You have already voted for position: " + vote.positionId());
            }

            Vote v = new Vote();
            v.setElectionId(electionId);
            v.setPositionId(vote.positionId());
            v.setCandidateId(vote.candidateId());
            v.setBranch(user.getBranch());
            v.setBatch(user.getBatch());
            v.setStudentType(user.getStudentType());
            voteRepository.save(v);

            VoteRecord record = new VoteRecord();
            record.setElectionId(electionId);
            record.setUserId(userId);
            record.setPositionId(vote.positionId());
            voteRecordRepository.save(record);
        }
    }

    public boolean hasVoted(Long userId, Long electionId) {
        return voteRecordRepository.existsByElectionIdAndUserId(electionId, userId);
    }
}
