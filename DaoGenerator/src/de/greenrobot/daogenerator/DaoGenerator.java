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
        generateAll(outDir, null, schema);
    }

    /** Generates all entities and DAOs for the given schema. */
    public void generateAll(String outDir, String outDirTest, Schema schema) throws Exception {
        long start = System.currentTimeMillis();

        System.out.println("greenDAO Generator (preview)");
        System.out.println("Copyright 2011 Markus Junginger, greenrobot.de. Licensed under GPL V3.");

        File outDirFile = toFileForceExists(outDir);

        File outDirTestFile = null;
        if (outDirTest != null) {
            outDirTestFile = toFileForceExists(outDirTest);
        }

        Configuration config = new Configuration();
        config.setClassForTemplateLoading(this.getClass(), "/");
        config.setObjectWrapper(new DefaultObjectWrapper());

        Template templateDao = config.getTemplate("dao.ftl");
        Template templateDaoMaster = config.getTemplate("dao-master.ftl");
        Template templateEntity = config.getTemplate("entity.ftl");
        Template templateDaoUnitTest = config.getTemplate("dao-unit-test.ftl");

        schema.init2ndPass();

        List<Entity> entities = schema.getEntities();

        for (Entity entity : entities) {
            generate(templateDao, outDirFile, entity.getJavaPackageDao(), entity.getClassNameDao(), schema, entity);
            if (!entity.isProtobuf() && !entity.isSkipGeneration()) {
                generate(templateEntity, outDirFile, entity.getJavaPackage(), entity.getClassName(), schema, entity);
            }
            if (outDirTestFile != null) {
                String javaPackageTest = entity.getJavaPackageTest();
                String classNameTest = entity.getClassNameTest();
                File javaFilename = toJavaFilename(outDirTestFile, javaPackageTest, classNameTest);
                if (!javaFilename.exists()) {
                    generate(templateDaoUnitTest, outDirTestFile, javaPackageTest, classNameTest, schema, entity);
                } else {
                    System.out.println("Skipped " + javaFilename.getCanonicalPath());
                }
            }
        }
        generate(templateDaoMaster, outDirFile, schema.getDefaultJavaPackageDao(), "DaoMaster", schema, null);

        long time = System.currentTimeMillis() - start;
        System.out.println("Processed " + entities.size() + " entities in " + time + "ms");
    }

    protected File toFileForceExists(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException(filename
                    + " does not exist. This check is to prevent accidential file generation into a wrong path.");
        }
        return file;
    }

    private void generate(Template template, File outDirFile, String javaPackage, String javaClassName, Schema schema,
            Entity entity) throws TemplateException, IOException {
        File file = toJavaFilename(outDirFile, javaPackage, javaClassName);
        file.getParentFile().mkdirs();

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("schema", schema);
        root.put("entity", entity);

        Writer writer = new FileWriter(file);
        try {
            template.process(root, writer);
            writer.flush();
            System.out.println("Written " + file.getCanonicalPath());
        } finally {
            writer.close();
        }
    }

    protected File toJavaFilename(File outDirFile, String javaPackage, String javaClassName) {
        String packageSubPath = javaPackage.replace('.', '/');
        File packagePath = new File(outDirFile, packageSubPath);
        File file = new File(packagePath, javaClassName + ".java");
        return file;
    }

}
