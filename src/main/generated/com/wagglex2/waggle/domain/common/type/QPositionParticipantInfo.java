package com.wagglex2.waggle.domain.common.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPositionParticipantInfo is a Querydsl query type for PositionParticipantInfo
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPositionParticipantInfo extends BeanPath<PositionParticipantInfo> {

    private static final long serialVersionUID = -1504561692L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPositionParticipantInfo positionParticipantInfo = new QPositionParticipantInfo("positionParticipantInfo");

    public final QParticipantInfo participantInfo;

    public final EnumPath<PositionType> position = createEnum("position", PositionType.class);

    public QPositionParticipantInfo(String variable) {
        this(PositionParticipantInfo.class, forVariable(variable), INITS);
    }

    public QPositionParticipantInfo(Path<? extends PositionParticipantInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPositionParticipantInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPositionParticipantInfo(PathMetadata metadata, PathInits inits) {
        this(PositionParticipantInfo.class, metadata, inits);
    }

    public QPositionParticipantInfo(Class<? extends PositionParticipantInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.participantInfo = inits.isInitialized("participantInfo") ? new QParticipantInfo(forProperty("participantInfo")) : null;
    }

}

