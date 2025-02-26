package com.luminabackend.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String storeFile(MultipartFile file, UUID referenceId) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("referenceId", referenceId);

        Object fileId = gridFsTemplate
                .store(file.getInputStream(),
                        file.getOriginalFilename(),
                        file.getContentType(),
                        metaData);
        return fileId.toString();
    }

    public GridFsResource getFile(String fileId) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        return gridFsTemplate.getResource(file);
    }

    public void deleteFile(String fileId) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
    }

    public void deleteAll(UUID referenceId) {
        gridFsTemplate.delete(new Query(Criteria.where("metadata.referenceId").is(referenceId)));
    }
}
