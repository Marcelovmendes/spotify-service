package com.example.spotify.conversion.application.port;

import com.example.spotify.conversion.domain.entity.ConversionJob;

public interface JobQueuePort {
    void enqueue(ConversionJob job);
}
