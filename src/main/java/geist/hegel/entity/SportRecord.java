package geist.hegel.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sport_record")
public class SportRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** DEVICE / MANUAL */
    private String source;

    /** 跑步/篮球/... */
    private String sportType;

    private LocalDateTime startTime;

    private Integer durationMin;

    private Integer calorieKcal;

    private BigDecimal distanceKm;

    private Integer avgHr;

    private String deviceId;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private int current;
    @TableField(exist = false)
    private int size;
}