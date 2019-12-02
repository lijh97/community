package com.example.demo.service;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.dto.PaginationDTO;
import com.example.demo.enums.NotificationStatusEnum;
import com.example.demo.enums.NotificationTypeEnum;
import com.example.demo.exception.CustomizeErrorCode;
import com.example.demo.exception.CustomizeException;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.mapper.NotificationMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CommentMapper commentMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
        Integer offset;
        if (page < 1)
            page = 1;
        Integer totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page > totalPage)
            page = totalPage;
        if (page > 0)
            offset = size * (page - 1);
        else
            offset = 0;
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalPage, size, page);
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        if (notifications.size() == 0) {
            return paginationDTO;
        }
        Set<Long> disUserIds = notifications.stream().map(Notification::getNotifier).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>(disUserIds);

        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        List<NotificationDTO> notificationDTOS = notifications.stream().map(notification -> {
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setGmtCreate(notification.getGmtCreate());
            notificationDTO.setNotifier(userMap.get(notification.getNotifier()));
            notificationDTO.setId(notification.getId());
            notificationDTO.setType(notification.getType());
            notificationDTO.setStatus(notification.getStatus());
            notificationDTO.setQuestionId(notification.getQuestionId());
            if (notification.getType() == NotificationTypeEnum.REPLY_QUESTION.getType()) {
                //通知回复问题
                Question question = questionMapper.selectByPrimaryKey(notification.getOuterid());
                notificationDTO.setOuterTitle(question.getTitle());
            } else {
                //通知回复评论
                Comment comment = commentMapper.selectByPrimaryKey(notification.getOuterid());
                notificationDTO.setOuterTitle(comment.getContent());
            }
            return notificationDTO;
        }).collect(Collectors.toList());
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unread(Long id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().
                andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus()).andReceiverEqualTo(id);
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setGmtCreate(notification.getGmtCreate());
        notificationDTO.setType(notification.getType());
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(notification.getNotifier());
        List<User> users = userMapper.selectByExample(userExample);
        notificationDTO.setNotifier(users.get(0));
        notificationDTO.setStatus(notification.getStatus());
        notificationDTO.setQuestionId(notification.getQuestionId());
        if (notification.getType() == NotificationTypeEnum.REPLY_QUESTION.getType()) {
            //通知回复问题
            Question question = questionMapper.selectByPrimaryKey(notification.getOuterid());
            notificationDTO.setOuterTitle(question.getTitle());
        } else {
            //通知回复评论
            Comment comment = commentMapper.selectByPrimaryKey(notification.getOuterid());
            notificationDTO.setOuterTitle(comment.getContent());
        }
        return notificationDTO;
    }
}
