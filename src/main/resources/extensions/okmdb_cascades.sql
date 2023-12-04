-- DMS_DOCUMENT

ALTER TABLE `okmdb`.`DMS_DOCUMENT`
DROP
FOREIGN KEY `FKiicgvivwmjicnfpr042xrohnq`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT`
    ADD CONSTRAINT `FKiicgvivwmjicnfpr042xrohnq`
        FOREIGN KEY (`state_id`)
            REFERENCES `okmdb`.`DMS_DOCUMENT_STATE` (`id`)
            ON DELETE CASCADE;


ALTER TABLE `okmdb`.`DMS_DOCUMENT`
DROP
FOREIGN KEY `FKd169c24uih3os6fajyoe5onbk`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT`
    ADD CONSTRAINT `FKd169c24uih3os6fajyoe5onbk`
        FOREIGN KEY (`file_NBS_UUID`)
            REFERENCES `okmdb`.`OKM_NODE_DOCUMENT` (`NBS_UUID`)
            ON DELETE CASCADE;

-------------------------------------------------------------------------------------------------
-- DMS_DOCUMENT_STATE

ALTER TABLE `okmdb`.`DMS_DOCUMENT_STATE`
DROP
FOREIGN KEY `FKeya1n6xty2e73mwlwu7usrjw2`,
DROP
FOREIGN KEY `FKi59xpreyi5711q3vd5on7fhym`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_STATE`
    ADD CONSTRAINT `FKeya1n6xty2e73mwlwu7usrjw2`
        FOREIGN KEY (`document_id`)
            REFERENCES `okmdb`.`DMS_DOCUMENT` (`id`)
            ON DELETE CASCADE,
ADD CONSTRAINT `FKi59xpreyi5711q3vd5on7fhym`
  FOREIGN KEY (`lastState_id`)
  REFERENCES `okmdb`.`DMS_DOCUMENT_STATE` (`id`)
  ON
DELETE
CASCADE;


------------------------------------------------------------------------------------------------
-- DMS_DOCUMENT_CONFLICT

ALTER TABLE `okmdb`.`DMS_DOCUMENT_CONFLICT`
DROP
FOREIGN KEY `FKhupro7xe7onvhne6nqnswqg3d`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_CONFLICT`
    ADD CONSTRAINT `FKhupro7xe7onvhne6nqnswqg3d`
        FOREIGN KEY (`DOCUMENT_INFO_ID`)
            REFERENCES `okmdb`.`DMS_DOCUMENT` (`id`)
            ON DELETE CASCADE;

--------------------------------------------------------------------------------------------------
-- DMS_DC_REASON

ALTER TABLE `okmdb`.`DMS_DC_REASON`
DROP
FOREIGN KEY `FK2lnigb8q92tlbbi8teess01ad`,
DROP
FOREIGN KEY `FKeunqdcmrptm1ca4cl000kgncd`;
ALTER TABLE `okmdb`.`DMS_DC_REASON`
    ADD CONSTRAINT `FK2lnigb8q92tlbbi8teess01ad`
        FOREIGN KEY (`DC_ID`)
            REFERENCES `okmdb`.`DMS_DOCUMENT_CONFLICT` (`id`)
            ON DELETE CASCADE,
ADD CONSTRAINT `FKeunqdcmrptm1ca4cl000kgncd`
  FOREIGN KEY (`REASON_ID`)
  REFERENCES `okmdb`.`DMS_CONFLICT_REASON` (`id`)
  ON
DELETE
CASCADE;


