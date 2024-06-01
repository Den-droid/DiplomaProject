package org.example.apiapplication.helpers;

import org.example.apiapplication.entities.extraction.FieldExtraction;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScholarExtractionHelper {
    public List<ProfileFieldValue> extractScholarProfile(String url,
                                                         List<FieldExtraction> fieldsExtraction) throws IOException {
        List<ProfileFieldValue> allProfileFieldValues = new ArrayList<>();
        Document document = Jsoup.connect(url).get();

        List<ProfileFieldValue> profileFieldValues = new ArrayList<>();
        for (FieldExtraction fieldExtraction : fieldsExtraction) {
            if (fieldExtraction.getRule() != null && !fieldExtraction.getRule().isEmpty()) {
                switch (fieldExtraction.getRuleType().getName()) {
                    case TEXT -> allProfileFieldValues.add(extractText(document, fieldExtraction));
                    case ORDERED -> allProfileFieldValues.add(extractOrdered(document, fieldExtraction));
                    case PROPERTY -> allProfileFieldValues.add(extractProperty(document, fieldExtraction));
                    case LABELS -> profileFieldValues = extractLabels(document, fieldExtraction);
                }

                allProfileFieldValues.addAll(profileFieldValues);
                profileFieldValues.clear();
            }
        }

        return allProfileFieldValues;
    }

    private ProfileFieldValue extractText(Document document, FieldExtraction fieldExtraction) {
        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
        profileFieldValue.setField(fieldExtraction.getField());

        Elements fieldElements = document
                .select(fieldExtraction.getRule());

        if (!fieldElements.isEmpty()) {
            profileFieldValue.setValue(fieldElements.get(0).text());
        } else {
            profileFieldValue.setValue("");
        }

        return profileFieldValue;
    }

    private ProfileFieldValue extractOrdered(Document document, FieldExtraction fieldExtraction) {
        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
        profileFieldValue.setField(fieldExtraction.getField());

        Elements fieldElements = document
                .select(fieldExtraction.getRule());

        if (!fieldElements.isEmpty()) {
            int index = Integer.parseInt(fieldExtraction.getKey());

            profileFieldValue.setValue(fieldElements.get(index).text());
        } else {
            profileFieldValue.setValue("");
        }

        return profileFieldValue;
    }

    private ProfileFieldValue extractProperty(Document document, FieldExtraction fieldExtraction) {
        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
        profileFieldValue.setField(fieldExtraction.getField());

        Elements fieldElements = document
                .select(fieldExtraction.getRule());

        if (!fieldElements.isEmpty()) {
            String property = fieldExtraction.getKey();

            profileFieldValue.setValue(fieldElements.get(0).attribute(property).getValue());
        } else {
            profileFieldValue.setValue("");
        }

        return profileFieldValue;
    }

    private List<ProfileFieldValue> extractLabels(Document document, FieldExtraction fieldExtraction) {
        return extractList(document, fieldExtraction);
    }

    private List<ProfileFieldValue> extractList(Document document, FieldExtraction fieldExtraction) {
        List<ProfileFieldValue> profileFieldValues = new ArrayList<>();

        Elements fieldElements = document
                .select(fieldExtraction.getRule());

        if (!fieldElements.isEmpty()) {
            for (Element fieldElement : fieldElements) {
                ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                profileFieldValue.setField(fieldExtraction.getField());
                profileFieldValue.setValue(fieldElement.text());

                profileFieldValues.add(profileFieldValue);
            }
        }

        return profileFieldValues;
    }
}
