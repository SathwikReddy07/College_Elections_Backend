package com.election.controller;

import com.election.dto.ElectionRequest;
import com.election.dto.EligibilityRuleRequest;
import com.election.dto.PositionRequest;
import com.election.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ElectionService electionService;

    public AdminController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping("/elections")
    public ResponseEntity<?> createElection(@RequestBody ElectionRequest request) {
        return ResponseEntity.ok(electionService.createElection(request));
    }

    @GetMapping("/elections")
    public ResponseEntity<?> getAllElections() {
        return ResponseEntity.ok(electionService.getAllElections());
    }

    @GetMapping("/elections/{id}")
    public ResponseEntity<?> getElection(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElection(id));
    }

    @PutMapping("/elections/{id}")
    public ResponseEntity<?> updateElection(@PathVariable Long id, @RequestBody ElectionRequest request) {
        return ResponseEntity.ok(electionService.updateElection(id, request));
    }

    @DeleteMapping("/elections/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable Long id) {
        electionService.deleteElection(id);
        return ResponseEntity.ok(Map.of("message", "Election deleted"));
    }



    @PostMapping("/elections/{electionId}/positions")
    public ResponseEntity<?> addPosition(@PathVariable Long electionId, @RequestBody PositionRequest request) {
        return ResponseEntity.ok(electionService.addPosition(electionId, request));
    }

    @GetMapping("/elections/{electionId}/positions")
    public ResponseEntity<?> getPositions(@PathVariable Long electionId) {
        return ResponseEntity.ok(electionService.getPositions(electionId));
    }

    @DeleteMapping("/positions/{id}")
    public ResponseEntity<?> deletePosition(@PathVariable Long id) {
        electionService.deletePosition(id);
        return ResponseEntity.ok(Map.of("message", "Position deleted"));
    }


    @PostMapping("/positions/{positionId}/candidates")
    public ResponseEntity<?> addCandidate(
            @PathVariable Long positionId,
            @RequestParam("name") String name,
            @RequestParam(value = "manifesto", required = false) String manifesto,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(electionService.addCandidate(positionId, name, manifesto, image));
    }

    @GetMapping("/positions/{positionId}/candidates")
    public ResponseEntity<?> getCandidates(@PathVariable Long positionId) {
        return ResponseEntity.ok(electionService.getCandidates(positionId));
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        electionService.deleteCandidate(id);
        return ResponseEntity.ok(Map.of("message", "Candidate deleted"));
    }



    @PostMapping("/elections/{electionId}/eligibility")
    public ResponseEntity<?> addEligibilityRule(@PathVariable Long electionId, @RequestBody EligibilityRuleRequest request) {
        return ResponseEntity.ok(electionService.addEligibilityRule(electionId, request));
    }

    @GetMapping("/elections/{electionId}/eligibility")
    public ResponseEntity<?> getEligibilityRules(@PathVariable Long electionId) {
        return ResponseEntity.ok(electionService.getEligibilityRules(electionId));
    }

    @DeleteMapping("/eligibility/{id}")
    public ResponseEntity<?> deleteEligibilityRule(@PathVariable Long id) {
        electionService.deleteEligibilityRule(id);
        return ResponseEntity.ok(Map.of("message", "Rule deleted"));
    }



    @GetMapping("/elections/{electionId}/results")
    public ResponseEntity<?> getResults(
            @PathVariable Long electionId,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String studentType) {
        return ResponseEntity.ok(electionService.getResults(electionId, branch, batch, studentType));
    }
}
