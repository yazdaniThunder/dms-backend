package com.sima.dms.domain.enums;

import com.sima.dms.utils.Responses;

import static com.sima.dms.utils.Responses.forbidden;
import static com.sima.dms.utils.Responses.unauthorized;

public enum DocumentSetStateEnum {

    REGISTERED("ثبت شده"),
    BRANCH_CONFIRMED("تایید شده در شعبه"),
    REJECTED("عدم تایید"),
    DELETED("حذف شده"),
    PRIMARY_CONFIRMED("تایید شده اولیه"),
    SCANNED("اسکن شده"),
    CONFLICTING(" دسته اسناد دارای مغایرت"),
    SENT_CONFLICT("دارای مغایرت ارسال شده"),
    PROCESSED("پردازش شده"),
    FIX_CONFLICT("رفع مغایرت دسته اسناد"),
    COMPLETED("تکمیل شده");

    private String persianName;

    DocumentSetStateEnum(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }

    public static DocumentSetStateEnum nextStep(DocumentSetStateEnum current, WorkflowOperation operation, RoleEnum role) {
        switch (current) {

            case REGISTERED:
                switch (operation) {
                    case confirm:
                        if (role.equals(RoleEnum.ADMIN) || role.equals(RoleEnum.BA))
                            return BRANCH_CONFIRMED;
                        else
                            throw Responses.unauthorized("you have not permission to this operation.");
                    case reject:
                        return REJECTED;
                    case delete:
                        return DELETED;
                }
                throw Responses.forbidden("Condition not valid");

            case BRANCH_CONFIRMED:
                switch (operation) {
                    case confirm:
                        return PRIMARY_CONFIRMED;
                    case conflicting:
                        return CONFLICTING;
                }
                throw Responses.forbidden("Condition not valid");

            case PRIMARY_CONFIRMED:
                return SCANNED;

            case SCANNED:
                return PROCESSED;

            case CONFLICTING:
                return FIX_CONFLICT;

            case FIX_CONFLICT:
                return BRANCH_CONFIRMED;

            case REJECTED:
                return REGISTERED;

            case PROCESSED:
                return COMPLETED;
            case COMPLETED:
            case DELETED:
                throw Responses.forbidden("Workflow is ended");
        }
        throw Responses.forbidden("DocumentSet state not found");
    }

}
