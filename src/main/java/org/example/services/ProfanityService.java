package org.example.services;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Set;

@Data
@Service
public class ProfanityService {
    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "большой брат",
            "диктатура",
            "свобода",
            "двуемыслие",
            "мыслепреступление"
    );

    public boolean containsForbiddenWords(String text) {
        String normalized = text.toLowerCase();
        for (String forbiddenWords: FORBIDDEN_WORDS) {
            if (normalized.contains(forbiddenWords)) {
                return true;
            }
        }
        return false;
    }
}
