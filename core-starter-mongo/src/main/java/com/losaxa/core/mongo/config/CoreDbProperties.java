package com.losaxa.core.mongo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DB相关配置类
 */
@Data
@ConfigurationProperties(prefix = "app.database")
public class CoreDbProperties {

    /**
     * 编辑DB数据时是否自定生成元数据(createdBy,createdDate,lastModifiedBy,lastModifiedDate)
     */
    private Boolean autoMatedata = true;

}
