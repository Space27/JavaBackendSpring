package edu.java.scrapper.service.linkUpdateService.clientUpdate.stackOverflowClientUpdate;

import edu.java.scrapper.service.client.stackOverflow.StackOverflowClient;
import edu.java.scrapper.service.client.stackOverflow.dto.QuestionResponse;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdateService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackOverflowClientUpdateService implements ClientUpdateService {

    private static final Pattern REGEX = Pattern.compile("stackoverflow\\.com/questions/(\\d+)");
    private static final String UPDATE_ANSWER = "StackOverflow: Последнее обновление в %s";

    private final StackOverflowClient client;

    @Override
    public boolean supports(URI link) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));

            try {
                client.fetchQuestion(questionId);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public Map<String, OffsetDateTime> handle(URI link, OffsetDateTime lastCheckTime) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));

            QuestionResponse response = client.fetchQuestion(questionId);
            OffsetDateTime lastModified = response.items().getFirst().update();

            if (lastModified.isAfter(lastCheckTime)) {
                return Map.of(String.format(UPDATE_ANSWER, lastModified), lastModified);
            }
        }

        return Map.of();
    }
}
