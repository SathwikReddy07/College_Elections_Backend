package com.election.service;

import com.election.dto.ElectionRequest;
import com.election.dto.EligibilityRuleRequest;
import com.election.dto.PositionRequest;
import com.election.entity.Candidate;
import com.election.entity.Election;
import com.election.entity.EligibilityRule;
import com.election.entity.Position;
import com.election.repository.CandidateRepository;
import com.election.repository.ElectionRepository;
import com.election.repository.EligibilityRuleRepository;
import com.election.repository.PositionRepository;
import com.election.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElectionService {
    private final ElectionRepository electionRepository;
    private final PositionRepository positionRepository;
    private final CandidateRepository candidateRepository;
    private final EligibilityRuleRepository eligibilityRuleRepository;
    private final VoteRepository voteRepository;
    private final CloudinaryService cloudinaryService;

    public ElectionService(ElectionRepository electionRepository,
                           PositionRepository positionRepository,
                           CandidateRepository candidateRepository,
                           EligibilityRuleRepository eligibilityRuleRepository,
                           VoteRepository voteRepository,
                           CloudinaryService cloudinaryService) {
        this.electionRepository = electionRepository;
        this.positionRepository = positionRepository;
        this.candidateRepository = candidateRepository;
        this.eligibilityRuleRepository = eligibilityRuleRepository;
        this.voteRepository = voteRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Election createElection(ElectionRequest request) {
        Election election = new Election();
        election.setTitle(request.title());
        election.setDescription(request.description());
        election.setStartTime(request.startTime());
        election.setEndTime(request.endTime());
        return electionRepository.save(election);
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Election getElection(Long id) {
        return electionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Election not found"));
    }

    private void assertElectionUpcoming(Election election) {
        if (!"UPCOMING".equalsIgnoreCase(election.getStatus())) {
            throw new RuntimeException("Election cannot be modified because its status is " +
                    election.getStatus() +". Only UPCOMING elections can be modified.");
        }
    }

    public Election updateElection(Long id, ElectionRequest request) {
        Election election = getElection(id);
        assertElectionUpcoming(election);

        election.setTitle(request.title());
        election.setDescription(request.description());
        election.setStartTime(request.startTime());
        election.setEndTime(request.endTime());
        return electionRepository.save(election);
    }

    public void deleteElection(Long id) {
        Election election = getElection(id);
        assertElectionUpcoming(election);

        List<Position> positions = positionRepository.findByElectionId(id);
        for (Position pos : positions) {
            List<Candidate> candidates = candidateRepository.findByPositionId(pos.getId());
            for (Candidate c : candidates) {
                if (c.getImageUrl() != null) {
                    cloudinaryService.deleteImageByUrl(c.getImageUrl());
                }
            }
        }

        electionRepository.deleteById(id);
    }

    public Position addPosition(Long electionId, PositionRequest request) {
        Election election = getElection(electionId);
        assertElectionUpcoming(election);

        Position position = new Position();
        position.setTitle(request.title());
        position.setDisplayOrder(request.displayOrder());
        position.setElection(election);
        return positionRepository.save(position);
    }

    public List<Position> getPositions(Long electionId) {
        return positionRepository.findByElectionId(electionId);
    }

    public void deletePosition(Long id) {
        Position position = positionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Position not found"));

        if (position.getElection() != null) {
            assertElectionUpcoming(position.getElection());
        }

        List<Candidate> candidates = candidateRepository.findByPositionId(id);
        for (Candidate c : candidates) {
            if (c.getImageUrl() != null) {
                cloudinaryService.deleteImageByUrl(c.getImageUrl());
            }
        }
        positionRepository.deleteById(id);
    }

    public Candidate addCandidate(Long positionId, String name, String manifesto, MultipartFile image) {
        Position position = positionRepository.findById(positionId)
            .orElseThrow(() -> new RuntimeException("Position not found"));

        if (position.getElection() != null) {
            assertElectionUpcoming(position.getElection());
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(image);
        }

        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setManifesto(manifesto);
        candidate.setImageUrl(imageUrl);
        candidate.setPosition(position);
        return candidateRepository.save(candidate);
    }

    public List<Candidate> getCandidates(Long positionId) {
        return candidateRepository.findByPositionId(positionId);
    }

    public void deleteCandidate(Long id) {
        Candidate candidate = candidateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (candidate.getPosition() != null && candidate.getPosition().getElection() != null) {
            assertElectionUpcoming(candidate.getPosition().getElection());
        }

        if (candidate.getImageUrl() != null) {
            cloudinaryService.deleteImageByUrl(candidate.getImageUrl());
        }

        candidateRepository.deleteById(id);
    }

    @Transactional
    public EligibilityRule addEligibilityRule(Long electionId, EligibilityRuleRequest request) {
        Election election = getElection(electionId);
        assertElectionUpcoming(election);

        EligibilityRule rule = new EligibilityRule();
        rule.setBranch(request.branch());
        rule.setBatch(request.batch());
        rule.setStudentType(request.studentType());
        rule.setElection(election);
        return eligibilityRuleRepository.save(rule);
    }

    public List<EligibilityRule> getEligibilityRules(Long electionId) {
        return eligibilityRuleRepository.findByElectionId(electionId);
    }

    public void deleteEligibilityRule(Long id) {
        EligibilityRule rule = eligibilityRuleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rule not found"));

        if (rule.getElection() != null) {
            assertElectionUpcoming(rule.getElection());
        }

        eligibilityRuleRepository.deleteById(id);
    }

    public Map<String, Object> getResults(Long electionId, String branch, String batch, String studentType) {
        Election election = getElection(electionId);
        List<Position> positions = positionRepository.findByElectionId(electionId);

        List<Map<String, Object>> positionResults = new ArrayList<>();
        for (Position position : positions) {
            List<Object[]> voteCounts;
            if (branch == null && batch == null && studentType == null) {
                voteCounts = voteRepository.countVotesByPosition(electionId, position.getId());
            } else {
                voteCounts = voteRepository.countVotesFiltered(electionId, position.getId(), branch, batch, studentType);
            }

            long totalVotes = voteRepository.countByElectionIdAndPositionId(electionId, position.getId());

            List<Map<String, Object>> candidateResults = new ArrayList<>();
            for (Object[] row : voteCounts) {
                Long candidateId = (Long) row[0];
                Long count = (Long) row[1];
                Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
                Map<String, Object> cr = new HashMap<>();
                cr.put("candidateId", candidateId);
                cr.put("candidateName", candidate != null ? candidate.getName() : "Unknown");
                cr.put("votes", count);
                candidateResults.add(cr);
            }

            Map<String, Object> pr = new HashMap<>();
            pr.put("positionId", position.getId());
            pr.put("positionTitle", position.getTitle());
            pr.put("totalVotes", totalVotes);
            pr.put("candidates", candidateResults);
            positionResults.add(pr);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("electionId", electionId);
        result.put("electionTitle", election.getTitle());
        result.put("status", election.getStatus());
        result.put("positions", positionResults);
        return result;
    }
}
