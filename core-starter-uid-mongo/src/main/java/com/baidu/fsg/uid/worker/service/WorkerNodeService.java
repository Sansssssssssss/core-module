package com.baidu.fsg.uid.worker.service;

import com.baidu.fsg.uid.worker.dao.WorkerNodeDAO;
import com.baidu.fsg.uid.worker.entity.WorkerNodeEntity;
import com.losaxa.core.mongo.constant.CommonField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WorkerNodeService {

    private final WorkerNodeDAO workerNodeDAO;

    @Value("${spring.application.name}")
    private String serverName;

    public WorkerNodeService(WorkerNodeDAO workerNodeDAO) {
        this.workerNodeDAO = workerNodeDAO;
    }

    public void addWorkerNode(WorkerNodeEntity workerNodeEntity) {
        workerNodeEntity.setCreated(new Date());
        workerNodeEntity.setModified(new Date());
        workerNodeEntity.setServerName(serverName);
        if (workerNodeEntity.getId() < 1) {
            List<WorkerNodeEntity> list = workerNodeDAO.find(new Query(), PageRequest.of(0, 1, Sort.by(Sort.Order.desc(CommonField.ID))));
            if (list.size() < 1) {
                workerNodeEntity.setId(1);
            } else {
                workerNodeEntity.setId(list.get(0).getId() + 1);
            }
        }
        workerNodeDAO.insert(workerNodeEntity);
    }

    public WorkerNodeEntity getWorkerNodeByHostPort(String host, String port) {
        Optional<WorkerNodeEntity> workerNodeOptional = workerNodeDAO.findOne(Query.query(Criteria.where("hostName").is(host).and("port").is(port)));
        return workerNodeOptional.orElse(null);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
