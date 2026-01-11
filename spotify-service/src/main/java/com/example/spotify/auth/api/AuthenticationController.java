package com.example.spotify.auth.api;
import com.example.spotify.auth.application.AuthUseCase;
import com.example.spotify.auth.application.TokenQuery;
import com.example.spotify.auth.domain.entity.Token;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final TokenQuery tokenQuery;
    private final AuthUseCase authUseCase;
    private static String frontendUrl = "http://localhost:3000/auth/callback";


    public AuthenticationController(TokenQuery tokenQuery, AuthUseCase authUseCase) {
        this.tokenQuery = tokenQuery;
        this.authUseCase = authUseCase;
    }

    @GetMapping("/")
    public ResponseEntity<String> initiateAuthentication(){

            URI response = authUseCase.initiateAuthentication();
            return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpSession session) {

        log.info("OAuth callback received - Session ID: {}", session.getId());
        log.info("Callback parameters - Code present: {}, State: {}, Error: {}"
        );

        if (error != null) {
            log.error("OAuth provider returned error: {}", error);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", frontendUrl + "?status=error&message=" + error)
                    .build();
        }

        try {
            Token token = authUseCase.completeAuthentication(code, state);

            tokenQuery.storeUserToken(session.getId(), token);

            log.info("Authentication successful - Token stored in session: {}", session.getId());

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", frontendUrl + "?status=success")
                    .build();
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : "authentication_failed";
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", frontendUrl + "?status=error&message=" + errorMessage)
                    .build();
        }
    }

    @PostMapping("/link-youtube")
    public ResponseEntity<Void> linkYoutubeSession(
            @RequestBody LinkYoutubeRequest request,
            HttpSession session
    ) {
        if (!tokenQuery.isUserAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        session.setAttribute("youtubeSessionId", request.youtubeSessionId());
        log.info("Linked YouTube session {} to Spotify session {}", request.youtubeSessionId(), session.getId());

        return ResponseEntity.ok().build();
    }

    public record LinkYoutubeRequest(String youtubeSessionId) {}
}