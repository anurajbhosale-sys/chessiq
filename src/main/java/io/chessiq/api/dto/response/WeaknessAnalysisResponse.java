package io.chessiq.api.dto.response;

import java.util.List;

public record WeaknessAnalysisResponse(
        double overallWinRatePercent,
        List<WeaknessResponse> weaknesses
) {}