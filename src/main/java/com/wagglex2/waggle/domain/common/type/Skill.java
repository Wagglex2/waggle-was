package com.wagglex2.waggle.domain.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Skill {
    HTML("HTML"),
    CSS("CSS"),
    JAVASCRIPT("JavaScript"),
    REACT("React"),
    NODE_JS("Node.js"),
    JAVA("Java"),
    KOTLIN("Kotlin"),
    SPRING_BOOT("Spring Boot"),
    PYTHON("Python"),
    DJANGO("Django"),
    PANDAS("Pandas"),
    SCIKIT_LEARN("scikit-learn"),
    PYTORCH("PyTorch"),
    TENSORFLOW("TensorFlow"),
    SWIFT("Swift"),
    FLUTTER("Flutter"),
    DOCKER("Docker"),
    UNITY("Unity"),
    UNREAL("Unreal"),
    GITHUB_ACTIONS("GitHub Actions"),
    FIGMA("Figma"),
    NOTION("Notion"),
    JIRA("Jira");

    private final String desc;

    public String getName() {
        return name();
    }
}
