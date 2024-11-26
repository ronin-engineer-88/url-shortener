package com.roninhub;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class KeyGeneratorService {
    private long rangeStart;
    private long rangeEnd;
    private final AtomicLong currentToken;
    private final TokenService tokenService;
    private final ReentrantLock rangeLock;

    public KeyGeneratorService(TokenService tokenService) throws Exception {
        this.tokenService = tokenService;
        this.currentToken = new AtomicLong();
        this.rangeLock = new ReentrantLock();
        fetchNewRange();
    }

    private void fetchNewRange() throws Exception {
        rangeLock.lock();
        try {
            long[] range = tokenService.allocateRange();
            this.rangeStart = range[0];
            this.rangeEnd = range[1];
            currentToken.set(rangeStart);
        } finally {
            rangeLock.unlock();
        }
    }

    public long getToken() throws Exception  {
        long token = currentToken.getAndIncrement();    // CAS - performance
        if (token > rangeEnd) {
            fetchNewRange();
            token = currentToken.getAndIncrement();
        }

        return token;
    }

    public String generateKey() throws Exception {
        long token = getToken();

        return EncodeUtility.encodeBase62(token);
    }


    public static void main(String[] args) throws Exception {
        ZKTokenService ZKTokenRangeService = new ZKTokenService();
        KeyGeneratorService keyGeneratorService = new KeyGeneratorService(ZKTokenRangeService);

        for (int i = 0; i < 10; i++) {
            System.out.println("Generated Key: " + keyGeneratorService.generateKey());
        }
    }
}
