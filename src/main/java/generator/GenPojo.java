package generator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenPojo {
    @RequestMapping("/generator/preview")
    public String greeting(
            @RequestParam(value = "schema", defaultValue = "World") String schema,
            @RequestParam(value = "targetpackage", defaultValue = "com.example") String targetPackage,
            @RequestParam(value = "classname", defaultValue = "Example") String className,
            @RequestParam(value = "targetlanguage", defaultValue = "java") String targetLanguage,
            @RequestParam(value = "sourcetype", defaultValue = "yamlschema") String sourceType,
            @RequestParam(value = "annotationstyle", defaultValue = "jackson2") String annotationStyle,
            @RequestParam(value = "usedoublenumbers", defaultValue = "true") String useDoubleNumbers,
            @RequestParam(value = "includeaccessors", defaultValue = "true") String includeAccessors,
            @RequestParam(value = "includeadditionalproperties", defaultValue = "true") String includeAdditionalProperties,
            @RequestParam(value = "propertyworddelimiters", defaultValue = "-_") String propertWordDelimiters
    ) {
        JCodeModel codeModel = new JCodeModel();
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() { // set config option by overriding method
                return true;
            }

            @Override
            public Language getTargetLanguage() {
                if (targetLanguage == "scala") {
                    return Language.JAVA;
                } else {
                    return Language.SCALA;
                }
            }

            @Override
            public SourceType getSourceType() {
                switch (sourceType) {
                    case "yamlschema":
                        return SourceType.YAMLSCHEMA;
                    case "yaml":
                        return SourceType.YAML;
                    case "jsonschema":
                        return SourceType.JSONSCHEMA;
                    default:
                        return SourceType.JSON;
                }
            }

            @Override
            public AnnotationStyle getAnnotationStyle() {
                switch (annotationStyle) {
                    case "jackson2":
                        return AnnotationStyle.JACKSON2;
                    case "jackson1":
                        return AnnotationStyle.JACKSON1;
                    case "jackson":
                        return AnnotationStyle.JACKSON;
                    case "GSON":
                        return AnnotationStyle.GSON;
                    case "moshi1":
                        return AnnotationStyle.MOSHI1;
                    default:
                        return AnnotationStyle.NONE;
                }
            }

            @Override
            public boolean isUseDoubleNumbers() {
                if (useDoubleNumbers == "true") {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean isIncludeDynamicAccessors() {
                if (includeAccessors == "true") {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean isIncludeAdditionalProperties() {
                if (includeAdditionalProperties == "true") {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public char[] getPropertyWordDelimiters(){
                return propertWordDelimiters.toCharArray();
            }
        };

        SchemaMapper mapper = new SchemaMapper(
                new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator()
        );
        try {
            mapper.generate(codeModel, className, targetPackage, schema);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            CodeWriter codeWriter = new SingleStreamCodeWriter(stream);
            codeModel.build(codeWriter);
            stream.close();
            return stream.toString();
        } catch (Exception e) {
            return e.toString();
        }

    }
}
