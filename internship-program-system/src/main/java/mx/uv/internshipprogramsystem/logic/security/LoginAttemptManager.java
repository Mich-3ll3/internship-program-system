package mx.uv.internshipprogramsystem.logic.security;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class LoginAttemptManager {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;

    public void validateAccountIsNotLocked(
            int failedAttempts,
            Timestamp lockDate
    ) throws BusinessException {
        if (lockDate != null && isStillLocked(lockDate)) {
            throw new BusinessException(
                "La cuenta está bloqueada temporalmente. "
                + "Intente nuevamente más tarde."
            );
        }
    }

    public boolean shouldLockAccount(int failedAttempts) {
        boolean shouldLock = failedAttempts + 1 >= MAX_FAILED_ATTEMPTS;

        return shouldLock;
    }

    private boolean isStillLocked(Timestamp lockDate) {
        Instant unlockTime = lockDate.toInstant().plus(
            LOCK_TIME_MINUTES,
            ChronoUnit.MINUTES
        );

        boolean isLocked = Instant.now().isBefore(unlockTime);

        return isLocked;
    }
}