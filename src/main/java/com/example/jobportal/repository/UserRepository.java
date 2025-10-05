package com.example.jobportal.repository;

import com.example.jobportal.data.pojo.UserDTO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.generated.jooq.tables.Users.USERS;
import static com.example.generated.jooq.tables.UserProfiles.USER_PROFILES;
import static com.example.generated.jooq.tables.UserRoles.USER_ROLES;
import static com.example.generated.jooq.tables.Roles.ROLES;
import static com.example.generated.jooq.tables.RolePermissions.ROLE_PERMISSIONS;
import static com.example.generated.jooq.tables.Permissions.PERMISSIONS;
import static com.example.generated.jooq.tables.UserTokens.USER_TOKENS;
import static org.jooq.impl.DSL.val;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<org.jooq.Field<?>> getUserFields() {
        return Arrays.asList(
                USERS.ID,
                USERS.USERNAME,
                USERS.EMAIL,
                USERS.PHONE,
                USERS.PASSWORD_HASH,
                USERS.FAILED_ATTEMPTS,
                USERS.LAST_LOGIN,
                USERS.LOCKED_UNTIL,
                USER_PROFILES.NAME,
                USER_PROFILES.AVATAR
        );
    }

    // --- Find by ID
    public Optional<UserDTO> findById(Long userId) {
        SelectConditionStep<Record> query = dsl.select(getUserFields())
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .where(USERS.ID.eq(userId));

        Optional<UserDTO> userOpt = query.fetchOptional(this::mapToDTO);
        userOpt.ifPresent(this::populateRolesPermissionsTokens);
        return userOpt;
    }

    // --- Find by username
    public Optional<UserDTO> findByUsername(String username) {
        SelectConditionStep<Record> query = dsl.select(getUserFields())
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .where(USERS.USERNAME.eq(username));

        Optional<UserDTO> userOpt = query.fetchOptional(this::mapToDTO);
        userOpt.ifPresent(this::populateRolesPermissionsTokens);
        return userOpt;
    }

    // --- Find by email
    public Optional<UserDTO> findByEmail(String email) {
        SelectConditionStep<Record> query = dsl.select(getUserFields())
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .where(USERS.EMAIL.eq(email));

        Optional<UserDTO> userOpt = query.fetchOptional(this::mapToDTO);
        userOpt.ifPresent(this::populateRolesPermissionsTokens);
        return userOpt;
    }

    // --- Insert new user
    public Long insertUser(String username, String email, String passwordHash) {
        return dsl.insertInto(USERS)
                .set(USERS.UID, val(UUID.randomUUID()))
                .set(USERS.USERNAME, val(username))
                .set(USERS.EMAIL, val(email))
                .set(USERS.PASSWORD_HASH, val(passwordHash))
                .set(USERS.FAILED_ATTEMPTS, val(0))
                .set(USERS.ACTIVE, val((short) 1))
                .set(USERS.LAST_LOGIN, (LocalDateTime) null)
                .set(USERS.LOCKED_UNTIL, (LocalDateTime) null)
                .set(USERS.CREATED_AT, val(LocalDateTime.now()))
                .returning(USERS.ID)
                .fetchOne()
                .getId();
    }

    // --- Update user
    public void updateUserLastLogin(Long userId, String username) {
        dsl.update(USERS)
                .set(USERS.USERNAME, val(username))
                .set(USERS.LAST_LOGIN, val(LocalDateTime.now()))
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Create new user
    public Long createUser(String username, String email, String passwordHash) {
        // Insert vào bảng USERS
        return dsl.insertInto(USERS)
                .set(USERS.UID, val(UUID.randomUUID()))
                .set(USERS.USERNAME, val(username))
                .set(USERS.EMAIL, val(email))
                .set(USERS.PASSWORD_HASH, val(passwordHash))
                .set(USERS.FAILED_ATTEMPTS, val(0))
                .set(USERS.ACTIVE, val((short)1))
                .set(USERS.LAST_LOGIN, (LocalDateTime) null)
                .set(USERS.LOCKED_UNTIL, (LocalDateTime) null)
                .set(USERS.CREATED_AT, val(LocalDateTime.now()))
                .returning(USERS.ID)
                .fetchOne()
                .getId();
    }

    // --- Update password
    public int updatePassword(Long userId, String passwordHash) {
        return dsl.update(USERS)
                .set(USERS.PASSWORD_HASH, passwordHash)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Unlock user
    public int unlockUser(Long userId) {
        return dsl.update(USERS)
                .set(USERS.LOCKED_UNTIL, (LocalDateTime) null)
                .execute();
    }

    // --- Tạo profile cho user
    public void createProfile(Long userId, String name, String avatar) {
        dsl.insertInto(USER_PROFILES)
                .set(USER_PROFILES.USER_ID, userId)
                .set(USER_PROFILES.NAME, name)
                .set(USER_PROFILES.AVATAR, avatar)
                .execute();
    }

    // --- Gán role cho user
    public void assignRole(Long userId, Long roleId) {
        dsl.insertInto(USER_ROLES)
                .set(USER_ROLES.USER_ID, userId)
                .set(USER_ROLES.ROLE_ID, roleId)
                .execute();
    }

    // --- Update last login
    public int updateLastLogin(Long userId) {
        return dsl.update(USERS)
                .set(USERS.LAST_LOGIN, LocalDateTime.now())
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Failed attempts
    public int increaseFailedAttempts(Long userId) {
        return dsl.update(USERS)
                .set(USERS.FAILED_ATTEMPTS, USERS.FAILED_ATTEMPTS.add(1))
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public int resetFailedAttempts(Long userId) {
        return dsl.update(USERS)
                .set(USERS.FAILED_ATTEMPTS, 0)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Lock user
    public int lockUser(Long userId, LocalDateTime lockedUntil) {
        return dsl.update(USERS)
                .set(USERS.LOCKED_UNTIL, lockedUntil)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Mapper
    private UserDTO mapToDTO(Record r) {
        UserDTO dto = UserDTO.builder()
                .id(r.get(USERS.ID))
                .username(r.get(USERS.USERNAME))
                .email(r.get(USERS.EMAIL))
                .phone(r.get(USERS.PHONE))
                .name(r.get(USER_PROFILES.NAME))
                .avatar(r.get(USER_PROFILES.AVATAR))
                .roles(new HashSet<>())
                .permissions(new HashSet<>())
                .activeTokens(new ArrayList<>())
                .build();

        dto.setPasswordHash(r.get(USERS.PASSWORD_HASH));
        dto.setFailedAttempts(r.get(USERS.FAILED_ATTEMPTS));
        dto.setLastLogin(r.get(USERS.LAST_LOGIN));
        dto.setLockedUntil(r.get(USERS.LOCKED_UNTIL));

        return dto;
    }

    // --- Populate roles, permissions, tokens
    private void populateRolesPermissionsTokens(UserDTO user) {
        Long userId = user.getId();

        // Roles
        Set<String> roles = new HashSet<>(dsl.select(ROLES.NAME)
                .from(USER_ROLES)
                .join(ROLES).on(USER_ROLES.ROLE_ID.eq(ROLES.ID))
                .where(USER_ROLES.USER_ID.eq(userId))
                .fetchInto(String.class));
        user.setRoles(roles);

        // Permissions
        Set<String> permissions = new HashSet<>(dsl.select(PERMISSIONS.CODE)
                .from(USER_ROLES)
                .join(ROLE_PERMISSIONS).on(USER_ROLES.ROLE_ID.eq(ROLE_PERMISSIONS.ROLE_ID))
                .join(PERMISSIONS).on(ROLE_PERMISSIONS.PERMISSION_ID.eq(PERMISSIONS.ID))
                .where(USER_ROLES.USER_ID.eq(userId))
                .fetchInto(String.class));
        user.setPermissions(permissions);

        // Active tokens
        List<String> activeTokens = dsl.select(USER_TOKENS.REFRESH_TOKEN)
                .from(USER_TOKENS)
                .where(USER_TOKENS.USER_ID.eq(userId))
                .and(USER_TOKENS.REVOKED.eq(false))
                .and(USER_TOKENS.EXPIRES_AT.gt(LocalDateTime.now()))
                .fetchInto(String.class);
        user.setActiveTokens(activeTokens);
    }
}