-------------------------------------------------------------------------------------------------------
-- DMS_DOCUMENT_SET
ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET`
DROP
FOREIGN KEY `FKp1h6lq7fgykqaao1v1ler64q9`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET`
    ADD CONSTRAINT `FKp1h6lq7fgykqaao1v1ler64q9`
        FOREIGN KEY (`state_id`)
            REFERENCES `okmdb`.`DMS_DOCUMENT_SET_STATE` (`id`)
            ON DELETE CASCADE;

ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET`
DROP FOREIGN KEY `FKkdevg1ewdpdcmi97b0ikkrgf`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET`
    ADD CONSTRAINT `FKkdevg1ewdpdcmi97b0ikkrgf`
        FOREIGN KEY (`FILE_STATUS_ID`)
            REFERENCES `okmdb`.`DMS_FILE_STATUS` (`id`)
            ON DELETE SET NULL;

-------------------------------------------------------------------------------------------------------
-- DMS_DOCUMENT_SET_STATE

ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET_STATE`
DROP
FOREIGN KEY `FK6pwbhh4b256ew1ke08l94jp4u`,
DROP
FOREIGN KEY `FKt5v7p623k3f7n5nnswpw2tiky`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET_STATE`
    ADD CONSTRAINT `FK6pwbhh4b256ew1ke08l94jp4u`
        FOREIGN KEY (`documentSet_id`)
            REFERENCES `okmdb`.`DMS_DOCUMENT_SET` (`id`)
            ON DELETE CASCADE,
ADD CONSTRAINT `FKt5v7p623k3f7n5nnswpw2tiky`
  FOREIGN KEY (`lastState_id`)
  REFERENCES `okmdb`.`DMS_DOCUMENT_SET_STATE` (`id`)
  ON
DELETE
CASCADE;

-------------------------------------------------------------------------------------------------------
-- DMS_DOCUMENT_SET_CONFLICT

ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET_CONFLICT`
DROP
FOREIGN KEY `FKiit19qpnithfx1bcsvcwv9x4i`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_SET_CONFLICT`
    ADD CONSTRAINT `FKiit19qpnithfx1bcsvcwv9x4i`
        FOREIGN KEY (`documentSet_id`)
            REFERENCES `okmdb`.`DMS_DOCUMENT_SET` (`id`)
            ON DELETE CASCADE;

-------------------------------------------------------------------------------------------------------
-- DMS_DSC_REASON

ALTER TABLE `okmdb`.`DMS_DSC_REASON`
DROP
FOREIGN KEY `FKc4pgjlo4b08fx1vb2s41y99in`,
DROP
FOREIGN KEY `FKes17b302g3eikkaws5juddb7h`;
ALTER TABLE `okmdb`.`DMS_DSC_REASON`
    ADD CONSTRAINT `FKc4pgjlo4b08fx1vb2s41y99in`
        FOREIGN KEY (`REASON_ID`)
            REFERENCES `okmdb`.`DMS_CONFLICT_REASON` (`id`)
            ON DELETE CASCADE,
ADD CONSTRAINT `FKes17b302g3eikkaws5juddb7h`
  FOREIGN KEY (`DSC_ID`)
  REFERENCES `okmdb`.`DMS_DOCUMENT_SET_CONFLICT` (`id`)
  ON
DELETE
CASCADE;

-------------------------------------------------------------------------------------------------------
-- DMS_DOCUMENT_REQUEST

ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST`
DROP
FOREIGN KEY `FKal55onw879hp0lq3qlloy5kbj`,
DROP
FOREIGN KEY `FKfmdwcnppcis9ou4l3ngq6wkhk`,
DROP
FOREIGN KEY `FKlgo6js5s5gu74kd2d97cc4gak`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST`
    ADD CONSTRAINT `FKal55onw879hp0lq3qlloy5kbj`
        FOREIGN KEY (`branchFile_NBS_UUID`)
            REFERENCES `okmdb`.`OKM_NODE_DOCUMENT` (`NBS_UUID`)
            ON DELETE CASCADE,
ADD CONSTRAINT `FKfmdwcnppcis9ou4l3ngq6wkhk`
  FOREIGN KEY (`officeFile_NBS_UUID`)
  REFERENCES `okmdb`.`OKM_NODE_DOCUMENT` (`NBS_UUID`)
  ON
