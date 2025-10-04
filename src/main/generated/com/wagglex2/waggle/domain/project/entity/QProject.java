package com.wagglex2.waggle.domain.project.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 1065980614L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final com.wagglex2.waggle.domain.common.entity.QBaseRecruitment _super;

    //inherited
    public final EnumPath<com.wagglex2.waggle.domain.common.type.RecruitmentCategory> category;

    //inherited
    public final StringPath content;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deadline;

    public final SetPath<Integer, NumberPath<Integer>> grades = this.<Integer, NumberPath<Integer>>createSet("grades", Integer.class, NumberPath.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> id;

    public final EnumPath<com.wagglex2.waggle.domain.project.type.MeetingType> meetingType = createEnum("meetingType", com.wagglex2.waggle.domain.project.type.MeetingType.class);

    public final com.wagglex2.waggle.domain.common.type.QPeriod period;

    public final SetPath<com.wagglex2.waggle.domain.common.type.PositionParticipantInfo, com.wagglex2.waggle.domain.common.type.QPositionParticipantInfo> positions = this.<com.wagglex2.waggle.domain.common.type.PositionParticipantInfo, com.wagglex2.waggle.domain.common.type.QPositionParticipantInfo>createSet("positions", com.wagglex2.waggle.domain.common.type.PositionParticipantInfo.class, com.wagglex2.waggle.domain.common.type.QPositionParticipantInfo.class, PathInits.DIRECT2);

    public final EnumPath<com.wagglex2.waggle.domain.project.type.ProjectPurpose> purpose = createEnum("purpose", com.wagglex2.waggle.domain.project.type.ProjectPurpose.class);

    public final SetPath<com.wagglex2.waggle.domain.common.type.Skill, EnumPath<com.wagglex2.waggle.domain.common.type.Skill>> skills = this.<com.wagglex2.waggle.domain.common.type.Skill, EnumPath<com.wagglex2.waggle.domain.common.type.Skill>>createSet("skills", com.wagglex2.waggle.domain.common.type.Skill.class, EnumPath.class, PathInits.DIRECT2);

    //inherited
    public final EnumPath<com.wagglex2.waggle.domain.common.type.RecruitmentStatus> status;

    //inherited
    public final StringPath title;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt;

    // inherited
    public final com.wagglex2.waggle.domain.user.entity.QUser user;

    //inherited
    public final NumberPath<Integer> viewCount;

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.wagglex2.waggle.domain.common.entity.QBaseRecruitment(type, metadata, inits);
        this.category = _super.category;
        this.content = _super.content;
        this.createdAt = _super.createdAt;
        this.deadline = _super.deadline;
        this.id = _super.id;
        this.period = inits.isInitialized("period") ? new com.wagglex2.waggle.domain.common.type.QPeriod(forProperty("period")) : null;
        this.status = _super.status;
        this.title = _super.title;
        this.updatedAt = _super.updatedAt;
        this.user = _super.user;
        this.viewCount = _super.viewCount;
    }

}

