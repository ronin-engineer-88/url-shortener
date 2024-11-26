package com.roninhub;

public class MockTokenService implements TokenService {
    @Override
    public long[] allocateRange() throws Exception {
        return new long[]{1, 100000000};
    }
}
