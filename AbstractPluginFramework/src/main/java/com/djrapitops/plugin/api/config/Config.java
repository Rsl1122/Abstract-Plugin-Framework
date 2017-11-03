/* 
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package com.djrapitops.plugin.api.config;

import com.djrapitops.plugin.api.utility.log.FileLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * //TODO Class Javadoc Comment
 *
 * @author Rsl1122
 */
public class Config extends ConfigNode {

    private final File file;

    public Config(File file) {
        super("", null, "");
        this.file = file;
    }

    public Config(File file, List<String> currentValues) {
        this(file);
        copyDefaults(currentValues);
    }

    public void read() throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        copyDefaults(file);
    }

    public void copyDefaults(File from) throws IOException {
        copyDefaults(Files.lines(from.toPath(), Charset.forName("UTF-8")).collect(Collectors.toList()));
    }

    public void copyDefaults(List<String> lines) {
        processLines(lines);
    }

    private void processLines(List<String> fileLines) {
        List<String> comments = new ArrayList<>();
        int lastDepth = 0;
        ConfigNode parent = this;
        ConfigNode lastNode = this;
        for (String line : fileLines) {
            System.out.print(fileLines.indexOf(line) + ": ");
            try {
                int depth = FileLogger.getIndentation(line);

                String trimmed = line.trim();
                if (trimmed.startsWith("#")) {
                    comments.add(trimmed);
                    System.out.println("Comment");
                    continue;
                }

                System.out.print("Depth:" + depth + " | ");
                if (depth > lastDepth) {
                    System.out.print("+");
                    parent = lastNode;
                } else if (depth < lastDepth) {
                    // Prevents incorrect indent in the case:
                    // 1:
                    //   2:
                    //     3:
                    // 1:
                    int nDepth = lastDepth;
                    while (nDepth > depth) {
                        System.out.print("-" + nDepth);
                        nDepth = parent.depth;
                        parent = parent.parent;
                    }
                }
                System.out.print(" | ");

                String[] keyAndValue = trimmed.split(":", 2);
                String configKey = keyAndValue[0];

                String value = keyAndValue[1];
                int indexOfHashtag = value.lastIndexOf(" #");
                String valueWithoutComment = indexOfHashtag < 0 ? value : value.substring(0, indexOfHashtag);

                ConfigNode node = new ConfigNode(configKey, parent, valueWithoutComment);
                node.depth = depth;
                node.setComment(new ArrayList<>(comments));
                if (!comments.isEmpty()) {
                    System.out.print("AddComments | ");
                }
                comments.clear();
                lastNode = node;
                lastDepth = depth;
                System.out.println(node.getKey(true));
                parent.addChild(configKey, node);
            } catch (Exception e) {
                throw new IllegalStateException("Malformed File (" + file.getName() + "), Error on line " + fileLines.indexOf(line) + ": " + line, e);
            }
        }
    }

    @Override
    public void save() throws IOException {
        Files.write(file.toPath(), processTree(), Charset.forName("UTF-8"));
    }

    private List<String> processTree() {
        return getLines(this, 0);
    }

    private List<String> getLines(ConfigNode root, int depth) {
        List<String> lines = new ArrayList<>();
        Map<String, ConfigNode> children = root.getChildren();

        for (String key : root.childOrder) {
            ConfigNode node = children.get(key);
            String value = node.getValue();

            for (String commentLine : node.getComment()) {
                StringBuilder comment = new StringBuilder();
                addIndentation(depth, comment);
                comment.append(commentLine);
                lines.add(comment.toString());
            }

            StringBuilder b = new StringBuilder();
            addIndentation(depth, b);
            if (value.startsWith("-")) {
                // Keyline
                lines.add(b.append(key).append(":").toString());
                // List
                String[] list = value.split("-");
                for (String listValue : list) {
                    String v = listValue.trim();
                    if (v.isEmpty()) {
                        continue;
                    }
                    StringBuilder listBuilder = new StringBuilder();
                    addIndentation(depth + 1, listBuilder);
                    listBuilder.append("- ").append(v);
                    lines.add(listBuilder.toString());
                }
            } else {
                b.append(key).append(":").append(value);
                lines.add(b.toString());
            }
            lines.addAll(getLines(node, depth + 1));
        }
        return lines;
    }

    private void addIndentation(int depth, StringBuilder b) {
        for (int i = 0; i < depth; i++) {
            b.append("    ");
        }
    }
}