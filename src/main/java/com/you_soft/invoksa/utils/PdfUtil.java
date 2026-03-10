package com.you_soft.invoksa.utils;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfUtil {

    private final TemplateEngine templateEngine;

    public PdfUtil(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateInvoicePdf(Map<String, Object> data) {

        try {

            Context context = new Context();
            context.setVariables(data);

            String htmlContent = templateEngine.process("invoice", context);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(baos);
            //System.out.println("HTML généré : " + htmlContent);

            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur génération PDF");
        }
    }}