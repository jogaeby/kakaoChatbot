package com.chatbot.base.dto.kakao.response.property.common;

import com.chatbot.base.dto.kakao.constatnt.block.BlockId;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ListItem {
    private String title;
    private String description;
    private String imageUrl;
    private Link link;
    private String action;
    private String blockId;
    private String messageText;
    private Map<ButtonParamKey,Object> extra = new HashMap<>();

    public ListItem(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public void setActionBlock(BlockId nextBlockId) {
        this.action = "block";
        this.blockId = nextBlockId.getBlockId();
    }

    public void setActionBlock(String  blockId) {
        this.action = "block";
        this.blockId = blockId;
    }

    public void setActionMessage(String messageText) {
        this.action = "message";
        this.messageText = messageText;
    }
    public void setExtra(String blockId, ButtonParamKey buttonParamKey, Object buttonParamValue) {
        setActionBlock(blockId);
        this.extra.put(buttonParamKey,buttonParamValue);
    }

    public void setExtra(ButtonParamKey buttonParamKey, Object buttonParamValue) {
        this.extra.put(buttonParamKey,buttonParamValue);
    }
}
