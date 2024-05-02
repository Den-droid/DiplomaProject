package org.example.apiapplication.helpers;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ExcelHelper {
    public List<List<String>> getAll(String filename) throws IOException {
        List<List<String>> data = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream != null) {
                try (ReadableWorkbook wb = new ReadableWorkbook(inputStream)) {
                    Sheet sheet = wb.getFirstSheet();
                    try (Stream<Row> rows = sheet.openStream()) {
                        rows.forEach(r -> {
                            List<String> row = new ArrayList<>();

                            for (Cell cell : r) {
                                if (cell != null)
                                    row.add(cell.getRawValue() == null ? "" : cell.getRawValue());
                                else {
                                    row.add("");
                                }
                            }

                            data.add(row);
                        });
                    }
                }
            }
        }

        return data;
    }
}
