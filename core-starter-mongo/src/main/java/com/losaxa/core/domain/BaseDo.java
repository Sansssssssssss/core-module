package com.losaxa.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import com.losaxa.core.common.UidGenerator;

import java.time.LocalDateTime;

import static com.losaxa.core.mongo.constant.CommonField.CREATED_BY;
import static com.losaxa.core.mongo.constant.CommonField.CREATED_DATE;
import static com.losaxa.core.mongo.constant.CommonField.ID;
import static com.losaxa.core.mongo.constant.CommonField.LAST_MODIFIED_BY;
import static com.losaxa.core.mongo.constant.CommonField.LAST_MODIFIED_DATE;

@ApiModel(description = "基础DO")
@Data
public class BaseDo {

    @JsonFormat(shape = JsonFormat.Shape.STRING) //解决 Long 值过大 JS 丢失进度
    @ApiModelProperty("id")
    @Id
    @Field(ID)
    protected Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("创建人")
    @Field(CREATED_BY)
    @CreatedBy
    protected Long createdBy;

    @ApiModelProperty("创建时间")
    @Field(CREATED_DATE)
    @CreatedDate
    protected Long createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("最后修改人")
    @Field(LAST_MODIFIED_BY)
    @LastModifiedBy
    protected Long lastModifiedBy;

    @ApiModelProperty("最后修改时间")
    @LastModifiedDate
    @Field(LAST_MODIFIED_DATE)
    protected Long lastModifiedDate;

    public void generateId(UidGenerator uidGenerator) {
        this.id = uidGenerator.getUID();
    }

}
