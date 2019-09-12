package com.hiflink.common.rules;


import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;


public class DroolUtils {

    public static KieContainer LoadRule(String ruleName, String ruleContent ) throws IOException {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write(ruleName, ResourceFactory.newInputStreamResource(new ByteArrayInputStream(ruleContent.getBytes())));

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(new Message.Level[]{Message.Level.ERROR})) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        } else {
            return ks.newKieContainer(kr.getDefaultReleaseId());
        }
    }

    public static StatelessKieSession LoadSessionFromRule(String ruleContent ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        Collection<KiePackage> pkgs;


        if (!ruleContent.trim().isEmpty()) {
            Resource myResource = ResourceFactory.newReaderResource((Reader) new StringReader(ruleContent));
            kbuilder.add(myResource, ResourceType.DRL);
        }

        // Check the builder for errors
        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            throw new RuntimeException("Unable to compile drl\".");
        }

        // get the compiled packages (which are serializable)
        pkgs = kbuilder.getKnowledgePackages();

        // add the packages to a knowledgebase (deploy the knowledge packages).
        kbase.addPackages(pkgs);

        return kbase.newStatelessKieSession();

    }

    public static StatelessKieSession LoadSessionFromRule(String[] ruleContentArray ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        Collection<KiePackage> pkgs;

        for (String ruleContent : ruleContentArray) {
            if (!ruleContent.trim().isEmpty()) {
                Resource myResource = ResourceFactory.newReaderResource((Reader) new StringReader(ruleContent));
                kbuilder.add(myResource, ResourceType.DRL);
            }
        }

        // Check the builder for errors
        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            throw new RuntimeException("Unable to compile drl\".");
        }

        // get the compiled packages (which are serializable)
        pkgs = kbuilder.getKnowledgePackages();

        // add the packages to a knowledgebase (deploy the knowledge packages).
        kbase.addPackages(pkgs);
//        kbase.addPackages(pkgs);

        return kbase.newStatelessKieSession();

    }


    public static KieContainer openDirectory(File drlDir ) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
        File[] listOfFiles = drlDir.listFiles();
        if (listOfFiles != null) {
            File[] var5 = listOfFiles;
            int var6 = listOfFiles.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                File fileEntry = var5[var7];
                if (fileEntry.isFile()) {
                    kfs.write(ResourceFactory.newFileResource(fileEntry));
                }
            }
        }

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(new Message.Level[]{Message.Level.ERROR})) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        } else {
            return ks.newKieContainer(kr.getDefaultReleaseId());
        }
    }

    public static Boolean areRulesComilables( String[] subRules ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
       InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        Collection<KiePackage> pkgs;


        //Rule content, can be one or mor rules,
        //We splitt by '|' and create a new rule for each result
//        String[] subRules = ruleContent.split("\\|");

        for (String subRule : subRules) {
            if (!subRule.trim().isEmpty()) {
                Resource myResource = ResourceFactory.newReaderResource((Reader) new StringReader(subRule));
                kbuilder.add(myResource, ResourceType.DRL);
            }
        }

        return kbuilder.hasErrors();

    }

    public static String[] getRulesErrorMessages( String[] subRules ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        Collection<KiePackage> pkgs;


        //Rule content, can be one or mor rules,
        //We splitt by '|' and create a new rule for each result
//        String[] subRules = ruleContent.split("\\|");

        for (String subRule : subRules) {
            if (!subRule.trim().isEmpty()) {
                Resource myResource = ResourceFactory.newReaderResource((Reader) new StringReader(subRule));
                kbuilder.add(myResource, ResourceType.DRL);
            }
        }

        String[] errorList;
        if (kbuilder.hasErrors()) {
            errorList = new String[kbuilder.getErrors().size()];

            Iterator<KnowledgeBuilderError> errorIterator = kbuilder.getErrors().iterator();
            int i = 0;
            while (errorIterator.hasNext()) {
                KnowledgeBuilderError knowledgeBuilderError = errorIterator.next();
                errorList[i] = knowledgeBuilderError.getMessage();
                i++;
            }
        } else {
            errorList = new String[0];
        }
        return errorList;
    }
}

