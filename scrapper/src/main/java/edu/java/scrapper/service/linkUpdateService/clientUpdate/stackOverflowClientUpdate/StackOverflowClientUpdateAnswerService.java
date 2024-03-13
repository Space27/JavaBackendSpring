package edu.java.scrapper.service.linkUpdateService.clientUpdate.stackOverflowClientUpdate;

import edu.java.scrapper.service.client.stackOverflowClient.StackOverflowClient;
import edu.java.scrapper.service.client.stackOverflowClient.dto.AnswerListResponse;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdateService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackOverflowClientUpdateAnswerService implements ClientUpdateService {

    private static final Pattern REGEX = Pattern.compile("stackoverflow\\.com/questions/(\\d+)");
    private static final String NEW_ANSWER_MESSAGE = "StackOverflow: Сообщение с текущим рейтингом %d появилось в %s";

    private final StackOverflowClient client;

    @Override
    public boolean supports(URI link) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            Long questionId = Long.parseLong(matcher.group(1));

            try {
                client.fetchAnswers(questionId);
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

            AnswerListResponse answerListResponse = client.fetchAnswers(questionId);

            return answerListResponse.items().stream()
                .filter(answer -> answer.createdAt().isAfter(lastCheckTime))
                .collect(Collectors.toMap(answer -> String.format(
                    NEW_ANSWER_MESSAGE,
                    answer.score(),
                    answer.createdAt()
                ), AnswerListResponse.AnswerResponse::createdAt));
        }

        return Map.of();
    }
}
