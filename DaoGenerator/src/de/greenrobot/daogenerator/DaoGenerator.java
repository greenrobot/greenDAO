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

        Template temp = config.getTemplate("dao.ftl");

        schema.init2ndPass();

        List<Entity> entities = schema.getEntities();

        for (Entity table : entities) {
            processEntity(temp, outDirFile, schema, table);
        }
        long time = System.currentTimeMillis()-start ;
        System.out.println("Processed " + entities.size() + " entities in " + time + "ms");
    }

    private void processEntity(Template daoTemplate, File outDirFile, Schema schema, Entity entity) throws TemplateException,
            IOException {
        String packageSubPath = entity.getJavaPackageDao().replace('.', '/');
        File packagePath = new File(outDirFile, packageSubPath);
        packagePath.mkdirs();

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("schema", schema);
        root.put("entity", entity);

        File file = new File(packagePath, entity.getClassNameDao() + ".java");
        Writer writer = new FileWriter(file);
        try {
            daoTemplate.process(root, writer);
            writer.flush();
            System.out.println("Written "+file.getAbsolutePath());
        } finally {
            writer.close();
        }
    }

}
