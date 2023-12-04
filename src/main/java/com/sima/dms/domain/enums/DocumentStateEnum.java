package com.sima.dms.domain.enums;

import com.sima.dms.utils.Responses;

import static com.sima.dms.utils.Responses.forbidden;

public enum DocumentStateEnum {

    NOT_CHECKED("بررسی نشده"),
    PRIMARY_CONFIRMED("تایید شده اولیه"),
    STAGNANT("راکد"),
    CONFLICTING("فایل دارای مغایرت"),
    SENT_CONFLICT("دارای مغایرت ارسال شده"),
    CONFLICTED_STAGNANT("راکد دارای مغایرت"),
    FIX_CONFLICT("رفع مغایرت فایل"),
    CONFIRM_FIX_CONFLICT("تایید رفع مغایرت فایل");

    private String persianName;

    DocumentStateEnum(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }

    public static DocumentStateEnum documentNextStep(DocumentStateEnum current, WorkflowOperation operation) {

        switch (current) {

            case NOT_CHECKED:
                switch (operation) {
                    case confirm:
                        return PRIMARY_CONFIRMED;
                    case conflicting:
                        return CONFLICTING;
                }
                throw Responses.forbidden("Condition not valid");

            case PRIMARY_CONFIRMED:
                switch (operation) {
                    case confirm:
                        return STAGNANT;
                    case reject:
                        return NOT_CHECKED;
                }
                throw Responses.forbidden("Condition not valid");

            case CONFLICTING:
                switch (operation) {
                    case confirm:
                        return SENT_CONFLICT;
                    case reject:
                        return NOT_CHECKED;
                    case expired:
                        return CONFLICTED_STAGNANT;
                }
                throw Responses.forbidden("Condition not valid");

            case SENT_CONFLICT:
                switch (operation) {
                    case confirm:
                        return FIX_CONFLICT;
                    case expired:
                        return CONFLICTED_STAGNANT;
                }
                throw Responses.forbidden("Condition not valid");

            case FIX_CONFLICT:
                return CONFIRM_FIX_CONFLICT;

            case CONFIRM_FIX_CONFLICT:
                return NOT_CHECKED;

            case STAGNANT:
            case CONFLICTED_STAGNANT:
                throw Responses.forbidden("Workflow is ended");
        }
        throw Responses.forbidden("Document state not found");
    }
}
