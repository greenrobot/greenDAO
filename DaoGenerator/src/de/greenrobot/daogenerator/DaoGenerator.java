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

/**
 * Once you have your model created, use this class to generate entities and DAOs.
 *    
 * @author Markus
 */
public class DaoGenerator {

    /** Generates all entities and DAOs for the given schema. */
    public void generateAll(String outDir, Schema schema) throws Exception {
        long start = System.currentTimeMillis();
        
        System.out.println("greenDAO Generator (preview)");
        
        File outDirFile = new File(outDir);
        if (!outDirFile.exists()) {
            throw new IOException(outDir + " does not exist. This check is to prevent accidential file generation into a wrong path.");
        }

        Configuration config = new Configuration();
        config.setClassForTemplateLoading(this.getClass(), "/");
        config.setObjectWrapper(new DefaultObjectWrapper());

        Template templateDao = config.getTemplate("dao.ftl");
        Template templateDaoMaster = config.getTemplate("dao-master.ftl");
        Template templateEntity = config.getTemplate("entity.ftl");

        schema.init2ndPass();

        List<Entity> entities = schema.getEntities();

        for (Entity entity : entities) {
            generate(templateDao, outDirFile, entity.getJavaPackageDao(), entity.getClassNameDao(), schema, entity);
            if (!entity.isProtobuf() && !entity.isSkipGeneration()) {
                generate(templateEntity, outDirFile, entity.getJavaPackage(), entity.getClassName(), schema, entity);
            }
        }
        generate(templateDaoMaster, outDirFile, schema.getDefaultJavaPackageDao(), "DaoMaster", schema, null);
        
        
        long time = System.currentTimeMillis() - start;
        System.out.println("Processed " + entities.size() + " entities in " + time + "ms");
    }

    private void generate(Template template, File outDirFile, String javaPackage, String javaFilename, Schema schema,
            Entity entity) throws TemplateException, IOException {
        String packageSubPath = javaPackage.replace('.', '/');
        File packagePath = new File(outDirFile, packageSubPath);
        packagePath.mkdirs();

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("schema", schema);
        root.put("entity", entity);

        File file = new File(packagePath, javaFilename + ".java");
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
