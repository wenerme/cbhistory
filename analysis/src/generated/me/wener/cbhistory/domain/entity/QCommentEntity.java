package me.wener.cbhistory.domain.entity;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QCommentEntity is a Querydsl query type for CommentEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCommentEntity extends EntityPathBase<CommentEntity> {

    private static final long serialVersionUID = 135202705L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentEntity commentEntity = new QCommentEntity("commentEntity");

    public final QArticleEntity article;

    public final StringPath comment = createString("comment");

    public final NumberPath<Integer> cons = createNumber("cons", Integer.class);

    public final DateTimePath<org.joda.time.LocalDateTime> date = createDateTime("date", org.joda.time.LocalDateTime.class);

    public final StringPath hostName = createString("hostName");

    public final StringPath icon = createString("icon");

    public final StringPath name = createString("name");

    public final QCommentEntity parent;

    public final NumberPath<Long> pid = createNumber("pid", Long.class);

    public final NumberPath<Integer> pros = createNumber("pros", Integer.class);

    public final NumberPath<Long> sid = createNumber("sid", Long.class);

    public final NumberPath<Long> tid = createNumber("tid", Long.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QCommentEntity(String variable) {
        this(CommentEntity.class, forVariable(variable), INITS);
    }

    public QCommentEntity(Path<? extends CommentEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCommentEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCommentEntity(PathMetadata<?> metadata, PathInits inits) {
        this(CommentEntity.class, metadata, inits);
    }

    public QCommentEntity(Class<? extends CommentEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new QArticleEntity(forProperty("article")) : null;
        this.parent = inits.isInitialized("parent") ? new QCommentEntity(forProperty("parent"), inits.get("parent")) : null;
    }

}

