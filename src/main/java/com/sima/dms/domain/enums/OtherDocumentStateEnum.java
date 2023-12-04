package com.sima.dms.domain.enums;

import static com.sima.dms.utils.Responses.forbidden;

public enum OtherDocumentStateEnum {

    REGISTERED("ثبت شده"),
    SENT("ارسال شده"),
    BRANCH_CONFIRMED("تایید شده در شعبه"),
    BRANCH_REJECTED("عدم تایید در شعبه");

    private String persianTitle;

    OtherDocumentStateEnum(String persianTitle) {
        this.persianTitle = persianTitle;
    }

    public String getPersianTitle() {
        return persianTitle;
    }

    public static OtherDocumentStateEnum nextState(OtherDocumentStateEnum content, WorkflowOperationState operation) {

        switch (content) {
            case REGISTERED:
                    return SENT;
            case SENT: {
                switch (operation) {
                    case confirm:
                        return BRANCH_CONFIRMED;
                    case reject:
                        return BRANCH_REJECTED;
                }
                throw forbidden("Condition not valid");
            }
            case BRANCH_REJECTED:
                return SENT;
            case BRANCH_CONFIRMED:
                throw forbidden("Workflow is ended");
        }
        throw forbidden("otherDocument state not found");
    }
}
