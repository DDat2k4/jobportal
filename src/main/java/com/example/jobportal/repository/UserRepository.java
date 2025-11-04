package com.example.jobportal.repository;

import com.example.jobportal.data.entity.User;
import com.example.jobportal.constant.UserStatus;
import com.example.jobportal.data.pojo.UserDTO;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import org.jooq.*;
import org.jooq.Record;
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
import static org.jooq.impl.DSL.noCondition;
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
                USERS.ACTIVE,
                USER_PROFILES.NAME,
                USER_PROFILES.AVATAR
        );
    }

    private Condition getWhereCondition(User filter) {
        Condition condition = noCondition();

        if (filter == null) return condition;

        if (filter.getId() != null)
            condition = condition.and(USERS.ID.eq(filter.getId()));

        if (filter.getUid() != null)
            condition = condition.and(USERS.UID.eq(filter.getUid()));

        if (filter.getUsername() != null && !filter.getUsername().isBlank())
            condition = condition.and(USERS.USERNAME.likeIgnoreCase("%" + filter.getUsername() + "%"));

        if (filter.getEmail() != null && !filter.getEmail().isBlank())
            condition = condition.and(USERS.EMAIL.likeIgnoreCase("%" + filter.getEmail() + "%"));

        if (filter.getPhone() != null && !filter.getPhone().isBlank())
            condition = condition.and(USERS.PHONE.likeIgnoreCase("%" + filter.getPhone() + "%"));

        if (filter.getActive() != null)
            condition = condition.and(USERS.ACTIVE.eq(filter.getActive()));

        if (filter.getFailedAttempts() != null)
            condition = condition.and(USERS.FAILED_ATTEMPTS.eq(filter.getFailedAttempts()));

        if (filter.getCreatedAt() != null)
            condition = condition.and(USERS.CREATED_AT.eq(filter.getCreatedAt()));

        if (filter.getLastLogin() != null)
            condition = condition.and(USERS.LAST_LOGIN.eq(filter.getLastLogin()));

        if (filter.getLockedUntil() != null)
            condition = condition.and(USERS.LOCKED_UNTIL.eq(filter.getLockedUntil()));

        return condition;
    }

    public Optional<UserDTO> findById(Long userId) {
        SelectConditionStep<Record> query = dsl.select(getUserFields())
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .where(USERS.ID.eq(userId));

        Optional<UserDTO> userOpt = query.fetchOptional(this::mapToDTO);
        userOpt.ifPresent(this::populateRolesPermissionsTokens);
        return userOpt;
    }

    public Optional<UserDTO> findByUsername(String username) {
        SelectConditionStep<Record> query = dsl.select(getUserFields())
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .where(USERS.USERNAME.eq(username));

        Optional<UserDTO> userOpt = query.fetchOptional(this::mapToDTO);
        userOpt.ifPresent(this::populateRolesPermissionsTokens);
        return userOpt;
    }

    public Optional<UserDTO> findByEmail(String email) {
        SelectConditionStep<Record> query = dsl.select(getUserFields())
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .where(USERS.EMAIL.eq(email));

        Optional<UserDTO> userOpt = query.fetchOptional(this::mapToDTO);
        userOpt.ifPresent(this::populateRolesPermissionsTokens);
        return userOpt;
    }

    public Page<UserDTO> findAll(UserDTO filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();

        var baseQuery = dsl.select(
                        USERS.ID,
                        USERS.UID,
                        USERS.USERNAME,
                        USERS.EMAIL,
                        USERS.PHONE,
                        USERS.PASSWORD_HASH,
                        USERS.FAILED_ATTEMPTS,
                        USERS.ACTIVE,
                        USERS.CREATED_AT,
                        USERS.LAST_LOGIN,
                        USERS.LOCKED_UNTIL,
                        USER_PROFILES.NAME,
                        USER_PROFILES.AVATAR,
                        ROLES.NAME.as("role_name")
                )
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .leftJoin(USER_ROLES).on(USERS.ID.eq(USER_ROLES.USER_ID))
                .leftJoin(ROLES).on(USER_ROLES.ROLE_ID.eq(ROLES.ID));

        Condition condition = noCondition();

        if (filter != null) {
            if (filter.getId() != null) condition = condition.and(USERS.ID.eq(filter.getId()));
            if (filter.getUsername() != null && !filter.getUsername().isBlank())
                condition = condition.and(USERS.USERNAME.likeIgnoreCase("%" + filter.getUsername() + "%"));
            if (filter.getEmail() != null && !filter.getEmail().isBlank())
                condition = condition.and(USERS.EMAIL.likeIgnoreCase("%" + filter.getEmail() + "%"));
            if (filter.getPhone() != null && !filter.getPhone().isBlank())
                condition = condition.and(USERS.PHONE.likeIgnoreCase("%" + filter.getPhone() + "%"));
            if (filter.getActive() != null) condition = condition.and(USERS.ACTIVE.eq(filter.getActive()));
            if (filter.getName() != null && !filter.getName().isBlank())
                condition = condition.and(USER_PROFILES.NAME.likeIgnoreCase("%" + filter.getName() + "%"));
            if (filter.getRoles() != null && !filter.getRoles().isEmpty())
                condition = condition.and(ROLES.NAME.in(filter.getRoles()));
        }

        baseQuery.where(condition);

        long total = dsl.fetchCount(baseQuery);
        pageable.setTotal(total);

        var order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean asc = order.isAsc();
        Field<?> sort = resolveSortField(sortField);

        // --- Gom các role lại
        Map<Long, UserDTO> map = new LinkedHashMap<>();
        baseQuery
                .orderBy(asc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetch()
                .forEach(record -> {
                    Long userId = record.get(USERS.ID);
                    UserDTO dto = map.getOrDefault(userId, UserDTO.builder()
                            .id(userId)
                            .username(record.get(USERS.USERNAME))
                            .email(record.get(USERS.EMAIL))
                            .phone(record.get(USERS.PHONE))
                            .active(record.get(USERS.ACTIVE))
                            .name(record.get(USER_PROFILES.NAME))
                            .avatar(record.get(USER_PROFILES.AVATAR))
                            .roles(new HashSet<>())
                            .build());

                    String role = record.get("role_name", String.class);
                    if (role != null && !role.isBlank()) {
                        dto.getRoles().add(role);
                    }

                    map.put(userId, dto);
                });

        List<UserDTO> users = new ArrayList<>(map.values());
        return new Page<>(pageable, users);
    }

    // --- Nếu muốn dùng tên cột để sort
    private Field<?> resolveSortField(String property) {
        if (property == null || property.isEmpty()) return USERS.ID;
        String dbField = property.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        Field<?> field = USERS.field(dbField);
        return field != null ? field : USERS.ID;
    }

    // --- Cập nhật trạng thái login
    public void updateUserLastLogin(Long userId, String username) {
        dsl.update(USERS)
                .set(USERS.USERNAME, val(username))
                .set(USERS.LAST_LOGIN, val(LocalDateTime.now()))
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public int updatePassword(Long userId, String passwordHash) {
        return dsl.update(USERS)
                .set(USERS.PASSWORD_HASH, passwordHash)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Mở khóa user
    public int unlockUser(Long userId) {
        return dsl.update(USERS)
                .set(USERS.LOCKED_UNTIL, (LocalDateTime) null)
                .set(USERS.ACTIVE, val(UserStatus.ACTIVE))
                .where(USERS.ID.eq(userId))
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

    // --- Cập nhật login time
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
                .set(USERS.ACTIVE, val(UserStatus.LOCKED))
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
        dto.setActive(r.get(USERS.ACTIVE));
        return dto;
    }

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

    // Phân trang UserDTO theo role, nhưng vẫn lấy tất cả trạng thái active
    public Page<UserDTO> findAllUsersByRole(String roleName, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();

        // Join user + profile + role
        var baseQuery = dsl.select(
                        USERS.ID,
                        USERS.USERNAME,
                        USERS.EMAIL,
                        USERS.PHONE,
                        USERS.ACTIVE,
                        USER_PROFILES.NAME,
                        USER_PROFILES.AVATAR,
                        ROLES.NAME.as("role_name")
                )
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .leftJoin(USER_ROLES).on(USERS.ID.eq(USER_ROLES.USER_ID))
                .leftJoin(ROLES).on(USER_ROLES.ROLE_ID.eq(ROLES.ID));

        // Nếu truyền roleName, chỉ lấy user có role đó
        if (roleName != null && !roleName.isBlank()) {
            baseQuery = (SelectOnConditionStep<org.jooq.Record8<Long, String, String, String, Short, String, String, String>>) baseQuery.where(ROLES.NAME.eq(roleName));
        }

        // Tổng số bản ghi
        long total = dsl.fetchCount(baseQuery);

        // Sắp xếp
        var order = pageable.getFirstOrder();
        var sortField = order.getPropertyOrDefault("ID");
        boolean asc = order.isAsc();

        // Lấy dữ liệu
        List<UserDTO> users = baseQuery
                .orderBy(asc ? USERS.field(sortField).asc() : USERS.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetch(record -> UserDTO.builder()
                        .id(record.get(USERS.ID))
                        .username(record.get(USERS.USERNAME))
                        .email(record.get(USERS.EMAIL))
                        .phone(record.get(USERS.PHONE))
                        .active(record.get(USERS.ACTIVE)) // giữ trường active
                        .name(record.get(USER_PROFILES.NAME))
                        .avatar(record.get(USER_PROFILES.AVATAR))
                        .roles(Set.of(Optional.ofNullable(record.get("role_name", String.class)).orElse("")))
                        .build()
                );

        pageable.setTotal(total);
        return new Page<>(pageable, users);
    }

    public Long create(String username, String email, String passwordHash) {
        return dsl.insertInto(USERS)
                .set(USERS.UID, val(UUID.randomUUID()))
                .set(USERS.USERNAME, val(username))
                .set(USERS.EMAIL, val(email))
                .set(USERS.PASSWORD_HASH, val(passwordHash))
                .set(USERS.FAILED_ATTEMPTS, val(0))
                .set(USERS.ACTIVE, val(UserStatus.ACTIVE))
                .set(USERS.LAST_LOGIN, (LocalDateTime) null)
                .set(USERS.LOCKED_UNTIL, (LocalDateTime) null)
                .set(USERS.CREATED_AT, val(LocalDateTime.now()))
                .returning(USERS.ID)
                .fetchOne()
                .getId();
    }

    public int update(Long id, String email, String passwordHash, short status) {
        return dsl.update(USERS)
                .set(USERS.EMAIL, val(email))
                .set(USERS.PASSWORD_HASH, val(passwordHash))
                .set(USERS.ACTIVE, val(status))
                .where(USERS.ID.eq(id))
                .execute();
    }

    public int delete(Long userId) {
        dsl.deleteFrom(USER_TOKENS).where(USER_TOKENS.USER_ID.eq(userId)).execute();
        dsl.deleteFrom(USER_ROLES).where(USER_ROLES.USER_ID.eq(userId)).execute();
        dsl.deleteFrom(USER_PROFILES).where(USER_PROFILES.USER_ID.eq(userId)).execute();

        return dsl.deleteFrom(USERS)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Deactivate user (INACTIVE)
    public int deactivateUser(Long userId) {
        return dsl.update(USERS)
                .set(USERS.ACTIVE, val(UserStatus.INACTIVE))
                .where(USERS.ID.eq(userId))
                .execute();
    }

    // --- Lấy tất cả user
    public List<UserDTO> findAll() {
        return dsl.select(getUserFields())
                .from(USERS)
                .leftJoin(USER_PROFILES).on(USERS.ID.eq(USER_PROFILES.USER_ID))
                .fetch(this::mapToDTO);
    }
}
