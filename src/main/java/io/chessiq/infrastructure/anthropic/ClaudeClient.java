package io.chessiq.infrastructure.anthropic;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import io.chessiq.api.dto.response.WeaknessExplanation;
import io.chessiq.api.dto.response.WeaknessExplanationList;
import io.chessiq.config.AnthropicProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClaudeClient {

    private final AnthropicClient client;

    public ClaudeClient(AnthropicProperties properties) {
        this.client = AnthropicOkHttpClient.builder()
                .apiKey(properties.getApiKey())
                .build();
    }

    public String sendMessage(String prompt) {
        MessageCreateParams params = MessageCreateParams.builder()
                .model(Model.CLAUDE_OPUS_4_6)
                .maxTokens(1024L)
                .addUserMessage(prompt)
                .build();

        Message response = client.messages().create(params);

        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(textBlock -> textBlock.text())
                .collect(Collectors.joining());
    }

    public List<WeaknessExplanation> sendStructuredMessage(String prompt) {
        StructuredMessageCreateParams<WeaknessExplanationList> params = MessageCreateParams.builder()
                .model(Model.CLAUDE_OPUS_4_6)
                .maxTokens(2048L)
                .outputConfig(WeaknessExplanationList.class)
                .addUserMessage(prompt)
                .build();

        return client.messages().create(params).content().stream()
                .flatMap(cb -> cb.text().stream())
                .map(typed -> typed.text().explanations())
                .findFirst()
                .orElse(List.of());
    }
}