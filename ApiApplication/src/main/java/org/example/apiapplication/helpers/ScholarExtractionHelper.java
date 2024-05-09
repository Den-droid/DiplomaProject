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
import java.util.Map;
import java.util.TreeMap;

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
                    case YEAR_CITATIONS -> profileFieldValues = extractYearCitations(document, fieldExtraction);
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
        profileFieldValue.setKey(fieldExtraction.getKey());

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
        profileFieldValue.setKey(fieldExtraction.getKey());

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

    private List<ProfileFieldValue> extractYearCitations(Document document, FieldExtraction fieldExtraction) {
        List<ProfileFieldValue> profileFieldValues = new ArrayList<>();

        Elements fieldElements = document
                .select(fieldExtraction.getRule());
        Elements yearsElements = fieldElements.select(".gsc_g_t");
        Elements citationElements = fieldElements.select(".gsc_g_a");

        if (!fieldElements.isEmpty()) {
            List<String> years = yearsElements.stream()
                    .map(Element::text)
                    .toList();
            List<String> citations = citationElements.stream()
                    .map(Element::text)
                    .toList();

            List<String> zIndexes = citationElements.stream()
                    .map((x) -> {
                        String[] strings = x.attribute("style").getValue().split("[:;]");
                        return strings[strings.length - 1];
                    })
                    .toList();

            Map<String, Integer> map = new TreeMap<>();
            int citationsIndex = 0;
            int zIndexIndex = 0;
            for (int i = 0; i < years.size(); i++) {
                int citation = 0;
                if (zIndexes.get(zIndexIndex).equals(String.valueOf(years.size() - i))) {
                    citation = Integer.parseInt(citations.get(citationsIndex));
                    citationsIndex++;
                    zIndexIndex++;
                }
                map.put(years.get(i), citation);
            }

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                profileFieldValue.setField(fieldExtraction.getField());
                profileFieldValue.setKey(entry.getKey());
                profileFieldValue.setValue(String.valueOf(entry.getValue()));

                profileFieldValues.add(profileFieldValue);
            }
        }

        return profileFieldValues;
    }
}