DELETE
CASCADE,
ADD CONSTRAINT `FKlgo6js5s5gu74kd2d97cc4gak`
  FOREIGN KEY (`lastState_id`)
  REFERENCES `okmdb`.`DMS_DOCUMENT_REQUEST_STATE` (`id`)
  ON DELETE
CASCADE;

ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST`
DROP
FOREIGN KEY `FK20plp400wnqgxhrjh7ykch3qx`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST`
    ADD CONSTRAINT `FK20plp400wnqgxhrjh7ykch3qx`
        FOREIGN KEY (`REASON_ID`)
            REFERENCES `okmdb`.`DMS_DOCUMENT_REQUEST_REASON` (`id`)
            ON DELETE SET NULL;


ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST_OFFICE_FILE`
DROP
FOREIGN KEY `FKcuojjumjseeyyo671c8tafx5h`,
DROP
FOREIGN KEY `FKjlcsp3xep1wmiwwkpc2fvqam3`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST_OFFICE_FILE`
    ADD CONSTRAINT `FKcuojjumjseeyyo671c8tafx5h`
        FOREIGN KEY (`UUID`)
            REFERENCES `okmdb`.`OKM_NODE_DOCUMENT` (`NBS_UUID`)
            ON DELETE CASCADE,
ADD CONSTRAINT `FKjlcsp3xep1wmiwwkpc2fvqam3`
  FOREIGN KEY (`DOCUMENT_REQUEST_ID`)
  REFERENCES `okmdb`.`DMS_DOCUMENT_REQUEST` (`id`)
  ON
DELETE
CASCADE;

alter table DMS_DOCUMENT_REQUEST_STATE_SEEN
drop foreign key FKs994kgaeo8otjboxtmuvmcywu;
alter table DMS_DOCUMENT_REQUEST_STATE_SEEN
    add constraint FKs994kgaeo8otjboxtmuvmcywu
        foreign key (DOCUMENT_REQUEST_STATE_ID) references DMS_DOCUMENT_REQUEST_STATE (id);

alter table DMS_DOCUMENT_REQUEST_OFFICE_FILE
drop foreign key FKjlcsp3xep1wmiwwkpc2fvqam3;
alter table DMS_DOCUMENT_REQUEST_OFFICE_FILE
    add constraint FKjlcsp3xep1wmiwwkpc2fvqam3
        foreign key (DOCUMENT_REQUEST_ID) references DMS_DOCUMENT_REQUEST (id)
            on delete cascade;

alter table DMS_DOCUMENT_REQUEST_STATE
drop foreign key FK63bdvhnpbxy356twi8jl6cd1x;
alter table DMS_DOCUMENT_REQUEST_STATE
    add constraint FK63bdvhnpbxy356twi8jl6cd1x
        foreign key (DOCUMENT_REQUEST_ID) references DMS_DOCUMENT_REQUEST (id)
            on delete cascade;

-------------------------------------------------------------------------------------------------------
-- DMS_DOCUMENT_REQUEST_STATE

ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST_STATE`
DROP
FOREIGN KEY `FK63bdvhnpbxy356twi8jl6cd1x`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_REQUEST_STATE`
    ADD CONSTRAINT `FK63bdvhnpbxy356twi8jl6cd1x`
        FOREIGN KEY (`DOCUMENT_REQUEST_ID`)
            REFERENCES `okmdb`.`DMS_DOCUMENT_REQUEST` (`id`)
            ON DELETE CASCADE;

-----------------------------------------------------------------------------------------------------
-- dms_document_ocr

ALTER TABLE `okmdb`.`DMS_DOCUMENT_OCR`
DROP
FOREIGN KEY `FK9w5gwmq4mda1127vufe4stnir`;
ALTER TABLE `okmdb`.`DMS_DOCUMENT_OCR`
    ADD CONSTRAINT `FK9w5gwmq4mda1127vufe4stnir`
        FOREIGN KEY (`nodeDocument_NBS_UUID`)
            REFERENCES `okmdb`.`OKM_NODE_DOCUMENT` (`NBS_UUID`)
            ON DELETE CASCADE;

