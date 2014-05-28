package me.wener.cbhistory.domain.entity;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QArticleEntity is a Querydsl query type for ArticleEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QArticleEntity extends EntityPathBase<ArticleEntity> {

    private static final long serialVersionUID = 1903188200L;

    public static final QArticleEntity articleEntity = new QArticleEntity("articleEntity");

    public final SetPath<CommentEntity, QCommentEntity> comments = this.<CommentEntity, QCommentEntity>createSet("comments", CommentEntity.class, QCommentEntity.class, PathInits.DIRECT2);

    public final DateTimePath<org.joda.time.LocalDateTime> date = createDateTime("date", org.joda.time.LocalDateTime.class);

    public final NumberPath<Integer> digNum = createNumber("digNum", Integer.class);

    public final NumberPath<Integer> discussCount = createNumber("discussCount", Integer.class);

    public final NumberPath<Integer> favNum = createNumber("favNum", Integer.class);

    public final StringPath introduction = createString("introduction");

    public final NumberPath<Integer> joinNum = createNumber("joinNum", Integer.class);

    public final DateTimePath<org.joda.time.LocalDateTime> lastUpdateDate = createDateTime("lastUpdateDate", org.joda.time.LocalDateTime.class);

    public final NumberPath<Integer> readCount = createNumber("readCount", Integer.class);

    public final NumberPath<Long> sid = createNumber("sid", Long.class);

    public final StringPath sn = createString("sn");

    public final StringPath source = createString("source");

    public final StringPath title = createString("title");

    public final StringPath token = createString("token");

    public QArticleEntity(String variable) {
        super(ArticleEntity.class, forVariable(variable));
    }

    public QArticleEntity(Path<? extends ArticleEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArticleEntity(PathMetadata<?> metadata) {
        super(ArticleEntity.class, metadata);
    }

}

