package geist.hegel.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sport_event_result")
public class SportEventResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long eventId;

    private Long userId;

    private BigDecimal scoreValue;

    private String scoreUnit;

    private Integer rankNo;

    private String remark;

    private Boolean published;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private int current;
    @TableField(exist = false)
    private int size;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String eventTitle;

    @TableField(exist = false)
    private Long eventCreatorId;
}