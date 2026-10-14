package com.election.service;

import com.election.enums.Branch;
import com.election.enums.StudentType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailParserService {

    public Map<String, String> parseEmail(String email) {
        String localPart = email.split("@")[0];

        int admYear = 2000 + Integer.parseInt(localPart.substring(0, 2));
        String typeCode = localPart.substring(3, 5);
        String branchCode = localPart.substring(6, 8);

        StudentType studentType = StudentType.fromCode(typeCode);
        Branch branch = Branch.fromCode(branchCode);

        int batchYear = (studentType == StudentType.REGULAR) ? admYear + 4 : admYear + 3;
        String batch = "BATCH_" + batchYear;

        Map<String, String> result = new HashMap<>();
        result.put("rollNumber", localPart);
        result.put("branch", branch.getDisplayName());
        result.put("batch", batch);
        result.put("studentType", studentType.name());
        return result;
    }
}
