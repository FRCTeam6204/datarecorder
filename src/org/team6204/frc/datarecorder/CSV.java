package org.team6204.frc.datarecorder;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class CSV {
    private LinkedHashMap<String, List<String>> data = new LinkedHashMap<String, List<String>>();

    public void addField(String name, List<String> values) {
        data.put(name, values);
    }

    public void addField(String name, String[] values) {
        data.put(name, Arrays.asList(values));
    }

    public List<String> getField(String name) {
        return data.get(name);
    }

    public LinkedHashMap<String, List<String>> getData() {
        return data;
    }

    public void write(String path) throws IOException {
        Collection<String> keys = data.keySet();
        StringBuilder contents = new StringBuilder(String.join(",", keys) + "\n");
        for (int i = 0; i < keys.stream().map( (String k) -> data.get(k).size() ).max(Integer::compare).get(); i++) {
            final int idx = i;
            contents.append(
                    String.join(
                            ",",
                            (Iterable<String>) keys.stream().map( (String k) -> {
                                List<String> values = data.get(k);
                                String value = values.size() < idx ? "" : values.get(idx);
                                return value;
                            } )::iterator) + "\n"
            );
        }

        Files.write(Paths.get(path), contents.toString().getBytes());
    }

    public void read(String path) throws IOException {
        List<String> csv = Files.readAllLines(Paths.get(path));

        for (String field : Arrays.asList(csv.get(0).split(","))) {
            data.put(field, new ArrayList<String>());
        }

        String[] keys = data.keySet().toArray(new String[0]);
        for (String line : csv.subList(1, csv.size() - 1)) {
            List<String> vals = Arrays.asList(line.split(","));
            for (int i = 0; i < keys.length; i++) {
                String value = "";
                if (vals.size() > i) {
                    value = vals.get(i);
                }
                data.get(keys[i]).add(value);
            }
        }
    }
}
