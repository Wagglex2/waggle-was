package com.wagglex2.waggle.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1834732778L;

    public static final QUser user = new QUser("user");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> grade = createNumber("grade", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final EnumPath<com.wagglex2.waggle.domain.common.type.PositionType> position = createEnum("position", com.wagglex2.waggle.domain.common.type.PositionType.class);

    public final EnumPath<com.wagglex2.waggle.domain.user.entity.type.UserRoleType> role = createEnum("role", com.wagglex2.waggle.domain.user.entity.type.UserRoleType.class);

    public final StringPath shortIntro = createString("shortIntro");

    public final SetPath<com.wagglex2.waggle.domain.common.type.Skill, EnumPath<com.wagglex2.waggle.domain.common.type.Skill>> skills = this.<com.wagglex2.waggle.domain.common.type.Skill, EnumPath<com.wagglex2.waggle.domain.common.type.Skill>>createSet("skills", com.wagglex2.waggle.domain.common.type.Skill.class, EnumPath.class, PathInits.DIRECT2);

    public final EnumPath<com.wagglex2.waggle.domain.user.entity.type.University> university = createEnum("university", com.wagglex2.waggle.domain.user.entity.type.University.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath username = createString("username");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

