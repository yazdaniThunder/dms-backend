package com.sima.dms.domain.enums;

import static com.sima.dms.utils.Responses.forbidden;

public enum DocumentRequestStateEnum {

    REGISTERED("ثبت شده"),
    BRANCH_CONFIRMED("تایید شده در شعبه"),
    BRANCH_REJECTED("عدم تایید در شعبه"),
    DOCUMENT_OFFICE_CONFIRMED("تایید شده در اداره اسناد"),
    DOCUMENT_OFFICE_REJECTED("عدم تایید در اداره اسناد"),
    UPLOAD_FILE_OFFICE("اسکن فایل در اداره اسناد"),
    SENT_DOCUMENT_REQUESTED("ارسال به شعبه درخواست دهنده"),
    RECEIVE_DOCUMENT_REQUESTED("دریافت در شعبه درخواست دهنده"),
    EXPIRED_REQUEST("درخواست منقضی شده");

    private String persianTitle;

    DocumentRequestStateEnum(String persianTitle) {
        this.persianTitle = persianTitle;
    }

    public String getPersianTitle() {
        return persianTitle;
    }

    public static DocumentRequestStateEnum nextState(DocumentRequestStateEnum current, WorkflowOperationState operation) {

        switch (current) {
            case REGISTERED:
                switch (operation) {
                    case confirm:
                        return BRANCH_CONFIRMED;
                    case reject:
                        return BRANCH_REJECTED;
                }
                throw forbidden("Condition not valid");

            case BRANCH_CONFIRMED:
                switch (operation) {
                    case confirm:
                        return DOCUMENT_OFFICE_CONFIRMED;
                    case reject:
                        return DOCUMENT_OFFICE_REJECTED;
                }
                throw forbidden("Condition not valid");

            case DOCUMENT_OFFICE_CONFIRMED:
                return UPLOAD_FILE_OFFICE;

            case UPLOAD_FILE_OFFICE:
                return SENT_DOCUMENT_REQUESTED;

            case SENT_DOCUMENT_REQUESTED:
                return RECEIVE_DOCUMENT_REQUESTED;

            case BRANCH_REJECTED:
            case DOCUMENT_OFFICE_REJECTED:
            case RECEIVE_DOCUMENT_REQUESTED:
            case EXPIRED_REQUEST:
                throw forbidden("Workflow is ended");
        }
        throw forbidden("DocumentSet state not found");
    }
}
