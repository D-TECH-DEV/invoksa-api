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
            // Préparer le contexte Thymeleaf
            Context context = new Context();
            context.setVariables(data);

            // Générer le HTML dynamique
            String htmlContent = templateEngine.process("invoice.html", context);

            // Convertir HTML -> PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(baos);
            baos.close();

            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, renvoyer un PDF vide ou un petit texte
            return "Erreur lors de la génération du PDF".getBytes();
        }
    }
}