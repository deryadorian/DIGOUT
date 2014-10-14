package com.digout.converter;

import com.digout.artifact.Message;
import com.digout.manager.UserSessionHolder;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.user.UserEntity;
import com.digout.model.entity.user.UserMessageEntity;
import com.digout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMessageConverter extends SimpleConverterFactory<Message, UserMessageEntity> {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSessionHolder userSessionHolder;
    @Autowired
    private UserPhotoConverter userPhotoConverter;

    @Override
    protected UserMessageEntity initEntity(final Message message) {
        return null;
    }

    @Override
    protected Message initTO(final UserMessageEntity entity) {
        Message message = new Message();
        message.setMessageId(entity.getId());
        message.setData(entity.getText());
        message.setRead(entity.isRead());
        message.setSentDate(entity.getSentDate());
        UserEntity sender = entity.getSender();
        Long senderId = sender.getId();
        UserEntity receiver = entity.getReceiver();
        Long receiverId = receiver.getId();
        message.setReceiverId(receiverId);
        message.setSenderId(senderId);
        message.setReceiverUserName(receiver.getUserCredentials().getUsername());
        message.setSenderUserName(sender.getUserCredentials().getUsername());
        if (!this.userSessionHolder.getUserId().equals(receiverId)) {
            message.setDialogThumbImage(this.userPhotoConverter.convertUserImageEntity(receiver.getImages(),
                    ImageFormat.THUMB));
        } else {
            message.setDialogThumbImage(this.userPhotoConverter.convertUserImageEntity(sender.getImages(), ImageFormat.THUMB));
        }
        return message;
    }
}
