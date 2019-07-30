package generator;
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
            @RequestParam(value="schema", defaultValue="World") String schema,
            @RequestParam(value="targetpackage", defaultValue="com.example") String targetPackage,
            @RequestParam(value="classname", defaultValue="Example") String className,
            @RequestParam(value="targetlanguage", defaultValue="java") String targetLanguage,
            @RequestParam(value="sourcetype", defaultValue="yamlschema") String yamlSchema,
            @RequestParam(value="annotationstyle", defaultValue="jackson2") String annotationStyle,
            @RequestParam(value="usedoublenumbers", defaultValue="true") String useDoubleNumbers,
            @RequestParam(value="includeaccessors", defaultValue="true") String includeAccessors,
            @RequestParam(value="includeadditionalproperties", defaultValue="true") String includeAdditionalProperties,
            @RequestParam(value="propertyworddelimiters", defaultValue="-_") String propertWordDelimiters
    ) {
        System.out.printf(schema);
        JCodeModel codeModel = new JCodeModel();
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() { // set config option by overriding method
                return true;
            }
        };


        SchemaMapper mapper = new SchemaMapper(
                new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator()
        );
        try {
            mapper.generate(codeModel, className, targetPackage, schema);
            CodeWriter codeWriter = new SingleStreamCodeWriter(System.out);
            codeModel.build(codeWriter);
        }catch (Exception e){
            return e.toString();
        }

        return "abc";
    }
}
