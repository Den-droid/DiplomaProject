package org.example.apiapplication.helpers;

import org.example.apiapplication.entities.fields.Field;
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
    public List<ProfileFieldValue> extractScholarProfile(String url, List<Field> fields) throws IOException {
        List<ProfileFieldValue> allProfileFieldValues = new ArrayList<>();
        Document document = Jsoup.connect(url).get();

        List<ProfileFieldValue> profileFieldValues = new ArrayList<>();
        for (Field field : fields) {
            if (field.getRule() != null && !field.getRule().isEmpty()) {
                switch (field.getRuleType().getName()) {
                    case TEXT -> allProfileFieldValues.add(extractText(document, field));
                    case ORDERED -> allProfileFieldValues.add(extractOrdered(document, field));
                    case PROPERTY -> allProfileFieldValues.add(extractProperty(document, field));
                    case LABELS -> profileFieldValues = extractLabels(document, field);
                    case YEAR_CITATIONS -> profileFieldValues = extractYearCitations(document, field);
                }

                allProfileFieldValues.addAll(profileFieldValues);
                profileFieldValues.clear();
            }
        }

        return allProfileFieldValues;
    }

    private ProfileFieldValue extractText(Document document, Field field) {
        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
        profileFieldValue.setField(field);

        Elements fieldElements = document
                .select(field.getRule());

        if (!fieldElements.isEmpty()) {
            profileFieldValue.setValue(fieldElements.get(0).text());
        } else {
            profileFieldValue.setValue("");
        }

        return profileFieldValue;
    }

    private ProfileFieldValue extractOrdered(Document document, Field field) {
        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
        profileFieldValue.setField(field);
        profileFieldValue.setKey(field.getKey());

        Elements fieldElements = document
                .select(field.getRule());

        if (!fieldElements.isEmpty()) {
            int index = Integer.parseInt(field.getKey());

            profileFieldValue.setValue(fieldElements.get(index).text());
        } else {
            profileFieldValue.setValue("");
        }

        return profileFieldValue;
    }

    private ProfileFieldValue extractProperty(Document document, Field field) {
        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
        profileFieldValue.setField(field);
        profileFieldValue.setKey(field.getKey());

        Elements fieldElements = document
                .select(field.getRule());

        if (!fieldElements.isEmpty()) {
            String property = field.getKey();

            profileFieldValue.setValue(fieldElements.get(0).attribute(property).getValue());
        } else {
            profileFieldValue.setValue("");
        }

        return profileFieldValue;
    }

    private List<ProfileFieldValue> extractLabels(Document document, Field field) {
        return extractList(document, field);
    }

    private List<ProfileFieldValue> extractList(Document document, Field field) {
        List<ProfileFieldValue> profileFieldValues = new ArrayList<>();

        Elements fieldElements = document
                .select(field.getRule());

        if (!fieldElements.isEmpty()) {
            for (Element fieldElement : fieldElements) {
                ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                profileFieldValue.setField(field);
                profileFieldValue.setValue(fieldElement.text());

                profileFieldValues.add(profileFieldValue);
            }
        }

        return profileFieldValues;
    }

    private List<ProfileFieldValue> extractYearCitations(Document document, Field field) {
        List<ProfileFieldValue> profileFieldValues = new ArrayList<>();

        Elements fieldElements = document
                .select(field.getRule());
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
                profileFieldValue.setField(field);
                profileFieldValue.setKey(entry.getKey());
                profileFieldValue.setValue(String.valueOf(entry.getValue()));

                profileFieldValues.add(profileFieldValue);
            }
        }

        return profileFieldValues;
    }
}
