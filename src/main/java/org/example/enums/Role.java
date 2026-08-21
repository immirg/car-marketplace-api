package org.example.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Set.of(
            Permission.CAR_READ,
            Permission.CAR_CREATE,
            Permission.CAR_EDIT_OWN,
            Permission.CAR_SELL_OWN,
            Permission.PREMIUM_BUY
    )),
    SELLER(Set.of(
            Permission.CAR_READ,
            Permission.CAR_CREATE,
            Permission.CAR_EDIT_OWN,
            Permission.CAR_SELL_OWN,
            Permission.CAR_DELETE_OWN,
            Permission.PREMIUM_BUY
    )),
    PLATFORM_MANAGER(Set.of(
            Permission.CAR_READ,
            Permission.REVIEW_MODERATE,
            Permission.CAR_DELETE_ANY,
            Permission.BRAND_MODEL_MANAGE,
            Permission.USER_BLOCK
    )),
    PLATFORM_ADMIN(EnumSet.allOf(Permission.class));

    private final Set<Permission> permissions;
}
