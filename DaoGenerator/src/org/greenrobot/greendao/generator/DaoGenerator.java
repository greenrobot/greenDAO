/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.greenrobot.greendao.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

/**
 * Once you have your model created, use this class to generate entities and DAOs.
 *
 * @author Markus
 */
public class DaoGenerator {

    private Pattern patternKeepIncludes;
    private Pattern patternKeepFields;
    private Pattern patternKeepMethods;

    private Template templateDao;
    private Template templateDaoMaster;
    private Template templateDaoSession;
    private Template templateEntity;
    private Template templateDaoUnitTest;
    private Template templateContentProvider;

    public DaoGenerator() throws IOException {
        System.out.println("greenDAO Generator");
        System.out.println("Copyright 2011-2016 Markus Junginger, greenrobot.de. Licensed under GPL V3.");
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY");

        patternKeepIncludes = compilePattern("INCLUDES");
        patternKeepFields = compilePattern("FIELDS");
        patternKeepMethods = compilePattern("METHODS");

        Configuration config = getConfiguration("dao.ftl");
        templateDao = config.getTemplate("dao.ftl");
        templateDaoMaster = config.getTemplate("dao-master.ftl");
        templateDaoSession = config.getTemplate("dao-session.ftl");
        templateEntity = config.getTemplate("entity.ftl");
        templateDaoUnitTest = config.getTemplate("dao-unit-test.ftl");
        templateContentProvider = config.getTemplate("content-provider.ftl");
    }

    private Configuration getConfiguration(String probingTemplate) throws IOException {
        Configuration config = new Configuration(Configuration.VERSION_2_3_23);
        config.setClassForTemplateLoading(getClass(), "/");

        try {
            config.getTemplate(probingTemplate);
        } catch (TemplateNotFoundException e) {
            // When running from an IDE like IntelliJ, class loading resources may fail for some reason (Gradle is OK)

            // Working dir is module dir
            File dir = new File("src/main/resources/");
            if (!dir.exists()) {
                // Working dir is base module dir
                dir = new File("DaoGenerator/src/main/resources/");
            }
            if (dir.exists() && new File(dir, probingTemplate).exists()) {
                config.setDirectoryForTemplateLoading(dir);
                config.getTemplate(probingTemplate);
            } else {
                throw e;
            }
        }
        return config;
    }

    private Pattern compilePattern(String sectionName) {
        int flags = Pattern.DOTALL | Pattern.MULTILINE;
        return Pattern.compile(".*^\\s*?//\\s*?KEEP " + sectionName + ".*?\n(.*?)^\\s*// KEEP " + sectionName
                + " END.*?\n", flags);
    }

    /** Generates all entities and DAOs for the given schema. */
    public void generateAll(Schema schema, String outDir) throws Exception {
        generateAll(schema, outDir, null, null);
    }

    /** Generates all entities and DAOs for the given schema. */
    public void generateAll(Schema schema, String outDir, String outDirEntity, String outDirTest) throws Exception {
        long start = System.currentTimeMillis();

        File outDirFile = toFileForceExists(outDir);
        File outDirEntityFile = outDirEntity != null ? toFileForceExists(outDirEntity) : outDirFile;
        File outDirTestFile = outDirTest != null ? toFileForceExists(outDirTest) : null;

        schema.init2ndPass();
        schema.init3rdPass();

        System.out.println("Processing schema version " + schema.getVersion() + "...");

        List<Entity> entities = schema.getEntities();
        for (Entity entity : entities) {
            generate(templateDao, outDirFile, entity.getJavaPackageDao(), entity.getClassNameDao(), schema, entity);
            if (!entity.isProtobuf() && !entity.isSkipGeneration()) {
                generate(templateEntity, outDirEntityFile, entity.getJavaPackage(), entity.getClassName(), schema, entity);
            }
            if (outDirTestFile != null && !entity.isSkipGenerationTest()) {
                String javaPackageTest = entity.getJavaPackageTest();
                String classNameTest = entity.getClassNameTest();
                File javaFilename = toJavaFilename(outDirTestFile, javaPackageTest, classNameTest);
                if (!javaFilename.exists()) {
                    generate(templateDaoUnitTest, outDirTestFile, javaPackageTest, classNameTest, schema, entity);
                } else {
                    System.out.println("Skipped " + javaFilename.getCanonicalPath());
                }
            }
            for (ContentProvider contentProvider : entity.getContentProviders()) {
                Map<String, Object> additionalObjectsForTemplate = new HashMap<>();
                additionalObjectsForTemplate.put("contentProvider", contentProvider);
                generate(templateContentProvider, outDirFile, entity.getJavaPackage(), entity.getClassName()
                        + "ContentProvider", schema, entity, additionalObjectsForTemplate);
            }
        }
        generate(templateDaoMaster, outDirFile, schema.getDefaultJavaPackageDao(),
                schema.getPrefix() + "DaoMaster", schema, null);
        generate(templateDaoSession, outDirFile, schema.getDefaultJavaPackageDao(),
                schema.getPrefix() + "DaoSession", schema, null);

        long time = System.currentTimeMillis() - start;
        System.out.println("Processed " + entities.size() + " entities in " + time + "ms");
    }

    protected File toFileForceExists(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException(filename
                    + " does not exist. This check is to prevent accidental file generation into a wrong path.");
        }
        return file;
    }

    private void generate(Template template, File outDirFile, String javaPackage, String javaClassName, Schema schema,
                          Entity entity) throws Exception {
        generate(template, outDirFile, javaPackage, javaClassName, schema, entity, null);
    }

    private void generate(Template template, File outDirFile, String javaPackage, String javaClassName, Schema schema,
                          Entity entity, Map<String, Object> additionalObjectsForTemplate) throws Exception {
        Map<String, Object> root = new HashMap<>();
        root.put("schema", schema);
        root.put("entity", entity);
        if (additionalObjectsForTemplate != null) {
            root.putAll(additionalObjectsForTemplate);
        }
        try {
            File file = toJavaFilename(outDirFile, javaPackage, javaClassName);
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();

            if (entity != null && entity.getHasKeepSections()) {
                checkKeepSections(file, root);
            }

            Writer writer = new FileWriter(file);
            try {
                template.process(root, writer);
                writer.flush();
                System.out.println("Written " + file.getCanonicalPath());
            } finally {
                writer.close();
            }
        } catch (Exception ex) {
            System.err.println("Data map for template: " + root);
            System.err.println("Error while generating " + javaPackage + "." + javaClassName + " ("
                    + outDirFile.getCanonicalPath() + ")");
            throw ex;
        }
    }

    private void checkKeepSections(File file, Map<String, Object> root) {
        if (file.exists()) {
            try {
                String contents = new String(DaoUtil.readAllBytes(file));

                Matcher matcher;

                matcher = patternKeepIncludes.matcher(contents);
                if (matcher.matches()) {
                    root.put("keepIncludes", matcher.group(1));
                }

                matcher = patternKeepFields.matcher(contents);
                if (matcher.matches()) {
                    root.put("keepFields", matcher.group(1));
                }

                matcher = patternKeepMethods.matcher(contents);
                if (matcher.matches()) {
                    root.put("keepMethods", matcher.group(1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected File toJavaFilename(File outDirFile, String javaPackage, String javaClassName) {
        String packageSubPath = javaPackage.replace('.', '/');
        File packagePath = new File(outDirFile, packageSubPath);
        return new File(packagePath, javaClassName + ".java");
    }

}