------------------------------------------------------------------------------------------------------

ALTER TABLE `okmdb`.`OKM_NODE_PROPERTY`
DROP
FOREIGN KEY `FK3B9645A41842E9DC`;
ALTER TABLE `okmdb`.`OKM_NODE_PROPERTY`
    ADD CONSTRAINT `FK3B9645A41842E9DC`
        FOREIGN KEY (`NPG_NODE`)
            REFERENCES `okmdb`.`OKM_NODE_BASE` (`NBS_UUID`)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;
---------------------------------------------------------------------------------------------------
ALTER TABLE `okmdb`.`OKM_NODE_DOCUMENT`
DROP
FOREIGN KEY `FKAA2538EA4829197B`;
ALTER TABLE `okmdb`.`OKM_NODE_DOCUMENT`
    ADD CONSTRAINT `FKAA2538EA4829197B`
        FOREIGN KEY (`NBS_UUID`)
            REFERENCES `okmdb`.`OKM_NODE_BASE` (`NBS_UUID`)
            ON DELETE CASCADE;

--------------------------------------------------------------------------------------------------

ALTER TABLE `okmdb`.`OKM_NODE_FOLDER`
DROP
FOREIGN KEY `FKBE9C2FFD4829197B`;
ALTER TABLE `okmdb`.`OKM_NODE_FOLDER`
    ADD CONSTRAINT `FKBE9C2FFD4829197B`
        FOREIGN KEY (`NBS_UUID`)
            REFERENCES `okmdb`.`OKM_NODE_BASE` (`NBS_UUID`)
            ON DELETE CASCADE;


----------------------------------------------------------------------------------------------------

ALTER TABLE `okmdb`.`OKM_NODE_ROLE_PERMISSION`
DROP
FOREIGN KEY `FKF4FBBA89916AFDF5`;
ALTER TABLE `okmdb`.`OKM_NODE_ROLE_PERMISSION`
    ADD CONSTRAINT `FKF4FBBA89916AFDF5`
        FOREIGN KEY (`NRP_NODE`)
            REFERENCES `okmdb`.`OKM_NODE_BASE` (`NBS_UUID`)
            ON DELETE CASCADE;

---------------------------------------------------------------------------------------------------

ALTER TABLE `okmdb`.`OKM_NODE_USER_PERMISSION`
DROP
FOREIGN KEY `FK68755814301DAFB8`;
ALTER TABLE `okmdb`.`OKM_NODE_USER_PERMISSION`
    ADD CONSTRAINT `FK68755814301DAFB8`
        FOREIGN KEY (`NUP_NODE`)
            REFERENCES `okmdb`.`OKM_NODE_BASE` (`NBS_UUID`)
            ON DELETE CASCADE;

---------------------------------------------------------------------------------------------------
-- DMS_OTHER_DOCUMENT

ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT`
DROP
FOREIGN KEY `FK8dvu9n3p4d7p7kx7hg06r3hol`,
DROP
FOREIGN KEY `FKr2i61v67c1t3k02pqxmygej4r`;

ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT`
    ADD CONSTRAINT `FK8dvu9n3p4d7p7kx7hg06r3hol`
        FOREIGN KEY (`FILE_Type_ID`)
            REFERENCES `okmdb`.`DMS_FILE_TYPE` (`id`)
            ON DELETE SET NULL,
ADD CONSTRAINT `FKr2i61v67c1t3k02pqxmygej4r`
  FOREIGN KEY (`LAST_STATE_ID`)
  REFERENCES `okmdb`.`DMS_OTHER_DOCUMENT_STATE` (`id`)
  ON
DELETE
CASCADE;

