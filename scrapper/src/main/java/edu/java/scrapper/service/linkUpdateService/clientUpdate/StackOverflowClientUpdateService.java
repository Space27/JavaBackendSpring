package edu.java.scrapper.service.linkUpdateService.clientUpdate;

import edu.java.scrapper.service.client.stackOverflowClient.QuestionResponse;
import edu.java.scrapper.service.client.stackOverflowClient.StackOverflowClient;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class StackOverflowClientUpdateService implements ClientUpdateService {

    private static final Pattern REGEX = Pattern.compile("stackoverflow\\.com/questions/(\\d+)");
    private static final String UPDATE_ANSWER = "StackOverflow: Обновление в %s\n";

    private final StackOverflowClient client;

    @Override
    public boolean supports(URI link) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));

            try {
                client.fetchQuestion(questionId);
                return true;
            } catch (WebClientResponseException e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public String handle(URI link, OffsetDateTime lastCheckTime) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));

            QuestionResponse response = client.fetchQuestion(questionId);
            OffsetDateTime lastModified = response.items().getFirst().update();

            if (lastModified.isAfter(lastCheckTime)) {
                return String.format(UPDATE_ANSWER, lastModified);
            }
        }

        return null;
    }
}
