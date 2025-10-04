package com.wagglex2.waggle.domain.common.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QParticipantInfo is a Querydsl query type for ParticipantInfo
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QParticipantInfo extends BeanPath<ParticipantInfo> {

    private static final long serialVersionUID = 1543846477L;

    public static final QParticipantInfo participantInfo = new QParticipantInfo("participantInfo");

    public final NumberPath<Integer> currParticipants = createNumber("currParticipants", Integer.class);

    public final NumberPath<Integer> maxParticipants = createNumber("maxParticipants", Integer.class);

    public QParticipantInfo(String variable) {
        super(ParticipantInfo.class, forVariable(variable));
    }

    public QParticipantInfo(Path<? extends ParticipantInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QParticipantInfo(PathMetadata metadata) {
        super(ParticipantInfo.class, metadata);
    }

}

