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

        List<Table> tables = schema.getTables();

        for (Table table : tables) {
            processTable(temp, outDirFile, schema, table);
        }
        long time = System.currentTimeMillis()-start ;
        System.out.println("Processed " + tables.size() + " table(s) in " + time + "ms");
    }

    private void processTable(Template daoTemplate, File outDirFile, Schema schema, Table table) throws TemplateException,
            IOException {
        String packageSubPath = table.getJavaPackageDao().replace('.', '/');
        File packagePath = new File(outDirFile, packageSubPath);
        packagePath.mkdirs();

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("schema", schema);
        root.put("table", table);

        File file = new File(packagePath, table.getClassNameDao() + ".java");
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
