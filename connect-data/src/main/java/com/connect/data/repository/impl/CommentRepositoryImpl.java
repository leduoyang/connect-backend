package com.connect.data.repository.impl;

import com.connect.common.exception.ConnectDataException;
import com.connect.common.exception.ConnectErrorCode;
import com.connect.data.dao.ICommentDao;
import com.connect.data.entity.Comment;
import com.connect.data.entity.Comment;
import com.connect.data.entity.Post;
import com.connect.data.param.QueryCommentParam;
import com.connect.data.param.QueryPostParam;
import com.connect.data.repository.ICommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class CommentRepositoryImpl implements ICommentRepository {

    @Autowired
    ICommentDao commentDao;

    public CommentRepositoryImpl(ICommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public Comment queryCommentById(long id, String userId) {
        return commentDao.queryCommentById(id, userId);
    }

    public List<Comment> queryComment(QueryCommentParam param, String userId) {
        return commentDao.queryComment(
                param.getPostId(),
                param.getUserId(),
                param.getKeyword(),
                param.getTags(),
                userId
        );
    }

    public long createComment(Comment comment) {
        int affected = commentDao.createComment(comment);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.COMMENT_CREATE_EXCEPTION);
        }

        return comment.getId();
    }

    public void updateComment(Comment comment) {
        long targetId = comment.getId();
        String userId = comment.getUpdatedUser();
        boolean existed = commentDao.commentExisting(targetId, userId);
        if (!existed) {
            throw new ConnectDataException(
                    ConnectErrorCode.COMMENT_NOT_EXISTED_EXCEPTION,
                    String.format("Post %s not exited or user %s is not the creator", targetId, userId)
            );
        }

        int affected = commentDao.updateComment(comment);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.COMMENT_UPDATE_EXCEPTION);
        }
    }

    public void incrementViews(long id, int version) {
        int affected = commentDao.incrementViews(id, version);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.COMMENT_UPDATE_EXCEPTION, "update viewCounts failed");
        }
    }

    public void refreshStars(long id, int version, int stars) {
        int affected = commentDao.refreshStars(id, version, stars);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.COMMENT_UPDATE_EXCEPTION, "update likesCount failed");
        }
    }

    public void deleteComment(Comment comment) {
        long targetId = comment.getId();
        String userId = comment.getUpdatedUser();
        boolean existed = commentDao.commentExisting(targetId, userId);
        if (!existed) {
            throw new ConnectDataException(
                    ConnectErrorCode.COMMENT_NOT_EXISTED_EXCEPTION,
                    String.format("Post %s not exited or user %s is not the creator", targetId, userId)
            );
        }

        int affected = commentDao.deleteComment(targetId, userId);
        if (affected <= 0) {
            throw new ConnectDataException(ConnectErrorCode.COMMENT_UPDATE_EXCEPTION);
        }
    }
}
