package de.greenrobot.daogenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DaoGenerator {

    public void createDaos(String outDir, Schema schema) throws Exception {
        long start = System.currentTimeMillis();
        File outDirFile = new File(outDir);
        outDirFile.mkdirs();

        Configuration config = new Configuration();
        config.setClassForTemplateLoading(this.getClass(), "/");
        config.setObjectWrapper(new DefaultObjectWrapper());

        Template templateDao = config.getTemplate("dao.ftl");
        Template templateEntity = config.getTemplate("entity.ftl");

        schema.init2ndPass();

        List<Entity> entities = schema.getEntities();

        for (Entity entity : entities) {
            generate(templateDao, outDirFile, entity.getJavaPackageDao(), entity.getClassNameDao(), schema, entity);
            if (!entity.isProtobuf()) {
                generate(templateEntity, outDirFile, entity.getJavaPackage(), entity.getClassName(), schema, entity);
            }
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Processed " + entities.size() + " entities in " + time + "ms");
    }

    private void generate(Template template, File outDirFile, String javaPackage, String javaClass, Schema schema,
            Entity entity) throws TemplateException, IOException {
        String packageSubPath = javaPackage.replace('.', '/');
        File packagePath = new File(outDirFile, packageSubPath);
        packagePath.mkdirs();

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("schema", schema);
        root.put("entity", entity);

        File file = new File(packagePath, javaClass + ".java");
        Writer writer = new FileWriter(file);
        try {
            template.process(root, writer);
            writer.flush();
            System.out.println("Written " + file.getAbsolutePath());
        } finally {
            writer.close();
        }
    }

}
