package geist.hegel.controller;

import com.baomidou.mybatisplus.plugins.Page;
import geist.hegel.common.R;
import geist.hegel.entity.SportEvent;
import geist.hegel.service.SportEventService;
import geist.hegel.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 赛事模块控制器
 * 角色说明：
 *  - ADMIN：创建、发布、关闭赛事，录入成绩，发布成绩
 *  - TEACHER：可与 ADMIN 同权限（根据项目需求）
 *  - STUDENT：报名、取消报名、查看赛事与成绩
 * 状态流转：
 *  DRAFT -> PUBLISHED -> CLOSED -> FINISHED
 * 业务主线：
 *  创建赛事 -> 发布赛事 -> 学生报名 -> 比赛结束 -> 录入成绩 -> 发布成绩
 */
@RestController
@RequestMapping("/api/events")
public class SportEventController {

    @Resource
    private SportEventService sportEventService;
    // =========================
    // 一、赛事基础管理
    // =========================

    /**
     * 接口说明：创建赛事
     * 权限：ADMIN / TEACHER
     * 思路：
     * 1. 接收赛事基本信息（时间、规则、人数限制等）
     * 2. 默认状态设置为 DRAFT
     * 3. 保存到 sport_event 表
     */
    @PostMapping
    public R<Object> createEvent(@RequestBody SportEvent sportEvent) {
        sportEvent.setCreatedAt(LocalDateTime.now());
        sportEvent.setCreatedBy(UserUtils.getUserId());
        sportEvent.setStatus("DRAFT");
        sportEventService.insert(sportEvent);
        return R.ok(null);
    }

    /**
     * 接口说明：修改赛事
     * 权限：ADMIN / TEACHER
     * 思路：
     * 1. 仅允许 DRAFT 状态修改
     * 2. 校验时间逻辑（报名时间 < 比赛时间）
     * 3. 更新 sport_event 表
     */
    @PutMapping("/{id}")
    public R<Object> updateEvent(@RequestBody SportEvent sportEvent) {
        sportEvent.setUpdatedAt(LocalDateTime.now());
        sportEventService.updateById(sportEvent);
        return R.ok(null);
    }

    @GetMapping("/{id}")
    public R<Object> getEvent(@PathVariable Long id) {
        return R.ok(sportEventService.selectById(id));
    }
    /**
     * 接口说明：发布赛事
     * 权限：ADMIN / TEACHER
     * 思路：
     * 1. 校验赛事信息完整
     * 2. 修改状态为 PUBLISHED
     * 3. 发布后学生可见
     */
    @PutMapping("/{id}/publish")
    public R<Object> publishEvent(@PathVariable Long id) {
        SportEvent sportEvent = new SportEvent();
        sportEvent.setId(id);
        sportEvent.setStatus("PUBLISHED");
        sportEventService.updateById(sportEvent);
        return R.ok(null);
    }

    /**
     * 接口说明：关闭赛事
     * 权限：ADMIN / TEACHER
     * 思路：
     * 1. 比赛结束后手动关闭
     * 2. 修改状态为 CLOSED
     * 3. 关闭后不允许报名
     */
    @PutMapping("/{id}/close")
    public R<Object> closeEvent(@PathVariable Long id) {
        SportEvent sportEvent = new SportEvent();
        sportEvent.setId(id);
        sportEvent.setStatus("CLOSED");
        sportEventService.updateById(sportEvent);
        return R.ok(null);
    }

    /**
     * 接口说明：完成赛事（发布最终成绩）
     * 权限：ADMIN / TEACHER
     * 思路：
     * 1. 校验当前状态必须为 CLOSED
     * 2. 校验成绩已录入
     * 3. 修改状态为 FINISHED
     * 4. 学生可查看成绩
     */
    @PutMapping("/{id}/finish")
    public R<Object> finishEvent(@PathVariable Long id) {
        SportEvent sportEvent = new SportEvent();
        sportEvent.setId(id);
        sportEvent.setStatus("FINISHED");
        sportEventService.updateById(sportEvent);
        return R.ok(null);
    }

    /**
     * 接口说明：赛事列表查询
     * 权限：所有角色
     * 思路：
     * 1. STUDENT 只能看到 PUBLISHED/CLOSED/FINISHED
     * 2. ADMIN/TEACHER 可看到全部
     */
    @GetMapping
    public R<Page<SportEvent>> listEvents(@ModelAttribute SportEvent cond) {
        return R.ok(sportEventService.queryPage(cond));
    }

}