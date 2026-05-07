package com.you_soft.invoksa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;

    public String getInvoiceFromAi(String description, String lang) {

        String prompt = """
    CONTEXTE : Tu es le moteur intelligent d'Invoksa, une application de facturation pour prestataires.
    MISSION : Convertis la description de l'utilisateur en une facture structurée.

    ### DONNÉES D'ENTRÉE
    - Langue cible : %s
    - Texte utilisateur : %s

    ### STRUCTURE JSON ATTENDUE
    {
      "client": { "nom": "string", "adresse": "string (si présent)" },
      "prestataire": { "nom": "string", "type_service": "string" },
      "items": [
        {
          "description": "Libellé précis du service",
          "quantité": nombre (ex: heures, jours, unités),
          "unité": "h", "j", ou "unité",
          "prix_unitaire": nombre,
          "total_ligne": nombre
        }
      ],
      "metriques": {
        "sous_total": nombre,
        "tva_applicable": boolean,
        "taux_tva": 18,
        "montant_tva": nombre,
        "total_ttc": nombre
      },
      "statut": "PENDING"
    }

    ### RÈGLES MÉTIER (STRICTES)
    1. ANALYSE : Si l'utilisateur dit "J'ai travaillé 3 jours à 150k", crée une ligne avec quantité: 3, unité: 'j', prix: 150000.
    2. PROFESSIONNALISME : Reformule les descriptions pour qu'elles soient pro (ex: "codage site" -> "Développement de fonctionnalités web").
    3. CALCULS : 
       - total_ligne = quantité * prix_unitaire
       - sous_total = somme des total_ligne
       - montant_tva = sous_total * 0.18 (par défaut pour la zone OHADA/CI, sauf si précisé autrement)
       - total_ttc = sous_total + montant_tva
    4. FORMAT : Réponds exclusivement en JSON. Pas de texte avant ou après.

    RÉSULTAT (JSON UNIQUEMENT) :
    """.formatted(lang, description);
        String response = chatModel.call(prompt);
        return extractJson(response);
    }

    private String extractJson(String response) {
        if (response == null) return "{}";

        int firstBrace = response.indexOf('{');
        int lastBrace = response.lastIndexOf('}');

        if (firstBrace >= 0 && lastBrace >= 0 && lastBrace > firstBrace) {
            return response.substring(firstBrace, lastBrace + 1);
        }

        return response.trim();
    }
}