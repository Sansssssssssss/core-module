package com.losaxa.core.swag;

import com.losaxa.core.common.DateUtil;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.oas.mappers.SchemaMapper;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.ModelNamesRegistry;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

@Primary
@Component
public class SwagSchemaMapper extends SchemaMapper {

    private static final Logger logger = LoggerFactory.getLogger(SwagSchemaMapper.class);

    @SuppressWarnings("all")
    @Override
    protected Map<String, Schema> mapModels(Map<String, ModelSpecification> specifications,
                                            ModelNamesRegistry modelNamesRegistry) {
        Field example = ReflectionUtils.findField(Schema.class, "example");
        example.setAccessible(true);
        Map<String, Schema> schemaMap = super.mapModels(specifications, modelNamesRegistry);
        try {
            for (Schema classSchema : schemaMap.values()) {
                for (Object propertySchema : classSchema.getProperties().values()) {
                    Object exampleValue = ((Schema) propertySchema).getExample();
                    if (exampleValue != null && StringUtils.isNotBlank(exampleValue.toString())) {
                        continue;
                    }
                    //时间数据格式化处理
                    if (propertySchema instanceof DateTimeSchema) {
                        example.set(propertySchema, DateUtil.toStr(LocalDateTime.now()));
                        continue;
                    }
                    //生成测试数据例子
                    //todo:luosx:待优化
                    //if (propertySchema instanceof StringSchema) {
                    //    ((StringSchema) propertySchema).setExample(OtherSource.getInstance().randomChineseIdiom());
                    //} else if (propertySchema instanceof NumberSchema) {
                    //    //有可能是整形/浮点型
                    //    ((NumberSchema) propertySchema).setExample(NumberSource.getInstance().randomInt(1, 101));
                    //} else if (propertySchema instanceof ArraySchema) {
                    //    ArraySchema                        arraySchema           = (ArraySchema) propertySchema;
                    //    ModelSpecification                 modelSpecification    = specifications.get(classSchema.getName());
                    //    List<PropertySpecification>        properties            = (List<PropertySpecification>) modelSpecification.getCompound().get().getProperties();
                    //    PropertySpecification              propertySpecification = properties.stream().filter(e -> e.getName().equals(arraySchema.getName())).findFirst().get();
                    //    Optional<ScalarModelSpecification> scalar                = propertySpecification.getType().getCollection().get().getModel().getScalar();
                    //    //基本类型列表
                    //    if (scalar.isPresent()) {
                    //        String type = scalar.get().getType().getType();
                    //        if ("string".equals(type)) {
                    //            arraySchema.setExample(Lists.newArrayList(OtherSource.getInstance().randomChineseIdiom()));
                    //        }
                    //        continue;
                    //    }
                    //    //引用类型列表
                    //    Optional<ReferenceModelSpecification> reference = propertySpecification.getType().getCollection().get().getModel().getReference();
                    //    if (reference.isPresent()) {
                    //        QualifiedModelName qualifiedModelName = reference.get().getKey().getQualifiedModelName();
                    //        Class<?>           modelClass         = Class.forName(qualifiedModelName.getNamespace() + "." + qualifiedModelName.getName());
                    //        arraySchema.setExample(Lists.newArrayList(modelClass.newInstance()));
                    //        continue;
                    //    }
                    //}
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return schemaMap;
    }
}
