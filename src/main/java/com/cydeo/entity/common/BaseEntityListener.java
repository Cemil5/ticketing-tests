package com.cydeo.entity.common;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.entity.common.UserPrincipal;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

//@Component  // we don't need this since we don't create bean from this class
public class BaseEntityListener extends AuditingEntityListener {


    @PrePersist
    private void onPrePersist(BaseEntity baseEntity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        baseEntity.setInsertDateTime(LocalDateTime.now());
        baseEntity.setLastUpdateDateTime(LocalDateTime.now());

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {  // checks for valid user
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            baseEntity.setInsertUserId(principal.getId());
            baseEntity.setLastUpdateUserId(principal.getId());
        }
    }

    @PreUpdate
    private void onPreUpdate(BaseEntity baseEntity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        baseEntity.setLastUpdateDateTime(LocalDateTime.now());

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            baseEntity.setLastUpdateUserId(principal.getId());
        }
    }
}
