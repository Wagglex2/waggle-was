package com.wagglex2.waggle.domain.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Skill {
    // 언어
    HTML("HTML"),
    CSS("CSS"),
    JAVASCRIPT("JavaScript"),
    JAVA("Java"),
    KOTLIN("Kotlin"),
    PYTHON("Python"),
    SWIFT("Swift"),
    C_CPP("C/C++"),
    CSHARP("C#"),
    TYPESCRIPT("TypeScript"),

    // 라이브러리 / 프레임워크
    REACT("React"),
    NODE_JS("Node.js"),
    EXPRESS("Express"),
    VUE_JS("Vue.js"),
    NEXT_JS("Next.js"),
    SPRING_BOOT("Spring Boot"),
    DJANGO("Django"),
    PANDAS("Pandas"),
    SCIKIT_LEARN("scikit-learn"),
    PYTORCH("PyTorch"),
    TENSORFLOW("TensorFlow"),
    FLUTTER("Flutter"),

    // 데이터베이스
    MYSQL("MySQL"),
    REDIS("Redis"),
    MONGODB("MongoDB"),
    POSTGRESQL("PostgreSQL"),

    // 협업 / 툴
    GIT_GITHUB("Git/GitHub"),
    GITHUB_ACTIONS("GitHub Actions"),
    FIGMA("Figma"),
    NOTION("Notion"),
    JIRA("Jira"),

    // 기타
    DOCKER("Docker"),
    UNITY("Unity"),
    UNREAL("Unreal");

    private final String desc;

    public String getName() {
        return name();
    }
}

