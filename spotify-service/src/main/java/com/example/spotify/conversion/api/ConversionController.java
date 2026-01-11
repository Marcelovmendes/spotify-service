package com.example.spotify.conversion.api;

import com.example.spotify.conversion.api.dto.ConversionStatusResponse;
import com.example.spotify.conversion.api.dto.CreateConversionRequest;
import com.example.spotify.conversion.api.dto.CreateConversionResponse;
import com.example.spotify.conversion.application.ConversionUseCase;
import com.example.spotify.conversion.domain.entity.Platform;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversions")
public class ConversionController {

    private static final Logger log = LoggerFactory.getLogger(ConversionController.class);

    private final ConversionUseCase conversionUseCase;

    public ConversionController(ConversionUseCase conversionUseCase) {
        this.conversionUseCase = conversionUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateConversionResponse> createConversion(
            @RequestBody CreateConversionRequest request,
            HttpSession session
    ) {
        log.info("Create conversion - Session: {}, Playlist: {}", session.getId(), request.sourcePlaylistId());

        var command = new ConversionUseCase.CreateConversionCommand(
                request.sourcePlaylistId(),
                Platform.valueOf(request.targetPlatform()),
                request.targetPlaylistName(),
                request.selectedTrackIds()
        );

        var result = conversionUseCase.createConversion(session.getId(), command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateConversionResponse(result.jobId(), result.status()));
    }

    @GetMapping("/{jobId}/status")
    public ResponseEntity<ConversionStatusResponse> getConversionStatus(@PathVariable String jobId) {
        log.info("Get conversion status - Job: {}", jobId);

        var status = conversionUseCase.getConversionStatus(jobId);

        return ResponseEntity.ok(ConversionStatusResponse.fromDomain(status));
    }
}
