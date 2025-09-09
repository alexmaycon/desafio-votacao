package com.voting.system.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private VotingSessionService votingSessionService;

    @Value("${scheduling.session.expiration-check-interval:60000}")
    private long checkInterval;

    @Scheduled(fixedRateString = "${scheduling.session.expiration-check-interval:60000}")
    public void closeExpiredVotingSessions() {
        logger.info("Executando verificacao de sessoes de votacao expiradas");
        try {
            int closedSessions = votingSessionService.closeExpiredSessions();
            if (closedSessions > 0) {
                logger.info("Fechadas {} sessoes de votacao expiradas", closedSessions);
            }
        } catch (Exception e) {
            logger.error("Erro ao fechar sessoes expiradas", e);
        }
    }
}