---------------------------------------------------------------------------------------------------
-- DMS_OTHER_DOCUMENT_FILE

ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_FILE`
DROP
FOREIGN KEY `FK2qhrxv7xsu2gwoniwx84jn0u4`,
DROP
FOREIGN KEY `FKa1si3v0x96vik56p4n7e8w7ga`,
DROP
FOREIGN KEY `FKs1mtw7lp3qa4mi838bxoxuk3a`,
DROP
FOREIGN KEY `FKtow2j91cp9bv90d1ssy206g5j`;
ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_FILE`
    ADD CONSTRAINT `FK2qhrxv7xsu2gwoniwx84jn0u4`
        FOREIGN KEY (`OTHER_DOCUMENT_TYPE_ID`)
            REFERENCES `okmdb`.`DMS_OTHER_DOCUMENT_TYPE` (`id`)
            ON DELETE SET NULL,
ADD CONSTRAINT `FKa1si3v0x96vik56p4n7e8w7ga`
  FOREIGN KEY (`FILE_STATUS_ID`)
  REFERENCES `okmdb`.`DMS_FILE_STATUS` (`id`)
  ON
DELETE
SET NULL,
ADD CONSTRAINT `FKs1mtw7lp3qa4mi838bxoxuk3a`
  FOREIGN KEY (`OTHER_DOCUMENT_ID`)
  REFERENCES `okmdb`.`DMS_OTHER_DOCUMENT` (`id`)
  ON DELETE
CASCADE,
ADD CONSTRAINT `FKtow2j91cp9bv90d1ssy206g5j`
  FOREIGN KEY (`FILE_UUID`)
  REFERENCES `okmdb`.`OKM_NODE_DOCUMENT` (`NBS_UUID`)
  ON DELETE
SET NULL;

---------------------------------------------------------------------------------------------------
-- dms_other_document_type

ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_TYPE`
DROP
FOREIGN KEY `FK86a56wb1ujovl69w0vo2ea2o7`;
ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_TYPE`
    ADD CONSTRAINT `FK86a56wb1ujovl69w0vo2ea2o7`
        FOREIGN KEY (`FILE_TYPE_ID`)
            REFERENCES `okmdb`.`DMS_FILE_TYPE` (`id`)
            ON DELETE CASCADE;

---------------------------------------------------------------------------------------------------
-- DMS_FILE_STATUS

ALTER TABLE `okmdb`.`DMS_FILE_STATUS`
DROP
FOREIGN KEY `FK6bvb3hdjtc9ct7mebo0k6la9c`;
ALTER TABLE `okmdb`.`DMS_FILE_STATUS`
    ADD CONSTRAINT `FK6bvb3hdjtc9ct7mebo0k6la9c`
        FOREIGN KEY (`FILE_TYPE_ID`)
            REFERENCES `okmdb`.`DMS_FILE_TYPE` (`id`)
            ON DELETE CASCADE;


---------------------------------------------------------------------------------------------------
-- DMS_OTHER_DOCUMENT_STATE

ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_STATE`
DROP
FOREIGN KEY `FK48pmyw5d6g9ir3xb8l6s011cw`;
ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_STATE`
    ADD CONSTRAINT `FK48pmyw5d6g9ir3xb8l6s011cw`
        FOREIGN KEY (`OTHER_DOCUMENT_ID`)
            REFERENCES `okmdb`.`DMS_OTHER_DOCUMENT` (`id`)
            ON DELETE CASCADE;
---------------------------------------------------------------------------------------------------
-- dms_other_document_state_seen

ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_STATE_SEEN`
DROP
FOREIGN KEY `FKjlrqxyfst88prxeejdoerqff4`;
ALTER TABLE `okmdb`.`DMS_OTHER_DOCUMENT_STATE_SEEN`
    ADD CONSTRAINT `FKjlrqxyfst88prxeejdoerqff4`
        FOREIGN KEY (`FILE_OTHER_DOCUMENT_STATE_ID`)
            REFERENCES `okmdb`.`DMS_OTHER_DOCUMENT_STATE` (`id`)
            ON DELETE CASCADE;
