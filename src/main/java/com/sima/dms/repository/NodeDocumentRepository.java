package com.sima.dms.repository;

import com.sima.dms.domain.entity.NodeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface NodeDocumentRepository extends JpaRepository<NodeDocument, String> {

    List<NodeDocument> findAllByParent(String parent);

    void deleteAllByUuidIn(List<String> uuids);

    @Modifying
    @Transactional
    @Query(value = "insert into OKM_NODE_THUMBNAIL (NT_DOCUMENT,NT_THUMBNAIL) VALUES (:documentUuid,:thumbnailUuid)", nativeQuery = true)
    void setThumbnail(@Param("documentUuid") String documentUuid, @Param("thumbnailUuid") String thumbnailUuid);

    @Modifying
    @Transactional
    @Query("update NodeDocument set text =:text , textExtracted=:textExtracted  where uuid=:uuid")
    void updateNodeDocument(@Param("uuid") String uuid, @Param("text") String text, @Param("textExtracted") boolean textExtracted);

    @Modifying
    @Transactional
    @Query("update NodeDocument set textExtracted=:textExtracted where uuid=:uuid")
    void updateTextExtracted(@Param("uuid") String uuid, @Param("textExtracted") boolean textExtracted);

    @Modifying
    @Transactional
    @Query("update NodeDocument document set document.description=:description where document.uuid=:uuid")
    void updateDescription(@Param("uuid") String uuid, @Param("description") String description);

    @Query(value = "select mimeType from NodeDocument where uuid =:uuid")
    String getMimeType(@Param("uuid") String uuid);

    @Query(value = "select nd.NBS_UUID from OKM_NODE_DOCUMENT nd" +
            " join OKM_NODE_ROLE_PERMISSION nrp on nrp.NRP_NODE = nd.NBS_UUID" +
            " join OKM_NODE_BASE onb on onb.NBS_UUID = nd.NBS_UUID" +
            " where nrp.NRP_ROLE =:role and onb.NBS_PARENT =:parent", nativeQuery = true)
    List<String> getDocumentByRolePermission(@Param("role") String role, @Param("parent") String parent);

    @Query(value = "select nd.NBS_UUID from OKM_NODE_DOCUMENT nd" +
            " join OKM_NODE_ROLE_PERMISSION nrp on nrp.NRP_NODE = nd.NBS_UUID" +
            " join OKM_NODE_BASE onb on onb.NBS_UUID = nd.NBS_UUID" +
            " where nrp.NRP_ROLE in:roles and onb.NBS_PARENT =:parent", nativeQuery = true)
    List<String> getDocumentByRolePermission(@Param("roles") List<String> roles, @Param("parent") String parent);

    @Modifying
    @Transactional
    @Query(value = "delete from OKM_NODE_PROPERTY where NPG_NODE =:uuid", nativeQuery = true)
    void deleteMetadataByUuid(String uuid);

    @Modifying
    @Transactional
    @Query(value = "insert into OKM_NODE_PROPERTY(NPG_GROUP, NPG_NAME, NPG_VALUE, NPG_NODE) VALUES (:group,:name,:value,:uuid)", nativeQuery = true)
    void insertMetadata(String group, String name, String value, String uuid);

    @Modifying
    @Transactional
    @Query(value = "insert into KB_CARD (CARD_NO, ACCOUNT_NO, CARD_NAME, CVV1, CVV2, EXPIRE_DATE, FILE_NAME, FILE_DATE, FILE_PATH,FILE_LAST_MODIFIED)\n" +
            "values (:cardNo,:accountNo,:cardName,:cvv1,:cvv2,:expireDate,:fileName,:fileDate,:filePath,:fileLastModified)", nativeQuery = true)
    void insert(String cardNo, String accountNo, String cardName, String cvv1, String cvv2, String expireDate, String fileName, String fileDate, String filePath, String fileLastModified);
}