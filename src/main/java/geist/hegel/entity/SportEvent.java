package geist.hegel.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import geist.hegel.utils.HtmlTextUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
@TableName("sport_event")
public class SportEvent {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    @TableField(value = "rule_text", exist = false)
    private String ruleText;

    private String eventType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private LocalDateTime signupStart;
    private LocalDateTime signupEnd;

    private Long maxParticipants;

    @TableField(exist = false)
    @JsonFormat(pattern = "MM-dd", timezone = "GMT+8")
    private LocalDateTime signupStartDate;

    @TableField(exist = false)
    @JsonFormat(pattern = "MM-dd", timezone = "GMT+8")
    private LocalDateTime signupEndDate;


    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy", timezone = "GMT+8")
    private LocalDateTime signupYear;

    @TableField(exist = false)
    private String simpleContent;

    @TableField(exist = false)
    private String signupStatus;

    @TableField(exist = false)
    private String firstImg;

    /** DRAFT / PUBLISHED / CLOSED / FINISHED */
    private String status;

    private Long createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private int current;
    @TableField(exist = false)
    private int size;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime publishTime;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime signupStartDateYMD;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime signupEndYMD;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime startTimeYMD;

    @TableField(exist = false)
    // 已报名人数
    private Long signupNumber;

    // 是否报名
    @TableField(exist = false)
    private Boolean hasSignup;

    public LocalDateTime getSignupStartDate() {
        return signupStart;
    }

    public LocalDateTime getSignupEndDate() {
        return signupEnd;
    }

    public String getSimpleContent() {
        return HtmlTextUtil.stripHtmlAndTake(content, 50);
    }

    public String getSignupStatus() {
        if (signupStart == null || signupEnd == null) {
            return "UNKNOWN";
        }

        if (StringUtils.isNotBlank(status) && !status.equals("PUBLISHED")) {
            return status;
        }

        LocalDateTime now = LocalDateTime.now();

        // 当前时间 >= signupStart 且 <= signupEnd
        if ((now.isEqual(signupStart) || now.isAfter(signupStart)) &&
                (now.isEqual(signupEnd)   || now.isBefore(signupEnd))) {
            return "SIGNUP";
        }

        // 当前时间 < signupStart
        if (now.isBefore(signupStart)) {
            return "NOT_START";
        }
        return "ENDED";
    }

    public String getFirstImg() {
        return HtmlTextUtil.extractFirstImgSrc(content);
    }

    public LocalDateTime getSignupYear() {
        return signupStart;
    }

    public LocalDateTime getPublishTime() {
        return createdAt;
    }

    public LocalDateTime getSignupStartDateYMD() {
        return signupStart;
    }

    public LocalDateTime getSignupEndYMD() {
        return signupEnd;
    }

    public LocalDateTime getStartTimeYMD() {
        return startTime;
    }
}