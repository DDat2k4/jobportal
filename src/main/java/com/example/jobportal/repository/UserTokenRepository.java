package com.example.jobportal.repository;

import com.example.generated.jooq.tables.records.UserTokensRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.UserTokens.USER_TOKENS;

@Repository
public class UserTokenRepository {

    private final DSLContext dsl;

    public UserTokenRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getTokenFields() {
        return Arrays.asList(
                USER_TOKENS.ID,
                USER_TOKENS.USER_ID,
                USER_TOKENS.REFRESH_TOKEN,
                USER_TOKENS.CREATED_AT,
                USER_TOKENS.EXPIRES_AT,
                USER_TOKENS.REVOKED
        );
    }

    // Lấy tất cả token chưa bị revoke và chưa hết hạn
    public List<UserTokensRecord> findActiveTokensByUserId(Long userId) {
        return dsl.selectFrom(USER_TOKENS)
                .where(USER_TOKENS.USER_ID.eq(userId))
                .and(USER_TOKENS.REVOKED.eq(false))
                .and(USER_TOKENS.EXPIRES_AT.gt(LocalDateTime.now()))
                .fetchInto(UserTokensRecord.class);
    }

    // Lấy token theo refreshToken và chưa revoke
    public Optional<UserTokensRecord> findByRefreshTokenAndRevokedFalse(String refreshToken) {
        return dsl.selectFrom(USER_TOKENS)
                .where(USER_TOKENS.REFRESH_TOKEN.eq(refreshToken))
                .and(USER_TOKENS.REVOKED.eq(false))
                .fetchOptional();
    }

    // Revoke tất cả token của user
    public int revokeAllTokensByUserId(Long userId) {
        return dsl.update(USER_TOKENS)
                .set(USER_TOKENS.REVOKED, true)
                .where(USER_TOKENS.USER_ID.eq(userId))
                .and(USER_TOKENS.REVOKED.eq(false))
                .execute();
    }

    // Revoke token theo tokenId
    public int revokeToken(Long tokenId) {
        return dsl.update(USER_TOKENS)
                .set(USER_TOKENS.REVOKED, true)
                .where(USER_TOKENS.ID.eq(tokenId))
                .execute();
    }

    // Insert token mới
    public int insertToken(Long userId, String refreshToken) {
        return dsl.insertInto(USER_TOKENS,
                        USER_TOKENS.USER_ID,
                        USER_TOKENS.REFRESH_TOKEN,
                        USER_TOKENS.CREATED_AT,
                        USER_TOKENS.EXPIRES_AT,
                        USER_TOKENS.REVOKED)
                .values(userId,
                        refreshToken,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7),
                        false)
                .execute();
    }
}
