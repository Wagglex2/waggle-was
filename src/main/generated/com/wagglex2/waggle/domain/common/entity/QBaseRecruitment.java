package com.wagglex2.waggle.domain.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBaseRecruitment is a Querydsl query type for BaseRecruitment
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBaseRecruitment extends EntityPathBase<BaseRecruitment> {

    private static final long serialVersionUID = -1416908672L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBaseRecruitment baseRecruitment = new QBaseRecruitment("baseRecruitment");

    public final EnumPath<com.wagglex2.waggle.domain.common.type.RecruitmentCategory> category = createEnum("category", com.wagglex2.waggle.domain.common.type.RecruitmentCategory.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deadline = createDateTime("deadline", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.wagglex2.waggle.domain.common.type.RecruitmentStatus> status = createEnum("status", com.wagglex2.waggle.domain.common.type.RecruitmentStatus.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final com.wagglex2.waggle.domain.user.entity.QUser user;

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QBaseRecruitment(String variable) {
        this(BaseRecruitment.class, forVariable(variable), INITS);
    }

    public QBaseRecruitment(Path<? extends BaseRecruitment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBaseRecruitment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBaseRecruitment(PathMetadata metadata, PathInits inits) {
        this(BaseRecruitment.class, metadata, inits);
    }

    public QBaseRecruitment(Class<? extends BaseRecruitment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.wagglex2.waggle.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

