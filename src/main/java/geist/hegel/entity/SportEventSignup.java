package geist.hegel.entity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sport_event_signup")
public class SportEventSignup {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long eventId;

    @TableField(exist = false)
    private String eventName;

    private Long userId;

    @TableField(exist = false)
    private String userName;

    private LocalDateTime signupTime;

    /** SIGNED / CANCELED */
    private String status;

    @TableField(exist = false)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private int current;
    @TableField(exist = false)
    private int size;

    @TableField(exist = false)
    private Long teacherId;
}